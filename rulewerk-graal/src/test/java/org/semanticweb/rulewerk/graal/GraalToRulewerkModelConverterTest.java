package org.semanticweb.rulewerk.graal;

/*-
 * #%L
 * Rulewerk Graal Import Components
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;

/**
 * @author Adrian Bielefeldt
 */
public class GraalToRulewerkModelConverterTest {

	private final String socrate = "socrate";
	private final String redsBike = "redsBike";

	private final String bicycle = "bicycle";
	private final String hasPart = "hasPart";
	private final String human = "human";
	private final String mortal = "mortal";
	private final String wheel = "wheel";

	private final String x = "X";
	private final String y = "Y";
	private final String z = "Z";

	private final Constant rulewerk_socrate = Expressions.makeAbstractConstant(this.socrate);

	private final Predicate rulewerk_bicycle = Expressions.makePredicate(this.bicycle, 1);
	private final Predicate rulewerk_hasPart = Expressions.makePredicate(this.hasPart, 2);
	private final Predicate rulewerk_human = Expressions.makePredicate(this.human, 1);
	private final Predicate rulewerk_mortal = Expressions.makePredicate(this.mortal, 1);
	private final Predicate rulewerk_wheel = Expressions.makePredicate(this.wheel, 1);

	private final Variable rulewerk_x = Expressions.makeUniversalVariable(this.x);
	private final Variable rulewerk_y = Expressions.makeUniversalVariable(this.y);
	private final Variable rulewerk_z = Expressions.makeUniversalVariable(this.z);
	private final Variable rulewerk_ex_y = Expressions.makeExistentialVariable(this.y);

	private final DefaultTermFactory termFactory = new DefaultTermFactory();

	private final fr.lirmm.graphik.graal.api.core.Constant graal_socrate = this.termFactory
			.createConstant(this.socrate);
	private final fr.lirmm.graphik.graal.api.core.Constant graal_redsBike = this.termFactory
			.createConstant(this.redsBike);

	private final fr.lirmm.graphik.graal.api.core.Predicate graal_bicycle = new fr.lirmm.graphik.graal.api.core.Predicate(
			this.bicycle, 1);
	private final fr.lirmm.graphik.graal.api.core.Predicate graal_hasPart = new fr.lirmm.graphik.graal.api.core.Predicate(
			this.hasPart, 2);
	private final fr.lirmm.graphik.graal.api.core.Predicate graal_human = new fr.lirmm.graphik.graal.api.core.Predicate(
			this.human, 1);
	private final fr.lirmm.graphik.graal.api.core.Predicate graal_mortal = new fr.lirmm.graphik.graal.api.core.Predicate(
			this.mortal, 1);
	private final fr.lirmm.graphik.graal.api.core.Predicate graal_wheel = new fr.lirmm.graphik.graal.api.core.Predicate(
			this.wheel, 1);

	private final fr.lirmm.graphik.graal.api.core.Variable graal_x = this.termFactory.createVariable(this.x);
	private final fr.lirmm.graphik.graal.api.core.Variable graal_y = this.termFactory.createVariable(this.y);
	private final fr.lirmm.graphik.graal.api.core.Variable graal_z = this.termFactory.createVariable(this.z);

	@Test
	public void testConvertAtom() throws ParseException {
		final PositiveLiteral rulewerk_atom = Expressions.makePositiveLiteral(this.rulewerk_human, this.rulewerk_socrate);
		final fr.lirmm.graphik.graal.api.core.Atom graal_atom = new DefaultAtom(this.graal_human, this.graal_socrate);
		assertEquals(rulewerk_atom, GraalToRulewerkModelConverter.convertAtom(graal_atom, Collections.emptySet()));

		final PositiveLiteral rulewerk_atom_2 = Expressions.makePositiveLiteral(this.rulewerk_hasPart, this.rulewerk_x,
				this.rulewerk_socrate);
		final fr.lirmm.graphik.graal.api.core.Atom graal_atom_2 = new DefaultAtom(this.graal_hasPart, this.graal_x,
				this.graal_socrate);
		assertEquals(rulewerk_atom_2, GraalToRulewerkModelConverter.convertAtom(graal_atom_2, Collections.emptySet()));
	}

	@Test
	public void testConvertFact() throws ParseException {
		final Fact rulewerk_atom = Expressions.makeFact(this.rulewerk_human, Arrays.asList(this.rulewerk_socrate));
		final fr.lirmm.graphik.graal.api.core.Atom graal_atom = new DefaultAtom(this.graal_human, this.graal_socrate);
		assertEquals(rulewerk_atom, GraalToRulewerkModelConverter.convertAtomToFact(graal_atom));
	}

