package org.vlog4j.client;

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
import org.vlog4j.client.picocli.SaveQueryResults;

public class SaveQueryResultsTest {

	@Test
	public void defaulfConfig() {
		SaveQueryResults sqr = new SaveQueryResults();
		sqr.setSaveResults(false);
		sqr.setOutputQueryResultDirectory("query-results");
		Assert.assertTrue(sqr.isConfigValid());
	}

	@Test
	public void dontSaveEmptyOutput() {
		SaveQueryResults sqr = new SaveQueryResults();
		sqr.setSaveResults(false);
		sqr.setOutputQueryResultDirectory("");
		Assert.assertTrue(sqr.isConfigValid());
	}

	@Test
	public void dontSaveNullOutput() {
		SaveQueryResults sqr = new SaveQueryResults();
		sqr.setSaveResults(false);
		sqr.setOutputQueryResultDirectory(null);
		Assert.assertTrue(sqr.isConfigValid());
	}

	@Test
	public void saveDefaulfDir() {
		SaveQueryResults sqr = new SaveQueryResults();
		sqr.setSaveResults(true);
		sqr.setOutputQueryResultDirectory("query-results");
		Assert.assertTrue(sqr.isConfigValid());
	}

	@Test
	public void saveEmptyOutputDir() {
		SaveQueryResults sqr = new SaveQueryResults();
		sqr.setSaveResults(true);
		sqr.setOutputQueryResultDirectory("");
		Assert.assertFalse(sqr.isConfigValid());
	}

	@Test
	public void saveNullDir() {
		SaveQueryResults sqr = new SaveQueryResults();
		sqr.setSaveResults(true);
		sqr.setOutputQueryResultDirectory(null);
		Assert.assertFalse(sqr.isConfigValid());
	}
}
