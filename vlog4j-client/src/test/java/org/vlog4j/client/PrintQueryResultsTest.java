package org.vlog4j.client;

import org.junit.Assert;

import org.junit.Test;
import org.vlog4j.client.picocli.PrintQueryResults;

public class PrintQueryResultsTest {

	@Test
	public void sizeOnly() {
		//default configuration
		PrintQueryResults prq = new PrintQueryResults();
		prq.sizeOnly = true;
		prq.complete = false;
		Assert.assertTrue(prq.isConfigOk());
	}

	@Test
	public void completeOnly() {
		PrintQueryResults prq = new PrintQueryResults();
		prq.sizeOnly = false;
		prq.complete = true;
		Assert.assertTrue(prq.isConfigOk());
	}

	@Test
	public void sizeAndComplete() {
		PrintQueryResults prq = new PrintQueryResults();
		prq.sizeOnly = true;
		prq.complete = true;
		Assert.assertFalse(prq.isConfigOk());
	}

	@Test
	public void none() {
		PrintQueryResults prq = new PrintQueryResults();
		prq.sizeOnly = false;
		prq.complete = false;
		Assert.assertTrue(prq.isConfigOk());
	}

}
