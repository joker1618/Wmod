package xxx.joker.apps.wmod.inputArgs;

import xxx.joker.libs.core.utils.JkStrings;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by f.barbano on 12/12/2017.
 */
public class WmodHelp {

	private static final String NEWLINE = "\n";

	public static final String USAGE;

	static {
		String temp = "" +
			"-- DELETE --" + NEWLINE +
			"wmod  -d  -s STRING          [-ic]  [-i STRING]  -f FILE_PATHS" + NEWLINE +
			"wmod  -d  -r START OFFSET    [-ic]  [-i STRING]  -f FILE_PATHS" + NEWLINE +
			"wmod  -d  -bi|-be BEGIN END  [-ic]  [-i STRING]  -f FILE_PATHS" + NEWLINE +
			"wmod  -d  -p POSITIONS       [-ic]  [-i STRING]  -f FILE_PATHS" + NEWLINE + NEWLINE +
			"-- CHANGE CASE --" + NEWLINE +
			"wmod  -l|-u  -s STRING          [-ic]  -f FILE_PATHS" + NEWLINE +
			"wmod  -l|-u  -r START OFFSET    [-ic]  -f FILE_PATHS" + NEWLINE +
			"wmod  -l|-u  -bi|-be BEGIN END  [-ic]  -f FILE_PATHS" + NEWLINE +
			"wmod  -l|-u  -p POSITIONS       [-ic]  -f FILE_PATHS" + NEWLINE + NEWLINE +
			"-- INSERT --" + NEWLINE +
			"wmod  -i STRING  -p POSITIONS  -f FILE_PATHS" + NEWLINE + NEWLINE +
			"-- ENUMERATE --" + NEWLINE +
			"wmod  -enum  -start START_NUMBER  [-step STEP]  [-digit MIN_DIGITS]  -f FILE_PATHS" + NEWLINE + NEWLINE +
			"-- NORMALIZE --" + NEWLINE +
			"wmod  -norm  -first|-all  [-o NAME_START_OFFSET] -f FILE_PATHS" + NEWLINE + NEWLINE +
			"-- NAMES FROM FILE --" + NEWLINE +
			"wmod  -nff SOURCE_FILE  [-o NAME_START_OFFSET]  -f FILE_PATHS" + NEWLINE + NEWLINE +
			"-- HELP --" + NEWLINE +
			"wmod  [-h]";

		List<String> usageLines = JkStrings.splitFieldsList(temp, "\n");
		int maxLineLen = usageLines.stream().mapToInt(String::length).max().getAsInt();

		List<String> manLines = new ArrayList<>();
		manLines.add("ALIASES:");
		manLines.add("");
		manLines.add("-d, --delete");
		manLines.add("-l, --lower");
		manLines.add("-u, --upper");
		manLines.add("-s, --string");
		manLines.add("-r, --range");
		manLines.add("-bi, --betweenInclude");
		manLines.add("-be, --betweenExclude");
		manLines.add("-p, --pos");
		manLines.add("-i, --ins");
		manLines.add("-ic, --ignoreCase");
		manLines.add("-f, --files");
		manLines.add("-enum, --enumerate");
		manLines.add("-norm, --normalize");
		manLines.add("-nff, --namesFromFile");
		manLines.add("-o, --offset");
		manLines.add("-h, --help");

		int maxOptLen = 6; // including '-' and ','
		String aliasFormat = "%-" + (maxOptLen+2) + "s%s";
		manLines = manLines.stream().map(str -> {
			if(str.contains(" ")) {
				String[] split = str.split(" ");
				return String.format(aliasFormat, split[0], split[1]);
			} else {
				return str;
			}
		}).collect(Collectors.toList());

		StringBuilder sbUsage = new StringBuilder();
		sbUsage.append("USAGE:").append(NEWLINE).append(NEWLINE);
		while(!usageLines.isEmpty() || !manLines.isEmpty()) {
			String left = usageLines.isEmpty() ? "" : usageLines.remove(0);
			String right = manLines.isEmpty() ? "" : manLines.remove(0);
			int distance = 4;
			String pattern = "%-" + (maxLineLen+distance) + "s|%" + distance + "s%s%s";
			sbUsage.append(String.format(pattern, left, "", right, NEWLINE));
		}

		USAGE = sbUsage.toString();
	}

}
