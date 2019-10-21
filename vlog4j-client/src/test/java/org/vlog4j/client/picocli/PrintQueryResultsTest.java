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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.vlog4j.client.picocli.PrintQueryResults;
import javax.naming.ConfigurationException;

public class PrintQueryResultsTest {
	String outputConfigurationBase = "  --print-query-result-size: %b\n  --print-complete-query-result: %b\n";

	@Test
	public void validate_sizeTrueCompleteFalse_valid() throws ConfigurationException {
		// default configuration
		PrintQueryResults prq = new PrintQueryResults();
		prq.setSizeOnly(true);
		prq.setComplete(false);
		prq.validate();
	}

	@Test
	public void validate_sizeFalseCompleteTrue_valid() throws ConfigurationException {
		PrintQueryResults prq = new PrintQueryResults();
		prq.setSizeOnly(false);
		prq.setComplete(true);
		prq.validate();
	}

	@Test(expected = ConfigurationException.class)
	public void validate_sizeTrueCompleteTrue_notValid() throws ConfigurationException {
		PrintQueryResults prq = new PrintQueryResults();
		prq.setSizeOnly(true);
		prq.setComplete(true);
		prq.validate();
	}

	@Test
	public void validate_sizeFalseCompleteFalse_valid() throws ConfigurationException {
		PrintQueryResults prq = new PrintQueryResults();
		prq.setSizeOnly(false);
		prq.setComplete(false);
		prq.validate();
	}

	@Test
	public void printConfiguration_sizeTrueCompleteFalse_valid() {
		PrintQueryResults prq = new PrintQueryResults();
		prq.setSizeOnly(true);
		prq.setComplete(false);
		assertEquals(String.format(outputConfigurationBase, true, false), captureOutputPrintConfiguration(prq));
	}

	@Test
	public void printConfiguration_sizeFalseCompleteTrue_valid() {
		PrintQueryResults prq = new PrintQueryResults();
		prq.setSizeOnly(false);
		prq.setComplete(true);
		assertEquals(String.format(outputConfigurationBase, false, true), captureOutputPrintConfiguration(prq));
	}

	@Test
	public void printConfiguration_sizeTrueCompleteTrue_notValid() {
		PrintQueryResults prq = new PrintQueryResults();
		prq.setSizeOnly(true);
		prq.setComplete(true);
		assertEquals(String.format(outputConfigurationBase, true, true), captureOutputPrintConfiguration(prq));
	}

	@Test
	public void printConfiguration_sizeFalseCompleteFalse_valid() {
		PrintQueryResults prq = new PrintQueryResults();
		prq.setSizeOnly(false);
		prq.setComplete(false);
		assertEquals(String.format(outputConfigurationBase, false, false), captureOutputPrintConfiguration(prq));
	}

	@Test
	public void setSizeOnly_and_isSizeOnly() {
		PrintQueryResults prq = new PrintQueryResults();
		prq.setSizeOnly(false);
		assertEquals(false, prq.isSizeOnly());
		prq.setSizeOnly(true);
		assertEquals(true, prq.isSizeOnly());
	}

	@Test
	public void setComplete_and_isComplete() {
		PrintQueryResults prq = new PrintQueryResults();
		prq.setComplete(false);
		assertEquals(false, prq.isComplete());
		prq.setComplete(true);
		assertEquals(true, prq.isComplete());
	}

	private String captureOutputPrintConfiguration(PrintQueryResults prq) {
		// Output Variables
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(result);
		// Save default System.out
		PrintStream systemOut = System.out;
		// Change System.out
		System.setOut(ps);
		// Do something
		prq.printConfiguration();
		// Restore previous state
		System.out.flush();
		System.setOut(systemOut);
		// return result
		return result.toString();
	}

}
