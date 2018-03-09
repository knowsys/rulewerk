package org.semanticweb.vlog4j.core.reasoner;

public class EDBPredConfig {
	private final int id;
	private final String predName;
	private final EDBSourceType sourceType;
	private final String sourceDirName;
	private final String sourceFileName;

	public EDBPredConfig(final int id, final String predName, final EDBSourceType sourceType, final String sourceDirName, final String sourceFileName) {
		this.id = id;
		this.predName = predName;
		this.sourceType = sourceType;
		this.sourceDirName = sourceDirName;
		this.sourceFileName = sourceFileName;
	}

	public String toVLogEDBConfigFileFormat() {
		return "EDB" + this.id + "_predname = " + this.predName + "\n" + "EDB" + this.id + "_type = " + this.sourceType + "\n" + "EDB" + this.id + "_param0 = "
				+ this.sourceDirName + "\n" + "EDB" + this.id + "_param1 = " + this.sourceFileName + "\n\n";
	}
}
