package org.semanticweb.rulewerk.client.picocli;

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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PrintQueryResultsTest {

	private static final PrintQueryResults sizeTrueCompleteTrue = new PrintQueryResults();
	private static final PrintQueryResults sizeTrueCompleteFalse = new PrintQueryResults();
	private static final PrintQueryResults sizeFalseCompleteTrue = new PrintQueryResults(false, true);
	private static final PrintQueryResults sizeFalseCompleteFalse = new PrintQueryResults();

	static {
		sizeTrueCompleteTrue.setComplete(true);
		sizeFalseCompleteFalse.setSizeOnly(false);
	}

	@Test
	public void isValid_sizeTrueCompleteFalse_valid() {
		// default configuration
		assertTrue(sizeTrueCompleteFalse.isValid());
	}

	@Test
	public void isValid_sizeTrueCompleteTrue_notValid() {
		assertFalse(sizeTrueCompleteTrue.isValid());
	}

	@Test
	public void isValid_sizeFalseCompleteTrue_valid() {
		assertTrue(sizeFalseCompleteTrue.isValid());
	}

	@Test
	public void isValid_sizeFalseCompleteFalse_valid() {
		assertTrue(sizeFalseCompleteFalse.isValid());
	}

	@Test
	public void isSizeOnly_sizeFalseCompleteTrue() {
		assertFalse(sizeFalseCompleteTrue.isSizeOnly());
	}

	@Test
	public void isSizeOnly_sizeTrueCompleteTrue() {
		assertTrue(sizeTrueCompleteTrue.isSizeOnly());
	}

	@Test
	public void isSizeOnly_sizeTrueCompleteFalse() {
		assertTrue(sizeTrueCompleteFalse.isSizeOnly());
	}

	@Test
	public void isSizeOnly_sizeFalseCompleteFalse() {
		assertFalse(sizeFalseCompleteFalse.isSizeOnly());
	}

	@Test
	public void isComplete_sizeTrueCompleteFalse() {
		assertFalse(sizeTrueCompleteFalse.isComplete());
	}

	@Test
	public void isComplete_sizeTrueCompleteTrue() {
		assertTrue(sizeTrueCompleteTrue.isComplete());
	}

	@Test
	public void isComplete_sizeFalseCompleteTrue() {
		assertTrue(sizeFalseCompleteTrue.isComplete());
	}

	@Test
	public void isComplete_sizeFalseCompleteFalse() {
		assertFalse(sizeFalseCompleteFalse.isComplete());
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

}
