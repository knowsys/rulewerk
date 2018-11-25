package org.semanticweb.vlog4j.core.reasoner;

import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeAtom;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConstant;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePredicate;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeRule;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeVariable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;

/**
 * Test case ensuring {@link Reasoner#setReasoningTimeout(Integer)} works as expected and terminates reasoning after the given {@link #timeout}.
 * Results are accepted within one second to account for setup and tear down of reasoning resources.
 * @author Adrian Bielefeldt
 *
 */
public class ReasonerTimeoutTest {
	
	/**
	 * The timeout after which reasoning should be completed in seconds.
	 */
	static int timeout = 1;
	
	static List<Atom> facts = new ArrayList<>();
	static List<Rule> rules = new ArrayList<>();
	
	Reasoner reasoner;
	
	/**
	 * The timeout after which reasoning should be completed. One second is added to account for setup and tear down of reasoning resources.
	 */
	@org.junit.Rule
	public Timeout globalTimeout = Timeout.seconds(timeout + 1);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Predicate infinite_EDB = makePredicate("infinite_EDB", 2);
		Predicate infinite_IDB = makePredicate("infinite_IDB", 2);
		
		facts.add(makeAtom(infinite_EDB, makeConstant("A"), makeConstant("B")));
		
		Variable x = makeVariable("x");
		Variable y = makeVariable("y");

		Atom infinite_IDB_xy = makeAtom(infinite_IDB, x, y);
		Atom infinite_EDB_xy = makeAtom(infinite_EDB, x, y);
		
		Rule import_rule = makeRule(infinite_IDB_xy, infinite_EDB_xy);
		rules.add(import_rule);
		
		Variable z = makeVariable("z");
		
		Atom further_yz = makeAtom(infinite_IDB, y, z);
		Rule infinite_rule = makeRule(further_yz, infinite_IDB_xy);
		rules.add(infinite_rule);
	}

	@Before
	public void setUp() throws Exception {
		reasoner = Reasoner.getInstance();
		
		reasoner.addFacts(facts);
		reasoner.addRules(rules);
		
		reasoner.setReasoningTimeout(timeout);
	}

	@Test
	public void skolem() throws EdbIdbSeparationException, IncompatiblePredicateArityException, IOException, ReasonerStateException {
		reasoner.setAlgorithm(Algorithm.SKOLEM_CHASE);
		
		reasoner.load();
		
		reasoner.reason();
	}
	
	@Test
	public void restricted() throws EdbIdbSeparationException, IncompatiblePredicateArityException, IOException, ReasonerStateException {
		reasoner.setAlgorithm(Algorithm.RESTRICTED_CHASE);
		
		reasoner.load();
		
		reasoner.reason();
	}
}
