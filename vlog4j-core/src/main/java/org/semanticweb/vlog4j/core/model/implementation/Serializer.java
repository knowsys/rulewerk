package org.semanticweb.vlog4j.core.model.implementation;

/*-
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 - 2019 VLog4j Developers
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

import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;

/**
 * Simple class implementation of various toString methods to ensure the correct
 * parsable string output of the different Data models.
 * 
 * @author Ali Elhalawati
 *
 */
public class Serializer {

	public Serializer() {

	}

	public static String getConjunctionString(Conjunction<Literal> conjunction) {
		return conjunction.toString();
	}

	public static String getConstantString(Constant constant) {
		return constant.toString();
	}

	public static String getPredicateString(Predicate predicate) {
		return predicate.toString();
	}

	public static String getVariableString(Variable variable) {
		return variable.toString();
	}

	public static String getRuleString(Rule rule) {
		return rule.toString() + " .";
	}

	public static String getLiteralString(Literal literal) {
		return literal.toString();
	}

	public static String getFactString(Fact fact) {
		return fact.toString() + ".";
	}

}
