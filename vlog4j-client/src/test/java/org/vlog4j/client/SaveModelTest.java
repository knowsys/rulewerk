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
import org.vlog4j.client.picocli.SaveModel;
import org.vlog4j.client.picocli.SaveQueryResults;

public class SaveModelTest {

	@Test
	public void defaulfConfig() {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(false);
		sm.setOutputModelDirectory("query-results");
		Assert.assertTrue(sm.isConfigValid());
	}

	@Test
	public void dontSaveEmptyOutput() {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(false);
		sm.setOutputModelDirectory("");
		Assert.assertTrue(sm.isConfigValid());
	}

	@Test
	public void dontSaveNullOutput() {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(false);
		sm.setOutputModelDirectory(null);
		Assert.assertTrue(sm.isConfigValid());
	}

	@Test
	public void saveDefaulfDir() {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(true);
		sm.setOutputModelDirectory("query-results");
		Assert.assertTrue(sm.isConfigValid());
	}

	@Test
	public void saveEmptyOutputDir() {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(true);
		sm.setOutputModelDirectory("");
		Assert.assertFalse(sm.isConfigValid());
	}

	@Test
	public void saveNullDir() {
		SaveModel sm = new SaveModel();
		sm.setSaveModel(true);
		sm.setOutputModelDirectory(null);
		Assert.assertFalse(sm.isConfigValid());
	}
}
