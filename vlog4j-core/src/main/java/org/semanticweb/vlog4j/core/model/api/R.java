package org.semanticweb.vlog4j.core.model.api;

import java.util.Set;

/**
 * Interface for classes representing a rule. This implementation assumes that
 * rules are defined by their head and body literals, without explicitly specifying
 * quantifiers. All variables in the body are considered universally quantified;
 * all variables in the head that do not occur in the body are considered
 * existentially quantified.
 * 
 * @author Markus Krötzsch
 *
 */
public interface R {
	
	/**
	 * Returns the conjunction of head literals (the consequence of the rule).
	 *
	 * @return conjunction of literals
	 */
	Conj<PositiveLiteral> getHead();

	/**
	 * Returns the conjunction of body literals (the premise of the rule).
	 *
	 * @return conjunction of literals
	 */
	Conj<Literal> getBody();

	/**
	 * Returns the existentially quantified head variables of this rule.
	 *
	 * @return a set of variables
	 */
	Set<Variable> getExistentiallyQuantifiedVariables();

	/**
	 * Returns the universally quantified variables of this rule.
	 *
	 * @return a set of variables
	 */
	Set<Variable> getUniversallyQuantifiedVariables();

}
