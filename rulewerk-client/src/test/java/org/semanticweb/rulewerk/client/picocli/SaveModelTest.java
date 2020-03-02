package org.semanticweb.rulewerk.client.picocli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;

/*-
 * #%L
 * Rulewerk Client
 * %%
 * Copyright (C) 2018 - 2020 Rulewerk Developers
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

public class SaveModelTest {

	private final static SaveModel saveTrueDefaultDir = new SaveModel();
	private final static SaveModel saveTrueEmptyDir = new SaveModel(true, "");
	private final static SaveModel saveTrueNullDir = new SaveModel(true, null);
	private final static SaveModel saveFalseDefaultDir = new SaveModel();
	private final static SaveModel saveFalseEmptyDir = new SaveModel(false, "");
	private final static SaveModel saveFalseNullDir = new SaveModel(false, null);

	static {
		saveTrueDefaultDir.setSaveModel(true);
		saveFalseDefaultDir.setSaveModel(false);
	}

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
	public void isSaveModel_saveTrueDefaultDir() {
		assertTrue(saveTrueDefaultDir.isSaveModel());
	}

	@Test
	public void getOutputModelDirectory_saveTrueDefaultDir() {
		assertEquals(SaveModel.DEFAULT_OUTPUT_DIR_NAME, saveTrueDefaultDir.getOutputModelDirectory());
	}

	@Test
	public void isSaveModel_saveTrueEmptyDir() {
		assertTrue(saveTrueEmptyDir.isSaveModel());
	}

	@Test
	public void getOutputModelDirectory_saveTrueEmptyDir() {
		assertEquals(StringUtils.EMPTY, saveTrueEmptyDir.getOutputModelDirectory());
	}

	@Test
	public void isSaveModel_saveTrueNullDir() {
		assertTrue(saveTrueNullDir.isSaveModel());
	}

	@Test
	public void getOutputModelDirectory_saveTrueNullDir() {
		assertNull(saveTrueNullDir.getOutputModelDirectory());
	}

	@Test
	public void isSaveModel_saveFalseDefaultDir() {
		assertFalse(saveFalseDefaultDir.isSaveModel());
	}

	@Test
	public void getOutputModelDirectory_saveFalseDefaultDir() {
		assertEquals(SaveModel.DEFAULT_OUTPUT_DIR_NAME, saveFalseDefaultDir.getOutputModelDirectory());
	}

	@Test
	public void isSaveModel_saveFalseEmptyDir() {
		assertFalse(saveFalseEmptyDir.isSaveModel());
	}

	@Test
	public void getOutputModelDirectory_saveFalseEmptyDir() {
		assertEquals(StringUtils.EMPTY, saveFalseEmptyDir.getOutputModelDirectory());
	}

	@Test
	public void isSaveModel_saveFalseNullDir() {
		assertFalse(saveFalseNullDir.isSaveModel());
	}

	@Test
	public void getOutputModelDirectory_saveFalseNullDir() {
		assertNull(saveFalseNullDir.getOutputModelDirectory());
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

}
