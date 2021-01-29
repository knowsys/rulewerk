package org.semanticweb.rulewerk.core.model.api;

import java.util.Set;

/*-
 * #%L
 * Rulewerk Core Components
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

/**
 * Interface for classes representing a rule. This implementation assumes that
 * rules are defined by their head and body literals, without explicitly
 * specifying quantifiers. All variables in the body are considered universally
 * quantified; all variables in the head that do not occur in the body are
 * considered existentially quantified.
 * 
 * @author Markus Krötzsch
 * @author Larry González
 *
 */
public interface Rule extends SyntaxObject, Statement {

	/**
	 * Returns the conjunction of head literals (the consequence of the rule).
	 *
	 * @return conjunction of literals
	 */
	Conjunction<PositiveLiteral> getHead();

	/**
	 * Returns the conjunction of body literals (the premise of the rule).
	 *
	 * @return conjunction of literals
	 */
	Conjunction<Literal> getBody();

	/**
	 * Returns the conjunction of positive body literals.
	 *
	 * @return conjunction of literals
	 */
	Conjunction<PositiveLiteral> getPositiveBodyLiterals();

	/**
	 * Returns the conjunction of negative body literals.
	 *
	 * @return conjunction of literals
	 */
	Conjunction<Literal> getNegativeBodyLiterals();

	/**
	 * Returns the list of pieces in the head of the rule.
	 *
	 * @return List of Piece
	 */
	Set<Piece> getPieces();

	/**
	 * @see {@code Piece.isUnconnected}
	 *
	 * @return True if the rule contains an unconnected piece.
	 */
	boolean containsUnconnectedPieces();

}
