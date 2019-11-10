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
import org.semanticweb.vlog4j.core.model.api.DataSourceDeclaration;
import org.semanticweb.vlog4j.core.model.api.DatatypeConstant;
import org.semanticweb.vlog4j.core.model.api.ExistentialVariable;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.LanguageStringConstant;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.NamedNull;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.UniversalVariable;
import org.semanticweb.vlog4j.core.model.api.Variable;

/**
 * A utility class with static methods to obtain the correct parsable string
 * representation of the different data models.
 * 
 * @author Ali Elhalawati
 *
 */
public final class Serializer {

	private Serializer() {

	}

	public static String getRuleString(Rule rule) {
		return getConjunctionString(rule.getHead()) + " :- " + getConjunctionString(rule.getBody()) + ".";
	}

	public static String getLiteralString(Literal literal) {
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

	public static String getConstantString(Constant constant) {
		return constant.getName();
	}

	public static String getExistentialVarString(ExistentialVariable existentialVariable) {
		return "!" + existentialVariable.getName();
	}

	public static String getUniversalVarString(UniversalVariable universalVariable) {
		return "?" + universalVariable.getName();
	}

	public static String getNamedNullString(NamedNull namedNull) {
		return "_" + namedNull.getName();
	}

	public static String getPredicateString(Predicate predicate) {
		return " Predicate [ name= " + predicate.getName() + ", arity= " + predicate.getArity() + "]";
	}

	public static String getDataSourceDeclarationString(DataSourceDeclaration dataSourceDeclaration) {
		return "@source " + dataSourceDeclaration.getPredicate().getName() + "("
				+ dataSourceDeclaration.getPredicate().getArity() + ") : "
				+ dataSourceDeclaration.getDataSource().toConfigString() + " .";
	}

	public static String getConjunctionString(Conjunction<? extends Literal> conjunction) {
		final StringBuilder stringBuilder = new StringBuilder();
		boolean first = true;
		for (final Literal literal : conjunction.getLiterals()) {
			if (first) {
				first = false;
			} else {
				stringBuilder.append(", ");
			}
			stringBuilder.append(getLiteralString(literal));
		}
		return stringBuilder.toString();
	}

}
