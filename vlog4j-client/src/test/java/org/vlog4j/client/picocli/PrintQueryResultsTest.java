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

import org.junit.Assert;

import org.junit.Test;
import org.vlog4j.client.picocli.PrintQueryResults;

public class PrintQueryResultsTest {

	@Test
	public void sizeOnly() {
		//default configuration
		PrintQueryResults prq = new PrintQueryResults();
		prq.setSizeOnly(true);
		prq.setComplete(false);
		Assert.assertTrue(prq.isConfigValid());
	}

	@Test
	public void completeOnly() {
		PrintQueryResults prq = new PrintQueryResults();
		prq.setSizeOnly(false);
		prq.setComplete(true);
		Assert.assertTrue(prq.isConfigValid());
	}

	@Test
	public void sizeAndComplete() {
		PrintQueryResults prq = new PrintQueryResults();
		prq.setSizeOnly(true);
		prq.setComplete(true);
		Assert.assertFalse(prq.isConfigValid());
	}

	@Test
	public void none() {
		PrintQueryResults prq = new PrintQueryResults();
		prq.setSizeOnly(false);
		prq.setComplete(false);
		Assert.assertTrue(prq.isConfigValid());
	}

}