	@Test
	public void testConvertRule() throws ParseException {
		// moral(X) :- human(X)
		final PositiveLiteral rulewerk_mortal_atom = Expressions.makePositiveLiteral(this.rulewerk_mortal, this.rulewerk_x);
		final PositiveLiteral rulewerk_human_atom = Expressions.makePositiveLiteral(this.rulewerk_human, this.rulewerk_x);
		final Rule rulewerk_rule = Expressions.makeRule(rulewerk_mortal_atom, rulewerk_human_atom);

		final fr.lirmm.graphik.graal.api.core.Atom graal_mortal_atom = new DefaultAtom(this.graal_mortal, this.graal_x);
		final fr.lirmm.graphik.graal.api.core.Atom graal_human_atom = new DefaultAtom(this.graal_human, this.graal_x);
		final fr.lirmm.graphik.graal.api.core.Rule graal_rule = new DefaultRule(new LinkedListAtomSet(graal_human_atom),
				new LinkedListAtomSet(graal_mortal_atom));

		assertEquals(rulewerk_rule, GraalToRulewerkModelConverter.convertRule(graal_rule));
	}

	@Test
	public void testConvertExistentialRule() throws ParseException {
		// hasPart(X, Y), wheel(Y) :- bicycle(X)

		final PositiveLiteral rulewerk_hasPart_atom = Expressions.makePositiveLiteral(this.rulewerk_hasPart, this.rulewerk_x,
				this.rulewerk_ex_y);
		final PositiveLiteral rulewerk_wheel_atom = Expressions.makePositiveLiteral(this.rulewerk_wheel, this.rulewerk_ex_y);
		final PositiveLiteral rulewerk_bicycle_atom = Expressions.makePositiveLiteral(this.rulewerk_bicycle, this.rulewerk_x);
		final Rule rulewerk_rule = Expressions.makeRule(
				Expressions.makePositiveConjunction(rulewerk_hasPart_atom, rulewerk_wheel_atom),
				Expressions.makeConjunction(rulewerk_bicycle_atom));

		final fr.lirmm.graphik.graal.api.core.Atom graal_hasPart_atom = new DefaultAtom(this.graal_hasPart,
				this.graal_x, this.graal_y);
		final fr.lirmm.graphik.graal.api.core.Atom graal_wheel_atom = new DefaultAtom(this.graal_wheel, this.graal_y);
		final fr.lirmm.graphik.graal.api.core.Atom graal_bicycle_atom = new DefaultAtom(this.graal_bicycle,
				this.graal_x);
		final fr.lirmm.graphik.graal.api.core.Rule graal_rule = new DefaultRule(
				new LinkedListAtomSet(graal_bicycle_atom), new LinkedListAtomSet(graal_hasPart_atom, graal_wheel_atom));

		assertEquals(rulewerk_rule, GraalToRulewerkModelConverter.convertRule(graal_rule));
	}

