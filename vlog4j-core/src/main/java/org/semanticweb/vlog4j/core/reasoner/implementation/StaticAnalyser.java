package org.semanticweb.vlog4j.core.reasoner.implementation;

import org.semanticweb.vlog4j.core.reasoner.AcyclicityProperty;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.RuleStaticAnalyser;

public final class StaticAnalyser {

	private StaticAnalyser() {
	}

	public static boolean checkProperty(final KnowledgeBase knowledgeBase, final AcyclicityProperty acyclicityProperty,
			final RuleStaticAnalyser analyser) {

		return analyser.checkProperty(knowledgeBase, acyclicityProperty);
	}

	// isCyclic()
	// isAcyclic()

	// checkCyclicity -> don't know, cyclic, acyclic - BOOLEAN / ENUM

}
