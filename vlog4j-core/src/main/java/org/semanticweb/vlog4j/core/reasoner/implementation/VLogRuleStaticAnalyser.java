package org.semanticweb.vlog4j.core.reasoner.implementation;

import org.semanticweb.vlog4j.core.reasoner.AcyclicityProperty;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.RuleStaticAnalyser;

import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.VLog;
import karmaresearch.vlog.VLog.CyclicCheckResult;

public class VLogRuleStaticAnalyser implements RuleStaticAnalyser {

	@Override
	public boolean checkProperty(final KnowledgeBase knowledgeBase, final AcyclicityProperty acyclicityProperty) {
		// TODO perhaps use knowledgeBase.getStaticAnalysisReasoner
		final VLog vLog = new VLog();
		// TODO would the Reasoner Rule rewrite strategy influence termination?
		// vLog.setRules(rules, rewriteStrategy);
		// vLog.addData(predicate, contents);
		// vLog.start(edbconfig, isFile);

		// TODO load only EDB configs
		// vlogReasoner.loadEDBConf

		// TODO Auto-generated method stub
		return false;
	}

	// 1. Do we want EDB acycl checks? (configurable, given set of predicates; by
	// default, the ones that have facts in the KB)
	// 2. Cool if we can configure, out of the EDBs, which will change, and which
	// will not. So there can be a table with facts that never changes, and we can
	// load that into the acyclicity check.
	// if fact over pred P, introduce aux_P, fact over aux_P and aux_P -> P

	// TODO warning if you have negation + restricted chase?

	void getEDBs() {

	}

	void getRules() {
		// if fact over pred P, introduce aux_P, fact over aux_P and aux_P -> P
	}

	boolean checkProperty(final VLog vLog, final AcyclicityProperty acyclicityProperty) {
		CyclicCheckResult checkCyclic;
		try {
			checkCyclic = vLog.checkCyclic(acyclicityProperty.name());
		} catch (final NotStartedException e) {
			throw new RuntimeException(e.getMessage(), e); // should be impossible
		}
		return CyclicCheckResult.NON_CYCLIC == checkCyclic;
	}

}
