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

public class SaveQueryResultsTest {
	private final String outputConfigurationBase = "  --save-query-results: %b\n  --output-query-result-directory: %s\n";
	private final String dir = "directory";
	private final String tempDir = "tempDir";
	private final String defaultDir = "query-results";

	@Test
	public void validate_saveQueryResultsTrueDefaultDir_valid() throws ConfigurationException {
		SaveQueryResults srq = new SaveQueryResults();
		srq.setSaveResults(true);
		srq.setOutputQueryResultDirectory(defaultDir);
		srq.validate();
	}

	@Test
	public void validate_saveModelTrueValidDir_valid() throws ConfigurationException {
		// default configuration
		SaveQueryResults srq = new SaveQueryResults();
		srq.setSaveResults(false);
		srq.setOutputQueryResultDirectory(dir);
		srq.validate();
	}

	@Test(expected = ConfigurationException.class)
	public void validate_saveModelTrueEmptyDir_notValid() throws ConfigurationException {
		SaveQueryResults srq = new SaveQueryResults();
		srq.setSaveResults(true);
		srq.setOutputQueryResultDirectory("");
		srq.validate();
	}

	@Test(expected = ConfigurationException.class)
	public void validate_saveModelTrueNullDir_notValid() throws ConfigurationException {
		SaveQueryResults srq = new SaveQueryResults();
		srq.setSaveResults(true);
		srq.setOutputQueryResultDirectory(null);
		srq.validate();
	}

	@Test
	public void validate_saveModelFalseDefaultDir_valid() throws ConfigurationException {
		SaveQueryResults srq = new SaveQueryResults();
		srq.setSaveResults(false);
		srq.setOutputQueryResultDirectory(defaultDir);
		srq.validate();
	}

	@Test
	public void validate_saveModelFalseValidDir_valid() throws ConfigurationException {
		SaveQueryResults srq = new SaveQueryResults();
		srq.setSaveResults(false);
		srq.setOutputQueryResultDirectory(dir);
		srq.validate();
	}

	@Test
	public void validate_saveModelFalseEmptyDir_valid() throws ConfigurationException {
		SaveQueryResults srq = new SaveQueryResults();
		srq.setSaveResults(false);
		srq.setOutputQueryResultDirectory("");
		srq.validate();
	}

	@Test
	public void validate_saveModelFalseNullDir_valid() throws ConfigurationException {
		SaveQueryResults srq = new SaveQueryResults();
		srq.setSaveResults(false);
		srq.setOutputQueryResultDirectory(null);
		srq.validate();
	}

	@Test
	public void printConfiguration_saveModelTrueDefaultDir_valid() {
		SaveQueryResults srq = new SaveQueryResults();
		srq.setSaveResults(true);
		srq.setOutputQueryResultDirectory(defaultDir);
		assertEquals(String.format(outputConfigurationBase, true, defaultDir), captureOutputPrintConfiguration(srq));
	}

	@Test
	public void printConfiguration_saveModelTrueValidDir_valid() {
		// default configuration
		SaveQueryResults srq = new SaveQueryResults();
		srq.setSaveResults(false);
		srq.setOutputQueryResultDirectory(dir);
		assertEquals(String.format(outputConfigurationBase, false, dir), captureOutputPrintConfiguration(srq));
	}

	@Test
	public void printConfiguration_saveModelTrueEmptyDir_notValid() {
		SaveQueryResults srq = new SaveQueryResults();
		srq.setSaveResults(true);
		srq.setOutputQueryResultDirectory("");
		assertEquals(String.format(outputConfigurationBase, true, ""), captureOutputPrintConfiguration(srq));
	}

	@Test
	public void printConfiguration_saveModelTrueNullDir_notValid() {
		SaveQueryResults srq = new SaveQueryResults();
		srq.setSaveResults(true);
		srq.setOutputQueryResultDirectory(null);
		assertEquals(String.format(outputConfigurationBase, true, null), captureOutputPrintConfiguration(srq));
	}

	@Test
	public void printConfiguration_saveModelFalseDefaultDir_valid() {
		SaveQueryResults srq = new SaveQueryResults();
		srq.setSaveResults(false);
		srq.setOutputQueryResultDirectory(defaultDir);
		assertEquals(String.format(outputConfigurationBase, false, defaultDir), captureOutputPrintConfiguration(srq));
	}

	@Test
	public void printConfiguration_saveModelFalseValidDir_valid() {
		SaveQueryResults srq = new SaveQueryResults();
		srq.setSaveResults(false);
		srq.setOutputQueryResultDirectory(dir);
		assertEquals(String.format(outputConfigurationBase, false, dir), captureOutputPrintConfiguration(srq));
	}

	@Test
	public void printConfiguration_saveModelFalseEmptyDir_valid() {
		SaveQueryResults srq = new SaveQueryResults();
		srq.setSaveResults(false);
		srq.setOutputQueryResultDirectory("");
		assertEquals(String.format(outputConfigurationBase, false, ""), captureOutputPrintConfiguration(srq));
	}

	@Test
	public void printConfiguration_saveModelFalseNullDir_valid() {
		SaveQueryResults srq = new SaveQueryResults();
		srq.setSaveResults(false);
		srq.setOutputQueryResultDirectory(null);
		assertEquals(String.format(outputConfigurationBase, false, null), captureOutputPrintConfiguration(srq));
	}

	@Test
	public void prepare_saveModelTrueValidDir() {
		SaveQueryResults srq = new SaveQueryResults();
		srq.setSaveResults(true);
		srq.setOutputQueryResultDirectory(tempDir);
		srq.prepare();
		File f = new File(tempDir);
		assert (f.exists() && f.isDirectory());
		f.delete();
	}

	@Test
	public void prepare_saveModelFalseValidDir() {
		SaveQueryResults srq = new SaveQueryResults();
		srq.setSaveResults(false);
		srq.setOutputQueryResultDirectory(dir);
		srq.prepare();
		File f = new File(tempDir);
		assertFalse(f.exists());
	}

	@Test
	public void setSaveResults_and_isSaveResults() {
		SaveQueryResults srq = new SaveQueryResults();
		srq.setSaveResults(true);
		assertEquals(true, srq.isSaveResults());
		srq.setSaveResults(false);
		assertEquals(false, srq.isSaveResults());
	}

	@Test
	public void setOutputQueryResultDirectory_and_getOutputModelDirectory() {
		SaveQueryResults srq = new SaveQueryResults();
		srq.setOutputQueryResultDirectory("");
		assertEquals("", srq.getOutputQueryResultDirectory());
		srq.setOutputQueryResultDirectory(dir);
		assertEquals(dir, srq.getOutputQueryResultDirectory());
		srq.setOutputQueryResultDirectory(null);
		assertEquals(null, srq.getOutputQueryResultDirectory());
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
