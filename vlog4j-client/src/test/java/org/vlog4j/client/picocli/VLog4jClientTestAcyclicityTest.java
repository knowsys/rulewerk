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

public class VLog4jClientTestAcyclicityTest {

	@Test
	public void run() {
		VLog4jClientTestAcyclicity ta = new VLog4jClientTestAcyclicity();
		assertEquals("Not implemented yet.\n", captureRunError(ta));
	}

	private String captureRunError(VLog4jClientTestAcyclicity ta) {
		// Output Variables
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(result);
		// Save default System.out
		PrintStream systemErr = System.err;
		// Change System.out
		System.setErr(ps);
		// Do something
		ta.run();
		// Restore previous state
		System.err.flush();
		System.setErr(systemErr);
		// return result
		return result.toString();
	}
}
