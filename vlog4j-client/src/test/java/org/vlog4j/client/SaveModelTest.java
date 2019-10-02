package org.vlog4j.client;

import org.junit.Assert;

import org.junit.Test;
import org.vlog4j.client.picocli.SaveModel;
import org.vlog4j.client.picocli.SaveQueryResults;

public class SaveModelTest {

	@Test
	public void defaulfConfig() {
		SaveModel sm = new SaveModel();
		sm.saveModel = false;
		sm.outputModelDirectory = "query-results";
		Assert.assertTrue(sm.isConfigOk());
	}

	@Test
	public void dontSaveEmptyOutput() {
		SaveModel sm = new SaveModel();
		sm.saveModel = false;
		sm.outputModelDirectory = "";
		Assert.assertTrue(sm.isConfigOk());
	}

	@Test
	public void dontSaveNullOutput() {
		SaveModel sm = new SaveModel();
		sm.saveModel = false;
		sm.outputModelDirectory = null;
		Assert.assertTrue(sm.isConfigOk());
	}

	@Test
	public void saveDefaulfDir() {
		SaveModel sm = new SaveModel();
		sm.saveModel = true;
		sm.outputModelDirectory = "query-results";
		Assert.assertTrue(sm.isConfigOk());
	}

	@Test
	public void saveEmptyOutputDir() {
		SaveModel sm = new SaveModel();
		sm.saveModel = true;
		sm.outputModelDirectory = "";
		Assert.assertFalse(sm.isConfigOk());
	}

	@Test
	public void saveNullDir() {
		SaveModel sm = new SaveModel();
		sm.saveModel = true;
		sm.outputModelDirectory = null;
		Assert.assertFalse(sm.isConfigOk());
	}
}
