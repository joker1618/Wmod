package xxx.joker.apps.wmod.service;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.apps.wmod.exception.WmodException;
import xxx.joker.apps.wmod.inputArgs.WmodCommand;
import xxx.joker.apps.wmod.inputArgs.WmodOptions;
import xxx.joker.apps.wmod.model.RenameResult;
import xxx.joker.apps.wmod.model.RenameStatus;
import xxx.joker.apps.wmod.model.RenamedPath;
import xxx.joker.libs.javalibs.utils.JkFiles;
import xxx.joker.libs.javalibs.utils.JkStreams;
import xxx.joker.libs.javalibs.utils.JkStrings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by f.barbano on 12/12/2017.
 */
public class RenameService {

	private final WmodOptions userInput;
	private final List<Path> pathList;

	public RenameService(WmodOptions userInput) {
		this.userInput = userInput;
		this.pathList = Arrays.stream(userInput.getFilePaths()).sorted().collect(Collectors.toList());
	}

	public RenameResult performRename() throws WmodException {
		WmodCommand selectedCommand = userInput.getSelectedCommand();
		List<RenamedPath> result;

		switch (selectedCommand) {
			case CMD_DELETE:			result = manageDelete(); break;
			case CMD_CHANGE_CASE:		result = manageChangeCase(); break;
			case CMD_INSERT:			result = manageInsert(); break;
			case CMD_ENUMERATE:			result = manageEnumerate(); break;
			case CMD_NORMALIZE:			result = manageNormalize(); break;
			case CMD_NAMES_FROM_FILE:	result = manageNamesFromFile(); break;
			default:
				throw new RuntimeException("Command " + selectedCommand.name() + " not managed");
		}

		// Checks if new names are duplicated
		for(int i = 0; i < result.size(); i++) {
			for(int j = 0; j < result.size(); j++) {
				if(i != j) {
					if(result.get(i).getStatus() == RenameStatus.NO_ERROR && result.get(i).getNewPath().equals(result.get(j).getNewPath())) {
						result.get(i).setStatus(RenameStatus.NEW_NAME_DUPLICATED);
					}
				}
			}
		}

		// Check if new names already exists
		try {
			List<Path> actuals = JkStreams.map(result, RenamedPath::getActualPath);
			List<Path> folderContent = Files.find(result.get(0).getActualPath().getParent(), 1, (p, a) -> true).collect(Collectors.toList());
			folderContent = JkStreams.filterAndMap(folderContent, p -> !actuals.contains(p.toAbsolutePath()), Path::toAbsolutePath);
			for(RenamedPath obj : result) {
				if(obj.getStatus() == RenameStatus.NO_ERROR && folderContent.contains(obj.getNewPath())) {
					obj.setStatus(RenameStatus.NEW_NAME_EXISTING);
				}
			}

		} catch (IOException e) {
			throw new WmodException(e, "Exception raised while check if new file names already exists");
		}

		return new RenameResult(result);
	}

	private List<RenamedPath> manageDelete() {
		String toIns = userInput.getInsert() == null ? "" : userInput.getInsert();

		if(userInput.getString() != null) {
			return JkStreams.map(pathList, p -> replaceString(p, userInput.getString(), toIns, userInput.isIgnoreCase()));

		} else if(userInput.getRange() != null) {
			return JkStreams.map(pathList, p -> replaceRange(p, userInput.getRange()[0], userInput.getRange()[1], toIns));

		} else if(userInput.getBetweenInclude() != null) {
			return JkStreams.map(pathList, p -> replaceBetween(p, userInput.getBetweenInclude()[0], userInput.getBetweenInclude()[1], toIns, true, userInput.isIgnoreCase()));

		} else if(userInput.getBetweenExclude() != null) {
			return JkStreams.map(pathList, p -> replaceBetween(p, userInput.getBetweenExclude()[0], userInput.getBetweenExclude()[1], toIns, false, userInput.isIgnoreCase()));

		} else if(userInput.getPositions() != null) {
			return JkStreams.map(pathList, p -> replacePositions(p, userInput.getPositions(), toIns));
		}

		throw new RuntimeException("No sub option found for manageDelete");
	}

