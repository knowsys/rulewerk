/**
 * 
 */
package org.semanticweb.vlog4j.graal;

import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeAtom;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConjunction;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConstant;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePredicate;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeRule;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeVariable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * Utility class to convert Graal data structures into VLog4J structures. Labels
 * are not converted since VLog4J does not support them.
 * 
 * @author Adrian Bielefeldt
 *
 */
public final class GraalToVLog4JModelConverter {

	private GraalToVLog4JModelConverter() {
	};

	/**
	 * Converts a {@link fr.lirmm.graphik.graal.api.core.Atom Graal Atom} into a
	 * {@link Atom VLog4J Atom}.
	 * 
	 * @param atom A {@link fr.lirmm.graphik.graal.api.core.Atom Graal Atom}
	 * @return A {@link Atom VLog4J Atom}
	 */
	public static Atom convertAtom(final fr.lirmm.graphik.graal.api.core.Atom atom) {
		final Predicate predicate = convertPredicate(atom.getPredicate());
		final List<Term> terms = convertTerms(atom.getTerms());
		return makeAtom(predicate, terms);
	}

	/**
	 * Converts a {@link List} of {@link fr.lirmm.graphik.graal.api.core.Atom Graal
	 * Atoms} into a {@link List} of {@link Atom VLog4J Atoms}.
	 * 
	 * @param atoms A {@link List} of {@link fr.lirmm.graphik.graal.api.core.Atom
	 *              Graal Atoms}.
	 * @return A {@link List} of {@link Atom VLog4J Atoms}.
	 */
	public static List<Atom> convertAtoms(final List<fr.lirmm.graphik.graal.api.core.Atom> atoms) {
		final List<Atom> result = new ArrayList<>();
		for (final fr.lirmm.graphik.graal.api.core.Atom atom : atoms) {
			result.add(convertAtom(atom));
		}
		return result;
	}

	/**
	 * Converts a {@link AtomSet Graal AtomSet} into a {@link Conjunction VLog4J
	 * Conjunction}.
	 * 
	 * @param atomSet A {@link AtomSet Graal Atomset}
	 * @return A {@link Conjunction VLog4J Conjunction}
	 */
	private static Conjunction convertAtomSet(final AtomSet atomSet) {
		final List<Atom> result = new ArrayList<>();
		try (CloseableIterator<fr.lirmm.graphik.graal.api.core.Atom> iterator = atomSet.iterator()) {
			while (iterator.hasNext()) {
				result.add(convertAtom(iterator.next()));
			}
		} catch (final IteratorException e) {
			throw new GraalConvertException(
					MessageFormat.format("Unexpected Iterator Exception when converting AtomSet {0}}.", atomSet), e);
		}
		return makeConjunction(result);
	}

	/**
	 * Converts a {@link fr.lirmm.graphik.graal.api.core.Constant Graal Constant}
	 * into a {@link Constant VLog4J Constant}.
	 * 
	 * @param constant A {@link fr.lirmm.graphik.graal.api.core.Constant Graal
	 *                 Constant}
	 * @return A {@link Constant VLog4J Constant}
	 */
	private static Constant convertConstant(final fr.lirmm.graphik.graal.api.core.Constant constant) {
		return makeConstant(constant.getIdentifier().toString());
	}

	/**
	 * Converts a {@link Set} of {@link fr.lirmm.graphik.graal.api.core.Constant
	 * Graal Constants} into a {@link Set} of {@link Constant VLog4J Constants}.
	 * 
	 * @param constants {@link Set} of
	 *                  {@link fr.lirmm.graphik.graal.api.core.Constant Graal
	 *                  Constants}
	 * @return {@link Set} of {@link Constant VLog4J Constants}
	 */
	@SuppressWarnings("unused")
	private static Set<Constant> convertConstants(final Set<fr.lirmm.graphik.graal.api.core.Constant> constants) {
		final Set<Constant> result = new HashSet<>();
		for (final fr.lirmm.graphik.graal.api.core.Constant constant : constants) {
			result.add(convertConstant(constant));
		}
		return result;
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
	 * Converts a {@link ConjunctiveQuery Graal Query} into a
	 * {@link ImportedGraalQuery}. To use this with the {@link Reasoner}, add the
	 * {@code rule} from {@link ImportedGraalQuery#getRule()} as a Rule via
	 * {@link Reasoner#addRules(Rule...)} and use it as the Atom for
	 * {@link Reasoner#answerQuery(Atom, boolean)}.
	 * 
	 * <p>
	 * <b>WARNING</b>: The supplied {@code identifier} will be used to create a
	 * predicate containing all answer variables from the {@code query}. If you use
	 * this identifier in another predicate, you will get conflicts.
	 * </p>
	 * 
	 * @param identifier
	 * @param query
	 * @return
	 */
	public static ImportedGraalQuery convertQuery(final String identifier, final ConjunctiveQuery query) {
		final Conjunction conjunction = convertAtomSet(query.getAtomSet());
		final List<Term> answerVariables = convertTerms(query.getAnswerVariables());

		return new ImportedGraalQuery(identifier, answerVariables, conjunction);
	}

	/**
	 * Converts a {@link fr.lirmm.graphik.graal.api.core.Rule Graal Rule} into a
	 * {@link Rule Vlog4J Rule}.
	 * 
	 * @param rule A {@link fr.lirmm.graphik.graal.api.core.Rule Graal Rule}
	 * @return A {@link Rule Vlog4J Rule}
	 */
	public static Rule convertRule(final fr.lirmm.graphik.graal.api.core.Rule rule) {
		final Conjunction head = convertAtomSet(rule.getHead());
		final Conjunction body = convertAtomSet(rule.getBody());
		return makeRule(head, body);
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
			;
		}
		return result;
	}

	/**
	 * Converts a {@link fr.lirmm.graphik.graal.api.core.Term Graal Term} into a
	 * {@link Term VLog4J Term}. Tests if the term is a
	 * {@link fr.lirmm.graphik.graal.api.core.Term#isVariable() Variable} or
	 * {@link fr.lirmm.graphik.graal.api.core.Term#isConstant() Constant} and
	 * converts accordingly. Throws a {@link GraalConvertException} if it is neither.
	 * 
	 * @param term A {@link fr.lirmm.graphik.graal.api.core.Term Graal Term}
	 * @return A {@link Term VLog4J Term}
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

	/**
	 * Converts a {@link fr.lirmm.graphik.graal.api.core.Variable Graal Variable}
	 * into a {@link Variable VLog4J Variable}.
	 * 
	 * @param variable A {@link fr.lirmm.graphik.graal.api.core.Variable Graal
	 *                 Variable}
	 * @return A {@link Variable VLog4J Variable}
	 */
	private static Variable convertVariable(final fr.lirmm.graphik.graal.api.core.Variable variable) {
		return makeVariable(variable.getIdentifier().toString());
	}

	/**
	 * Converts a {@link Set} of {@link fr.lirmm.graphik.graal.api.core.Variable
	 * Graal Variables} into a {@link Set} of {@link Variable VLog4J Variables}.
	 * 
	 * @param variables A {@link Set} of
	 *                  {@link fr.lirmm.graphik.graal.api.core.Variable Graal
	 *                  Variables}
	 * @return A {@link Set} of {@link Variable VLog4J Variables}
	 */
	@SuppressWarnings("unused")
	private static Set<Variable> convertVariables(final Set<fr.lirmm.graphik.graal.api.core.Variable> variables) {
		final Set<Variable> result = new HashSet<>();
		for (final fr.lirmm.graphik.graal.api.core.Variable variable : variables) {
			result.add(convertVariable(variable));
		}
		return result;
	}
}
