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

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.semanticweb.rulewerk.core.model.api.Argument;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.model.api.DataSourceDeclaration;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.model.implementation.MergingPrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.implementation.NamedNullImpl;
import org.semanticweb.rulewerk.core.model.implementation.Serializer;
import org.semanticweb.rulewerk.core.reasoner.implementation.CsvFileDataSource;
import org.semanticweb.rulewerk.core.reasoner.implementation.RdfFileDataSource;
import org.semanticweb.rulewerk.core.reasoner.implementation.SparqlQueryResultDataSource;

public class SerializerTest {

	static Term abstractConstant = Expressions.makeAbstractConstant("http://example.org/test");
	static Term abstractConstantShort = Expressions.makeAbstractConstant("c");
	static Term existentialVariable = Expressions.makeExistentialVariable("X");
	static Term universalVariable = Expressions.makeUniversalVariable("X");
	static Term languageStringConstant = Expressions.makeLanguageStringConstant("abc", "de");
	static Term datatypeConstantGeneral = Expressions.makeDatatypeConstant("abc", "http://example.org/test");
	static Term datatypeConstantString = Expressions.makeDatatypeConstant("abc", PrefixDeclarationRegistry.XSD_STRING);
	static Term datatypeConstantInteger = Expressions.makeDatatypeConstant("123",
			PrefixDeclarationRegistry.XSD_INTEGER);
	static Term namedNull = new NamedNullImpl("n1");

	static Predicate p1 = Expressions.makePredicate("p1", 1);
	static Predicate p2 = Expressions.makePredicate("p2", 2);
	static Predicate p3 = Expressions.makePredicate("p3", 3);

	static Fact fact = Expressions.makeFact(p1, abstractConstantShort);
	static PositiveLiteral l1 = Expressions.makePositiveLiteral(p1, universalVariable);
	static Literal l2 = Expressions.makePositiveLiteral(p2, universalVariable, abstractConstantShort);
	static Rule rule = Expressions.makeRule(l1, l2, fact);
	static Literal ln1 = Expressions.makeNegativeLiteral(p1, existentialVariable);

	StringWriter writer;
	Serializer serializer;

	@Before
	public void init() {
		writer = new StringWriter();
		serializer = new Serializer(writer);
	}

	private Serializer getThrowingSerializer() throws IOException {
		Writer writerMock = Mockito.mock(Writer.class);
		Mockito.doThrow(IOException.class).when(writerMock).write(Mockito.anyString());
		return new Serializer(writerMock);
	}

	@Test
	public void serializeDatatypeConstant() throws IOException {
		serializer.writeTerm(datatypeConstantGeneral);
		assertEquals("\"abc\"^^<http://example.org/test>", writer.toString());
	}

	@Test
	public void serializeDatatypeConstantString() throws IOException {
		serializer.writeTerm(datatypeConstantString);
		assertEquals("\"abc\"", writer.toString());
	}

	@Test
	public void serializeDatatypeConstantInteger() throws IOException {
		serializer.writeTerm(datatypeConstantInteger);
		assertEquals("123", writer.toString());
	}

	@Test
	public void serializeExistentialVariable() throws IOException {
		serializer.writeTerm(existentialVariable);
		assertEquals("!X", writer.toString());
	}

	@Test
	public void serializeUniversalVariable() throws IOException {
		serializer.writeTerm(universalVariable);
		assertEquals("?X", writer.toString());
	}

	@Test
	public void serializeLanguageStringConstant() throws IOException {
		serializer.writeTerm(languageStringConstant);
		assertEquals("\"abc\"@de", writer.toString());
	}

	@Test
	public void serializeNamedNull() throws IOException {
		serializer.writeTerm(namedNull);
		assertEquals("_:n1", writer.toString());
	}

	@Test
	public void serializeFact() throws IOException {
		serializer.writeStatement(fact);
		assertEquals("p1(c) .", writer.toString());
		assertEquals("p1(c) .", Serializer.getSerialization(serializer -> serializer.writeFact(fact)));
	}

	@Test
	public void serializeRule() throws IOException {
		serializer.writeStatement(rule);
		assertEquals("p1(?X) :- p2(?X, c), p1(c) .", writer.toString());
	}

	@Test
	public void serializeCsvDataSourceDeclaration() throws IOException {
		DataSourceDeclaration csvSourceDecl = new DataSourceDeclarationImpl(p1, new CsvFileDataSource("test.csv"));
		serializer.writeStatement(csvSourceDecl);
		assertEquals("@source p1[1]: load-csv(\"test.csv\") .", writer.toString());
	}

	@Test
	public void serializeRdfDataSourceDeclaration() throws IOException {
		DataSourceDeclaration rdfSourceDecl = new DataSourceDeclarationImpl(p3, new RdfFileDataSource("test.nt"));
		serializer.writeStatement(rdfSourceDecl);
		assertEquals("@source p3[3]: load-rdf(\"test.nt\") .", writer.toString());
	}

	@Test
	public void serializeSparqlDataSourceDeclaration() throws IOException {
		DataSourceDeclaration sparqlSourceDecl = new DataSourceDeclarationImpl(p1,
				new SparqlQueryResultDataSource(new URL("http://example.org"), "var", "?var <a> <b>"));
		serializer.writeStatement(sparqlSourceDecl);
		assertEquals("@source p1[1]: sparql(<http://example.org>, \"var\", \"?var <a> <b>\") .", writer.toString());
	}

