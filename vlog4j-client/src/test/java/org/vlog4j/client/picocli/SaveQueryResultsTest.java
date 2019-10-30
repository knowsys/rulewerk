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

public class SaveQueryResultsTest {
	private final String outputConfigurationBase = "  --save-query-results: %b\n  --output-query-result-directory: %s\n";
	private final String defaultDir = "query-results";

	private final SaveQueryResults saveTrueDefaultDir = new SaveQueryResults(true, defaultDir);
	private final SaveQueryResults saveTrueEmptyDir = new SaveQueryResults(true, "");
	private final SaveQueryResults saveTrueNullDir = new SaveQueryResults(true, null);
	private final SaveQueryResults saveFalseDefaultDir = new SaveQueryResults(false, defaultDir);
	private final SaveQueryResults saveFalseEmptyDir = new SaveQueryResults(false, "");
	private final SaveQueryResults saveFalseNullDir = new SaveQueryResults(false, null);

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void isConfigurationValid_saveTrueDefaultDir_valid() {
		assertTrue(saveTrueDefaultDir.isConfigurationValid());
	}

	@Test
	public void isConfigurationValid_saveTrueEmptyDir_notValid() {
		assertFalse(saveTrueEmptyDir.isConfigurationValid());
	}

	@Test
	public void isConfigurationValid_saveTrueNullDir_notValid() {
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
		SaveQueryResults temp = new SaveQueryResults(true, nonExistingDirectory.getAbsolutePath());
		assertTrue(temp.isDirectoryValid());
	}

	@Test
	public void isDirectoryValid_existingDirectory_valid() throws IOException {
		File existingDirectory = tempFolder.newFolder("folderPath");
		existingDirectory.mkdir();
		SaveQueryResults temp = new SaveQueryResults(true, existingDirectory.getAbsolutePath());
		assertTrue(temp.isDirectoryValid());
	}

	@Test
	public void isDirectoryValid_existingFile_nonValid() throws IOException {
		File existingFile = tempFolder.newFile("filePath");
		existingFile.createNewFile();
		SaveQueryResults temp = new SaveQueryResults(true, existingFile.getAbsolutePath());
		assertFalse(temp.isDirectoryValid());
	}

	@Test
	public void mkdir_saveTrueNonExistingDirectory() throws IOException {
		File subDirectory = tempFolder.newFolder("folderPath", "subFolder");
		subDirectory.delete();
		SaveQueryResults temp = new SaveQueryResults(true, subDirectory.getAbsolutePath());
		temp.mkdir();
		assertTrue(subDirectory.isDirectory());
	}

	@Test
	public void mkdir_saveTrueExistingDirectory() throws IOException {
		File subDirectory = tempFolder.newFolder("folderPath", "subFolder");
		subDirectory.mkdirs();
		SaveQueryResults temp = new SaveQueryResults(true, subDirectory.getAbsolutePath());
		temp.mkdir();
		assertTrue(subDirectory.isDirectory());
	}

	@Test
	public void mkdir_saveFalse() throws IOException {
		File folder = tempFolder.newFile("validNonExistingFolder");
		folder.delete();
		SaveQueryResults temp = new SaveQueryResults(false, folder.getAbsolutePath());
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
	public void setSaveResults_and_isSaveResults() {
		SaveQueryResults srq = new SaveQueryResults();
		srq.setSaveResults(true);
		assertTrue(srq.isSaveResults());
		srq.setSaveResults(false);
		assertFalse(srq.isSaveResults());
	}

	@Test
	public void setOutputQueryResultDirectory_and_getOutputQueryResultsDirectory() {
		SaveQueryResults srq = new SaveQueryResults();
		srq.setOutputQueryResultDirectory("");
		assertEquals("", srq.getOutputQueryResultDirectory());
		srq.setOutputQueryResultDirectory(null);
		assertNull(srq.getOutputQueryResultDirectory());
	}

	private String captureOutputPrintConfiguration(SaveQueryResults srq) {
		// Output Variables
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(result);
		// Save default System.out
		PrintStream systemOut = System.out;
		// Change System.out
		System.setOut(ps);
		// Do something
		srq.printConfiguration();
		// Restore previous state
		System.out.flush();
		System.setOut(systemOut);
		// return result
		return result.toString();
	}
}
