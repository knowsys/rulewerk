package org.semanticweb.vlog4j.syntax.parser;

/*-
 * #%L
 * VLog4j Syntax
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
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.semanticweb.vlog4j.core.model.api.DataSourceDeclaration;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.implementation.CsvFileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.RdfFileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.SparqlQueryResultDataSource;
import org.semanticweb.vlog4j.parser.DataSourceDeclarationHandler;
import org.semanticweb.vlog4j.parser.ParserConfiguration;
import org.semanticweb.vlog4j.parser.ParsingException;
import org.semanticweb.vlog4j.parser.RuleParser;
import org.semanticweb.vlog4j.parser.javacc.SubParserFactory;

public class RuleParserDataSourceTest {
	@Test
	public void testCsvSource() throws ParsingException, IOException {
		String input = "@source p[2] : load-csv(\"src/main/data/input/example.csv\") .";
		CsvFileDataSource csvds = new CsvFileDataSource(new File("src/main/data/input/example.csv"));
		assertEquals(csvds, RuleParser.parseDataSourceDeclaration(input));
	}

	@Test
	public void testRdfSource() throws ParsingException, IOException {
		String input = "@source p[3] : load-rdf(\"src/main/data/input/example.nt.gz\") .";
		RdfFileDataSource rdfds = new RdfFileDataSource(new File("src/main/data/input/example.nt.gz"));
		assertEquals(rdfds, RuleParser.parseDataSourceDeclaration(input));
	}

	@Test(expected = ParsingException.class)
	public void testRdfSourceInvalidArity() throws ParsingException, IOException {
		String input = "@source p[2] : load-rdf(\"src/main/data/input/example.nt.gz\") .";
		RuleParser.parseDataSourceDeclaration(input);
	}

	@Test
	public void testSparqlSource() throws ParsingException, MalformedURLException {
		String input = "@source p[2] : sparql(<https://query.wikidata.org/sparql>,\"disease, doid\",\"?disease wdt:P699 ?doid .\") .";
		SparqlQueryResultDataSource sparqlds = new SparqlQueryResultDataSource(
				new URL("https://query.wikidata.org/sparql"), "disease, doid", "?disease wdt:P699 ?doid .");
		assertEquals(sparqlds, RuleParser.parseDataSourceDeclaration(input));
	}

	@Test(expected = ParsingException.class)
	public void testSparqlSourceMalformedUrl() throws ParsingException, MalformedURLException {
		String input = "@source p[2] : sparql(<not a URL>,\"disease, doid\",\"?disease wdt:P699 ?doid .\") .";
		RuleParser.parseDataSourceDeclaration(input);
	}

	@Test(expected = ParsingException.class)
	public void testUnknownDataSource() throws ParsingException {
		String input = "@source p[2] : unknown-data-source(\"hello, world\") .";
		RuleParser.parseDataSourceDeclaration(input);
	}

	@Test
	public void testCustomDataSource() throws ParsingException {
		CsvFileDataSource source = mock(CsvFileDataSource.class);
		DataSourceDeclarationHandler handler = mock(DataSourceDeclarationHandler.class);
		ParserConfiguration parserConfiguration = new ParserConfiguration();
		parserConfiguration.registerDataSource("mock-source", handler);
		doReturn(source).when(handler).handleDeclaration(ArgumentMatchers.<List<String>>any(),
				ArgumentMatchers.<SubParserFactory>any());

		String input = "@source p[2] : mock-source(\"hello\", \"world\") .";
		List<String> expectedArguments = Arrays.asList("hello", "world");
		RuleParser.parseDataSourceDeclaration(input, parserConfiguration);

		verify(handler).handleDeclaration(eq(expectedArguments), ArgumentMatchers.<SubParserFactory>any());
	}

	@Test
	public void sparqlDataSourceDeclarationToStringParsingTest() throws ParsingException, IOException {
		KnowledgeBase kb = new KnowledgeBase();
		Predicate predicate1 = Expressions.makePredicate("p", 3);
		SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(new URL("https://example.org/sparql"),
				"var", "?var wdt:P31 wd:Q5 .");
		DataSourceDeclaration dataSourceDeclaration1 = new DataSourceDeclarationImpl(predicate1, dataSource);
		RuleParser.parseInto(kb, dataSourceDeclaration1.toString());
		assertEquals(dataSourceDeclaration1, kb.getDataSourceDeclarations().get(0));
	}

	@Test
	public void rdfDataSourceDeclarationToStringParsingTest() throws ParsingException, IOException {
		KnowledgeBase kb = new KnowledgeBase();
		Predicate predicate1 = Expressions.makePredicate("p", 3);
		RdfFileDataSource unzippedRdfFileDataSource = new RdfFileDataSource(new File("src/test/data/input/file.nt"));
		DataSourceDeclaration dataSourceDeclaration = new DataSourceDeclarationImpl(predicate1,
				unzippedRdfFileDataSource);
		RuleParser.parseInto(kb, dataSourceDeclaration.toString());
		assertEquals(dataSourceDeclaration, kb.getDataSourceDeclarations().get(0));
	}

	@Test
	public void csvDataSourceDeclarationToStringParsingTest() throws ParsingException, IOException {
		KnowledgeBase kb = new KnowledgeBase();
		Predicate predicate1 = Expressions.makePredicate("q", 1);
		CsvFileDataSource unzippedCsvFileDataSource = new CsvFileDataSource(new File("src/test/data/input/file.csv"));
		final DataSourceDeclaration dataSourceDeclaration = new DataSourceDeclarationImpl(predicate1,
				unzippedCsvFileDataSource);
		RuleParser.parseInto(kb, dataSourceDeclaration.toString());
		assertEquals(dataSourceDeclaration, kb.getDataSourceDeclarations().get(0));
	}
}