	@Test
	public void testConvertQuery() throws ParseException {
		// ?(X) :- mortal(X)
		final String mortalQuery = "mortalQuery";
		final PositiveLiteral query = Expressions.makePositiveLiteral(Expressions.makePredicate(mortalQuery, 1),
				this.rulewerk_x);
		final Rule queryRule = Expressions.makeRule(query,
				Expressions.makePositiveLiteral(this.rulewerk_mortal, this.rulewerk_x));

		final fr.lirmm.graphik.graal.api.core.Atom graal_query_atom = new DefaultAtom(this.graal_mortal, this.graal_x);

		final ConjunctiveQuery graal_query = new DefaultConjunctiveQuery(new LinkedListAtomSet(graal_query_atom),
				Arrays.asList(this.graal_x));

		final GraalConjunctiveQueryToRule importedQuery = GraalToRulewerkModelConverter.convertQuery(mortalQuery,
				graal_query);
		assertEquals(query, importedQuery.getQuery());
		assertEquals(queryRule, importedQuery.getRule());

		final String complexQuery = "complexQuery";
		final String predicate1 = "predicate1";
		final String predicate2 = "predicate2";
		final String predicate3 = "predicate3";
		final String predicate4 = "predicate4";
		final String stockholm = "stockholm";

		final fr.lirmm.graphik.graal.api.core.Predicate graal_predicate1 = new fr.lirmm.graphik.graal.api.core.Predicate(
				predicate1, 1);
		final fr.lirmm.graphik.graal.api.core.Predicate graal_predicate2 = new fr.lirmm.graphik.graal.api.core.Predicate(
				predicate2, 2);
		final fr.lirmm.graphik.graal.api.core.Predicate graal_predicate3 = new fr.lirmm.graphik.graal.api.core.Predicate(
				predicate3, 2);
		final fr.lirmm.graphik.graal.api.core.Predicate graal_predicate4 = new fr.lirmm.graphik.graal.api.core.Predicate(
				predicate4, 3);

		final fr.lirmm.graphik.graal.api.core.Atom graal_predicate1_atom = new DefaultAtom(graal_predicate1,
				this.graal_x);
		final fr.lirmm.graphik.graal.api.core.Atom graal_predicate2_atom = new DefaultAtom(graal_predicate2,
				this.graal_y, this.graal_x);
		final fr.lirmm.graphik.graal.api.core.Atom graal_predicate3_atom = new DefaultAtom(graal_predicate3,
				this.graal_y, this.termFactory.createConstant(stockholm));

		final fr.lirmm.graphik.graal.api.core.Atom graal_predicate4_atom = new DefaultAtom(graal_predicate4,
				this.graal_x, this.graal_y, this.graal_z);

		final ConjunctiveQuery graal_complex_query = new DefaultConjunctiveQuery(
				new LinkedListAtomSet(graal_predicate1_atom, graal_predicate2_atom, graal_predicate3_atom,
						graal_predicate4_atom),
				Arrays.asList(this.graal_x, this.graal_x, this.graal_y));

		final GraalConjunctiveQueryToRule importedComplexQuery = GraalToRulewerkModelConverter.convertQuery(complexQuery,
				graal_complex_query);

		final PositiveLiteral expectedComplexQueryAtom = Expressions.makePositiveLiteral(
				Expressions.makePredicate(complexQuery, 3), this.rulewerk_x, this.rulewerk_x, this.rulewerk_y);
		final PositiveLiteral rulewerk_predicate1_atom = Expressions
				.makePositiveLiteral(Expressions.makePredicate(predicate1, 1), this.rulewerk_x);
		final PositiveLiteral rulewerk_predicate2_atom = Expressions
				.makePositiveLiteral(Expressions.makePredicate(predicate2, 2), this.rulewerk_y, this.rulewerk_x);
		final PositiveLiteral rulewerk_predicate3_atom = Expressions.makePositiveLiteral(
				Expressions.makePredicate(predicate3, 2), this.rulewerk_y, Expressions.makeAbstractConstant(stockholm));
		final PositiveLiteral rulewerk_predicate4_atom = Expressions.makePositiveLiteral(
				Expressions.makePredicate(predicate4, 3), this.rulewerk_x, this.rulewerk_y, this.rulewerk_z);
		final Rule expectedComplexQueryRule = Expressions.makeRule(expectedComplexQueryAtom, rulewerk_predicate1_atom,
				rulewerk_predicate2_atom, rulewerk_predicate3_atom, rulewerk_predicate4_atom);

		assertEquals(expectedComplexQueryAtom, importedComplexQuery.getQuery());

		assertEquals(expectedComplexQueryRule, importedComplexQuery.getRule());
	}

	@Test(expected = GraalConvertException.class)
	public void testConvertQueryExceptionNoVariables() {

		final fr.lirmm.graphik.graal.api.core.Atom graal_atom = new DefaultAtom(this.graal_hasPart, this.graal_x,
				this.graal_socrate);
		final ConjunctiveQuery graal_query_without_answer_variables = new DefaultConjunctiveQuery(
				new LinkedListAtomSet(graal_atom), new ArrayList<>());
		GraalToRulewerkModelConverter.convertQuery("name", graal_query_without_answer_variables);
	}

	@Test(expected = GraalConvertException.class)
	public void testConvertQueryExceptionEmptyBody() {

		final ConjunctiveQuery graal_query_without_body = new DefaultConjunctiveQuery(new LinkedListAtomSet(),
				Arrays.asList(this.graal_y));
		GraalToRulewerkModelConverter.convertQuery("name", graal_query_without_body);
	}

	@Test(expected = GraalConvertException.class)
	public void testConvertQueryExceptionBlankPredicate() {

		final fr.lirmm.graphik.graal.api.core.Atom graal_atom_1 = new DefaultAtom(this.graal_hasPart,
				this.graal_redsBike, this.graal_z);
		final fr.lirmm.graphik.graal.api.core.Atom graal_atom_2 = new DefaultAtom(this.graal_human, this.graal_z);
		final ConjunctiveQuery graal_query = new DefaultConjunctiveQuery(
				new LinkedListAtomSet(graal_atom_1, graal_atom_2), Arrays.asList(this.graal_z));

		GraalToRulewerkModelConverter.convertQuery(" ", graal_query);
	}
}
