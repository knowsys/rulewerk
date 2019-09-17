package org.semanticweb.vlog4j.graal;

import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConjunction;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConstant;

/*-
 * #%L
 * VLog4J Graal Import Components
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

import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePositiveLiteral;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePositiveLiteralsRule;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeFact;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePredicate;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeVariable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * Utility class to convert
 * <a href="http://graphik-team.github.io/graal/">Graal</a> data structures into
 * VLog4J data structures. Labels ({@link ConjunctiveQuery#getLabel()},
 * {@link fr.lirmm.graphik.graal.api.core.Rule#getLabel() Rule.getLabel()}, or
 * {@link fr.lirmm.graphik.graal.api.core.Term#getLabel() Term.getLabel()}) are
 * not converted since VLog4J does not support them.
 *
 * @author Adrian Bielefeldt
 *
 */
public final class GraalToVLog4JModelConverter {

	private GraalToVLog4JModelConverter() {
	}

	/**
	 * Converts a {@link fr.lirmm.graphik.graal.api.core.Atom Graal Atom} into a
	 * {@link PositiveLiteral VLog4J PositiveLiteral}.
	 *
	 * @param atom A {@link fr.lirmm.graphik.graal.api.core.Atom Graal Atom}
	 * @return A {@link PositiveLiteral VLog4J PositiveLiteral}
	 */
	public static PositiveLiteral convertAtom(final fr.lirmm.graphik.graal.api.core.Atom atom) {
		final Predicate predicate = convertPredicate(atom.getPredicate());
		final List<Term> terms = convertTerms(atom.getTerms());
		return makePositiveLiteral(predicate, terms);
	}

	/**
	 * Converts a {@link fr.lirmm.graphik.graal.api.core.Atom Graal Atom} into a
	 * {@link Fact VLog4J fact}.
	 *
	 * @param atom A {@link fr.lirmm.graphik.graal.api.core.Atom Graal Atom}
	 * @return A {@link Fact VLog4J fact}
	 * @throws IllegalArgumentException if the converted atom contains terms that
	 *                                  cannot occur in facts
	 */
	public static Fact convertAtomToFact(final fr.lirmm.graphik.graal.api.core.Atom atom) {
		final Predicate predicate = convertPredicate(atom.getPredicate());
		final List<Term> terms = convertTerms(atom.getTerms());
		return makeFact(predicate, terms);
	}

	/**
	 * Converts a {@link List} of {@link fr.lirmm.graphik.graal.api.core.Atom Graal
	 * Atoms} into a {@link List} of {@link PositiveLiteral VLog4J
	 * PositiveLiterals}.
	 *
	 * @param literals A {@link List} of {@link fr.lirmm.graphik.graal.api.core.Atom
	 *                 Graal Atoms}.
	 * @return A {@link List} of {@link PositiveLiteral VLog4J PositiveLiterals}.
	 */
	public static List<PositiveLiteral> convertAtoms(final List<fr.lirmm.graphik.graal.api.core.Atom> atoms) {
		final List<PositiveLiteral> result = new ArrayList<>();
		for (final fr.lirmm.graphik.graal.api.core.Atom atom : atoms) {
			result.add(convertAtom(atom));
		}
		return result;
	}

	/**
	 * Converts a {@link List} of {@link fr.lirmm.graphik.graal.api.core.Atom Graal
	 * Atoms} into a {@link List} of {@link Fact VLog4j facts}.
	 *
	 * @param literals A {@link List} of {@link fr.lirmm.graphik.graal.api.core.Atom
	 *                 Graal Atoms}.
	 * @return A {@link List} of {@link Fact VLog4j facts}.
	 */
	public static List<Fact> convertAtomsToFacts(final List<fr.lirmm.graphik.graal.api.core.Atom> atoms) {
		final List<Fact> result = new ArrayList<>();
		for (final fr.lirmm.graphik.graal.api.core.Atom atom : atoms) {
			result.add(convertAtomToFact(atom));
		}
		return result;
	}

