package org.semanticweb.rulewerk.reasoner.vlog;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.core.exceptions.RulewerkRuntimeException;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.reasoner.LogLevel;
import org.semanticweb.rulewerk.core.reasoner.RulesCyclicityProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.VLog;
import karmaresearch.vlog.VLog.CyclicCheckResult;
import karmaresearch.vlog.VLog.RuleRewriteStrategy;

//TODO add javadoc
public class VLogStaticAnalyser implements AutoCloseable {

	private static Logger LOGGER = LoggerFactory.getLogger(VLogStaticAnalyser.class);

	private final VLog vLog = new VLog();

	private LogLevel internalLogLevel = LogLevel.WARNING;

	public VLogStaticAnalyser() {
		super();
		this.setLogLevel(this.internalLogLevel);
	}

	// TODO would the Reasoner Rule rewrite strategy influence termination?
	// vLog.setRules(rules, rewriteStrategy);
	// vLog.addData(predicate, contents);
	// vLog.start(edbconfig, isFile);

	public void setLogLevel(final LogLevel logLevel) {
		// this.validateNotClosed();
		Validate.notNull(logLevel, "Log level cannot be null!");
		this.internalLogLevel = logLevel;
		this.vLog.setLogLevel(ModelToVLogConverter.toVLogLogLevel(this.internalLogLevel));
	}

	public LogLevel getLogLevel() {
		return this.internalLogLevel;
	}

	public void setLogFile(final String filePath) {
		// this.validateNotClosed();
		this.vLog.setLogFile(filePath);
	}

	public boolean checkProperty(final RulesCyclicityProperty property, final Collection<Rule> rules) {

		this.startReasoner();

		this.loadRules(rules);

//		this.validateNotClosed();
//		if (this.reasonerState == ReasonerState.KB_NOT_LOADED) {
//			try {
//				this.load();
//			} catch (final IOException e) { // FIXME: quick fix for https://github.com/knowsys/rulewerk/issues/128
//				throw new RulewerkRuntimeException(e);
//			}
//		}

		CyclicCheckResult checkCyclic;
		try {
			checkCyclic = this.vLog.checkCyclic(property.name());
		} catch (final NotStartedException e) {
			throw new RulewerkRuntimeException(e.getMessage(), e); // should be impossible
		}

		switch (property.getType()) {
		case ACYCLIC:
			return checkCyclic == CyclicCheckResult.NON_CYCLIC;
		case CYCLIC:
			return checkCyclic == CyclicCheckResult.CYCLIC;
		default:
			throw new RulewerkRuntimeException(
					"Unexpected cyclicity result [" + checkCyclic + "] for property [" + property + " ]!");
		}
	}

	private void loadRules(final Collection<Rule> rules) {
		final karmaresearch.vlog.Rule[] ruleArray = ModelToVLogConverter.toVLogRuleArray(rules);

		try {
			this.vLog.setRules(ruleArray, RuleRewriteStrategy.NONE);
			if (LOGGER.isDebugEnabled()) {
				for (final karmaresearch.vlog.Rule rule : ruleArray) {
					LOGGER.debug("Loaded rule {}.", rule.toString());
				}
			}
		} catch (final NotStartedException e) {
			throw new RulewerkRuntimeException("Inconsistent reasoner state!", e);
		}
	}

	private void startReasoner() {
		try {
			this.vLog.start(StringUtils.EMPTY, false);
		} catch (final AlreadyStartedException e) {
			throw new RulewerkRuntimeException("Inconsistent reasoner state.", e);
		} catch (final EDBConfigurationException | IOException e) {
			throw new RulewerkRuntimeException("Invalid VLog configuration.", e);
		}
	}

//	void getRules(final KnowledgeBase knowledgeBase, final Collection<Predicate> dataVaryingPredicates) {
//		// if fact over pred P, introduce aux_P, fact over aux_P and aux_P -> P
//		// what about the dataVaryingPredicates (also if P in dataVaryingPredicates)
//
//		// check this corresponds to our rule rewriting and does not influence
//		// termination
//		final List<Rule> rules = knowledgeBase.getRules();
//	}

//	// 1. Do we want EDB acycl checks? (configurable, given set of predicates; by
//	// default, the ones that have facts in the KB)
//	// 2. Cool if we can configure, out of the EDBs, which will change, and which
//	// will not. So there can be a table with facts that never changes, and we can
//	// load that into the acyclicity check.
//	// if fact over pred P, introduce aux_P, fact over aux_P and aux_P -> P
//
//	// TODO warning if you have negation + restricted chase?
//
//	// TODO rename rules to VLog to do the check
//	static boolean checkProperty(final VLog vLog, final AcyclicityProperty acyclicityProperty) {
//		CyclicCheckResult checkCyclic;
//		try {
//			checkCyclic = vLog.checkCyclic(acyclicityProperty.name());
//		} catch (final NotStartedException e) {
//			throw new RuntimeException(e.getMessage(), e); // should be impossible
//		}
//		return CyclicCheckResult.NON_CYCLIC == checkCyclic;
//	}

	@Override
	public void close() {
//		if (this.reasonerState == ReasonerState.CLOSED) {
//			LOGGER.info("Reasoner is already closed.");
//		} else {
//			this.reasonerState = ReasonerState.CLOSED;
//			this.knowledgeBase.deleteListener(this);
		this.vLog.stop();
		LOGGER.info("Analyser closed.");
//		}
	}

}
