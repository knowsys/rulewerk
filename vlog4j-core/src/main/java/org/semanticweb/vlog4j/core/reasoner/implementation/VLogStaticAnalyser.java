package org.semanticweb.vlog4j.core.reasoner.implementation;

import java.util.Collection;
import java.util.List;

import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.reasoner.AcyclicityProperty;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;

import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.VLog;
import karmaresearch.vlog.VLog.CyclicCheckResult;

public class VLogStaticAnalyser {

	// TODO would the Reasoner Rule rewrite strategy influence termination?
	// vLog.setRules(rules, rewriteStrategy);
	// vLog.addData(predicate, contents);
	// vLog.start(edbconfig, isFile);

	// TODO load only EDB configs
	// vlogReasoner.loadEDBConf

	// TODO Auto-generated method stub

	public static boolean checkProperty(final AcyclicityProperty acyclicityProperty, final KnowledgeBase knowledgeBase,
			final Collection<Predicate> dataVaryingPredicates) {
//TODO define what the dataVaryingPredicates are, and what you do with your KB EDBs.

		// The EDBs that are not set as dataVaryingPredicates are turned into rules with
		// constants

		// The predicates that are set as dataVaryingPredicates -> EDBs ... but what if
		// they appear in rules?

		// TODO
		return false;

	}

	void getEDBs() {

	}

	void getRules(final KnowledgeBase knowledgeBase, final Collection<Predicate> dataVaryingPredicates) {
		// if fact over pred P, introduce aux_P, fact over aux_P and aux_P -> P
		// what about the dataVaryingPredicates (also if P in dataVaryingPredicates)

		// check this corresponds to our rule rewriting and does not influence
		// termination
		final List<Rule> rules = knowledgeBase.getRules();

	}

	// 1. Do we want EDB acycl checks? (configurable, given set of predicates; by
	// default, the ones that have facts in the KB)
	// 2. Cool if we can configure, out of the EDBs, which will change, and which
	// will not. So there can be a table with facts that never changes, and we can
	// load that into the acyclicity check.
	// if fact over pred P, introduce aux_P, fact over aux_P and aux_P -> P

	// TODO warning if you have negation + restricted chase?

	// TODO rename rules to VLog to do the check
	static boolean checkProperty(final VLog vLog, final AcyclicityProperty acyclicityProperty) {
		CyclicCheckResult checkCyclic;
		try {
			checkCyclic = vLog.checkCyclic(acyclicityProperty.name());
		} catch (final NotStartedException e) {
			throw new RuntimeException(e.getMessage(), e); // should be impossible
		}
		return CyclicCheckResult.NON_CYCLIC == checkCyclic;
	}

}
