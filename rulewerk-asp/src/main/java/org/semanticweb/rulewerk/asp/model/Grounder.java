package org.semanticweb.rulewerk.asp.model;

/*
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

import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.StatementVisitor;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * A grounder is used to transform an ASP program with variables into an ground program, i.e., a program without
 * variables.
 *
 * @author Philipp Hanisch
 */
public interface Grounder extends StatementVisitor<Boolean> {

	/**
	 * Grounds the knowledge base. Returns true if the grounding was successful.
	 *
	 * @return whether the grounding was successful
	 */
	boolean ground() throws IOException;

	/**
	 * Grounds the knowledge base. Returns true if the grounding was successful.
	 *
	 * @param stringRepresentation whether the literals should be written as strings (otherwise integers are used)
	 * @param predicates the predicates for which the literals should be added to the grounding,
	 *                      only if stringRepresentation is requested
	 * @return whether the grounding was successful
	 */
	boolean ground(boolean stringRepresentation, Set<Predicate> predicates) throws IOException;

	/**
	 * During grounding integers might be used to represent literals in a short way. This function gets the map that
	 * contains this integer-to-literal mapping, used by the grounder.
	 *
	 * @return the integer-to-literal map
	 */
	Map<Integer, Literal> getIntegerLiteralMap();
}
