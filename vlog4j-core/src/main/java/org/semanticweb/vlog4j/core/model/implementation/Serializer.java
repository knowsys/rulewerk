package org.semanticweb.vlog4j.core.model.implementation;

import java.util.List;

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
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.Variable;

/**
 * Simple class implementation of various toString methods to ensure the correct
 * parsable string output of the different Data models.
 * 
 * @author Ali Elhalawati
 *
 */
public final class Serializer {

	private Serializer() {

	}

	public static String getRuleString(RuleImpl rule) {
		return rule.getHead() + " :- " + rule.getBody() + ".";
	}

	public static String getLiteralString(AbstractLiteralImpl literal) {
		final StringBuilder stringBuilder = new StringBuilder("");
		if (literal.isNegated()) {
			stringBuilder.append("~");
		}
		stringBuilder.append(literal.getPredicate().getName()).append("(");
		boolean first = true;
		for (final Term term : literal.getArguments()) {
			if (first) {
				first = false;
			} else {
				stringBuilder.append(", ");
			}
			stringBuilder.append(term);
		}
		stringBuilder.append(")");
		return stringBuilder.toString();
	}

	public static String getFactString(FactImpl fact) {
		return fact.toString() + ".";
	}

	public static String getConstantString(AbstractConstantImpl constant) {
		return constant.getName();
	}

	public static String getExistentialVarString(ExistentialVariableImpl existentialvariable) {
		return "!" + existentialvariable.getName();
	}

	public static String getUniversalVarString(UniversalVariableImpl universalvariable) {
		return "?" + universalvariable.getName();
	}

	public static String getDatatypeConstantString(DatatypeConstantImpl datatypeconstant) {
		return datatypeconstant.getName();
	}

	public static String getNamedNullString(NamedNullImpl namednull) {
		return "_" + namednull.getName();
	}

	public static String getLanguageConstantString(LanguageStringConstantImpl languagestringconstant) {
		return languagestringconstant.getName();
	}

	public static String getPredicateString(Predicate predicate) {
		return " Predicate [ name= " + predicate.getName() + ", arity= " + predicate.getArity() + "]";
	}

}
