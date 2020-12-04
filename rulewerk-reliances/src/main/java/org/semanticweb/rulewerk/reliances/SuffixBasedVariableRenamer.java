package org.semanticweb.rulewerk.reliances;

/*-
 * #%L
 * Rulewerk Reliances
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

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.TermType;

public class SuffixBasedVariableRenamer {

	/**
	 * String to concatenate the old variable name with the suffix.
	 */
	static String splitter = "-";

	/**
	 * Rename all the variables in the rule by concatenating the old variable name
	 * with {@code splitter} and suffix.
	 * 
	 * @param rule which its variables are going to be renamed.
	 * @param idx  suffix to concatenate to the variable names
	 * @return new Rule with renamed variable names.
	 */
	static public Rule rename(Rule rule, int suffix) {
		return rename(rule, String.valueOf(suffix));
	}

	/**
	 * Rename all the variables in the rule with the concatenation of old variable
	 * name, {@code splitter}, and suffix.
	 * 
	 * @param rule which its variables are going to be renamed.
	 * @param idx  suffix to concatenate to the variable names
	 * @return new Rule with renamed variable names.
	 */
	static public Rule rename(Rule rule, String suffix) {
		List<Literal> newBody = new ArrayList<>();
		rule.getBody().forEach(literal -> newBody.add(rename(literal, suffix)));

		List<PositiveLiteral> newHead = new ArrayList<>();
		rule.getHead().forEach(literal -> newHead.add((PositiveLiteral) rename(literal, suffix)));

		return Expressions.makeRule(Expressions.makeConjunction(newHead), Expressions.makeConjunction(newBody));
	}

	/**
	 * Rename all the variables present in literal with the concatenation of old
	 * variable name, {@code splitter}, and suffix.
	 * 
	 * @param literal which its variables are going to be renamed.
	 * @param idx     suffix to concatenate to the variable names (after
	 *                {@code splitter})
	 * @return a new Literal with renamed variables.
	 */
	static private Literal rename(Literal literal, String suffix) {
		List<Term> newTerms = new ArrayList<>();
		for (Term term : literal.getArguments()) {
			newTerms.add(rename(term, suffix));
		}
		if (literal.isNegated()) {
			return Expressions.makeNegativeLiteral(literal.getPredicate(), newTerms);
		} else {
			return Expressions.makePositiveLiteral(literal.getPredicate(), newTerms);
		}
	}

	/**
	 * If the term is a variable, then rename it with the concatenation of old name,
	 * {@code splitter}, and suffix.
	 * 
	 * @param term to be renamed
	 * @param idx  suffix to concatenate to the variable names
	 * @return a renamed Term, if it is variable, or a constant with the same name.
	 */
	static private Term rename(Term term, String suffix) {
		if (term.getType() == TermType.UNIVERSAL_VARIABLE) {
			return Expressions.makeUniversalVariable(term.getName() + splitter + suffix);
		} else if (term.getType() == TermType.EXISTENTIAL_VARIABLE) {
			return Expressions.makeExistentialVariable(term.getName() + splitter + suffix);
		} else {
			return term;
		}
	}

}
