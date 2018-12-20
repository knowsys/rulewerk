package org.semanticweb.vlog4j.graal;

import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeAtom;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConjunction;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePredicate;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeRule;

import java.util.List;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;

/**
 * A utility class containing a {@link ConjunctiveQuery Graal ConjunctiveQuery}.
 * To use this with the {@link Reasoner}, add the {@code rule} from
 * {@link #getRule()} via {@link Reasoner#addRules(Rule...)} and use the
 * {@code query} from {@link #getQueryAtom()} in
 * {@link Reasoner#answerQuery(Atom, boolean)}.
 * 
 * @author Adrian Bielefeldt
 */
public class ConjunctiveGraalQueryToRule {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ConjunctiveGraalQueryToRule.class);
	
	final Rule rule;
	
	final Atom query;
	
	boolean ruleAccessed = false;
	
	protected ConjunctiveGraalQueryToRule(final String identifier, final List<Term> answerVariables, final Conjunction conjunction) {
		final Predicate answerPredicate = makePredicate(identifier, answerVariables.size());
		query = makeAtom(answerPredicate, answerVariables);
		rule = makeRule(makeConjunction(query), conjunction);
	}
	
	public Rule getRule() {
		ruleAccessed = true;
		return rule;
	}
	
	public Atom getQueryAtom() {
		if (!ruleAccessed) {
			LOGGER.warn(
					"Acessing converted graal query without accessing converted rule. The rule needs to be added to the reasoner to obtain results!");
		}
		return query;
	}
}
