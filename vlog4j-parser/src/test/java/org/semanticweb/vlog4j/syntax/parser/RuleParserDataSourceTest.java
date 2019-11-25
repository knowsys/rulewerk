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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
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
		String input = "@source p(2) : load-csv(\"src/main/data/input/example.csv\") .";
		CsvFileDataSource csvds = new CsvFileDataSource(new File("src/main/data/input/example.csv"));
		assertEquals(csvds, RuleParser.parseDataSourceDeclaration(input));
	}

	@Test
	public void testRdfSource() throws ParsingException, IOException {
		String input = "@source p(3) : load-rdf(\"src/main/data/input/example.nt.gz\") .";
		RdfFileDataSource rdfds = new RdfFileDataSource(new File("src/main/data/input/example.nt.gz"));
		assertEquals(rdfds, RuleParser.parseDataSourceDeclaration(input));
	}

	@Test(expected = ParsingException.class)
	public void testRdfSourceInvalidArity() throws ParsingException, IOException {
		String input = "@source p(2) : load-rdf(\"src/main/data/input/example.nt.gz\") .";
		RuleParser.parseDataSourceDeclaration(input);
	}

	@Test
	public void testSparqlSource() throws ParsingException, MalformedURLException {
		String input = "@source p(2) : sparql(<https://query.wikidata.org/sparql>,\"disease, doid\",\"?disease wdt:P699 ?doid .\") .";
		SparqlQueryResultDataSource sparqlds = new SparqlQueryResultDataSource(
				new URL("https://query.wikidata.org/sparql"), "disease, doid", "?disease wdt:P699 ?doid .");
		assertEquals(sparqlds, RuleParser.parseDataSourceDeclaration(input));
	}

	@Test(expected = ParsingException.class)
	public void testSparqlSourceMalformedUrl() throws ParsingException, MalformedURLException {
		String input = "@source p(2) : sparql(<not a URL>,\"disease, doid\",\"?disease wdt:P699 ?doid .\") .";
		RuleParser.parseDataSourceDeclaration(input);
	}

	@Test(expected = ParsingException.class)
	public void testUnknownDataSource() throws ParsingException {
		String input = "@source p(2) : unknown-data-source(\"hello, world\") .";
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

		String input = "@source p(2) : mock-source(\"hello\", \"world\") .";
		List<String> expectedArguments = Arrays.asList("hello", "world");
		RuleParser.parseDataSourceDeclaration(input, parserConfiguration);

		verify(handler).handleDeclaration(eq(expectedArguments), ArgumentMatchers.<SubParserFactory>any());
	}
}