	private List<RenamedPath> manageChangeCase() {
		if(userInput.getString() != null) {
			return JkStreams.map(pathList, p -> changeCaseString(p, userInput.getString(), userInput.isLower(), userInput.isIgnoreCase()));

		} else if(userInput.getRange() != null) {
			return JkStreams.map(pathList, p -> changeCaseRange(p, userInput.getRange()[0], userInput.getRange()[1], userInput.isLower()));

		} else if(userInput.getBetweenInclude() != null) {
			return JkStreams.map(pathList, p -> changeCaseBetween(p, userInput.getBetweenInclude()[0], userInput.getBetweenInclude()[1], userInput.isLower(), true, userInput.isIgnoreCase()));

		} else if(userInput.getBetweenExclude() != null) {
			return JkStreams.map(pathList, p -> changeCaseBetween(p, userInput.getBetweenExclude()[0], userInput.getBetweenExclude()[1], userInput.isLower(), false, userInput.isIgnoreCase()));

		} else if(userInput.getPositions() != null) {
			return JkStreams.map(pathList, p -> changeCasePositions(p, userInput.getPositions(), userInput.isIgnoreCase()));
		}

		throw new RuntimeException("No sub option found for manageChangeCase");

	}

	private List<RenamedPath> manageInsert() {
		return JkStreams.map(pathList, p -> insertPositions(p, userInput.getPositions(), userInput.getInsert()));
	}

	private List<RenamedPath> manageEnumerate() {
		Integer start = userInput.getStart();
		Integer step = userInput.getStep();
		Integer digits = userInput.getDigit();
		List<RenamedPath> toRet = new ArrayList<>();

		for(int i = 0; i < pathList.size(); i++) {
			String num = String.format("%0" + digits + "d", start + step * i);
			toRet.add(insertPositions(pathList.get(i), new Integer[] {0}, num));
		}

		return toRet;
	}

	private List<RenamedPath> manageNormalize() {
		return JkStreams.map(pathList, p -> normalizeFileName(p, userInput.isFirst(), userInput.getOffset()));
	}

	private RenamedPath normalizeFileName(Path actual, boolean onlyFirstWord, int offset) {
		String extension = JkFiles.getExtension(actual);
		String format = Pattern.quote("." + extension) + "$";
		String actualName = actual.getFileName().toString().replaceAll(format, "");
		Path renamed;
		RenameStatus status = RenameStatus.NO_ERROR;

		if(actualName.length() <= offset) {
			renamed = actual;
		} else {
			String prefix = actualName.substring(0, offset);
			String suffix = actualName.substring(offset);
			String[] split = JkStrings.splitAllFields(suffix, " ");
			StringBuilder sb = new StringBuilder(prefix);
			boolean capitalize = true;
			for(int i = 0; i < split.length; i++) {
				String word = split[i];
				String str = "";

				if(!word.isEmpty()) {
					if(i == 0) {
						if(prefix.isEmpty() || prefix.endsWith(" ")) {
							str = capitalizeString(word);
						} else {
							str = word;
						}
					} else {
						str = capitalize ? capitalizeString(word) : word;
					}

					if(capitalize && onlyFirstWord) {
						capitalize = false;
					}
				}

				if(i > 0)	sb.append(" ");
				sb.append(str);
			}

			renamed = actual.getParent().resolve(sb.toString() + "." + extension);
			if(StringUtils.isBlank(sb.toString())) {
				status = RenameStatus.NEW_NAME_BLANK;
			}
		}

		RenamedPath renamedPath = new RenamedPath(actual, renamed);
		renamedPath.setStatus(status);
		return renamedPath;
	}

	private String capitalizeString(String source) {
		String res = "";
		if(!source.isEmpty()) {
			res = source.substring(0, 1).toUpperCase();
			res += StringUtils.substring(source, 1);
		}
		return res;
	}



	private List<RenamedPath> manageNamesFromFile() throws WmodException {
		try {
			List<String> lines = Files.readAllLines(userInput.getNamesFile());
			lines = JkStreams.filterAndMap(lines, StringUtils::isNotBlank, String::trim);

			if (lines.size() != pathList.size()) {
				throw new WmodException("Names found in file %d, but file paths are %d", lines.size(), pathList.size());
			}

			List<RenamedPath> toRet = new ArrayList<>();
			for (int i = 0; i < lines.size(); i++) {
				RenamedPath renamedPath = setNewFilename(pathList.get(i), lines.get(i), userInput.getOffset());
				toRet.add(renamedPath);
			}
			return toRet;

		} catch(IOException ex) {
			throw new WmodException(ex);
		}
	}


