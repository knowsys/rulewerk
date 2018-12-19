package org.semanticweb.vlog4j.graal;

import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeAtom;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConjunction;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePredicate;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeRule;

import java.util.List;
import java.util.Set;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Blank;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.implementation.VLogReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;

/**
 * A utility class containing a {@link ConjunctiveQuery Graal ConjunctiveQuery}.
 * To use this with the {@link Reasoner}, add the {@code rule} from {@link #getRule()} via {@link Reasoner#addRules(Rule...)}
 * and use the {@code query} from {@link #getQuery()} in {@link Reasoner#answerQuery(Atom, boolean)}. 
 * @author adrian
 */
public class ImportedGraalQuery {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ImportedGraalQuery.class);
	
	Rule rule;
	
	Atom query;
	
	boolean ruleAccessed = false;
	
	protected ImportedGraalQuery(String identifier, List<Term> answerVariables, Conjunction conjunction) {
		Predicate answerPredicate = makePredicate(identifier, answerVariables.size());
		query = makeAtom(answerPredicate, answerVariables);
		rule = makeRule(makeConjunction(query), conjunction);
	}
	
	public Rule getRule() {
		ruleAccessed = true;
		return rule;
	}
	
	public Atom getQuery() {
		LOGGER.warn("Acessing imported graal query without accessing imported rule. The rule needs to be added to the reasoner to obtain results!");
		return query;
	}
}
