package xxx.joker.apps.wmod.main;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.apps.wmod.exception.WmodException;
import xxx.joker.apps.wmod.inputArgs.WmodCommand;
import xxx.joker.apps.wmod.inputArgs.WmodHelp;
import xxx.joker.apps.wmod.inputArgs.WmodOptName;
import xxx.joker.apps.wmod.inputArgs.WmodOptions;
import xxx.joker.apps.wmod.model.RenameResult;
import xxx.joker.apps.wmod.model.RenameStatus;
import xxx.joker.apps.wmod.model.RenamedPath;
import xxx.joker.apps.wmod.service.RenameService;
import xxx.joker.libs.argsparser.IInputParser;
import xxx.joker.libs.argsparser.InputParserImpl;
import xxx.joker.libs.argsparser.exception.InputParserException;
import xxx.joker.libs.core.format.JkColumnFmtBuilder;
import xxx.joker.libs.core.utils.JkConsole;
import xxx.joker.libs.core.utils.JkFiles;
import xxx.joker.libs.core.utils.JkStreams;
import xxx.joker.libs.core.utils.JkStrings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by f.barbano on 12/12/2017.
 */
public class Main {

	public static void main(String[] args) throws WmodException, IOException {

		// Parse user input
		WmodOptions userInput = null;
		try {
			userInput = parseUserInput(args);
		} catch (InputParserException e) {
			JkConsole.display("%s\n", e.getMessage());
			JkConsole.display(WmodHelp.USAGE);
			System.exit(1);
		}

		// Perform operation
		RenameResult renameResult = performOperation(userInput);

		// Display results
		displayRenameResult(renameResult);

		// Persist changes
		if(renameResult.canBeCommitted() && renameResult.isAnyChanged()) {
			JkConsole.display("");
			String choice = JkConsole.readUserInput("Rename files (y/n)?  ", s -> StringUtils.equalsAnyIgnoreCase(s, "y", "n"));
			JkConsole.display("");
			if(choice.equalsIgnoreCase("y")) {
//				persistChanges2(renameResult);
				persistChanges(renameResult);
                List<RenamedPath> changedPaths = JkStreams.filter(renameResult.getRenamedPaths(RenameStatus.NO_ERROR), RenamedPath::isChanged);
                JkConsole.display("%d files renamed\n", changedPaths.size());
			}
		}
	}

	private static WmodOptions parseUserInput(String[] args) throws InputParserException {
		WmodOptions userInput = new WmodOptions();
		IInputParser parser = new InputParserImpl(userInput, WmodOptName.class, WmodCommand.class, JkFiles.getLauncherPath(Main.class));
		parser.parse(args);
		return userInput;
	}

	private static RenameResult performOperation(WmodOptions userInput) throws WmodException {
		WmodCommand selCmd = userInput.getSelectedCommand();

		if(selCmd == WmodCommand.CMD_HELP) {
			JkConsole.display(WmodHelp.USAGE);
			System.exit(0);
		}

		RenameService renameService = new RenameService(userInput);
		return renameService.performRename();
	}

	private static void displayRenameResult(RenameResult result) {
		Path folder = result.getRenamedPaths().get(0).getActualPath().getParent();
		String strFolder = "FOLDER:  " + folder;

		List<String> renLines;
		if(result.canBeCommitted()) {
			renLines = JkStreams.map(result.getRenamedPaths(), rp -> toString(rp, false));
		} else {
			renLines = JkStreams.map(result.getRenamedPaths(RenameStatus.NO_ERROR), rp -> toString(rp, true));
			for(RenameStatus status : RenameStatus.values()) {
				if(status != RenameStatus.NO_ERROR) {
					renLines.addAll(JkStreams.map(result.getRenamedPaths(status), rp -> toString(rp, true)));
				}
			}
		}

		String strRename = new JkColumnFmtBuilder(renLines).toString("|", 3);
		renLines = JkStrings.splitFieldsList(strRename, "\n");

		String pad = StringUtils.repeat(' ', 2);
		int maxLineLen = JkStrings.splitFieldsList(strRename, "\n").stream().mapToInt(String::length).max().orElse(0);
		int max = Math.max(strFolder.length(), maxLineLen);
		int len = max + pad.length() * 2;
		int lenRem = len - pad.length();
		String sepLine = StringUtils.repeat('-', len);

		List<String> lines = new ArrayList<>();
		lines.add(String.format("-%s-", sepLine));
		lines.add(String.format("|%s%-"+ lenRem +"s|", pad, strFolder));
		lines.add(String.format("|%s|", sepLine));
		renLines.forEach(rl -> lines.add(String.format("|%s%-"+ lenRem +"s|", pad, rl)));
		lines.add(String.format("-%s-", sepLine));

		String content = JkStreams.join(lines, "\n");
		JkConsole.display(content);
	}

	private static String toString(RenamedPath renamedPath, boolean fourCols) {
		Path folder = renamedPath.getActualPath().getParent();
		String act = folder.relativize(renamedPath.getActualPath()).toString();
		String target = folder.relativize(renamedPath.getNewPath()).toString();
		String sep = renamedPath.isChanged() ? "-->" : "";
		String lastSep = fourCols ? "|" : "";
		boolean isErr = renamedPath.getStatus() != RenameStatus.NO_ERROR;
		String err = fourCols && isErr ? renamedPath.getStatus().name() : "";
		return String.format("%s|%3s|%s%s", act, sep, target, lastSep, err);
	}

    private static void persistChanges(RenameResult result) {
        // Two phases rename
        Map<RenamedPath, Path> map = new HashMap<>();
        long num = System.currentTimeMillis();
        for(RenamedPath renamedPath : result.getRenamedPaths()) {
            Path middlePath = JkFiles.computeSafelyPath(".tmp." + num++);
			JkFiles.moveFile(renamedPath.getActualPath(), middlePath, false);
            map.put(renamedPath, middlePath);
        }

        for(Map.Entry<RenamedPath, Path> entry : map.entrySet()) {
            RenamedPath renamedPath = entry.getKey();
            Path middlePath = entry.getValue();
			JkFiles.moveFile(middlePath, renamedPath.getNewPath(), false);
        }
    }
}
