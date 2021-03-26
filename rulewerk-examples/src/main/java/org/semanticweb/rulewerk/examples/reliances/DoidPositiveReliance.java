package org.semanticweb.rulewerk.examples.reliances;

/*-
 * #%L
 * Rulewerk Examples
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

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.implementation.ConjunctionImpl;
import org.semanticweb.rulewerk.core.model.implementation.RuleImpl;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.examples.ExamplesUtils;
import org.semanticweb.rulewerk.parser.RuleParser;
import org.semanticweb.rulewerk.reliances.Reliance;

public class DoidPositiveReliance {

	static private Rule addHeadAtom(Rule rule, PositiveLiteral literal) {
		List<PositiveLiteral> head = new ArrayList<>();
		rule.getHead().getLiterals().forEach(pl -> head.add(pl));
		head.add(literal);
		return new RuleImpl(new ConjunctionImpl<PositiveLiteral>(head), rule.getBody());
	}

	static private Rule addBodyAtom(Rule rule, Literal literal) {
		List<Literal> body = new ArrayList<>();
		rule.getBody().getLiterals().forEach(l -> body.add(l));
		body.add(literal);
		return new RuleImpl(rule.getHead(), new ConjunctionImpl<Literal>(body));
	}

	static public void main(String args[]) throws Exception {
		KnowledgeBase kb = RuleParser.parse(new FileInputStream(ExamplesUtils.INPUT_FOLDER + "/doid.rls"));

		HashMap<Integer, Rule> rules = new HashMap<>();
		kb.getRules().forEach(rule -> rules.put(rules.size(), rule));

		System.out.println("Rules used in this example:");
		for (int i = 0; i < rules.size(); i++) {
			System.out.println(i + ": " + rules.get(i));
		}

		List<int[]> positiveDependency = new ArrayList<>();
		for (int i = 0; i < rules.size(); i++) {
			for (int j = 0; j < rules.size(); j++) {
				if (Reliance.positively(rules.get(i), rules.get(j))) {
					positiveDependency.add(new int[] { i, j });
				}
			}
		}

		KnowledgeBase kb2 = new KnowledgeBase();
		for (int i = 0; i < positiveDependency.size(); i++) {
			String name = "newPredicateName";
			Fact fact = RuleParser.parseFact(name + i + "(1).");
			PositiveLiteral literal1 = RuleParser.parsePositiveLiteral(name + i + "(1)");
			Literal literal2 = RuleParser.parseLiteral("~" + name + i + "(2)");

			int[] pair = positiveDependency.get(i);
			if (pair[0] != pair[1]) {
				kb2.addStatement(fact);
				Rule r1 = addHeadAtom(rules.get(pair[0]), literal1);
				System.out.println(r1);
				rules.replace(pair[0], addHeadAtom(rules.get(pair[0]), literal1));
				rules.replace(pair[1], addBodyAtom(rules.get(pair[1]), literal2));
			}
		}

		for (int i = 0; i < rules.size(); i++) {
			kb2.addStatement(rules.get(i));
		}

		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		kb2.getFacts().forEach(System.out::println);
		kb2.getRules().forEach(System.out::println);
	}
}
