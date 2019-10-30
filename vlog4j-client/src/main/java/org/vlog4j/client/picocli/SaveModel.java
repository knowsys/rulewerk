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

	static final String configurationErrorMessage = "Configuration Error: If @code{--save-model} is true, then a non empty @code{--output-model-directory} is required.\nExiting the program.";
	static final String wrongDirectoryErrorMessage = "Configuration Error: wrong @code{--output-model-directory}. Please check the path.\nExiting the program.";

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

	public SaveModel() {
	}

	public SaveModel(boolean saveModel, String outputDir) {
		this.saveModel = saveModel;
		this.outputModelDirectory = outputDir;
	}

	/**
	 * Check correct configuration of the class. If @code{--save-model} is true,
	 * then a non-empty @code{--output-model-directory} is required.
	 * 
	 * @return @code{true} if configuration is valid.
	 */
	protected boolean isConfigurationValid() {
		return !saveModel || (outputModelDirectory != null && !outputModelDirectory.isEmpty());
	}

	/**
	 * Check that the path to store the model is either non-existing or a directory.
	 * 
	 * @return @code{true} if conditions are satisfied.
	 */
	protected boolean isDirectoryValid() {
		File file = new File(outputModelDirectory);
		return !file.exists() || file.isDirectory();
	}

	/**
	 * Create directory to store the model
	 */
	public void mkdir() {
		if (saveModel) {
			File file = new File(outputModelDirectory);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
	}

	public void printConfiguration() {
		System.out.println("  --save-model: " + saveModel);
		System.out.println("  --output-model-directory: " + outputModelDirectory);
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
