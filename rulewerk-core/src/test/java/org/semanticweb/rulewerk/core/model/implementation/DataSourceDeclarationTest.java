package org.semanticweb.rulewerk.core.model.implementation;

/*-
 * #%L
 * Rulewerk Core Components
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.DataSource;
import org.semanticweb.rulewerk.core.model.api.DataSourceDeclaration;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.implementation.CsvFileDataSource;
import org.semanticweb.rulewerk.core.reasoner.implementation.RdfFileDataSource;
import org.semanticweb.rulewerk.core.reasoner.implementation.SparqlQueryResultDataSource;

public class DataSourceDeclarationTest {

	@Test
	public void testEquality() throws MalformedURLException {
		final DataSource dataSource1 = new SparqlQueryResultDataSource(new URL("https://example.org/"), "var",
				"?var wdt:P31 wd:Q5 .");
		final Predicate predicate1 = Expressions.makePredicate("p", 3);
		final DataSourceDeclaration dataSourceDeclaration1 = new DataSourceDeclarationImpl(predicate1, dataSource1);
		final DataSource dataSource2 = new SparqlQueryResultDataSource(new URL("https://example.org/"), "var",
				"?var wdt:P31 wd:Q5 .");
		final Predicate predicate2 = Expressions.makePredicate("p", 3);
		final DataSourceDeclaration dataSourceDeclaration2 = new DataSourceDeclarationImpl(predicate2, dataSource2);

		final DataSource dataSource3 = new SparqlQueryResultDataSource(new URL("https://example.org/"), "var2",
				"?var2 wdt:P31 wd:Q5 .");
		final DataSourceDeclaration dataSourceDeclaration3 = new DataSourceDeclarationImpl(predicate2, dataSource3);

		final Predicate predicate4 = Expressions.makePredicate("q", 1);
		final DataSourceDeclaration dataSourceDeclaration4 = new DataSourceDeclarationImpl(predicate4, dataSource2);

		assertEquals(dataSourceDeclaration1, dataSourceDeclaration1);
		assertEquals(dataSourceDeclaration1, dataSourceDeclaration2);
		assertEquals(dataSourceDeclaration1.hashCode(), dataSourceDeclaration2.hashCode());
		assertNotEquals(dataSourceDeclaration1, dataSource1);
		assertNotEquals(dataSourceDeclaration1, dataSourceDeclaration3);
		assertNotEquals(dataSourceDeclaration1, dataSourceDeclaration4);
		assertFalse(dataSourceDeclaration1.equals(null)); // written like this for recording coverage properly
	}

	@Test
	public void toString_SparqlQueryResultDataSource() throws IOException {
		final Predicate predicate = Expressions.makePredicate("p", 3);
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(
				new URL("https://example.org/sparql"), "var", "?var wdt:P31 wd:Q5 .");

		final DataSourceDeclaration dataSourceDeclaration = new DataSourceDeclarationImpl(predicate, dataSource);
		assertEquals("@source p[3]: sparql(<https://example.org/sparql>, \"var\", \"?var wdt:P31 wd:Q5 .\") .",
				dataSourceDeclaration.toString());

	}

	@Test
	public void toString_CsvFileDataSource() throws IOException {
		final Predicate predicate2 = Expressions.makePredicate("q", 1);
		final String relativeDirName = "dir/";
		final String fileName = "file.csv";

		final CsvFileDataSource unzippedCsvFileDataSource = new CsvFileDataSource(relativeDirName + fileName);
		final DataSourceDeclaration dataSourceDeclaration = new DataSourceDeclarationImpl(predicate2,
				unzippedCsvFileDataSource);

		final String expectedFilePath = "\"" + relativeDirName + fileName + "\"";
		assertEquals("@source q[1]: load-csv(" + expectedFilePath + ") .", dataSourceDeclaration.toString());
	}

	@Test
	public void toString_CsvFileDataSource_absolutePath_windowsPathSeparator() throws IOException {
		final Predicate predicate = Expressions.makePredicate("q", 1);
		final String absoluteFilePathWindows = "D:\\input\\file.csv";
		final String escapedPath = absoluteFilePathWindows.replace("\\", "\\\\");
		final CsvFileDataSource unzippedCsvFileDataSource = new CsvFileDataSource(absoluteFilePathWindows);
		final DataSourceDeclaration dataSourceDeclaration = new DataSourceDeclarationImpl(predicate,
				unzippedCsvFileDataSource);
		assertEquals("@source q[1]: load-csv(\"" + escapedPath + "\") .", dataSourceDeclaration.toString());
	}

	@Test
	public void toString_RdfFileDataSource_relativePath() throws IOException {
		final Predicate predicate = Expressions.makePredicate("q", 1);
		final String relativeDirName = "dir/";
		final String fileName = "file.nt";
		final String unzippedRdfFile = relativeDirName + fileName;
		final RdfFileDataSource unzippedRdfFileDataSource = new RdfFileDataSource(unzippedRdfFile);
		final DataSourceDeclaration dataSourceDeclaration = new DataSourceDeclarationImpl(predicate,
				unzippedRdfFileDataSource);

		final String expectedFilePath = "\"" + relativeDirName + fileName + "\"";
		assertEquals("@source q[1]: load-rdf(" + expectedFilePath + ") .", dataSourceDeclaration.toString());
	}
}