	/**
	 * Converts a {@link AtomSet Graal AtomSet} into a {@link Conjunction VLog4J
	 * Conjunction} of {@link PositiveLiteral}s.
	 *
	 * @param atomSet A {@link AtomSet Graal AtomSet}
	 * @return A {@link Conjunction VLog4J Conjunction}
	 */
	private static Conjunction<PositiveLiteral> convertAtomSet(final AtomSet atomSet) {
		final List<PositiveLiteral> result = new ArrayList<>();
		try (CloseableIterator<fr.lirmm.graphik.graal.api.core.Atom> iterator = atomSet.iterator()) {
			while (iterator.hasNext()) {
				result.add(convertAtom(iterator.next()));
			}
		} catch (final IteratorException e) {
			throw new GraalConvertException(MessageFormat
					.format("Unexpected Iterator Exception when converting PositiveLiteralSet {0}}.", atomSet), e);
		}
		return makeConjunction(result);
	}

	/**
	 * Converts a {@link fr.lirmm.graphik.graal.api.core.Predicate Graal Predicate}
	 * into a {@link Predicate VLog4J Predicate}.
	 *
	 * @param predicate A {@link fr.lirmm.graphik.graal.api.core.Predicate Graal
	 *                  Predicate}
	 * @return A {@link Predicate VLog4J Predicate}
	 */
	private static Predicate convertPredicate(final fr.lirmm.graphik.graal.api.core.Predicate predicate) {
		return makePredicate(predicate.getIdentifier().toString(), predicate.getArity());
	}

	/**
	 * Converts a {@link ConjunctiveQuery Graal ConjunctiveQuery} into a
	 * {@link GraalConjunctiveQueryToRule}. Answering a Graal ConjunctiveQuery over
	 * a certain knowledge base is equivalent to adding a {@link Rule} to the
	 * knowledge base, <em> prior to reasoning</em>. The rule consists of the query
	 * literals as the body and a single {@link PositiveLiteral} with a new
	 * predicate containing all the query variables as the head. After the reasoning
	 * process, in which the rule is materialised, is completed, this rule head can
	 * then be used as a query PositiveLiteral to obtain the results of the Graal
	 * ConjunctiveQuery.
	 *
	 * <p>
	 * <b>WARNING</b>: The supplied {@code ruleHeadPredicateName} will be used to
	 * create a {@link Predicate} containing all answer variables from the
	 * {@code conjunctiveQuery}. If a Predicate with the same name and arity is used
	 * elsewhere in the same program, the result will differ from the one expected
	 * from the Graal ConjunctiveQuery.
	 * </p>
	 *
	 * @param ruleHeadPredicateName A name to create a program-unique predicate for
	 *                              the query PositiveLiteral.
	 * @param conjunctiveQuery      A {@link ConjunctiveQuery Graal Query}.
	 * @return A {@link GraalConjunctiveQueryToRule} equivalent to the
	 *         {@code conjunctiveQuery} input.
	 */
	public static GraalConjunctiveQueryToRule convertQuery(final @NonNull String ruleHeadPredicateName,
			final ConjunctiveQuery conjunctiveQuery) {
		if (StringUtils.isBlank(ruleHeadPredicateName)) {
			throw new GraalConvertException(MessageFormat.format(
					"Rule head predicate for Graal ConjunctiveQuery {0} cannot be a blank string.", conjunctiveQuery));
		}

		if (conjunctiveQuery.getAtomSet().isEmpty()) {
			throw new GraalConvertException(MessageFormat.format(
					"Graal ConjunctiveQuery {0} with empty body is not supported in VLog4j.", conjunctiveQuery));
		}

		if (conjunctiveQuery.getAnswerVariables().isEmpty()) {
			throw new GraalConvertException(MessageFormat.format(
					"Graal ConjunctiveQuery {0} with no answer variables is not supported in VLog4J.",
					conjunctiveQuery));
		}

		final Conjunction<PositiveLiteral> conjunction = convertAtomSet(conjunctiveQuery.getAtomSet());
		final List<Term> answerVariables = convertTerms(conjunctiveQuery.getAnswerVariables());

		return new GraalConjunctiveQueryToRule(ruleHeadPredicateName, answerVariables, conjunction);
	}

