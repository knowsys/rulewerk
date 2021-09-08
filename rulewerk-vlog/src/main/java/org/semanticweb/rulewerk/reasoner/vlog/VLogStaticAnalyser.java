package org.semanticweb.rulewerk.reasoner.vlog;

/*-
 * #%L
 * Rulewerk VLog Reasoner Support
 * %%
 * Copyright (C) 2018 - 2021 Rulewerk Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
//TODO state management
public class VLogStaticAnalyser implements AutoCloseable {

	private static Logger LOGGER = LoggerFactory.getLogger(VLogStaticAnalyser.class);

	private final VLog vLog = new VLog();

	private LogLevel internalLogLevel = LogLevel.WARNING;

	public VLogStaticAnalyser() {
		super();
		this.setLogLevel(this.internalLogLevel);
	}

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

	// TODO warning if you have negation + restricted chase?

	@Override
	public void close() {
//		if (this.reasonerState == ReasonerState.CLOSED) {
//			LOGGER.info("Reasoner is already closed.");
//		} else {
//			this.reasonerState = ReasonerState.CLOSED;
		this.vLog.stop();
		LOGGER.info("Analyser closed.");
//		}
	}

}
