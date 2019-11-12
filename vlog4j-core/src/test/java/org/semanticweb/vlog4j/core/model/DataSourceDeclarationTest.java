package org.semanticweb.vlog4j.core.model;

/*-
 * #%L
 * VLog4j Core Components
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

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.DataSource;
import org.semanticweb.vlog4j.core.model.api.DataSourceDeclaration;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.implementation.CsvFileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.FileDataSourceTestUtils;
import org.semanticweb.vlog4j.core.reasoner.implementation.RdfFileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.SparqlQueryResultDataSource;

public class DataSourceDeclarationTest {

	@Test
	public void equalityTest() throws MalformedURLException {
		DataSource dataSource1 = new SparqlQueryResultDataSource(new URL("https://example.org/"), "var",
				"?var wdt:P31 wd:Q5 .");
		Predicate predicate1 = Expressions.makePredicate("p", 3);
		DataSourceDeclaration dataSourceDeclaration1 = new DataSourceDeclarationImpl(predicate1, dataSource1);
		DataSource dataSource2 = new SparqlQueryResultDataSource(new URL("https://example.org/"), "var",
				"?var wdt:P31 wd:Q5 .");
		Predicate predicate2 = Expressions.makePredicate("p", 3);
		DataSourceDeclaration dataSourceDeclaration2 = new DataSourceDeclarationImpl(predicate2, dataSource2);

		DataSource dataSource3 = new SparqlQueryResultDataSource(new URL("https://example.org/"), "var2",
				"?var2 wdt:P31 wd:Q5 .");
		DataSourceDeclaration dataSourceDeclaration3 = new DataSourceDeclarationImpl(predicate2, dataSource3);

		Predicate predicate4 = Expressions.makePredicate("q", 1);
		DataSourceDeclaration dataSourceDeclaration4 = new DataSourceDeclarationImpl(predicate4, dataSource2);

		assertEquals(dataSourceDeclaration1, dataSourceDeclaration1);
		assertEquals(dataSourceDeclaration1, dataSourceDeclaration2);
		assertEquals(dataSourceDeclaration1.hashCode(), dataSourceDeclaration2.hashCode());
		assertNotEquals(dataSourceDeclaration1, dataSource1);
		assertNotEquals(dataSourceDeclaration1, dataSourceDeclaration3);
		assertNotEquals(dataSourceDeclaration1, dataSourceDeclaration4);
		assertFalse(dataSourceDeclaration1.equals(null)); // written like this for recording coverage properly
	}

	@Test
	public void dataSourceDecalarationToStringTest() throws MalformedURLException {
		DataSource dataSource1 = new SparqlQueryResultDataSource(new URL("https://example.org/"), "var",
				"?var wdt:P31 wd:Q5 .");
		Predicate predicate1 = Expressions.makePredicate("p", 3);
		DataSourceDeclaration dataSourceDeclaration1 = new DataSourceDeclarationImpl(predicate1, dataSource1);
		DataSource dataSource2 = new SparqlQueryResultDataSource(new URL("https://example.org/"), "var",
				"?var wdt:P31 wd:Q5 .");
		Predicate predicate2 = Expressions.makePredicate("p", 3);
		DataSourceDeclaration dataSourceDeclaration2 = new DataSourceDeclarationImpl(predicate2, dataSource2);
		assertEquals(dataSourceDeclaration1.toString(), dataSourceDeclaration2.toString());
	}

	@Test
	public void DataSourceDeclarationToStringTest() throws IOException {
		final String csvFile = FileDataSourceTestUtils.INPUT_FOLDER + "file.csv";
		final File unzippedRdfFile = new File(FileDataSourceTestUtils.INPUT_FOLDER + "file.nt");
		Predicate predicate1 = Expressions.makePredicate("p", 3);
		Predicate predicate2 = Expressions.makePredicate("q", 1);
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(
				new URL("https://example.org/sparql"), "var", "?var wdt:P31 wd:Q5 .");
		final CsvFileDataSource unzippedCsvFileDataSource = new CsvFileDataSource(new File(csvFile));
		final RdfFileDataSource unzippedRdfFileDataSource = new RdfFileDataSource(unzippedRdfFile);
		final DataSourceDeclaration dataSourceDeclaration1 = new DataSourceDeclarationImpl(predicate1, dataSource);
		final DataSourceDeclaration dataSourceDeclaration2 = new DataSourceDeclarationImpl(predicate2,
				unzippedCsvFileDataSource);
		final DataSourceDeclaration dataSourceDeclaration3 = new DataSourceDeclarationImpl(predicate2,
				unzippedRdfFileDataSource);
		assertEquals("@source p(3): sparql(<https://example.org/sparql>, \"var\", \"?var wdt:P31 wd:Q5 .\") .",
				dataSourceDeclaration1.toString());
		assertEquals("@source q(1): load-csv(\"src/test/data/input/file.csv\") .", dataSourceDeclaration2.toString());
		assertEquals("@source q(1): load-rdf(\"src/test/data/input/file.nt\") .", dataSourceDeclaration3.toString());

	}
}
