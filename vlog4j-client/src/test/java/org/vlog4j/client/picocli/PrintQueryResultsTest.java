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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.vlog4j.client.picocli.PrintQueryResults;

public class PrintQueryResultsTest {
	String outputConfigurationBase = "  --print-query-result-size: %b\n  --print-complete-query-result: %b\n";

	private final PrintQueryResults sizeTrueCompleteTrue = new PrintQueryResults(true, true);
	private final PrintQueryResults sizeTrueCompleteFalse = new PrintQueryResults(true, false);
	private final PrintQueryResults sizeFalseCompleteTrue = new PrintQueryResults(false, true);
	private final PrintQueryResults sizeFalseCompleteFalse = new PrintQueryResults(false, false);

	@Test
	public void isValid_sizeTrueCompleteFalse_valid() {
		// default configuration
		assertTrue(sizeTrueCompleteFalse.isValid());
	}

	@Test
	public void isValid_sizeFalseCompleteTrue_valid() {
		assertTrue(sizeFalseCompleteTrue.isValid());
	}

	@Test
	public void isValid_sizeTrueCompleteTrue_notValid() {
		assertFalse(sizeTrueCompleteTrue.isValid());
	}

	@Test
	public void isValid_sizeFalseCompleteFalse_valid() {
		assertTrue(sizeFalseCompleteFalse.isValid());
	}

	@Test
	public void printConfiguration_sizeTrueCompleteFalse() {
		assertEquals(String.format(outputConfigurationBase, true, false),
				captureOutputPrintConfiguration(sizeTrueCompleteFalse));
	}

	@Test
	public void printConfiguration_sizeFalseCompleteTrue() {
		assertEquals(String.format(outputConfigurationBase, false, true),
				captureOutputPrintConfiguration(sizeFalseCompleteTrue));
	}

	@Test
	public void printConfiguration_sizeTrueCompleteTrue() {
		assertEquals(String.format(outputConfigurationBase, true, true),
				captureOutputPrintConfiguration(sizeTrueCompleteTrue));
	}

	@Test
	public void printConfiguration_sizeFalseCompleteFalse() {
		assertEquals(String.format(outputConfigurationBase, false, false),
				captureOutputPrintConfiguration(sizeFalseCompleteFalse));
	}

	@Test
	public void setSizeOnly_and_isSizeOnly() {
		PrintQueryResults prq = new PrintQueryResults();
		prq.setSizeOnly(false);
		assertFalse(prq.isSizeOnly());
		prq.setSizeOnly(true);
		assertTrue(prq.isSizeOnly());
	}

	@Test
	public void setComplete_and_isComplete() {
		PrintQueryResults prq = new PrintQueryResults();
		prq.setComplete(false);
		assertFalse(prq.isComplete());
		prq.setComplete(true);
		assertTrue(prq.isComplete());
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
