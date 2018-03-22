package org.semanticweb.vlog4j.core.reasoner.util;

/*
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 VLog4j Developers
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

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.reasoner.RuleRewriteStrategy;

/**
 * Utility class with static methods for converting from
 * {@code org.semanticweb.vlog4j.core.model} model objects to
 * {@code karmaresearch.vlog} model objects
 * 
 * @author Irina Dragoste
 *
 */
public final class ModelToVLogConverter {

	public static final String PREDICATE_ARITY_SUFFIX_SEPARATOR = "-";

	private ModelToVLogConverter() {
	}

	public static karmaresearch.vlog.Term toVLogTerm(final Term term) {
		Validate.notNull(term);
		final TermToVLogConverter termToVlogConverter = new TermToVLogConverter();
		term.accept(termToVlogConverter);
		return termToVlogConverter.getVlogTerm();
	}

	public static karmaresearch.vlog.Term[] toVLogTermArray(final List<Term> terms) {
		Validate.noNullElements(terms);
		final karmaresearch.vlog.Term[] vlogTerms = new karmaresearch.vlog.Term[terms.size()];
		int i = 0;
		for (final Term term : terms) {
			vlogTerms[i] = toVLogTerm(term);
			i++;
		}
		return vlogTerms;
	}

	public static String[][] toVLogFactTuples(final List<Atom> facts) {
		Validate.noNullElements(facts);
		final String[][] tuples = new String[facts.size()][];
		int i = 0;
		for (final Atom atom : facts) {
			final String[] vLogFactTuple = ModelToVLogConverter.toVLogFactTuple(atom);
			tuples[i] = vLogFactTuple;
			i++;
		}
		return tuples;
	}

	private static String[] toVLogFactTuple(final Atom fact) {
		final List<Term> terms = fact.getTerms();
		final String[] vlogFactTuple = new String[terms.size()];
		int i = 0;
		for (final Term term : terms) {
			final String vlogTupleTerm = term.getName();
			vlogFactTuple[i] = vlogTupleTerm;
			i++;
		}
		return vlogFactTuple;
	}

	/**
	 * Internal String representation that uniquely identifies a {@link Predicate}.
	 * 
	 * @param predicate
	 *            a {@link Predicate}
	 * @return String representation corresponding to given predicate name and
	 *         arity.
	 */
	public static String toVlogPredicate(Predicate predicate) {
		Validate.notNull(predicate);
		final String vLogPredicate = predicate.getName() + PREDICATE_ARITY_SUFFIX_SEPARATOR + predicate.getArity();
		return vLogPredicate;
	}

	public static karmaresearch.vlog.Atom toVLogAtom(final Atom atom) {
		Validate.notNull(atom);
		final karmaresearch.vlog.Term[] vlogTerms = toVLogTermArray(atom.getTerms());
		final String vLogPredicate = toVlogPredicate(atom.getPredicate());
		final karmaresearch.vlog.Atom vLogAtom = new karmaresearch.vlog.Atom(vLogPredicate, vlogTerms);
		return vLogAtom;
	}

	private static karmaresearch.vlog.Atom[] toVLogAtomArray(final Conjunction conjunction) {
		final karmaresearch.vlog.Atom[] vlogAtoms = new karmaresearch.vlog.Atom[conjunction.getAtoms().size()];
		int i = 0;
		for (final Atom atom : conjunction.getAtoms()) {
			vlogAtoms[i] = toVLogAtom(atom);
			i++;
		}
		return vlogAtoms;
	}

	private static karmaresearch.vlog.Rule toVLogRule(final Rule rule) {
		Validate.notNull(rule);
		final karmaresearch.vlog.Atom[] vlogHead = toVLogAtomArray(rule.getHead());
		final karmaresearch.vlog.Atom[] vlogBody = toVLogAtomArray(rule.getBody());
		return new karmaresearch.vlog.Rule(vlogHead, vlogBody);
	}

	public static karmaresearch.vlog.Rule[] toVLogRuleArray(final List<Rule> rules) {
		Validate.noNullElements(rules);
		final karmaresearch.vlog.Rule[] vlogRules = new karmaresearch.vlog.Rule[rules.size()];
		int i = 0;
		for (final Rule rule : rules) {
			vlogRules[i] = toVLogRule(rule);
			i++;
		}
		return vlogRules;
	}

	public static karmaresearch.vlog.VLog.RuleRewriteStrategy toVLogRuleRewriteStrategy(
			final RuleRewriteStrategy ruleRewriteStrategy) {
		Validate.notNull(ruleRewriteStrategy);
		final karmaresearch.vlog.VLog.RuleRewriteStrategy vLogRuleRewriteStrategy;
		switch (ruleRewriteStrategy) {
		case SPLIT_HEAD_PIECES_AGGRESIVE:
			vLogRuleRewriteStrategy = karmaresearch.vlog.VLog.RuleRewriteStrategy.AGGRESSIVE;
			break;
		case NONE:
		default:
			vLogRuleRewriteStrategy = karmaresearch.vlog.VLog.RuleRewriteStrategy.NONE;
			break;
		}
		return vLogRuleRewriteStrategy;
	}
}
