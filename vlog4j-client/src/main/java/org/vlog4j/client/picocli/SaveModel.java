package org.vlog4j.client.picocli;

import java.io.File;

import picocli.CommandLine.Option;

class SaveModel {

	@Option(names = "--save-model", description = "Boolean. If true, Vlog4jClient will save the model into --output-model-directory. False by default.")
	public boolean saveModel = false;

	@Option(names = "--output-model-directory", description = "Directory to store the model. Used only if --store-model is set true. \"model\" by default.")
	public String outputModelDirectory = "model";

	public boolean check() {
		if (saveModel & outputModelDirectory == null) {
			System.out.println("--save-model requires an --output-model-directory.");
			return false;
		} else {
			return true;
		}
	}

	public void prepare() {
		if (saveModel) {
			new File(outputModelDirectory).mkdirs();
		}
	}

	public void print() {
		if (saveModel) {
			System.out.println("  --save-model: " + saveModel);
			System.out.println("  --output-model-directory: " + outputModelDirectory);
		}
	}

}
