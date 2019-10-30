package org.vlog4j.client.picocli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Rule;

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

import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.vlog4j.client.picocli.SaveModel;

public class SaveModelTest {
	private final String outputConfigurationBase = "  --save-model: %b\n  --output-model-directory: %s\n";
	private final String defaultDir = "model";

	private final SaveModel saveTrueDefaultDir = new SaveModel(true, defaultDir);
	private final SaveModel saveTrueEmptyDir = new SaveModel(true, "");
	private final SaveModel saveTrueNullDir = new SaveModel(true, null);
	private final SaveModel saveFalseDefaultDir = new SaveModel(false, defaultDir);
	private final SaveModel saveFalseEmptyDir = new SaveModel(false, "");
	private final SaveModel saveFalseNullDir = new SaveModel(false, null);

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void isConfigurationValid_saveTrueDefaultDir_valid() {
		assertTrue(saveTrueDefaultDir.isConfigurationValid());
	}

	@Test
	public void isConfigurationValid_saveTrueEmptyDir_nonValid() {
		assertFalse(saveTrueEmptyDir.isConfigurationValid());
	}

	@Test
	public void isConfigurationValid_saveTrueNullDir_nonValid() {
		assertFalse(saveTrueNullDir.isConfigurationValid());
	}

	@Test
	public void isConfigurationValid_saveFalseDefaultDir_valid() {
		assertTrue(saveFalseDefaultDir.isConfigurationValid());
	}

	@Test
	public void isConfigurationValid_saveFalseEmptyDir_valid() {
		assertTrue(saveFalseEmptyDir.isConfigurationValid());
	}

	@Test
	public void isConfigurationValid_saveFalseNullDir_valid() {
		assertTrue(saveFalseNullDir.isConfigurationValid());
	}

	@Test
	public void isDirectoryValid_nonExistingDirectory_valid() throws IOException {
		File nonExistingDirectory = tempFolder.newFolder("folderPath");
		nonExistingDirectory.delete();
		SaveModel temp = new SaveModel(true, nonExistingDirectory.getAbsolutePath());
		assertTrue(temp.isDirectoryValid());
	}

	@Test
	public void isDirectoryValid_existingDirectory_valid() throws IOException {
		File existingDirectory = tempFolder.newFolder("folderPath");
		existingDirectory.mkdir();
		SaveModel temp = new SaveModel(true, existingDirectory.getAbsolutePath());
		assertTrue(temp.isDirectoryValid());
	}

	@Test
	public void isDirectoryValid_existingFile_nonValid() throws IOException {
		File existingFile = tempFolder.newFile("filePath");
		existingFile.createNewFile();
		SaveModel temp = new SaveModel(true, existingFile.getAbsolutePath());
		assertFalse(temp.isDirectoryValid());
	}

	@Test
	public void mkdir_saveTrueNonExistingDirectory() throws IOException {
		File subDirectory = tempFolder.newFolder("folderPath", "subFolder");
		subDirectory.delete();
		SaveModel temp = new SaveModel(true, subDirectory.getAbsolutePath());
		temp.mkdir();
		assertTrue(subDirectory.isDirectory());
	}

	@Test
	public void mkdir_saveTrueExistingDirectory() throws IOException {
		File subDirectory = tempFolder.newFolder("folderPath", "subFolder");
		subDirectory.mkdirs();
		SaveModel temp = new SaveModel(true, subDirectory.getAbsolutePath());
		temp.mkdir();
		assertTrue(subDirectory.isDirectory());
	}

	@Test
	public void mkdir_saveFalse() throws IOException {
		File folder = tempFolder.newFile("validNonExistingFolder");
		folder.delete();
		SaveModel temp = new SaveModel(false, folder.getAbsolutePath());
		temp.mkdir();
		assertFalse(folder.exists());
	}

	@Test
	public void printConfiguration_saveTrueDefaultDir() {
		assertEquals(String.format(outputConfigurationBase, true, defaultDir),
				captureOutputPrintConfiguration(saveTrueDefaultDir));
	}

	@Test
	public void printConfiguration_saveTrueEmptyDir() {
		assertEquals(String.format(outputConfigurationBase, true, ""),
				captureOutputPrintConfiguration(saveTrueEmptyDir));
	}

	@Test
	public void printConfiguration_saveTrueNullDir() {
		assertEquals(String.format(outputConfigurationBase, true, null),
				captureOutputPrintConfiguration(saveTrueNullDir));
	}

	@Test
	public void printConfiguration_saveFalseDefaultDir() {
		assertEquals(String.format(outputConfigurationBase, false, defaultDir),
				captureOutputPrintConfiguration(saveFalseDefaultDir));
	}

	@Test
	public void printConfiguration_saveFalseEmptyDir() {
		assertEquals(String.format(outputConfigurationBase, false, ""),
				captureOutputPrintConfiguration(saveFalseEmptyDir));
	}

	@Test
	public void printConfiguration_saveFalseNullDir() {
		assertEquals(String.format(outputConfigurationBase, false, null),
				captureOutputPrintConfiguration(saveFalseNullDir));
	}

	@Test
	public void setSaveModel_and_isSaveModel() {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(true);
		assertTrue(sm.isSaveModel());
		sm.setSaveModel(false);
		assertFalse(sm.isSaveModel());
	}

	@Test
	public void setOutputModelDirectory_and_getOutputModelDirectory() {
		SaveModel sm = new SaveModel();
		sm.setOutputModelDirectory("");
		assertEquals("", sm.getOutputModelDirectory());
		sm.setOutputModelDirectory(null);
		assertNull(sm.getOutputModelDirectory());
	}

	private String captureOutputPrintConfiguration(SaveModel sm) {
		// Output Variables
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(result);
		// Save default System.out
		PrintStream systemOut = System.out;
		// Change System.out
		System.setOut(ps);
		// Do something
		sm.printConfiguration();
		// Restore previous state
		System.out.flush();
		System.setOut(systemOut);
		// return result
		return result.toString();
	}
}
