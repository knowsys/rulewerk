package org.semanticweb.vlog4j.graal;

import static org.junit.Assert.assertEquals;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeAtom;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConstant;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConjunction;
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

public class GraalImporterTest {
	
	private String bicycle = "bicycle";
	private String hasPart = "hasPart";
	private String human = "human";
	private String mortal = "mortal";
	private String socrate = "socrate";
	private String wheel = "wheel";
	
	private String x = "X";
	private String y = "Y";
	
	private Constant vlog4j_socrate = makeConstant(socrate);
	
	private Predicate vlog4j_bicycle = makePredicate(bicycle, 1);
	private Predicate vlog4j_hasPart = makePredicate(hasPart, 2);
	private Predicate vlog4j_mortal = makePredicate(mortal, 1);
	private Predicate vlog4j_human = makePredicate(human, 1);
	private Predicate vlog4j_wheel = makePredicate(wheel, 1);
	
	private Variable vlog4j_x = makeVariable(x);
	private Variable vlog4j_y = makeVariable(y);

	@Test
	public void testImportAtom() throws ParseException {
		Atom vlog4j_atom = makeAtom(vlog4j_human, vlog4j_socrate);
		fr.lirmm.graphik.graal.api.core.Atom graal_atom = DlgpParser.parseAtom(human + "(" + socrate + ").");
		assertEquals(vlog4j_atom, GraalImporter.importAtom(graal_atom));
	}

	@Test
	public void testImportRule() throws ParseException {
		// moral(X) :- human(X)
		Atom vlog4j_mortal_atom = makeAtom(vlog4j_mortal, vlog4j_x);
		Atom vlog4j_human_atom = makeAtom(vlog4j_human, vlog4j_x);
		Rule vlog4j_rule = makeRule(vlog4j_mortal_atom, vlog4j_human_atom);
		fr.lirmm.graphik.graal.api.core.Rule graal_rule = DlgpParser.parseRule(mortal + "(" + x + ") :- " + human + "(" + x + ").");
		assertEquals(vlog4j_rule, GraalImporter.importRule(graal_rule));
	}
	
	@Test
	public void testImportExistentialRule() throws ParseException {
		// hasPart(X, Y), wheel(Y) :- bicycle(X)
		Atom vlog4j_hasPart_atom = makeAtom(vlog4j_hasPart, vlog4j_x, vlog4j_y);
		Atom vlog4j_wheel_atom = makeAtom(vlog4j_wheel, vlog4j_y);
		Atom vlog4j_bicycle_atom = makeAtom(vlog4j_bicycle, vlog4j_x);
		Rule vlog4j_rule = makeRule(makeConjunction(vlog4j_hasPart_atom, vlog4j_wheel_atom), makeConjunction(vlog4j_bicycle_atom));
		fr.lirmm.graphik.graal.api.core.Rule graal_rule = DlgpParser.parseRule(hasPart + "(" + x + "," + y + ")," + wheel + "(" + y + "):-" + bicycle + "(" + x + ")."); 
		assertEquals(vlog4j_rule, GraalImporter.importRule(graal_rule));
	}
}
