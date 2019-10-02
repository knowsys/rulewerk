package org.vlog4j.client.picocli;

import java.io.File;

import picocli.CommandLine.Option;

public class SaveModel {

	/**
	 * If true, Vlog4jClient will save the model in {@code --output-model-directory}
	 *
	 * @default false
	 */
	@Option(names = "--save-model", description = "Boolean. If true, Vlog4jClient will save the model into --output-model-directory. False by default.")
	public boolean saveModel = false;

	/**
	 * Directory to store the model. Used only if {@code --store-model} is true.
	 *
	 * @default "model"
	 */
	@Option(names = "--output-model-directory", description = "Directory to store the model. Used only if --store-model is true. \"model\" by default.")
	public String outputModelDirectory = "model";

	public boolean isConfigOk() {
		if (saveModel & (outputModelDirectory == null || outputModelDirectory.isEmpty())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Print configuration error and exit the program
	 */
	public void printErrorAndExit() {
		System.out.println("Configuration error: --save-model requires a non-null --output-model-directory.");
		System.exit(1);
	}

	/**
	 * Create directory to store the model
	 */
	public void prepare() {
		if (saveModel) {
			new File(outputModelDirectory).mkdirs();
		}
	}

	public void printConfiguration() {
		if (saveModel) {
			System.out.println("  --save-model: " + saveModel);
			System.out.println("  --output-model-directory: " + outputModelDirectory);
		}
	}

}
