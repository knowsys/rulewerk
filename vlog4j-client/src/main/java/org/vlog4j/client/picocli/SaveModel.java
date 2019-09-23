package org.vlog4j.client.picocli;

import java.io.File;

import picocli.CommandLine.Option;

class SaveModel {

	@Option(names = "--save-model", description = "Boolean. If true, Vlog4jClient will save the model into --output-model-folder. False by default.")
	public boolean saveModel = false;

	@Option(names = "--output-model-folder", description = "Folder to store the model. Used only if --store-model is set true. \"model\" by default.")
	public String outputModelFolder = "model";

	public boolean check() {
		if (saveModel & outputModelFolder == null) {
			System.out.println("--save-model requires an --output-model-folder.");
			return false;
		} else {
			return true;
		}
	}

	public void prepare() {
		if (saveModel) {
			new File(outputModelFolder).mkdirs();
		}
	}

	public void print() {
		if (saveModel) {
			System.out.println("  --save-model: " + saveModel);
			System.out.println("  --output-model-folder: " + outputModelFolder);
		}
	}

}
