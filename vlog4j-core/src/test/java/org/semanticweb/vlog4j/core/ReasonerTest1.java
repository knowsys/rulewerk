package org.semanticweb.vlog4j.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.vlog4j.core.model.Atom;
import org.semanticweb.vlog4j.core.model.AtomImpl;
import org.semanticweb.vlog4j.core.model.Rule;
import org.semanticweb.vlog4j.core.model.RuleImpl;
import org.semanticweb.vlog4j.core.model.Term;
import org.semanticweb.vlog4j.core.model.VariableImpl;
import org.semanticweb.vlog4j.core.validation.VLog4jAtomValidationException;
import org.semanticweb.vlog4j.core.validation.VLog4jRuleValidationException;
import org.semanticweb.vlog4j.core.validation.VLog4jTermValidationException;

import junit.framework.TestCase;

public class ReasonerTest1 extends TestCase {

	public void simpleInference() throws VLog4jRuleValidationException, VLog4jAtomValidationException, VLog4jTermValidationException {

		// Rule set: { H(x) :- B(x) . }
		final List<Term> bodyAtomArgs = new ArrayList<>();
		bodyAtomArgs.add(new VariableImpl("x"));
		final Atom bodyAtom = new AtomImpl("B", bodyAtomArgs);
		final List<Atom> body = new ArrayList<>();
		body.add(bodyAtom);
		final List<Term> headAtomArgs = new ArrayList<>();
		headAtomArgs.add(new VariableImpl("x"));
		final Atom headAtom = new AtomImpl("H", bodyAtomArgs);
		final List<Atom> head = new ArrayList<>();
		head.add(headAtom);
		final Rule rule = new RuleImpl(body, head);
		final Set<Rule> ruleSet = new HashSet<>();
		ruleSet.add(rule);

		// Facts
		final String csvFilePath = File.pathSeparator + "Users" + File.pathSeparator + "carralma" + File.pathSeparator + "eclipse-workspace"
				+ File.pathSeparator + "vlog4j-parent" + File.pathSeparator + "vlog4j-core" + File.pathSeparator + "data" + File.pathSeparator
				+ "unaryFacts.csv";
		final Set<String[]> edbConfig = new HashSet<>();
		edbConfig.add(new String[] { csvFilePath, "B" });

		// VLog reasoner
		// final Reasoner reasoner = new ReasonerImpl(ruleSet, edbConfig);
	}
}
