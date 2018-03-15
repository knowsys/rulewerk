package org.semanticweb.vlog4j.core;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.semanticweb.vlog4j.core.model.Atom;
import org.semanticweb.vlog4j.core.model.AtomImpl;
import org.semanticweb.vlog4j.core.model.ConstantImpl;
import org.semanticweb.vlog4j.core.model.Rule;
import org.semanticweb.vlog4j.core.model.RuleImpl;
import org.semanticweb.vlog4j.core.model.VariableImpl;
import org.semanticweb.vlog4j.core.model.validation.AtomValidationException;
import org.semanticweb.vlog4j.core.model.validation.BlankNameValidationException;
import org.semanticweb.vlog4j.core.model.validation.ConstantNameValidationException;
import org.semanticweb.vlog4j.core.model.validation.PredicateNameValidationException;
import org.semanticweb.vlog4j.core.model.validation.RuleValidationException;
import org.semanticweb.vlog4j.core.model.validation.VariableNameValidationException;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.ReasonerImpl;

import junit.framework.TestCase;
import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NotStartedException;

public class ReasonerTest extends TestCase {

	public void testSimpleInference()
			throws AtomValidationException, PredicateNameValidationException, ConstantNameValidationException, VariableNameValidationException,
			RuleValidationException, AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException, BlankNameValidationException {
		final Atom factAc = new AtomImpl("A", new ConstantImpl("c"));
		final Atom factAd = new AtomImpl("A", new ConstantImpl("d"));
		final Atom atomAx = new AtomImpl("A", new VariableImpl("X"));
		final Atom atomBx = new AtomImpl("B", new VariableImpl("X"));
		final Atom atomCx = new AtomImpl("C", new VariableImpl("X"));
		final Rule ruleBxAx = new RuleImpl(Arrays.asList(atomBx), Arrays.asList(atomAx));
		final Rule ruleCxBx = new RuleImpl(Arrays.asList(atomCx), Arrays.asList(atomBx));

		final Reasoner reasoner = new ReasonerImpl();
		reasoner.addFacts(factAc, factAd);
		reasoner.addRules(ruleBxAx, ruleCxBx);
		reasoner.load();
		final List<List<String>> answerBefore = reasoner.compileQuerySet(atomCx);
		reasoner.reason();
		final List<List<String>> answersAfter = reasoner.compileQuerySet(atomCx);

		System.out.print("Answers before reasoning: ");
		for (final List<String> answer : answerBefore) {
			System.out.print(answer);
		}
		System.out.println();

		System.out.print(atomCx.toString() + " answers after reasoning: ");
		for (final List<String> answer : answersAfter) {
			System.out.print(answer + ",");
		}
		System.out.println();
	}
}
