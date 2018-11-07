package xxx.joker.apps.wmod.model;

import xxx.joker.libs.core.utils.JkFiles;

import java.nio.file.Path;

/**
 * Created by f.barbano on 02/07/2017.
 */
public class RenamedPath {

	private Path actualPath;
	private Path newPath;
	private RenameStatus status;

	public RenamedPath(Path actualPath, Path newPath) {
		this.actualPath = actualPath;
		this.newPath = newPath;
	}

	public boolean isChanged() {
		return !actualPath.equals(newPath) || !actualPath.toString().equals(newPath.toString());
	}

	public Path getActualPath() {
		return actualPath;
	}

	public void setActualPath(Path actualPath) {
		this.actualPath = actualPath;
	}

	public Path getNewPath() {
		return newPath;
	}

	public void setNewPath(Path newPath) {
		this.newPath = newPath;
	}

	public RenameStatus getStatus() {
		return status;
	}

	public void setStatus(RenameStatus error) {
		this.status = error;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append("Actual path: ").append(actualPath).append(", ");
		sb.append("New path: ").append(newPath).append(", ");
		sb.append(isChanged() ? "CHANGED" : "NOT CHANGED").append(", ");
		sb.append(status.name());
		sb.append("]");
		return sb.toString();
	}

}
