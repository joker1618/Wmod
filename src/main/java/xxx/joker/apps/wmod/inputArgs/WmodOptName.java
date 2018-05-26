package xxx.joker.apps.wmod.inputArgs;

import xxx.joker.libs.argsparser.design.annotation.OptName;
import xxx.joker.libs.argsparser.design.classType.OptionName;

/**
 * Created by f.barbano on 26/08/2017.
 */
public enum WmodOptName implements OptionName {

	@OptName
	DELETE("-d"),
	@OptName
	LOWER("-l"),
	@OptName
	UPPER("-u"),

	@OptName
	STRING("-s"),
	@OptName
	RANGE("-r"),
	@OptName
	BETWEEN_INCLUDE("-bi"),
	@OptName
	BETWEEN_EXCLUDE("-be"),
	@OptName
	POSITIONS("-p"),
	@OptName
	IGNORE_CASE("-ic"),
	@OptName
	INSERT("-i"),
	@OptName
	ENUMERATE("-enum"),
	@OptName
	START("-start"),
	@OptName
	STEP("-step"),
	@OptName
	DIGIT("-digit"),
	@OptName
	NORMALIZE("-norm"),
	@OptName
	FIRST("-first"),
	@OptName
	ALL("-all"),
	@OptName
	NAMES_FROM_FILE("-nff"),
	@OptName
	OFFSET("-o"),
	@OptName
	FILE_PATHS("-f"),
	@OptName
	HELP("-h"),

	;

	private String optionName;

	WmodOptName(String optionName) {
		this.optionName = optionName;
	}

	@Override
	public String optName() {
		return optionName;
	}
}
