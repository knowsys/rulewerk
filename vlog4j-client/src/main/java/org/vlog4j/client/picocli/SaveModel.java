package org.vlog4j.client.picocli;

/*-
 * #%L
 * VLog4j Client
 * %%
 * Copyright (C) 2018 - 2019 VLog4j Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.File;

import picocli.CommandLine.Option;

/**
 * Helper class to save the resulting model of the materialization process.
 * 
 * @author Larry Gonzalez
 *
 */
public class SaveModel {

	/**
	 * If true, Vlog4jClient will save the model in {@code --output-model-directory}
	 *
	 * @default false
	 */
	@Option(names = "--save-model", description = "Boolean. If true, Vlog4jClient will save the model into --output-model-directory. False by default.")
	private boolean saveModel = false;

	/**
	 * Directory to store the model. Used only if {@code --store-model} is true.
	 *
	 * @default "model"
	 */
	@Option(names = "--output-model-directory", description = "Directory to store the model. Used only if --store-model is true. \"model\" by default.")
	private String outputModelDirectory = "model";

	public boolean isConfigValid() {
		return !saveModel || (outputModelDirectory != null && !outputModelDirectory.isEmpty());
	}

	/**
	 * Print configuration error and exit the program
	 */
	public void printErrorAndExit() {
		System.err.println("Configuration error: --save-model requires a non-null --output-model-directory.");
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

	public boolean isSaveModel() {
		return saveModel;
	}

	public void setSaveModel(boolean saveModel) {
		this.saveModel = saveModel;
	}

	public String getOutputModelDirectory() {
		return outputModelDirectory;
	}

	public void setOutputModelDirectory(String outputModelDirectory) {
		this.outputModelDirectory = outputModelDirectory;
	}

}
