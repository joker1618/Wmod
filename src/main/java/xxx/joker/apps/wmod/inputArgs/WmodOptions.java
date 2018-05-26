package xxx.joker.apps.wmod.inputArgs;

import xxx.joker.libs.argsparser.design.annotation.Opt;
import xxx.joker.libs.argsparser.design.classType.InputOption;

import java.nio.file.Path;

/**
 * Created by f.barbano on 26/08/2017.
 */
public class WmodOptions extends InputOption<WmodCommand> {

	@Opt(name = "-d", aliases = {"--delete"})
	private Boolean delete = false;
	@Opt(name = "-l", aliases = {"--lower"})
	private Boolean lower = false;
	@Opt(name = "-u", aliases = {"--upper"})
	private Boolean upper = false;
	@Opt(name = "-s", aliases = {"--string"})
	private String string;
	@Opt(name = "-r", aliases = {"--range"})
	private Integer[] range;
	@Opt(name = "-bi", aliases = {"--betweenInclude"})
	private String[] betweenInclude;
	@Opt(name = "-be", aliases = {"--betweenExclude"})
	private String[] betweenExclude;
	@Opt(name = "-p", aliases = {"--pos"})
	private Integer[] positions;
	@Opt(name = "-ic", aliases = {"--ignoreCase"})
	private Boolean ignoreCase = false;
	@Opt(name = "-i", aliases = {"--ins"})
	private String insert = "";
	@Opt(name = "-enum", aliases = {"--enumerate"})
	private Boolean enumerate = false;
	@Opt(name = "-start")
	private Integer start;
	@Opt(name = "-step")
	private Integer step = 1;
	@Opt(name = "-digit")
	private Integer digit = 1;
	@Opt(name = "-norm", aliases = {"--normalize"})
	private Boolean normalize = false;
	@Opt(name = "-first")
	private Boolean first = false;
	@Opt(name = "-all")
	private Boolean all = false;
	@Opt(name = "-nff", aliases = {"--namesFromFile"})
	private Path namesFile;
	@Opt(name = "-o", aliases = {"--offset"})
	private Integer offset = 0;
	@Opt(name = "-f", aliases = {"--files"})
	private Path[] filePaths;
	@Opt(name = "-h", aliases = {"--help"})
	private Boolean help = false;

	public Boolean isDelete() {
		return delete;
	}

	public Boolean isLower() {
		return lower;
	}

	public Boolean isUpper() {
		return upper;
	}

	public String getString() {
		return string;
	}

	public Integer[] getRange() {
		return range;
	}

	public String[] getBetweenInclude() {
		return betweenInclude;
	}

	public String[] getBetweenExclude() {
		return betweenExclude;
	}

	public Integer[] getPositions() {
		return positions;
	}

	public Boolean isIgnoreCase() {
		return ignoreCase;
	}

	public String getInsert() {
		return insert;
	}

	public Boolean isEnumerate() {
		return enumerate;
	}

	public Integer getStart() {
		return start;
	}

	public Integer getStep() {
		return step;
	}

	public Integer getDigit() {
		return digit;
	}

	public Boolean isNormalize() {
		return normalize;
	}

	public Boolean isFirst() {
		return first;
	}

	public Boolean isAll() {
		return all;
	}

	public Path getNamesFile() {
		return namesFile;
	}

	public Integer getOffset() {
		return offset;
	}

	public Path[] getFilePaths() {
		return filePaths;
	}

	public Boolean isHelp() {
		return help;
	}
}
