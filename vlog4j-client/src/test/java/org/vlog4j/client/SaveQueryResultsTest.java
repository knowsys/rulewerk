package org.vlog4j.client;

import org.junit.Assert;

import org.junit.Test;
import org.vlog4j.client.picocli.SaveQueryResults;

public class SaveQueryResultsTest {

	@Test
	public void defaulfConfig() {
		SaveQueryResults sqr = new SaveQueryResults();
		sqr.saveResults = false;
		sqr.outputQueryResultDirectory = "query-results";
		Assert.assertTrue(sqr.isConfigOk());
	}

	@Test
	public void dontSaveEmptyOutput() {
		SaveQueryResults sqr = new SaveQueryResults();
		sqr.saveResults = false;
		sqr.outputQueryResultDirectory = "";
		Assert.assertTrue(sqr.isConfigOk());
	}

	@Test
	public void dontSaveNullOutput() {
		SaveQueryResults sqr = new SaveQueryResults();
		sqr.saveResults = false;
		sqr.outputQueryResultDirectory = null;
		Assert.assertTrue(sqr.isConfigOk());
	}

	@Test
	public void saveDefaulfDir() {
		SaveQueryResults sqr = new SaveQueryResults();
		sqr.saveResults = true;
		sqr.outputQueryResultDirectory = "query-results";
		Assert.assertTrue(sqr.isConfigOk());
	}

	@Test
	public void saveEmptyOutputDir() {
		SaveQueryResults sqr = new SaveQueryResults();
		sqr.saveResults = true;
		sqr.outputQueryResultDirectory = "";
		Assert.assertFalse(sqr.isConfigOk());
	}

	@Test
	public void saveNullDir() {
		SaveQueryResults sqr = new SaveQueryResults();
		sqr.saveResults = true;
		sqr.outputQueryResultDirectory = null;
		Assert.assertFalse(sqr.isConfigOk());
	}
}
