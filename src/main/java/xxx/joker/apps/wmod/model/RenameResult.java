package xxx.joker.apps.wmod.model;

import xxx.joker.libs.core.utils.JkStreams;

import java.util.List;

/**
 * Created by f.barbano on 16/12/2017.
 */
public class RenameResult {

	private List<RenamedPath> renamedPaths;

	public RenameResult(List<RenamedPath> renamedPaths) {
		this.renamedPaths = renamedPaths;
	}

	public List<RenamedPath> getRenamedPaths() {
		return renamedPaths;
	}

	public List<RenamedPath> getRenamedPaths(RenameStatus status) {
		return JkStreams.filter(renamedPaths, ro -> ro.getStatus() == status);
	}

	public boolean canBeCommitted() {
		return getRenamedPaths().size() == getRenamedPaths(RenameStatus.NO_ERROR).size();
	}

	public boolean isAnyChanged() {
		return !JkStreams.filter(renamedPaths, RenamedPath::isChanged).isEmpty();
	}
}
