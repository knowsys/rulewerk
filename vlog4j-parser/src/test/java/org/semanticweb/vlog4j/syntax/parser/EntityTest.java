package org.semanticweb.vlog4j.syntax.parser;

/*-
 * #%L
 * VLog4j Parser
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.DataSourceDeclaration;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.NegativeLiteral;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.AbstractConstantImpl;
import org.semanticweb.vlog4j.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.model.implementation.LanguageStringConstantImpl;
import org.semanticweb.vlog4j.core.model.implementation.RuleImpl;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.implementation.CsvFileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.RdfFileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.SparqlQueryResultDataSource;
import org.semanticweb.vlog4j.parser.ParsingException;
import org.semanticweb.vlog4j.parser.RuleParser;

public class EntityTest {

	@Test
	public void languageStringConstantToStringRoundTripTest() throws ParsingException {
		LanguageStringConstantImpl s = new LanguageStringConstantImpl("Test", "en");
		Predicate p = Expressions.makePredicate("p", 1);
		Fact f3 = Expressions.makeFact(p, Arrays.asList(s));
		assertEquals(f3, RuleParser.parseFact(f3.toString()));
	}

	public void abstractConstantToStringRoundTripTest() throws ParsingException {
		AbstractConstantImpl f = new AbstractConstantImpl("f");
		AbstractConstantImpl a = new AbstractConstantImpl("1");
		Predicate p = Expressions.makePredicate("p", 1);
		Fact f1 = Expressions.makeFact(p, Arrays.asList(f));
		Fact f2 = Expressions.makeFact(p, Arrays.asList(a));
		assertEquals(f1, RuleParser.parseFact(f1.toString()));
		assertEquals(f2, RuleParser.parseFact(f2.toString()));
	}

	@Test
	public void ruleToStringRoundTripTest() throws ParsingException {
		Constant c = Expressions.makeAbstractConstant("c");
		Variable x = Expressions.makeUniversalVariable("X");
		Variable y = Expressions.makeUniversalVariable("Y");
		Variable z = Expressions.makeExistentialVariable("Z");
		PositiveLiteral atom1 = Expressions.makePositiveLiteral("p", x, c);
		PositiveLiteral atom2 = Expressions.makePositiveLiteral("p", x, y);
		PositiveLiteral headAtom1 = Expressions.makePositiveLiteral("q", x, z);
		Conjunction<Literal> bodyLiterals = Expressions.makeConjunction(atom1, atom2);
		Conjunction<PositiveLiteral> headPositiveLiterals = Expressions.makePositiveConjunction(headAtom1);
		Rule rule1 = new RuleImpl(headPositiveLiterals, bodyLiterals);
		assertEquals(rule1, RuleParser.parseRule(rule1.toString()));
	}

	@Test
	public void conjunctionToStringRoundTripTest() throws ParsingException {
		Constant c = Expressions.makeAbstractConstant("c");
		Variable x = Expressions.makeUniversalVariable("X");
		Variable y = Expressions.makeUniversalVariable("Y");
		Variable z = Expressions.makeExistentialVariable("Z");
		NegativeLiteral atom1 = Expressions.makeNegativeLiteral("p", x, c);
		PositiveLiteral atom2 = Expressions.makePositiveLiteral("p", x, y);
		PositiveLiteral headAtom1 = Expressions.makePositiveLiteral("q", x, z);
		Conjunction<Literal> bodyLiterals = Expressions.makeConjunction(atom1, atom2);
		Conjunction<PositiveLiteral> headPositiveLiterals = Expressions.makePositiveConjunction(headAtom1);
		Rule rule1 = new RuleImpl(headPositiveLiterals, bodyLiterals);
		assertEquals(rule1, RuleParser.parseRule(rule1.toString()));
	}

	@Test
	public void literalToStringRoundTripTest() throws ParsingException {
		Constant c = Expressions.makeAbstractConstant("c");
		Variable x = Expressions.makeUniversalVariable("X");
		Variable z = Expressions.makeExistentialVariable("Z");
		NegativeLiteral atom1 = Expressions.makeNegativeLiteral("p", x, c);
		PositiveLiteral headAtom1 = Expressions.makePositiveLiteral("q", x, z);
		Rule rule1 = Expressions.makeRule(headAtom1, atom1);
		assertEquals(rule1, RuleParser.parseRule(rule1.toString()));
	}

	@Test
	public void datatypeDoubleConstantToStringRoundTripTest() throws ParsingException {
		String shortDoubleConstant = "12.345E67";
		assertEquals(shortDoubleConstant,
				RuleParser.parseFact("p(\"12.345E67\"^^<http://www.w3.org/2001/XMLSchema#double>).").getArguments()
						.get(0).toString());
		assertEquals(shortDoubleConstant, RuleParser.parseFact("p(12.345E67).").getArguments().get(0).toString());
	}

	@Test
	public void datatypeFloatConstantToStringRoundTripTest() throws ParsingException {
		String floatConstant = "\"0.5\"^^<http://www.w3.org/2001/XMLSchema#float>";
		assertEquals(floatConstant, RuleParser.parseFact("p(\"0.5\"^^<http://www.w3.org/2001/XMLSchema#float>).")
				.getArguments().get(0).toString());
	}

	@Test
	public void datatypeStringConstantToStringRoundTripTest() throws ParsingException {
		String shortStringConstant = "\"data\"";
		assertEquals(shortStringConstant, RuleParser
				.parseFact("p(\"data\"^^<http://www.w3.org/2001/XMLSchema#string>).").getArguments().get(0).toString());
		assertEquals(shortStringConstant, RuleParser.parseFact("p(\"data\").").getArguments().get(0).toString());
	}

	@Test
	public void datatypeIntegerConstantToStringRoundTripTest() throws ParsingException {
		String shortIntegerConstant = "1";
		assertEquals(shortIntegerConstant, RuleParser.parseFact("p(\"1\"^^<http://www.w3.org/2001/XMLSchema#integer>).")
				.getArguments().get(0).toString());
		assertEquals(shortIntegerConstant, RuleParser.parseFact("p(1).").getArguments().get(0).toString());
	}

	@Test
	public void datatypeDecimalToStringRoundTripTest() throws ParsingException {
		String shortDecimalConstant = "0.23";
		assertEquals(shortDecimalConstant,
				RuleParser.parseFact("p(\"0.23\"^^<http://www.w3.org/2001/XMLSchema#decimal>).").getArguments().get(0)
						.toString());
		assertEquals(shortDecimalConstant, RuleParser.parseFact("p(0.23).").getArguments().get(0).toString());
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
		String INPUT_FOLDER = "src/test/data/input/";
		File unzippedRdfFile = new File(INPUT_FOLDER + "file.nt");
		Predicate predicate1 = Expressions.makePredicate("p", 3);
		RdfFileDataSource unzippedRdfFileDataSource = new RdfFileDataSource(unzippedRdfFile);
		DataSourceDeclaration dataSourceDeclaration = new DataSourceDeclarationImpl(predicate1,
				unzippedRdfFileDataSource);
		RuleParser.parseInto(kb, dataSourceDeclaration.toString());
		assertEquals(dataSourceDeclaration, kb.getDataSourceDeclarations().get(0));
	}

	@Test
	public void csvDataSourceDeclarationToStringParsingTest() throws ParsingException, IOException {
		KnowledgeBase kb = new KnowledgeBase();
		String INPUT_FOLDER = "src/test/data/input/";
		String csvFile = INPUT_FOLDER + "file.csv";
		Predicate predicate1 = Expressions.makePredicate("q", 1);
		CsvFileDataSource unzippedCsvFileDataSource = new CsvFileDataSource(new File(csvFile));
		final DataSourceDeclaration dataSourceDeclaration = new DataSourceDeclarationImpl(predicate1,
				unzippedCsvFileDataSource);
		RuleParser.parseInto(kb, dataSourceDeclaration.toString());
		assertEquals(dataSourceDeclaration, kb.getDataSourceDeclarations().get(0));
	}

}
