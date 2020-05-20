package org.semanticweb.rulewerk.reasoner.vlog;

/*-
 * #%L
 * Rulewerk VLog Reasoner Support
 * %%
 * Copyright (C) 2018 - 2020 Rulewerk Developers
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

import java.util.Collection;
import java.util.List;

import org.semanticweb.rulewerk.core.exceptions.RulewerkRuntimeException;
import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.NamedNull;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.reasoner.LogLevel;
import org.semanticweb.rulewerk.core.reasoner.RuleRewriteStrategy;

/**
 * Utility class with static methods for converting from VLog API model objects
 * ({@code org.semanticweb.rulewerk.core.model}) to internal VLog model objects
 * ({@code karmaresearch.vlog}).
 *
 * @author Irina Dragoste
 *
 */
final class ModelToVLogConverter {

	public static final String PREDICATE_ARITY_SUFFIX_SEPARATOR = "-";

	private ModelToVLogConverter() {
	}

	static karmaresearch.vlog.Term toVLogTerm(final Term term) {
		final TermToVLogConverter termToVLogConverter = new TermToVLogConverter();
		return term.accept(termToVLogConverter);
	}

	static karmaresearch.vlog.Term[] toVLogTermArray(final List<Term> terms) {
		final karmaresearch.vlog.Term[] vLogTerms = new karmaresearch.vlog.Term[terms.size()];
		int i = 0;
		for (final Term term : terms) {
			vLogTerms[i] = toVLogTerm(term);
			i++;
		}
		return vLogTerms;
	}

	static String[][] toVLogFactTuples(final Collection<Fact> facts) {
		final String[][] tuples = new String[facts.size()][];
		int i = 0;
		for (final Fact fact : facts) {
			final String[] vLogFactTuple = ModelToVLogConverter.toVLogFactTuple(fact);
			tuples[i] = vLogFactTuple;
			i++;
		}
		return tuples;
	}

	static String[] toVLogFactTuple(final Fact fact) {
		final List<Term> terms = fact.getArguments();
		final String[] vLogFactTuple = new String[terms.size()];
		int i = 0;
		for (final Term term : terms) {
			// No checks for type of term -- only constants allowed in facts!
			if (term instanceof Constant) {
				vLogFactTuple[i] = TermToVLogConverter.getVLogNameForConstant((Constant) term);
			} else if (term instanceof NamedNull) {
				vLogFactTuple[i] = TermToVLogConverter.getVLogNameForNamedNull((NamedNull) term);
			} else {
				throw new RulewerkRuntimeException("Terms in facts must be constants or named nulls. Encountered "
						+ term + " of type " + term.getType() + ".");
			}
			i++;
		}
		return vLogFactTuple;
	}

	/**
	 * Internal String representation that uniquely identifies a {@link Predicate}.
	 *
	 * @param predicate a {@link Predicate}
	 * @return String representation corresponding to given predicate name and
	 *         arity.
	 */
	static String toVLogPredicate(Predicate predicate) {
		final String vLogPredicate = predicate.getName() + PREDICATE_ARITY_SUFFIX_SEPARATOR + predicate.getArity();
		return vLogPredicate;
	}

	static karmaresearch.vlog.Atom toVLogAtom(final Literal literal) {
		final karmaresearch.vlog.Term[] vLogTerms = toVLogTermArray(literal.getArguments());
		final String vLogPredicate = toVLogPredicate(literal.getPredicate());
		final karmaresearch.vlog.Atom vLogAtom = new karmaresearch.vlog.Atom(vLogPredicate, literal.isNegated(),
				vLogTerms);
		return vLogAtom;
	}

	static karmaresearch.vlog.Atom[] toVLogAtomArray(final Conjunction<?> conjunction) {
		final karmaresearch.vlog.Atom[] vLogAtoms = new karmaresearch.vlog.Atom[conjunction.getLiterals().size()];
		int i = 0;
		for (final Literal atom : conjunction.getLiterals()) {
			vLogAtoms[i] = toVLogAtom(atom);
			i++;
		}
		return vLogAtoms;
	}

	static karmaresearch.vlog.Rule toVLogRule(final Rule rule) {
		final karmaresearch.vlog.Atom[] vLogHead = toVLogAtomArray(rule.getHead());
		final karmaresearch.vlog.Atom[] vLogBody = toVLogAtomArray(rule.getBody());
		return new karmaresearch.vlog.Rule(vLogHead, vLogBody);
	}

	static karmaresearch.vlog.Rule[] toVLogRuleArray(final Collection<Rule> rules) {
		final karmaresearch.vlog.Rule[] vLogRules = new karmaresearch.vlog.Rule[rules.size()];
		int i = 0;
		for (final Rule rule : rules) {
			vLogRules[i] = toVLogRule(rule);
			i++;
		}
		return vLogRules;
	}

	static karmaresearch.vlog.VLog.RuleRewriteStrategy toVLogRuleRewriteStrategy(
			final RuleRewriteStrategy ruleRewriteStrategy) {
		switch (ruleRewriteStrategy) {
		case SPLIT_HEAD_PIECES:
			return karmaresearch.vlog.VLog.RuleRewriteStrategy.AGGRESSIVE;
		case NONE:
		default:
			return karmaresearch.vlog.VLog.RuleRewriteStrategy.NONE;
		}
	}

	static karmaresearch.vlog.VLog.LogLevel toVLogLogLevel(final LogLevel logLevel) {
		switch (logLevel) {
		case DEBUG:
			return karmaresearch.vlog.VLog.LogLevel.DEBUG;
		case INFO:
			return karmaresearch.vlog.VLog.LogLevel.INFO;
		case ERROR:
			return karmaresearch.vlog.VLog.LogLevel.ERROR;
		case WARNING:
		default:
			return karmaresearch.vlog.VLog.LogLevel.WARNING;
		}
	}
}