	/**
	 * Converts a {@link fr.lirmm.graphik.graal.api.core.Rule Graal Rule} into a
	 * {@link Rule Vlog4J Rule}.
	 *
	 * @param rule A {@link fr.lirmm.graphik.graal.api.core.Rule Graal Rule}.
	 * @return A {@link Rule Vlog4J Rule}.
	 */
	public static Rule convertRule(final fr.lirmm.graphik.graal.api.core.Rule rule) {
		final Conjunction<PositiveLiteral> head = convertAtomSet(rule.getHead());
		final Conjunction<PositiveLiteral> body = convertAtomSet(rule.getBody());
		return makePositiveLiteralsRule(head, body);
	}

	/**
	 * Converts a {@link List} of {@link fr.lirmm.graphik.graal.api.core.Rule Graal
	 * Rules} into a {@link List} of {@link Rule VLog4J Rules}.
	 *
	 * @param rules A {@link List} of {@link fr.lirmm.graphik.graal.api.core.Rule
	 *              Graal Rules}.
	 * @return A {@link List} of {@link Rule VLog4J Rules}.
	 */
	public static List<Rule> convertRules(final List<fr.lirmm.graphik.graal.api.core.Rule> rules) {
		final List<Rule> result = new ArrayList<>();
		for (final fr.lirmm.graphik.graal.api.core.Rule rule : rules) {
			result.add(convertRule(rule));
		}
		return result;
	}

	/**
	 * Converts a {@link fr.lirmm.graphik.graal.api.core.Term Graal Term} into a
	 * {@link Term VLog4J Term}. If the {@code term} is neither
	 * {@link fr.lirmm.graphik.graal.api.core.Term#isVariable() Variable} nor
	 * {@link fr.lirmm.graphik.graal.api.core.Term#isConstant() Constant}, a
	 * {@link GraalConvertException} is thrown.
	 *
	 * @param term A {@link fr.lirmm.graphik.graal.api.core.Term Graal Term.
	 * @return A {@link Term VLog4J Term}, with {@link Term#getName()} equal to
	 *         {@link fr.lirmm.graphik.graal.api.core.Term#getIdentifier()}, if it
	 *         is a Variable, and {@link Term#getName()} equal to
	 *         <{@link fr.lirmm.graphik.graal.api.core.Term#getIdentifier()}>, if it
	 *         is a Constant. <br>
	 *         Graal Variable with identifier <b>"a"</b> will be transformed to
	 *         vlog4j Variable with name <b>"a"</b>. Graal Constant with identifier
	 *         <b>"c"</b> will be transformed to vlog4j Constant with name
	 *         <b>"&lt;c&gt;"</b>.
	 * 
	 * @throws GraalConvertException If the term is neither variable nor constant.
	 */
	private static Term convertTerm(final fr.lirmm.graphik.graal.api.core.Term term) {
		final String id = term.getIdentifier().toString();
		if (term.isConstant()) {
			return makeConstant(id);
		} else if (term.isVariable()) {
			return makeVariable(id);
		} else {
			throw new GraalConvertException(MessageFormat.format(
					"Term {0} with identifier {1} and label {2} could not be converted because it is neither constant nor variable.",
					term, id, term.getLabel()));
		}
	}

	/**
	 * Converts a {@link List} of {@link fr.lirmm.graphik.graal.api.core.Term Graal
	 * Terms} into a {@link List} of {@link Term VLog4J Terms}.
	 *
	 * @param terms A {@link List} of {@link fr.lirmm.graphik.graal.api.core.Term
	 *              Graal Terms}
	 * @return A {@link List} of {@link Term VLog4J Terms}
	 */
	private static List<Term> convertTerms(final List<fr.lirmm.graphik.graal.api.core.Term> terms) {
		final List<Term> result = new ArrayList<>();
		for (final fr.lirmm.graphik.graal.api.core.Term term : terms) {
			result.add(convertTerm(term));
		}
		return result;
	}
}
