package org.semanticweb.vlog4j.graal;

/*-
 * #%L
 * VLog4J Graal Import Components
 * %%
 * Copyright (C) 2018 VLog4j Developers
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
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeAtom;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConjunction;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConstant;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePredicate;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeRule;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeVariable;

import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;

import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

/**
 * @author Adrian Bielefeldt
 */
public class GraalToVLog4JModelConverterTest {
	
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
	
	private final Constant vlog4j_socrate = makeConstant(socrate);
	private final Constant vlog4j_redsBike = makeConstant(redsBike);
	
	private final Predicate vlog4j_bicycle = makePredicate(bicycle, 1);
	private final Predicate vlog4j_hasPart = makePredicate(hasPart, 2);
	private final Predicate vlog4j_human = makePredicate(human, 1);
	private final Predicate vlog4j_mortal = makePredicate(mortal, 1);
	private final Predicate vlog4j_wheel = makePredicate(wheel, 1);
	
	private final Variable vlog4j_x = makeVariable(x);
	private final Variable vlog4j_y = makeVariable(y);
	private final Variable vlog4j_z = makeVariable(z);

	@Test
	public void testConvertAtom() throws ParseException {
		final Atom vlog4j_atom = makeAtom(vlog4j_human, vlog4j_socrate);
		final fr.lirmm.graphik.graal.api.core.Atom graal_atom = DlgpParser.parseAtom(human + "(" + socrate + ").");
		assertEquals(vlog4j_atom, GraalToVLog4JModelConverter.convertAtom(graal_atom));

		final Atom vlog4j_atom_2 = makeAtom(vlog4j_hasPart, vlog4j_redsBike, vlog4j_x);
		final fr.lirmm.graphik.graal.api.core.Atom graal_atom_2 = DlgpParser
				.parseAtom(hasPart + "(" + redsBike + ", " + x + ").");
		assertEquals(vlog4j_atom_2, GraalToVLog4JModelConverter.convertAtom(graal_atom_2));
	}

	@Test
	public void testConvertRule() throws ParseException {
		// moral(X) :- human(X)
		final Atom vlog4j_mortal_atom = makeAtom(vlog4j_mortal, vlog4j_x);
		final Atom vlog4j_human_atom = makeAtom(vlog4j_human, vlog4j_x);
		final Rule vlog4j_rule = makeRule(vlog4j_mortal_atom, vlog4j_human_atom);
		final fr.lirmm.graphik.graal.api.core.Rule graal_rule = DlgpParser.parseRule(mortal + "(" + x + ") :- " + human + "(" + x + ").");
		assertEquals(vlog4j_rule, GraalToVLog4JModelConverter.convertRule(graal_rule));
	}
	
	@Test
	public void testConvertExistentialRule() throws ParseException {
		// hasPart(X, Y), wheel(Y) :- bicycle(X)
		final Atom vlog4j_hasPart_atom = makeAtom(vlog4j_hasPart, vlog4j_x, vlog4j_y);
		final Atom vlog4j_wheel_atom = makeAtom(vlog4j_wheel, vlog4j_y);
		final Atom vlog4j_bicycle_atom = makeAtom(vlog4j_bicycle, vlog4j_x);
		final Rule vlog4j_rule = makeRule(makeConjunction(vlog4j_hasPart_atom, vlog4j_wheel_atom), makeConjunction(vlog4j_bicycle_atom));
		final fr.lirmm.graphik.graal.api.core.Rule graal_rule = DlgpParser.parseRule(hasPart + "(" + x + "," + y + ")," + wheel + "(" + y + "):-" + bicycle + "(" + x + ")."); 
		assertEquals(vlog4j_rule, GraalToVLog4JModelConverter.convertRule(graal_rule));
	}
	
	@Test
	public void testConvertQuery() throws ParseException {
		// ?(X) :- mortal(X)
		final String mortalQuery = "mortalQuery";
		final Atom query = makeAtom(makePredicate(mortalQuery, 1), vlog4j_x);
		final Rule queryRule = makeRule(query, makeAtom(vlog4j_mortal, vlog4j_x));
		
		final GraalConjunctiveQueryToRule importedQuery = GraalToVLog4JModelConverter.convertQuery(mortalQuery, DlgpParser.parseQuery("?(" + x + ") :- " + mortal + "(" + x + ")."));
		assertEquals(query, importedQuery.getQueryAtom());
		assertEquals(queryRule, importedQuery.getRule());

		final String complexQuery = "complexQuery";
		final String predicate1 = "predicate1";
		final String predicate2 = "predicate2";
		final String predicate3 = "predicate3";
		final String predicate4 = "predicate4";
		final String stockholm = "stockholm";

		final Atom complexQueryAtom = makeAtom(makePredicate(complexQuery, 3), vlog4j_x, vlog4j_x, vlog4j_y);

		final Atom vlog4j_predicate1_atom = makeAtom(makePredicate(predicate1, 1), vlog4j_x);
		final Atom vlog4j_predicate2_atom = makeAtom(makePredicate(predicate2, 2), vlog4j_y, vlog4j_x);
		final Atom vlog4j_predicate3_atom = makeAtom(makePredicate(predicate3, 2), vlog4j_y, makeConstant(stockholm));
		final Atom vlog4j_predicate4_atom = makeAtom(makePredicate(predicate4, 3), vlog4j_x, vlog4j_y, vlog4j_z);

		final Rule complexQueryRule = makeRule(complexQueryAtom, vlog4j_predicate1_atom, vlog4j_predicate2_atom,
				vlog4j_predicate3_atom, vlog4j_predicate4_atom);

		final GraalConjunctiveQueryToRule importedComplexQuery = GraalToVLog4JModelConverter.convertQuery("query",
				DlgpParser.parseQuery(
						"?(" + x + ", " + x + ", " + y + ") :- " + predicate1
								+ "(" + x + "), " + predicate2 + "(" + y + ", " + x + "), " + predicate3 + "(" + y
								+ ", " + stockholm + "), " + predicate4 + "(" + x + ", " + y + ", " + z + ")."));
		assertEquals(complexQueryAtom, importedComplexQuery.getQueryAtom());
		assertEquals(complexQueryRule, importedComplexQuery.getRule());
	}
}
