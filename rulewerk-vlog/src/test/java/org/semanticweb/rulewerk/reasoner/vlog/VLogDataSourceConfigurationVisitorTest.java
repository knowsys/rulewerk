package org.semanticweb.rulewerk.reasoner.vlog;

/*-
 * #%L
 * Rulewerk VLog Reasoner Support
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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.implementation.CsvFileDataSource;
import org.semanticweb.rulewerk.core.reasoner.implementation.FileDataSource;
import org.semanticweb.rulewerk.core.reasoner.implementation.InMemoryDataSource;
import org.semanticweb.rulewerk.core.reasoner.implementation.RdfFileDataSource;
import org.semanticweb.rulewerk.core.reasoner.implementation.ReasonerDataSource;
import org.semanticweb.rulewerk.core.reasoner.implementation.SparqlQueryResultDataSource;

public class VLogDataSourceConfigurationVisitorTest {
	private final String csvFile = FileDataSourceTestUtils.INPUT_FOLDER + "file.csv";
	private final String unzippedRdfFile = FileDataSourceTestUtils.INPUT_FOLDER + "file.nt";
	private final String zippedRdfFile = FileDataSourceTestUtils.INPUT_FOLDER + "file.nt.gz";
	private final String gzFile = csvFile + ".gz";
	final URL endpoint = new URL("http://query.wikidata.org/sparql");

	public VLogDataSourceConfigurationVisitorTest() throws MalformedURLException {
	}

	@Test
	public void visit_CsvFileDataSource_succeeds() throws IOException {
		final CsvFileDataSource unzippedCsvFileDataSource = new CsvFileDataSource(csvFile);
		final CsvFileDataSource zippedCsvFileDataSource = new CsvFileDataSource(gzFile);

		final String expectedDirCanonicalPath = new File(FileDataSourceTestUtils.INPUT_FOLDER).getCanonicalPath();
		final String expectedConfigString = "EDB%1$d_predname=%2$s\n" + "EDB%1$d_type=INMEMORY\n" + "EDB%1$d_param0="
				+ expectedDirCanonicalPath + "\n" + "EDB%1$d_param1=file\n";

		assertEquals(expectedConfigString, toConfigString(unzippedCsvFileDataSource));
		assertEquals(expectedConfigString, toConfigString(zippedCsvFileDataSource));
	}

	@Test
	public void visit_RdfFileDataSource_succeeds() throws IOException {
		final RdfFileDataSource unzippedRdfFileDataSource = new RdfFileDataSource(unzippedRdfFile);
		final RdfFileDataSource zippedRdfFileDataSource = new RdfFileDataSource(zippedRdfFile);

		final String expectedDirCanonicalPath = new File(FileDataSourceTestUtils.INPUT_FOLDER).getCanonicalPath();
		final String expectedConfigString = "EDB%1$d_predname=%2$s\n" + "EDB%1$d_type=INMEMORY\n" + "EDB%1$d_param0="
				+ expectedDirCanonicalPath + "\n" + "EDB%1$d_param1=file\n";

		assertEquals(expectedConfigString, toConfigString(unzippedRdfFileDataSource));
		assertEquals(expectedConfigString, toConfigString(zippedRdfFileDataSource));
	}

	@Test
	public void visit_SparqlQueryResultDataSource_succeeds() throws IOException, MalformedURLException {
		final SparqlQueryResultDataSource simpleDataSource = new SparqlQueryResultDataSource(endpoint, "b,a",
																							 "?a wdt:P22 ?b");
		final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(
				Arrays.asList(Expressions.makeUniversalVariable("b"), Expressions.makeUniversalVariable("a")));
		final SparqlQueryResultDataSource listDataSource = new SparqlQueryResultDataSource(endpoint, queryVariables,
				"?a wdt:P22 ?b");
		final String expectedConfigString = "EDB%1$d_predname=%2$s\n" + "EDB%1$d_type=SPARQL\n"
				+ "EDB%1$d_param0=http://query.wikidata.org/sparql\n" + "EDB%1$d_param1=b,a\n"
				+ "EDB%1$d_param2=?a wdt:P22 ?b\n";
		assertEquals(expectedConfigString, toConfigString(simpleDataSource));
		assertEquals(expectedConfigString, toConfigString(listDataSource));
	}

	@Test
	public void visit_InMemoryDataSource_returnsNull() throws IOException {
		final InMemoryDataSource inMemoryDataSource = new VLogInMemoryDataSource(1, 1);
		assertEquals(null, toConfigString(inMemoryDataSource));
	}

	@Test
	public void getDirCanonicalPath_relativePath_succeeds() throws IOException {
		final VLogDataSourceConfigurationVisitor visitor = new VLogDataSourceConfigurationVisitor();
		final FileDataSource fileDataSource = new CsvFileDataSource("file.csv");
		final String currentFolder = new File(".").getCanonicalPath();
		assertEquals(currentFolder, visitor.getDirCanonicalPath(fileDataSource));
	}

	@Test
	public void getDirCanonicalPath_nonNormalisedPath_succeeds() throws IOException {
		final VLogDataSourceConfigurationVisitor visitor = new VLogDataSourceConfigurationVisitor();
		final FileDataSource fileDataSource = new CsvFileDataSource("./././file.csv");
		final String currentFolder = new File(".").getCanonicalPath();
		assertEquals(currentFolder, visitor.getDirCanonicalPath(fileDataSource));
	}

	private String toConfigString(ReasonerDataSource dataSource) throws IOException {
		VLogDataSourceConfigurationVisitor visitor = new VLogDataSourceConfigurationVisitor();
		dataSource.accept(visitor);
		return visitor.getConfigString();
	}
}
