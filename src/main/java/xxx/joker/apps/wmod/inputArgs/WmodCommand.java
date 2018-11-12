package xxx.joker.apps.wmod.inputArgs;

import xxx.joker.libs.argsparser.design.annotation.Cmd;
import xxx.joker.libs.argsparser.design.classType.InputCommand;
import xxx.joker.libs.argsparser.functions.ArgsCheck;
import xxx.joker.libs.argsparser.functions.ArgsParse;
import xxx.joker.libs.argsparser.model.CmdOption;
import xxx.joker.libs.argsparser.model.CmdParam;
import xxx.joker.libs.core.utils.JkConverter;

import java.util.List;

import static xxx.joker.apps.wmod.inputArgs.WmodOptName.*;

/**
 * Created by f.barbano on 26/08/2017.
 */
public enum WmodCommand implements InputCommand {

	@Cmd
	CMD_DELETE(
		new CmdParam(new CmdOption(DELETE)),
		new CmdParam(
			new CmdOption(STRING),
			new CmdOption(RANGE, ArgsCheck.rangeStartOffset(0)),
			new CmdOption(BETWEEN_INCLUDE, ArgsCheck.numValuesExpected(2)),
			new CmdOption(BETWEEN_EXCLUDE, ArgsCheck.numValuesExpected(2)),
			new CmdOption(POSITIONS, null, null, ArgsParse.orderDistinctInt(true), ArgsCheck.intGE(0))
		),
		new CmdParam(false, new CmdOption(IGNORE_CASE)),
		new CmdParam(false, new CmdOption(INSERT)),
		new CmdParam(false, new CmdOption(DEBUG)),
		new CmdParam(new CmdOption(FILE_PATHS, ArgsParse.windowsPathFormat(), ArgsParse.distinctPath(true), ArgsCheck.pathExists(), ArgsCheck.pathsParentEquals()))
	),

	@Cmd
	CMD_CHANGE_CASE(
		new CmdParam(new CmdOption(LOWER), new CmdOption(UPPER)),
		new CmdParam(
			new CmdOption(STRING),
			new CmdOption(RANGE, ArgsCheck.rangeStartOffset(0)),
			new CmdOption(BETWEEN_INCLUDE, ArgsCheck.numValuesExpected(2)),
			new CmdOption(BETWEEN_EXCLUDE, ArgsCheck.numValuesExpected(2)),
			new CmdOption(POSITIONS, null, null, ArgsParse.orderDistinctInt(true), ArgsCheck.intGE(0))
		),
		new CmdParam(false, new CmdOption(IGNORE_CASE)),
		new CmdParam(false, new CmdOption(DEBUG)),
		new CmdParam(new CmdOption(FILE_PATHS, ArgsParse.windowsPathFormat(), ArgsParse.distinctPath(true), ArgsCheck.pathExists(), ArgsCheck.pathsParentEquals()))
	),

	@Cmd
	CMD_INSERT(
		new CmdParam(new CmdOption(INSERT)),
		new CmdParam(new CmdOption(POSITIONS, null, null, ArgsParse.orderDistinctInt(true), ArgsCheck.intGE(0))),
		new CmdParam(false, new CmdOption(IGNORE_CASE)),
		new CmdParam(false, new CmdOption(DEBUG)),
		new CmdParam(new CmdOption(FILE_PATHS, ArgsParse.windowsPathFormat(), ArgsParse.distinctPath(true), ArgsCheck.pathExists(), ArgsCheck.pathsParentEquals()))
	),

	@Cmd
	CMD_ENUMERATE(
		new CmdParam(new CmdOption(ENUMERATE)),
		new CmdParam(new CmdOption(START)),
		new CmdParam(false, new CmdOption(STEP, ArgsCheck.intNE(0))),
		new CmdParam(false, new CmdOption(DIGIT, ArgsCheck.intGE(1))),
		new CmdParam(false, new CmdOption(DEBUG)),
		new CmdParam(new CmdOption(FILE_PATHS, ArgsParse.windowsPathFormat(), ArgsParse.distinctPath(true), ArgsCheck.pathExists(), ArgsCheck.pathsParentEquals()))
	),

	@Cmd
	CMD_NORMALIZE(
		new CmdParam(new CmdOption(NORMALIZE)),
		new CmdParam(new CmdOption(FIRST), new CmdOption(ALL)),
		new CmdParam(false, new CmdOption(OFFSET, ArgsCheck.intGE(0))),
		new CmdParam(false, new CmdOption(DEBUG)),
		new CmdParam(new CmdOption(FILE_PATHS, ArgsParse.windowsPathFormat(), ArgsParse.distinctPath(true), ArgsCheck.pathExists(), ArgsCheck.pathsParentEquals()))
	),

	@Cmd
	CMD_NAMES_FROM_FILE(
		new CmdParam(new CmdOption(NAMES_FROM_FILE, ArgsParse.windowsPathFormat(), ArgsCheck.pathIsFile())),
		new CmdParam(false, new CmdOption(OFFSET, ArgsCheck.intGE(0))),
		new CmdParam(false, new CmdOption(DEBUG)),
		new CmdParam(new CmdOption(FILE_PATHS, ArgsParse.windowsPathFormat(), ArgsParse.distinctPath(true), ArgsCheck.pathExists(), ArgsCheck.pathsParentEquals()))
	),

	@Cmd
	CMD_HELP(
		new CmdParam(false, new CmdOption(HELP)),
		new CmdParam(false, new CmdOption(DEBUG))
	)

	;


	private List<CmdParam> paramList;

	WmodCommand(CmdParam... params) {
		this.paramList = JkConverter.toArrayList(params);
	}

	@Override
	public List<CmdParam> paramList() {
		return paramList;
	}

}
