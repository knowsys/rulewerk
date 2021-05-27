package org.semanticweb.rulewerk.utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;

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

import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.logic.Substitution;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.logic.Substitute;

public class RuleUtil {

	static public boolean isApplicable(Rule rule) throws ParsingException, IOException {
		List<Fact> instance = Transform.intoFacts(Transform.uni2cons(rule.getPositiveBodyLiterals().getLiterals()));
		List<PositiveLiteral> query = Transform
				.intoPositiveLiterals(Transform.exi2uni(Transform.uni2cons(rule.getHead().getLiterals())));
		return !BCQA.query2(instance, query);
	}

	/*
	 * True if a rule contains any repeated literal
	 *
	 * TODO This should be in RuleImpl and/or RuleParser.
	 */
	static public boolean containsRepeatedAtoms(Rule rule) {
		Set<Literal> literals = new HashSet<>();

		for (Literal l : rule.getBody().getLiterals()) {
			if (literals.contains(l)) {
				return true;
			} else {
				literals.add(l);
			}
		}

		for (Literal l : rule.getHead().getLiterals()) {
			if (literals.contains(l)) {
				return true;
			} else {
				literals.add(l);
			}
		}
		return false;
	}

	/*
	 * Append a suffix to every variable name. The suffix is a dash and the hashCode
	 * of the Rule.
	 */
	static public Rule renameVariablesWithSufix(Rule rule, int n) {
		String suffix = "-" + n;
		Substitution s = new Substitution();
		rule.getVariables().forEach(var -> {
			String newName = var.getName() + suffix;
			s.add(var, var.isExistentialVariable() ? Expressions.makeExistentialVariable(newName)
					: Expressions.makeUniversalVariable(newName));
		});
		return Substitute.rule(s, rule);
	}

}