	@Test
	public void serializePositiveLiteral() throws IOException {
		serializer.writeLiteral(l1);
		assertEquals("p1(?X)", writer.toString());
	}

	@Test
	public void serializePositiveLiteralFromTerms() throws IOException {
		serializer.writePositiveLiteral(l1.getPredicate(), l1.getArguments());
		assertEquals("p1(?X)", writer.toString());
	}

	@Test
	public void serializeNegativeLiteral() throws IOException {
		serializer.writeLiteral(ln1);
		assertEquals("~p1(!X)", writer.toString());
	}

	@Test
	public void serializeAbstractConstantWithPrefixDeclarations() throws IOException {
		final MergingPrefixDeclarationRegistry prefixes = new MergingPrefixDeclarationRegistry();
		prefixes.setPrefixIri("eg:", "http://example.org/");
		Serializer prefSerializer = new Serializer(writer, prefixes);

		prefSerializer.writeTerm(abstractConstant);
		assertEquals("eg:test", writer.toString());
	}

	@Test
	public void serializeDatatypeConstantWithPrefixDeclarations() throws IOException {
		final MergingPrefixDeclarationRegistry prefixes = new MergingPrefixDeclarationRegistry();
		prefixes.setPrefixIri("eg:", "http://example.org/");
		Serializer prefSerializer = new Serializer(writer, prefixes);

		prefSerializer.writeTerm(datatypeConstantGeneral);
		assertEquals("\"abc\"^^eg:test", writer.toString());
	}

	@Test
	public void serializePrefixDeclarations() throws IOException {
		final MergingPrefixDeclarationRegistry prefixes = new MergingPrefixDeclarationRegistry();
		prefixes.setBaseIri("http://example.org/base");
		prefixes.setPrefixIri("eg:", "http://example.org/");
		Serializer prefSerializer = new Serializer(writer, prefixes);

		boolean result = prefSerializer.writePrefixDeclarationRegistry(prefixes);
		assertEquals("@base <http://example.org/base> .\n@prefix eg: <http://example.org/> .\n", writer.toString());
		assertTrue(result);
	}

	@Test
	public void serializeEmptyPrefixDeclarations() throws IOException {
		final MergingPrefixDeclarationRegistry prefixes = new MergingPrefixDeclarationRegistry();
		Serializer prefSerializer = new Serializer(writer, prefixes);

		boolean result = prefSerializer.writePrefixDeclarationRegistry(prefixes);
		assertEquals("", writer.toString());
		assertFalse(result);
	}

	@Test
	public void serializeCommand() throws IOException {
		ArrayList<Argument> arguments = new ArrayList<>();
		arguments.add(Argument.term(abstractConstant));
		arguments.add(Argument.positiveLiteral(fact));
		arguments.add(Argument.rule(rule));
		Command command = new Command("command", arguments);

		serializer.writeCommand(command);
		assertEquals("@command <http://example.org/test> p1(c) p1(?X) :- p2(?X, c), p1(c) .", writer.toString());
	}

	@Test
	public void createThrowingSerializer_succeeds() throws IOException {
		getThrowingSerializer();
	}

	@Test(expected = IOException.class)
	public void serializeAbstractConstant_fails() throws IOException {
		getThrowingSerializer().writeTerm(abstractConstant);
	}

	@Test(expected = IOException.class)
	public void serializeDatatypeConstant_fails() throws IOException {
		getThrowingSerializer().writeTerm(datatypeConstantGeneral);
	}

	@Test(expected = IOException.class)
	public void serializeExistentialVariable_fails() throws IOException {
		getThrowingSerializer().writeTerm(existentialVariable);
	}

	@Test(expected = IOException.class)
	public void serializeUniversalVariable_fails() throws IOException {
		getThrowingSerializer().writeTerm(universalVariable);
	}

	@Test(expected = IOException.class)
	public void serializeLanguageStringConstant_fails() throws IOException {
		getThrowingSerializer().writeTerm(languageStringConstant);
	}

	@Test(expected = IOException.class)
	public void serializeNamedNull_fails() throws IOException {
		getThrowingSerializer().writeTerm(namedNull);
	}

	@Test(expected = IOException.class)
	public void serializeFact_fails() throws IOException {
		getThrowingSerializer().writeStatement(fact);
	}

	@Test(expected = IOException.class)
	public void serializeRule_fails() throws IOException {
		getThrowingSerializer().writeStatement(rule);
	}

	@Test(expected = IOException.class)
	public void serializeDataSourceDeclaration_fails() throws IOException {
		DataSourceDeclaration csvSourceDecl = new DataSourceDeclarationImpl(p1, new CsvFileDataSource("test.csv"));
		getThrowingSerializer().writeStatement(csvSourceDecl);
	}

	@Test(expected = IOException.class)
	public void serializePrefixDeclarations_fails() throws IOException {
		final MergingPrefixDeclarationRegistry prefixes = new MergingPrefixDeclarationRegistry();
		prefixes.setBaseIri("http://example.org/base");
		prefixes.setPrefixIri("eg:", "http://example.org/");
		getThrowingSerializer().writePrefixDeclarationRegistry(prefixes);
	}

}
