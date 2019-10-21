package org.vlog4j.client.picocli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import javax.naming.ConfigurationException;

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
import org.vlog4j.client.picocli.SaveModel;

public class SaveModelTest {
	private final String outputConfigurationBase = "  --save-model: %b\n  --output-model-directory: %s\n";
	private final String dir = "directory";
	private final String tempDir = "tempDir";
	private final String defaultDir = "model";

	@Test
	public void validate_saveModelTrueDefaultDir_valid() throws ConfigurationException {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(true);
		sm.setOutputModelDirectory(defaultDir);
		sm.validate();
	}

	@Test
	public void validate_saveModelTrueValidDir_valid() throws ConfigurationException {
		// default configuration
		SaveModel sm = new SaveModel();
		sm.setSaveModel(false);
		sm.setOutputModelDirectory(dir);
		sm.validate();
	}

	@Test(expected = ConfigurationException.class)
	public void validate_saveModelTrueEmptyDir_notValid() throws ConfigurationException {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(true);
		sm.setOutputModelDirectory("");
		sm.validate();
	}

	@Test(expected = ConfigurationException.class)
	public void validate_saveModelTrueNullDir_notValid() throws ConfigurationException {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(true);
		sm.setOutputModelDirectory(null);
		sm.validate();
	}

	@Test
	public void validate_saveModelFalseDefaultDir_valid() throws ConfigurationException {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(false);
		sm.setOutputModelDirectory(defaultDir);
		sm.validate();
	}

	@Test
	public void validate_saveModelFalseValidDir_valid() throws ConfigurationException {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(false);
		sm.setOutputModelDirectory(dir);
		sm.validate();
	}

	@Test
	public void validate_saveModelFalseEmptyDir_valid() throws ConfigurationException {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(false);
		sm.setOutputModelDirectory("");
		sm.validate();
	}

	@Test
	public void validate_saveModelFalseNullDir_valid() throws ConfigurationException {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(false);
		sm.setOutputModelDirectory(null);
		sm.validate();
	}

	@Test
	public void printConfiguration_saveModelTrueDefaultDir_valid() {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(true);
		sm.setOutputModelDirectory(defaultDir);
		assertEquals(String.format(outputConfigurationBase, true, defaultDir), captureOutputPrintConfiguration(sm));
	}

	@Test
	public void printConfiguration_saveModelTrueValidDir_valid() {
		// default configuration
		SaveModel sm = new SaveModel();
		sm.setSaveModel(false);
		sm.setOutputModelDirectory(dir);
		assertEquals(String.format(outputConfigurationBase, false, dir), captureOutputPrintConfiguration(sm));
	}

	@Test
	public void printConfiguration_saveModelTrueEmptyDir_notValid() {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(true);
		sm.setOutputModelDirectory("");
		assertEquals(String.format(outputConfigurationBase, true, ""), captureOutputPrintConfiguration(sm));
	}

	@Test
	public void printConfiguration_saveModelTrueNullDir_notValid() {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(true);
		sm.setOutputModelDirectory(null);
		assertEquals(String.format(outputConfigurationBase, true, null), captureOutputPrintConfiguration(sm));
	}

	@Test
	public void printConfiguration_saveModelFalseDefaultDir_valid() {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(false);
		sm.setOutputModelDirectory(defaultDir);
		assertEquals(String.format(outputConfigurationBase, false, defaultDir), captureOutputPrintConfiguration(sm));
	}

	@Test
	public void printConfiguration_saveModelFalseValidDir_valid() {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(false);
		sm.setOutputModelDirectory(dir);
		assertEquals(String.format(outputConfigurationBase, false, dir), captureOutputPrintConfiguration(sm));
	}

	@Test
	public void printConfiguration_saveModelFalseEmptyDir_valid() {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(false);
		sm.setOutputModelDirectory("");
		assertEquals(String.format(outputConfigurationBase, false, ""), captureOutputPrintConfiguration(sm));
	}

	@Test
	public void printConfiguration_saveModelFalseNullDir_valid() {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(false);
		sm.setOutputModelDirectory(null);
		assertEquals(String.format(outputConfigurationBase, false, null), captureOutputPrintConfiguration(sm));
	}

	@Test
	public void prepare_saveModelTrueValidDir() {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(true);
		sm.setOutputModelDirectory(tempDir);
		sm.prepare();
		File f = new File(tempDir);
		assert (f.exists() && f.isDirectory());
		f.delete();
	}

	@Test
	public void prepare_saveModelFalseValidDir() {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(false);
		sm.setOutputModelDirectory(dir);
		sm.prepare();
		File f = new File(tempDir);
		assertFalse(f.exists());
	}

	@Test
	public void setSaveModel_and_isSaveModel() {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(true);
		assertEquals(true, sm.isSaveModel());
		sm.setSaveModel(false);
		assertEquals(false, sm.isSaveModel());
	}

	@Test
	public void setOutputModelDirectory_and_getOutputModelDirectory() {
		SaveModel sm = new SaveModel();
		sm.setOutputModelDirectory("");
		assertEquals("", sm.getOutputModelDirectory());
		sm.setOutputModelDirectory(dir);
		assertEquals(dir, sm.getOutputModelDirectory());
		sm.setOutputModelDirectory(null);
		assertEquals(null, sm.getOutputModelDirectory());
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