	private RenamedPath replaceString(Path actual, String toDel, String toIns, boolean ignoreCase) {
		String delQuoted = Pattern.quote(toDel);
		Pattern pattern = ignoreCase ? Pattern.compile(delQuoted, Pattern.CASE_INSENSITIVE) : Pattern.compile(delQuoted);
		Matcher matcher = pattern.matcher(actual.getFileName().toString());
		String newFilename = matcher.replaceAll(toIns);
		Path renamed = actual.getParent().resolve(newFilename);

		RenameStatus status = StringUtils.isBlank(newFilename) ? RenameStatus.NEW_NAME_BLANK : RenameStatus.NO_ERROR;
		RenamedPath renamedPath = new RenamedPath(actual, renamed);
		renamedPath.setStatus(status);
		return renamedPath;
	}

	private RenamedPath replaceRange(Path actual, int start, int offset, String toIns) {
		Path renamed = actual;
		RenameStatus status = RenameStatus.NO_ERROR;

		String fn = actual.getFileName().toString();
		if(start < fn.length()) {
			String newFn = fn.substring(0, start);
			newFn += toIns;
			int reStart = start + offset;
			if (reStart < fn.length()) {
				newFn += fn.substring(reStart);
			}

			renamed = actual.getParent().resolve(newFn);
			if(StringUtils.isBlank(newFn)) {
				status = RenameStatus.NEW_NAME_BLANK;
			}
		}

		RenamedPath renamedPath = new RenamedPath(actual, renamed);
		renamedPath.setStatus(status);
		return renamedPath;
	}

	private RenamedPath replaceBetween(Path actual, String begin, String end, String toIns, boolean includeExtremes, boolean ignoreCase) {
		int startsub = 0;
		int beginIdx, endIdx;
		int start, offset;

		StringBuilder newName = new StringBuilder();
		String sub = actual.getFileName().toString();
		boolean go = true;

		while (go) {
			sub = sub.substring(startsub);
			go = false;

			if (sub.contains(begin)) {
				beginIdx = ignoreCase ? JkStrings.indexOfIgnoreCase(sub, begin) : sub.indexOf(begin);

				String tempSub = sub.substring(beginIdx + begin.length());
				boolean found = ignoreCase ? StringUtils.containsIgnoreCase(tempSub, end) : tempSub.contains(end);
				if (found) {
					endIdx = beginIdx + begin.length();
					if(ignoreCase)	endIdx += JkStrings.indexOfIgnoreCase(tempSub, end);
					else			endIdx += tempSub.indexOf(end);

					start = beginIdx + (includeExtremes ? 0 : begin.length());
					offset = endIdx + (includeExtremes ? end.length() : 0) - start;

					newName.append(sub.substring(0, start));
					newName.append(offset > 0 ? toIns : "");

					startsub = start + offset;
					go = true;
				}
			}
		}
		newName.append(sub);

		RenameStatus status = StringUtils.isBlank(newName.toString()) ? RenameStatus.NEW_NAME_BLANK : RenameStatus.NO_ERROR;
		Path renamed = actual.getParent().resolve(newName.toString());
		RenamedPath renamedPath = new RenamedPath(actual, renamed);
		renamedPath.setStatus(status);
		return renamedPath;
	}

	private RenamedPath replacePositions(Path actual, Integer[] posArr, String toIns) {
		return insertInPositions(actual, posArr, toIns, true);
	}

	private RenamedPath changeCaseString(Path actual, String toDel, boolean lowerize, boolean ignoreCase) {
		String delQuoted = Pattern.quote(toDel);
		Pattern pattern = ignoreCase ? Pattern.compile(delQuoted, Pattern.CASE_INSENSITIVE) : Pattern.compile(delQuoted);
		Matcher matcher = pattern.matcher(actual.getFileName().toString());
		String newFilename = matcher.replaceAll(lowerize ? toDel.toLowerCase() : toDel.toUpperCase());
		Path renamed = actual.getParent().resolve(newFilename);

		RenamedPath renamedPath = new RenamedPath(actual, renamed);
		renamedPath.setStatus(RenameStatus.NO_ERROR);
		return renamedPath;
	}

	private RenamedPath changeCaseRange(Path actual, int start, int offset, boolean lowerize) {
		Path renamed = actual;

		String fn = actual.getFileName().toString();
		if(start < fn.length()) {
			String newFn = fn.substring(0, start);
			int reStart = start + offset;
			String temp = StringUtils.substring(fn, start, reStart);
			newFn += lowerize ? temp.toLowerCase() : temp.toUpperCase();
			if (reStart < fn.length()) {
				newFn += fn.substring(reStart);
			}

			renamed = actual.getParent().resolve(newFn);
		}

		RenamedPath renamedPath = new RenamedPath(actual, renamed);
		renamedPath.setStatus(RenameStatus.NO_ERROR);
		return renamedPath;
	}

