package org.semanticweb.vlog4j.graal;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;

/**
 * A utility class containing a {@link ConjunctiveQuery Graal ConjunctiveQuery}.
 * Answering a {@link ConjunctiveQuery Graal ConjunctiveQuery} is equivalent to
 * adding a {@link Rule} with the query atoms as the body and a single atom with
 * a new predicate containing all the query variables as the head. This rule
 * head can then be used as a query atom to obtain the results of the Graal
 * ConjunctiveQuery.
 * 
 * @author Adrian Bielefeldt
 */
public class GraalConjunctiveQueryToRule {
	
	private static Logger LOGGER = LoggerFactory.getLogger(GraalConjunctiveQueryToRule.class);
	
	private final Rule rule;
	
	private final Atom query;
	
	private boolean ruleAccessed = false;
	
	protected GraalConjunctiveQueryToRule(final String ruleHeadPredicateName, final List<Term> answerVariables,
			final Conjunction conjunction) {
		final Predicate answerPredicate = makePredicate(ruleHeadPredicateName, answerVariables.size());
		query = makeAtom(answerPredicate, answerVariables);
		rule = makeRule(makeConjunction(query), conjunction);
	}

	/**
	 * A rule that needs to be added to the program to answer the
	 * {@link ConjunctiveQuery Graal ConjunctiveQuery} represented by this object.
	 * It consists of all query atoms from the original Graal ConjunctiveQuery as
	 * the body and a single atom containing all the query variables as the head.
	 * 
	 * @return The rule equivalent to the Graal ConjunctiveQuery represented by this
	 *         object.
	 */
	public Rule getRule() {
		ruleAccessed = true;
		return rule;
	}
	
	/**
	 * A query atom that returns the results of the {@link ConjunctiveQuery Graal
	 * ConjunctiveQuery} represented by this object, provided the corresponding
	 * {@link #getRule() Rule} has been added to the program. It is equal to the
	 * head of that Rule.
	 * 
	 * @return The query atom to obtain the results of the Graal ConjunctiveQuery
	 *         represented by this object.
	 */
	public Atom getQueryAtom() {
		if (!ruleAccessed) {
			LOGGER.warn(
					"Acessing converted graal query without accessing converted rule. The rule needs to be added to the reasoner to obtain results!");
		}
		return query;
	}
}
