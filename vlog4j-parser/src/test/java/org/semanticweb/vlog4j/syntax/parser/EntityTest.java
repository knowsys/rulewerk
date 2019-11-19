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
import java.util.List;

import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.DataSourceDeclaration;
import org.semanticweb.vlog4j.core.model.api.DatatypeConstant;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.NegativeLiteral;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.AbstractConstantImpl;
import org.semanticweb.vlog4j.core.model.implementation.ConjunctionImpl;
import org.semanticweb.vlog4j.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.vlog4j.core.model.implementation.DatatypeConstantImpl;
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
	final Variable x = Expressions.makeUniversalVariable("X");
	final Variable y = Expressions.makeUniversalVariable("Y");
	final Variable z = Expressions.makeExistentialVariable("Z");
	final Variable y2 = Expressions.makeUniversalVariable("Y");
	final Constant d = Expressions.makeAbstractConstant("d");
	final Constant c = Expressions.makeAbstractConstant("c");
	final AbstractConstantImpl f = new AbstractConstantImpl("f");
	final LanguageStringConstantImpl s = new LanguageStringConstantImpl("Test", "en");
	final DatatypeConstantImpl data = new DatatypeConstantImpl("data", "http://example.org/mystring");
	final PositiveLiteral atom1 = Expressions.makePositiveLiteral("p", x, c);
	final PositiveLiteral atom2 = Expressions.makePositiveLiteral("p", x, y);
	final PositiveLiteral headAtom1 = Expressions.makePositiveLiteral("q", x, z);
	final PositiveLiteral positiveLiteral1 = Expressions.makePositiveLiteral("p", x, c);
	final PositiveLiteral positiveLiteral2 = Expressions.makePositiveLiteral("p", y2, x);
	final PositiveLiteral positiveLiteral3 = Expressions.makePositiveLiteral("q", x, d);
	final NegativeLiteral NegativeLiteral = Expressions.makeNegativeLiteral("r", x, d);
	final PositiveLiteral PositiveLiteral4 = Expressions.makePositiveLiteral("s", c, s);
	final Predicate p = Expressions.makePredicate("p", 2);
	final Fact f1 = Expressions.makeFact(p, Arrays.asList(f, s));
	final Fact f2 = Expressions.makeFact("p", Arrays.asList(data, d));
	final List<Literal> LiteralList = Arrays.asList(positiveLiteral1, positiveLiteral2, positiveLiteral3,
			NegativeLiteral, PositiveLiteral4);
	final Conjunction<Literal> bodyLiterals = Expressions.makeConjunction(atom1, atom2);
	final Conjunction<PositiveLiteral> headPositiveLiterals = Expressions.makePositiveConjunction(headAtom1);
	final Conjunction<Literal> bodyConjunction = new ConjunctionImpl<>(LiteralList);
	final Rule rule1 = new RuleImpl(headPositiveLiterals, bodyLiterals);
	final Rule rule2 = new RuleImpl(headPositiveLiterals, bodyConjunction);

	@Test
	public void factToStringRoundTripTest() throws ParsingException {
		assertEquals(RuleParser.parseFact(f1.toString()), RuleParser.parseFact("p(f, \"Test\"@en)."));
		assertEquals(RuleParser.parseFact(f2.toString()),
				RuleParser.parseFact("p(\"data\"^^<http://example.org/mystring>, d)."));
	}

	@Test
	public void ruleToStringRoundTripTest() throws ParsingException {
		assertEquals(RuleParser.parseRule(rule1.toString()), RuleParser.parseRule("q(?X, !Z) :- p(?X, c), p(?X, ?Y)."));
		assertEquals(RuleParser.parseRule(rule2.toString()),
				RuleParser.parseRule("q(?X, !Z) :- p(?X, c), p(?Y, ?X), q(?X, d), ~r(?X, d), s(c, \"Test\"@en)."));
	}

	@Test
	public void dataSourceDeclarationToStringParsingTest() throws ParsingException, IOException {
		KnowledgeBase kb = new KnowledgeBase();
		final String INPUT_FOLDER = "src/test/data/input/";
		final String csvFile = INPUT_FOLDER + "file.csv";
		final File unzippedRdfFile = new File(INPUT_FOLDER + "file.nt");
		Predicate predicate1 = Expressions.makePredicate("p", 3);
		Predicate predicate2 = Expressions.makePredicate("q", 1);
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(
				new URL("https://example.org/sparql"), "var", "?var wdt:P31 wd:Q5 .");
		final CsvFileDataSource unzippedCsvFileDataSource = new CsvFileDataSource(new File(csvFile));
		final RdfFileDataSource unzippedRdfFileDataSource = new RdfFileDataSource(unzippedRdfFile);
		final DataSourceDeclaration dataSourceDeclaration1 = new DataSourceDeclarationImpl(predicate1, dataSource);
		final DataSourceDeclaration dataSourceDeclaration2 = new DataSourceDeclarationImpl(predicate2,
				unzippedCsvFileDataSource);
		final DataSourceDeclaration dataSourceDeclaration3 = new DataSourceDeclarationImpl(predicate1,
				unzippedRdfFileDataSource);
		RuleParser.parseInto(kb, dataSourceDeclaration1.toString());
		RuleParser.parseInto(kb, dataSourceDeclaration2.toString());
		RuleParser.parseInto(kb, dataSourceDeclaration3.toString());
	}

}