	private RenamedPath changeCaseBetween(Path actual, String begin, String end, boolean lowerize, boolean includeExtremes, boolean ignoreCase) {
		int startsub = 0;
		int beginIdx, endIdx;
		int start, offset;

		StringBuilder newName = new StringBuilder();
		String sub = actual.getFileName().toString();
		boolean go = true;

		while (go) {
			sub = sub.substring(startsub);
			go = false;

			if (sub.contains(begin)) {
				beginIdx = ignoreCase ? JkStrings.indexOfIgnoreCase(sub, begin) : sub.indexOf(begin);

				String tempSub = sub.substring(beginIdx + begin.length());
				boolean found = ignoreCase ? StringUtils.containsIgnoreCase(tempSub, end) : tempSub.contains(end);
				if (found) {
					endIdx = beginIdx + begin.length();
					if(ignoreCase)	endIdx += JkStrings.indexOfIgnoreCase(tempSub, end);
					else			endIdx += tempSub.indexOf(end);

					start = beginIdx + (includeExtremes ? 0 : begin.length());
					offset = endIdx + (includeExtremes ? end.length() : 0) - start;

					newName.append(sub.substring(0, start));

					String temp = StringUtils.substring(sub, start, start + offset);
					newName.append(lowerize ? temp.toLowerCase() : temp.toUpperCase());

					startsub = start + offset;
					go = true;
				}
			}
		}
		newName.append(sub);

		Path renamed = actual.getParent().resolve(newName.toString());
		RenamedPath renamedPath = new RenamedPath(actual, renamed);
		renamedPath.setStatus(RenameStatus.NO_ERROR);
		return renamedPath;
	}

	private RenamedPath changeCasePositions(Path actual, Integer[] posArr, boolean lowerize) {
		StringBuilder newName = new StringBuilder(actual.getFileName().toString());
		for(int i = 0; i < posArr.length; i++) {	// posList decr ordered
			int pos = posArr[i];
			if(pos < newName.length()) {
				String temp = newName.substring(pos, pos + 1);
				newName.replace(i, i+1, lowerize ? temp.toLowerCase() : temp.toUpperCase());
			}
		}

		Path renamed = actual.getParent().resolve(newName.toString());
		RenamedPath renamedPath = new RenamedPath(actual, renamed);
		renamedPath.setStatus(RenameStatus.NO_ERROR);
		return renamedPath;
	}

	private RenamedPath insertPositions(Path actual, Integer[] posArr, String toIns) {
		return insertInPositions(actual, posArr, toIns, false);
	}

	private RenamedPath insertInPositions(Path actual, Integer[] posArr, String toIns, boolean removeCharAtPos) {
		StringBuilder newName = new StringBuilder(actual.getFileName().toString());
		for(int i = 0; i < posArr.length; i++) {	// posList decr ordered
			int pos = posArr[i];
			if(pos == newName.length() && !removeCharAtPos) {
				newName.append(toIns);
			} else if(pos < newName.length()) {
				int end = removeCharAtPos ? pos+1 : pos;
				newName.replace(pos, end, toIns);
			}
		}

		RenameStatus status = StringUtils.isBlank(newName.toString()) ? RenameStatus.NEW_NAME_BLANK : RenameStatus.NO_ERROR;
		Path renamed = actual.getParent().resolve(newName.toString());
		RenamedPath renamedPath = new RenamedPath(actual, renamed);
		renamedPath.setStatus(status);
		return renamedPath;
	}

	private RenamedPath setNewFilename(Path actual, String newName, int offset) throws WmodException {
		String actualName = actual.getFileName().toString();
		if(actualName.length() < offset) {
			throw new WmodException("The filename length of %s is less than offset (%d)", actual, offset);
		}

		String extension = JkFiles.getExtension(actual);
		String newFn = String.format("%s%s.%s", actual.getFileName().toString().substring(0, offset), newName, extension);
		Path renamed = actual.getParent().resolve(newFn);

		RenameStatus status = StringUtils.isBlank(newFn) ? RenameStatus.NEW_NAME_BLANK : RenameStatus.NO_ERROR;
		RenamedPath renamedPath = new RenamedPath(actual, renamed);
		renamedPath.setStatus(status);
		return renamedPath;
	}
}
