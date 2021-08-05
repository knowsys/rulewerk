package org.semanticweb.rulewerk.integrationtests.acyclicity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

public class AcyclicRuleSets {

	private final String basicJARuleSet = "idbS(?y, !z) :- idbR(?x, ?y). \n idbV(?z, !t) :- idbS(?y, ?z).";

	@Test
	public void isJA() throws ParsingException {

		final KnowledgeBase kb = new KnowledgeBase();
		RuleParser.parseInto(kb, this.basicJARuleSet);

		try (Reasoner r = new VLogReasoner(kb)) {
			assertTrue(r.isJA());
		}
	}

	@Test
	public void isRJA() throws ParsingException {
		final KnowledgeBase kb = new KnowledgeBase();
		RuleParser.parseInto(kb, this.basicJARuleSet);

		try (Reasoner r = new VLogReasoner(kb)) {
			assertTrue(r.isRJA());
		}
	}

	@Test
	public void isMFA() throws ParsingException {
		final KnowledgeBase kb = new KnowledgeBase();
		RuleParser.parseInto(kb, this.basicJARuleSet);

		try (Reasoner r = new VLogReasoner(kb)) {
			assertTrue(r.isMFA());
		}
	}

	@Test
	public void isRMFA() throws ParsingException {
		final KnowledgeBase kb = new KnowledgeBase();
		RuleParser.parseInto(kb, this.basicJARuleSet);

		try (Reasoner r = new VLogReasoner(kb)) {
			assertTrue(r.isRMFA());
		}
	}

	@Test
	public void isNotMFC() throws ParsingException {
		final KnowledgeBase kb = new KnowledgeBase();
		RuleParser.parseInto(kb, this.basicJARuleSet);

		try (Reasoner r = new VLogReasoner(kb)) {
			assertFalse(r.isMFC());
		}
	}

}
