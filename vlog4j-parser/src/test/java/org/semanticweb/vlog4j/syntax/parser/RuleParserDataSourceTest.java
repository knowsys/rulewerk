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
import java.util.ArrayList;
import java.util.Arrays;

import javax.sql.DataSource;

import org.junit.Test;
import org.mockito.Matchers;
import org.semanticweb.vlog4j.core.model.api.DataSourceDeclaration;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Statement;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.vlog4j.core.reasoner.implementation.CsvFileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.RdfFileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.SparqlQueryResultDataSource;
import org.semanticweb.vlog4j.parser.DataSourceDeclarationHandler;
import org.semanticweb.vlog4j.parser.ParserConfiguration;
import org.semanticweb.vlog4j.parser.ParsingException;
import org.semanticweb.vlog4j.parser.RuleParser;

public class RuleParserDataSourceTest {
	@Test
	public void testCsvSource() throws ParsingException, IOException {
		String input = "@source p(2) : load-csv(\"src/main/data/input/example.csv\") .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		CsvFileDataSource csvds = new CsvFileDataSource(new File("src/main/data/input/example.csv"));
		Predicate p = Expressions.makePredicate("p", 2);
		DataSourceDeclaration d = new DataSourceDeclarationImpl(p, csvds);
		assertEquals(Arrays.asList(d), statements);
	}

	@Test
	public void testRdfSource() throws ParsingException, IOException {
		String input = "@source p(3) : load-rdf(\"src/main/data/input/example.nt.gz\") .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		RdfFileDataSource rdfds = new RdfFileDataSource(new File("src/main/data/input/example.nt.gz"));
		Predicate p = Expressions.makePredicate("p", 3);
		DataSourceDeclaration d = new DataSourceDeclarationImpl(p, rdfds);
		assertEquals(Arrays.asList(d), statements);
	}

	@Test(expected = ParsingException.class)
	public void testRdfSourceInvalidArity() throws ParsingException, IOException {
		String input = "@source p(2) : load-rdf(\"src/main/data/input/example.nt.gz\") .";
		RuleParser.parse(input);
	}

	@Test
	public void testSparqlSource() throws ParsingException, MalformedURLException {
		String input = "@source p(2) : sparql(<https://query.wikidata.org/sparql>,\"disease, doid\",\"?disease wdt:P699 ?doid .\") .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		SparqlQueryResultDataSource sparqlds = new SparqlQueryResultDataSource(
				new URL("https://query.wikidata.org/sparql"), "disease, doid", "?disease wdt:P699 ?doid .");
		Predicate p = Expressions.makePredicate("p", 2);
		DataSourceDeclaration d = new DataSourceDeclarationImpl(p, sparqlds);
		assertEquals(Arrays.asList(d), statements);
	}

	@Test(expected = ParsingException.class)
	public void testSparqlSourceMalformedUrl() throws ParsingException, MalformedURLException {
		String input = "@source p(2) : sparql(<not a URL>,\"disease, doid\",\"?disease wdt:P699 ?doid .\") .";
		RuleParser.parse(input);
	}

    @Test(expected = ParsingException.class)
    public void testUnknownDataSource() throws ParsingException {
        String input = "@source p(2) : unknown-data-source(\"hello, world\") .";
        RuleParser.parse(input);
    }

    @Test
    public void testCustomDataSource() throws ParsingException {
        CsvFileDataSource source = mock(CsvFileDataSource.class);
        DataSourceDeclarationHandler handler = mock(DataSourceDeclarationHandler.class);
        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.registerDataSource("mock-source", handler);
        doReturn(source).when(handler).handleDeclaration(Matchers.<String[]>any());

        String input = "@source p(2) : mock-source(\"hello\", \"world\") .";
        String[] expectedArguments = {"hello", "world"};
        RuleParser.parse(input, parserConfiguration);

        verify(handler).handleDeclaration(eq(expectedArguments));
    }
}
