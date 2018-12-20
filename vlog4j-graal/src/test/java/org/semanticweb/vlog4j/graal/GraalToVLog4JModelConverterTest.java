package org.semanticweb.vlog4j.graal;

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
	
	private final String bicycle = "bicycle";
	private final String hasPart = "hasPart";
	private final String human = "human";
	private final String mortal = "mortal";
	private final String socrate = "socrate";
	private final String wheel = "wheel";
	
	private final String x = "X";
	private final String y = "Y";
	
	private final Constant vlog4j_socrate = makeConstant(socrate);
	
	private final Predicate vlog4j_bicycle = makePredicate(bicycle, 1);
	private final Predicate vlog4j_hasPart = makePredicate(hasPart, 2);
	private final Predicate vlog4j_mortal = makePredicate(mortal, 1);
	private final Predicate vlog4j_human = makePredicate(human, 1);
	private final Predicate vlog4j_wheel = makePredicate(wheel, 1);
	
	private final Variable vlog4j_x = makeVariable(x);
	private final Variable vlog4j_y = makeVariable(y);

	@Test
	public void testImportAtom() throws ParseException {
		final Atom vlog4j_atom = makeAtom(vlog4j_human, vlog4j_socrate);
		final fr.lirmm.graphik.graal.api.core.Atom graal_atom = DlgpParser.parseAtom(human + "(" + socrate + ").");
		assertEquals(vlog4j_atom, GraalToVLog4JModelConverter.importAtom(graal_atom));
	}

	@Test
	public void testImportRule() throws ParseException {
		// moral(X) :- human(X)
		final Atom vlog4j_mortal_atom = makeAtom(vlog4j_mortal, vlog4j_x);
		final Atom vlog4j_human_atom = makeAtom(vlog4j_human, vlog4j_x);
		final Rule vlog4j_rule = makeRule(vlog4j_mortal_atom, vlog4j_human_atom);
		final fr.lirmm.graphik.graal.api.core.Rule graal_rule = DlgpParser.parseRule(mortal + "(" + x + ") :- " + human + "(" + x + ").");
		assertEquals(vlog4j_rule, GraalToVLog4JModelConverter.importRule(graal_rule));
	}
	
	@Test
	public void testImportExistentialRule() throws ParseException {
		// hasPart(X, Y), wheel(Y) :- bicycle(X)
		final Atom vlog4j_hasPart_atom = makeAtom(vlog4j_hasPart, vlog4j_x, vlog4j_y);
		final Atom vlog4j_wheel_atom = makeAtom(vlog4j_wheel, vlog4j_y);
		final Atom vlog4j_bicycle_atom = makeAtom(vlog4j_bicycle, vlog4j_x);
		final Rule vlog4j_rule = makeRule(makeConjunction(vlog4j_hasPart_atom, vlog4j_wheel_atom), makeConjunction(vlog4j_bicycle_atom));
		final fr.lirmm.graphik.graal.api.core.Rule graal_rule = DlgpParser.parseRule(hasPart + "(" + x + "," + y + ")," + wheel + "(" + y + "):-" + bicycle + "(" + x + ")."); 
		assertEquals(vlog4j_rule, GraalToVLog4JModelConverter.importRule(graal_rule));
	}
	
	@Test
	public void testImportQuery() throws ParseException {
		// ?(X) :- mortal(X)
		final String mortalQuery = "mortalQuery";
		final Atom query = makeAtom(makePredicate(mortalQuery, 1), vlog4j_x);
		final Rule queryRule = makeRule(query, makeAtom(vlog4j_mortal, vlog4j_x));
		
		final ImportedGraalQuery importedQuery = GraalToVLog4JModelConverter.importQuery(mortalQuery, DlgpParser.parseQuery("?(" + x + ") :- " + mortal + "(" + x + ")."));
		assertEquals(query, importedQuery.getQuery());
		assertEquals(queryRule, importedQuery.getRule());
	}
}
