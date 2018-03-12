package org.semanticweb.vlog4j.core;

import java.io.IOException;
import java.util.Iterator;

import org.semanticweb.vlog4j.core.validation.VLog4jAtomValidationException;
import org.semanticweb.vlog4j.core.validation.VLog4jRuleValidationException;
import org.semanticweb.vlog4j.core.validation.VLog4jTermValidationException;

import junit.framework.TestCase;
import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.Atom;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.Rule;
import karmaresearch.vlog.StringQueryResultEnumeration;
import karmaresearch.vlog.Term;
import karmaresearch.vlog.Term.TermType;
import karmaresearch.vlog.VLog;
import karmaresearch.vlog.VLog.RuleRewriteStrategy;

public class VLogTest extends TestCase {

	public void testVLogSimpleInference() throws AlreadyStartedException, EDBConfigurationException, IOException, VLog4jAtomValidationException,
			VLog4jTermValidationException, VLog4jRuleValidationException, NotStartedException {

		// Initialize and start VLog
		final VLog vlog = new VLog();
		vlog.start("", false);

		// Loading Fact: B(a)
		final String predA = "A";
		final String[][] argsAMatrix = { { "a" }, { "b" } };
		vlog.addData(predA, argsAMatrix);
		final String predB = "B";
		// final String[][] argsBMatrix = { { "c" }, { "d" } };
		// vlog.addData(predB, argsBMatrix);

		// Loading Rule: B(X) :- A(X) .
		final Term[] args = { new Term(TermType.VARIABLE, "X") };
		final Atom atomBx = new Atom(predB, args);
		final Atom[] headAtoms = { atomBx };
		final Atom atomAx = new Atom(predA, args);
		final Atom[] bodyAtoms = { atomAx };
		final Rule[] rules = { new Rule(headAtoms, bodyAtoms) };
		vlog.setRules(rules, RuleRewriteStrategy.NONE);

		// Materialization
		vlog.materialize(true);

		// Querying
		System.out.println("Querying predicate: " + atomBx.getPredicate());
		final StringQueryResultEnumeration answers = vlog.query(atomBx);
		final Iterator<String[]> answerIterator = answers.asIterator();
		while (answerIterator.hasNext()) {
			final String[] answer = answerIterator.next();
			System.out.println(answer[0]);
		}

		// Loading rule: B(X) :- A(X) .
		// final List<Rule> rules = new ArrayList<>();
		// final List<Term> bodyAtomArgs1 = new ArrayList<>();
		// bodyAtomArgs1.add(new VariableImpl("X"));
		// final Atom bodyAtom1 = new AtomImpl("A", bodyAtomArgs1);
		// final List<Atom> body1 = new ArrayList<>();
		// body1.add(bodyAtom1);
		// final List<Term> headAtomArgs1 = new ArrayList<>();
		// headAtomArgs1.add(new VariableImpl("X"));
		// final Atom headAtom1 = new AtomImpl("B", bodyAtomArgs1);
		// final List<Atom> head1 = new ArrayList<>();
		// head1.add(headAtom1);
		// final Rule rule1 = new RuleImpl(body1, head1);
		// rules.add(rule1);
		// vlog.setRules(ModelToVLogConverter.toVLogRuleArray(rules), RuleRewriteStrategy.NONE);

		// Loading facts
		// final List<Term> firstFactArgs = new ArrayList<>();
		// firstFactArgs.add(new ConstantImpl("a"));
		// reasoner.getFacts().add(new AtomImpl("C", firstFactArgs));
		// final List<Term> secondFactArgs = new ArrayList<>();
		// secondFactArgs.add(new ConstantImpl("b"));
		// reasoner.getFacts().add(new AtomImpl("C", secondFactArgs));
		// vlog.addData(predName, tuplesMatrix);

		// this.vlog.setRules(ModelToVLogConverter.toVLogRuleArray(this.rules), RuleRewriteStrategy.NONE);
	}
}
