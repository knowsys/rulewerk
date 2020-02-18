package org.semanticweb.vlog4j.examples;

import java.io.BufferedReader;

/*-
 * #%L
 * VLog4j Examples
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.NegativeLiteral;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.QueryResult;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.QueryResultIterator;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.implementation.VLogReasoner;
import org.semanticweb.vlog4j.parser.ParsingException;
import org.semanticweb.vlog4j.parser.RuleParser;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class Crossword2 {

	// change the path of the input file as appropriate
	final static String baseDir = "/mnt/c/Users/Ali Elhalawati/files/strategic";
	static int i = 0;
	static int k = 0;
	static int j = 3;
	public static HashSet<Rule> domainRestrictedRules = new HashSet<Rule>();
	public static HashSet<Rule> disjunctiveHeadedRules = new HashSet<Rule>();
	public static HashSet<Rule> disjunctiveLiteralsRules = new HashSet<Rule>();
	public static HashSet<Predicate> nDomainpredicates = new HashSet<Predicate>();
	public static ArrayList<Rule> auxiliaryRules = new ArrayList<Rule>();
	public static ArrayList<Rule> auxiliaryDomainLiteralsFreeRules = new ArrayList<Rule>();
	public static HashSet<Literal> constraintLiterals = new HashSet<Literal>();
	public static HashSet<Literal> disjunctiveLiterals = new HashSet<Literal>();
	public static HashSet<Literal> negatedLiterals = new HashSet<Literal>();
	public static HashSet<Rule> disjunctiveDomainBodiedRule = new HashSet<Rule>();
	public static BiMap<Literal, Integer> factId = HashBiMap.create();

	public static int getAuxiliaryCount() {
		int j = i;
		i++;
		return j;
	}

	public static int getConstraintCount() {
		int j = k;
		k++;
		return j;
	}

	public static boolean checkNegativeDependency(Literal l1, Literal l2, KnowledgeBase kb) {
		boolean check1 = false;
		boolean check2 = false;
		NegativeLiteral negativel1 = Expressions.makeNegativeLiteral(l1.getPredicate(), l1.getTerms());
		NegativeLiteral negativel2 = Expressions.makeNegativeLiteral(l2.getPredicate(), l2.getTerms());
		if (negativel1.getPredicate().getName().equals(negativel2.getPredicate().getName())
				&& (negativel1.getPredicate().getArity() == negativel2.getPredicate().getArity())) {
			if (getNegativeLiterals(kb).contains(negativel2)) {
				negativel1 = negativel2;
			} else {
				negativel2 = negativel1;
			}

		}
		for (Rule r1 : kb.getRules()) {
			for (Literal literal : r1.getHead().getLiterals()) {
				if ((literal.getPredicate().getName().equals(l1.getPredicate().getName()))
						&& ((r1.getBody().getLiterals().contains(l2))
								|| (r1.getBody().getLiterals().contains(negativel2)))) {
					check1 = true;
				}
			}
		}
		for (Rule r2 : kb.getRules()) {
			for (Literal literal : r2.getHead().getLiterals()) {
				if ((literal.getPredicate().getName().equals(l2.getPredicate().getName()))
						&& ((r2.getBody().getLiterals().contains(l1))
								|| (r2.getBody().getLiterals().contains(negativel1)))) {
					check2 = true;
				}
			}
		}
		return (check1 & check2);
	}

	public static HashSet<Literal> getNegativeLiterals(KnowledgeBase kb) {
		HashSet<Literal> negativeLiterals = new HashSet<Literal>();
		for (Rule r : kb.getRules()) {
			for (Literal l : r.getBody().getLiterals()) {
				if (l.isNegated()) {
					negativeLiterals.add(l);
				}
			}
		}
		return negativeLiterals;
	}

	public static HashSet<Predicate> domainDifferentiationSimple(KnowledgeBase kb) {
		HashSet<Literal> negativeLiterals = getNegativeLiterals(kb);
		HashSet<Predicate> nDomainPredicates = new HashSet<Predicate>();
		for (Rule r : kb.getRules()) {
			for (Literal l : r.getHead()) {
				for (Literal nl : negativeLiterals) {
					if ((!(nDomainPredicates.contains(l.getPredicate())
							&& (nDomainPredicates.contains(nl.getPredicate()))))
							&& checkNegativeDependency(l, nl, kb)) {
						nDomainPredicates.add(l.getPredicate());
						nDomainPredicates.add(nl.getPredicate());
					}
				}
			}
		}
		return nDomainPredicates;
	}

	public static HashSet<Predicate> domainDiffMain(KnowledgeBase kb, HashSet<Predicate> ndpredicates2) {
		for (Rule r : kb.getRules()) {
			for (Predicate predicate : ndpredicates2) {
				for (Literal literal1 : r.getBody().getLiterals()) {
					if (literal1.getPredicate().equals(predicate)) {
						for (Literal literal2 : r.getHead()) {
							if (!ndpredicates2.contains(literal2.getPredicate())) {
								ndpredicates2.add(literal2.getPredicate());
								return (domainDiffMain(kb, ndpredicates2));
							}
						}
					}

				}
			}
		}
		return ndpredicates2;
	}

	public static KnowledgeBase rewriteNegatedRule(Rule r, KnowledgeBase kb) throws ParsingException {
		ArrayList<Literal> bodyLiterals = new ArrayList<Literal>();
		Set<Term> terms = new HashSet<Term>();
		for (Literal literal : r.getBody().getLiterals()) {
			if (!literal.isNegated()) {
				bodyLiterals.add(literal);
				terms.addAll(literal.getTerms());
			}
		}
		Set<Term> auxiliaryTerms = r.getBody().getTerms();
		Predicate auxiliaryPredicate = Expressions.makePredicate("Rule" + getAuxiliaryCount(), auxiliaryTerms.size());
		PositiveLiteral auxiliaryLiteral = Expressions.makePositiveLiteral(auxiliaryPredicate,
				new ArrayList<Term>(auxiliaryTerms));
		Conjunction<PositiveLiteral> auxiliaryHead = Expressions.makePositiveConjunction(auxiliaryLiteral);
		Conjunction<Literal> auxiliaryBody = Expressions.makeConjunction(auxiliaryLiteral);
		Rule groundingRule = Expressions.makeRule(auxiliaryHead, r.getBody());
		Rule auxiliaryrule1 = Expressions.makeRule(auxiliaryHead, Expressions.makeConjunction(bodyLiterals));
		Rule auxiliaryrule2 = Expressions.makeRule(r.getHead(), auxiliaryBody);
		kb.addStatement(auxiliaryrule1);
		kb.addStatement(auxiliaryrule2);
		auxiliaryRules.add(groundingRule);
		return kb;
	}

	public static boolean domainRulesChecker(Rule r) {
		for (Literal l : r.getBody().getLiterals()) {
			if (nDomainpredicates.contains(l.getPredicate())) {
				return true;

			}
		}
		return false;
	}

	public static Rule rewriteDisjunctiveRule(String DisjunctiveRuleString) throws ParsingException {
		StringBuilder newDisjunctiveRuleString = new StringBuilder(DisjunctiveRuleString.replace("|", ","));
		Rule newDisjunctiveRule = RuleParser.parseRule(newDisjunctiveRuleString.toString());
		disjunctiveHeadedRules.add(newDisjunctiveRule);
		addDisLiteralsNDomainPredicates(newDisjunctiveRule);
		return newDisjunctiveRule;
	}

	public static KnowledgeBase rewriteIntegrityConstraint(String integrityConstraintString, KnowledgeBase kb)
			throws ParsingException {
		StringBuilder trivalHeadedIntegrityRule = new StringBuilder(
				"integrityConstraint(a) " + integrityConstraintString);
		Rule r = RuleParser.parseRule(trivalHeadedIntegrityRule.toString());
		Set<Term> terms = r.getBody().getTerms();
		Predicate auxiliaryIntegrityPredicate = Expressions.makePredicate("Constraint" + getConstraintCount(),
				terms.size());
		PositiveLiteral auxiliaryIntegrityLiteral = Expressions.makePositiveLiteral(auxiliaryIntegrityPredicate,
				new ArrayList<Term>(terms));
		Conjunction<PositiveLiteral> auxiliaryIntegrityConjunction = Expressions
				.makePositiveConjunction(auxiliaryIntegrityLiteral);
		Rule auxiliaryIntegrityRule = Expressions.makeRule(auxiliaryIntegrityConjunction, r.getBody());
		kb.addStatement(auxiliaryIntegrityRule);
		constraintLiterals.add(auxiliaryIntegrityLiteral);
		return kb;

	}

	public static KnowledgeBase rewriteRules(KnowledgeBase originalKB, KnowledgeBase rewrittenKB)
			throws ParsingException {
		for (Rule r : originalKB.getRules()) {
			boolean notdomain = false;
			if (domainRulesChecker(r)) {
				for (Literal l : r.getHead().getLiterals()) {
					if (nDomainpredicates.contains(l.getPredicate())) {
						notdomain = true;
						break;
					}
				}
			}
			if (notdomain || disjunctiveHeadedRules.contains(r)) {
				if (r.toString().contains("~")) {
					rewriteNegatedRule(r, rewrittenKB);
				} else {
					rewriteNormalRule(r, rewrittenKB);
				}
			} else {
				rewrittenKB.addStatement(r);
				domainRestrictedRules.add(r);
			}
		}
		return rewrittenKB;
	}

	public static KnowledgeBase rewriteNormalRule(Rule r, KnowledgeBase kb) throws ParsingException {
		Set<Term> ruleTerms = r.getBody().getTerms();
		Predicate auxiliaryPredicate = Expressions.makePredicate("Rule" + getAuxiliaryCount(), ruleTerms.size());
		PositiveLiteral auxiliaryPositiveLiteral = Expressions.makePositiveLiteral(auxiliaryPredicate,
				new ArrayList<Term>(ruleTerms));
		Conjunction<PositiveLiteral> auxiliaryConjunction = Expressions
				.makePositiveConjunction(auxiliaryPositiveLiteral);
		Rule auxiliaryHeadedRule = Expressions.makeRule(auxiliaryConjunction, r.getBody());
		Conjunction<Literal> auxiliaryLiteral = Expressions.makeConjunction(auxiliaryPositiveLiteral);
		Rule auxiliaryBodiedRule = Expressions.makeRule(r.getHead(), auxiliaryLiteral);
		kb.addStatement(auxiliaryHeadedRule);
		kb.addStatement(auxiliaryBodiedRule);
		auxiliaryRules.add(auxiliaryHeadedRule);
		return kb;
	}

	public static void addDisLiteralsNDomainPredicates(Rule disjunctiveHeadRule) {
		for (Literal disjunctiveLiteral : disjunctiveHeadRule.getHead().getLiterals()) {
			Predicate p = disjunctiveLiteral.getPredicate();
			nDomainpredicates.add(p);
			disjunctiveLiterals.add(disjunctiveLiteral);
		}
	}

	public static Literal instantiateLiteral(Literal auxiliaryLiteral, Literal tobeInstantiatedLiteral,
			List<Term> bindings) {
		ArrayList<Term> groundTerms = new ArrayList<Term>();
		ArrayList<Term> tobeGroundedTerms = new ArrayList<Term>(tobeInstantiatedLiteral.getTerms());
		for (Term t : tobeGroundedTerms) {
			groundTerms.add(bindings.get(auxiliaryLiteral.getTerms().indexOf(t)));
		}
		if (tobeInstantiatedLiteral.isNegated()) {
			return Expressions.makeNegativeLiteral(tobeInstantiatedLiteral.getPredicate().getName(), groundTerms);
		} else {
			return Expressions.makePositiveLiteral(tobeInstantiatedLiteral.getPredicate().getName(), groundTerms);
		}
	}

	public static Rule getAuxiliaryBodiedRule(Rule r1, KnowledgeBase kb) {
		for (Rule r : kb.getRules()) {
			if (r1.getHead().equals(r.getBody())) {
				return r;
			}

		}
		return r1;
	}

	public static void writeFactsIDs(FileWriter writer) throws IOException {
		for (Literal f : factId.keySet()) {
			if (!f.isNegated())
				writer.write(factId.get(f) + " " + f.toString() + "\n");
		}
	}

	public static void groundDomainRestrictedProgram(HashSet<Predicate> toBeQueriedHeadPredicates,
			FileWriter groundingsWriter, Reasoner domainRulesReasoner) throws IOException, ParsingException {
		for (Predicate predicate : toBeQueriedHeadPredicates) {
			ArrayList<Term> tobeGroundedVariables = new ArrayList<Term>();
			for (int i = 0; i < predicate.getArity(); i++) {
				tobeGroundedVariables.add(Expressions.makeVariable("?X" + i));
			}
			try (final QueryResultIterator queryAnswers = domainRulesReasoner
					.answerQuery(Expressions.makePositiveLiteral(predicate, tobeGroundedVariables), true)) {
				while (queryAnswers.hasNext()) {
					QueryResult queryAnswer = queryAnswers.next();
					Fact groundedFact = Expressions.makeFact(predicate.getName(), queryAnswer.getTerms());
					if (!factId.containsKey(groundedFact)) {
						factId.put(groundedFact, j);
						groundingsWriter.write("1 " + j + " 0 0" + "\n");
						j++;
					}
				}
			}
		}
	}

	public static void groundNonDomainRestrictedProgram(KnowledgeBase rewrittenKB,
			HashSet<Predicate> toBeQueriedHeadPredicates, FileWriter groundingsWriter, Reasoner domainRulesReasoner)
			throws IOException, ParsingException {
		for (Predicate predicate : toBeQueriedHeadPredicates) {
			ArrayList<Term> tobeGroundedVariables = new ArrayList<Term>();
			for (int i = 0; i < predicate.getArity(); i++) {
				tobeGroundedVariables.add(Expressions.makeVariable("?X" + i));
			}
			try (final QueryResultIterator queryAnswers = domainRulesReasoner
					.answerQuery(Expressions.makePositiveLiteral(predicate, tobeGroundedVariables), true)) {
				while (queryAnswers.hasNext()) {
					QueryResult queryAnswer = queryAnswers.next();
					Fact groundedFact = Expressions.makeFact(predicate.getName(), queryAnswer.getTerms());
					if (!factId.containsKey(groundedFact)) {
						factId.put(groundedFact, j);
						groundingsWriter.write("1 " + j + " 0 0" + "\n");
						j++;
						RuleParser.parseInto(rewrittenKB, groundedFact.toString() + ".");
					}
				}
			}
		}

	}

	public static void groundDomainRestrictedRules(KnowledgeBase rewrittenKB, KnowledgeBase domainRestrictedKB,
			FileWriter groundingsWriter, Reasoner domainRulesReasoner) throws IOException, ParsingException {
		HashSet<Predicate> toBeQueriedHeadPredicates = new HashSet<Predicate>();
		for (Fact fact : rewrittenKB.getFacts()) {
			factId.put(fact, j);
			groundingsWriter.write("1 " + j + " 0 0" + "\n");
			j++;
		}
		for (Rule domainRestrictedRule : domainRestrictedKB.getRules()) {
			for (Literal literal : domainRestrictedRule.getHead()) {
				toBeQueriedHeadPredicates.add(literal.getPredicate());
			}
		}
		if (auxiliaryRules.isEmpty()) {
			groundDomainRestrictedProgram(toBeQueriedHeadPredicates, groundingsWriter, domainRulesReasoner);
		} else {
			groundNonDomainRestrictedProgram(rewrittenKB, toBeQueriedHeadPredicates, groundingsWriter,
					domainRulesReasoner);
		}
	}

	public static void ground(KnowledgeBase rewrittenKB, KnowledgeBase originalKB, String pathfile,
			Reasoner rewrittenRulesReasoner) throws IOException, ParsingException {
		FileWriter groundingWriter = new FileWriter(pathfile);
		KnowledgeBase domainRestrictedKB = new KnowledgeBase();
		domainRestrictedKB.addStatements(domainRestrictedRules);
		domainRestrictedKB.addStatements(rewrittenKB.getFacts());
		Reasoner domainRestrictedReasoner = new VLogReasoner(domainRestrictedKB);
		domainRestrictedReasoner.reason();
		groundDomainRestrictedRules(rewrittenKB, domainRestrictedKB, groundingWriter, domainRestrictedReasoner);
		groundRewrittenRules(rewrittenKB, groundingWriter, rewrittenRulesReasoner);
		groundingWriter.write("0" + "\n");
		writeFactsIDs(groundingWriter);
		groundingWriter
				.write("0" + "\n" + "B+" + "\n" + "0" + "\n" + "B-" + "\n" + "1" + "\n" + "0" + "\n" + "1" + "\n");
		groundingWriter.close();
	}

	public static void removeDomainPredicates(ArrayList<Rule> auxiliaryRules) {
		for (Rule rule : auxiliaryRules) {
			HashSet<Literal> nonDomainLiterals = new HashSet<Literal>(rule.getBody().getLiterals());
			for (Literal literal : rule.getBody().getLiterals()) {
				if (!nDomainpredicates.contains(literal.getPredicate())) {
					nonDomainLiterals.remove(literal);
				}
			}
			Rule DomainLiteralsFreeRules;
			if (!nonDomainLiterals.isEmpty()) {
				ArrayList<Literal> nDomainLiterals = new ArrayList<Literal>(nonDomainLiterals);
				DomainLiteralsFreeRules = Expressions.makeRule(rule.getHead(),
						Expressions.makeConjunction(nDomainLiterals));
			} else {
				disjunctiveDomainBodiedRule.add(rule);
				DomainLiteralsFreeRules = rule;
			}
			auxiliaryDomainLiteralsFreeRules.add(DomainLiteralsFreeRules);

		}
	}

	public static Integer countNegated(List<Integer> facts) {
		int i = 0;
		BiMap<Integer, Literal> inverse = factId.inverse();
		for (int fact : facts) {
			if (negatedLiterals.contains(inverse.get(fact))) {
				i++;
			}
		}
		return i;

	}

	public static void writeRewrittenGroundedRules(Rule auxiliaryBodiedRule, HashSet<Fact> facts, QueryResult bindings,
			List<Integer> headGroundedLiterals, List<Integer> bodyGroundedLiterals, int negated,
			FileWriter groundingsWriter) throws IOException {
		Boolean isFact = false;
		for (Literal tobeGroundedHeadLiteral : auxiliaryBodiedRule.getHead().getLiterals()) {
			if (!constraintLiterals.contains(tobeGroundedHeadLiteral)) {
				PositiveLiteral groundedHeadLiteral = (PositiveLiteral) instantiateLiteral(
						auxiliaryBodiedRule.getBody().getLiterals().get(0), tobeGroundedHeadLiteral,
						bindings.getTerms());
				Fact candidate = Expressions.makeFact(groundedHeadLiteral.getPredicate(),
						groundedHeadLiteral.getTerms());
				if (!facts.contains(candidate)) {
					if (!factId.containsKey(candidate)) {
						factId.put(candidate, j);
						headGroundedLiterals.add(j);
						j++;
					} else {
						headGroundedLiterals.add(factId.get(candidate));
					}

				} else {
					isFact = true;
				}
			}
		}
		if (!bodyGroundedLiterals.isEmpty()) {
			if (!headGroundedLiterals.isEmpty()) {
				// Rule groundedRule =
				// Expressions.makeRule(Expressions.makePositiveConjunction(headGroundedLiterals),
				// Expressions.makeConjunction(bodyGroundedLiterals));
				if (!(headGroundedLiterals.size() > 1)) {
					groundingsWriter.write(
							"1 " + headGroundedLiterals.toString().replace(",", "").replace("[", "").replace("]", "")
									+ " " + bodyGroundedLiterals.size() + " " + negated + " "
									+ bodyGroundedLiterals.toString().replace(",", "").replace("[", "").replace("]", "")
									+ " \n");
				} else {
					groundingsWriter.write("8 " + headGroundedLiterals.size() + " "
							+ headGroundedLiterals.toString().replace(",", "").replace("[", "").replace("]", "") + " "
							+ bodyGroundedLiterals.size() + " " + negated + " "
							+ bodyGroundedLiterals.toString().replace(",", "").replace("[", "").replace("]", "")
							+ " \n");
				}
			} else {
				if (!isFact) {
					groundingsWriter.write("1 1 " + bodyGroundedLiterals.size() + " " + negated + " "
							+ bodyGroundedLiterals.toString().replace(",", "").replace("[", "").replace("]", "")
							+ " \n");
				}
			}
		} else {
			if (!headGroundedLiterals.isEmpty()) {
				Boolean isFact2 = false;
				BiMap<Integer, Literal> inverse = factId.inverse();
				for (int groundedHeadLiteral : headGroundedLiterals) {
					if (facts.contains(inverse.get(groundedHeadLiteral))) {
						isFact2 = true;
						break;
					}
				}
				if (!isFact2) {
					if (!(headGroundedLiterals.size() > 1)) {
						groundingsWriter.write("1 "
								+ headGroundedLiterals.toString().replace(",", "").replace("[", "").replace("]", "")
								+ " " + "0 0" + "\n");
					} else {
						groundingsWriter.write("8 " + headGroundedLiterals.size() + " "
								+ headGroundedLiterals.toString().replace(",", "").replace("[", "").replace("]", "")
								+ " " + "0 0" + "\n");
					}
					for (int fId : headGroundedLiterals) {
						facts.add(Expressions.makeFact(inverse.get(fId).getPredicate(), inverse.get(fId).getTerms()));
					}

				}
			}
		}
	}

	public static void groundRewrittenRules(KnowledgeBase rewrittenRulesKB, FileWriter groundingsWriter,
			Reasoner auxiliaryRulesReasoner) throws IOException {
		auxiliaryRulesReasoner.reason();
		HashSet<Fact> facts = new HashSet<Fact>(rewrittenRulesKB.getFacts());
		removeDomainPredicates(auxiliaryRules);
		for (Rule auxiliaryHeadedRule : auxiliaryDomainLiteralsFreeRules) {
			System.out.println(auxiliaryHeadedRule.toString());
			PositiveLiteral auxiliaryHead = auxiliaryHeadedRule.getHead().getLiterals().get(0);
			List<Literal> auxiliaryBody = auxiliaryHeadedRule.getBody().getLiterals();
			Rule auxiliaryBodiedRule = getAuxiliaryBodiedRule(auxiliaryHeadedRule, rewrittenRulesKB);
			try (final QueryResultIterator queryAnswers = auxiliaryRulesReasoner.answerQuery(auxiliaryHead, true)) {
				while (queryAnswers.hasNext()) {
					int negated = 0;
					List<Integer> bodyGroundedLiterals = new ArrayList<Integer>();
					QueryResult bindings = queryAnswers.next();
					if (!disjunctiveDomainBodiedRule.contains(auxiliaryHeadedRule)) {
						for (Literal tobeinstantiatedliteral : auxiliaryBody) {
							Literal instantiatedLiteral = instantiateLiteral(auxiliaryHead, tobeinstantiatedliteral,
									bindings.getTerms());

							Fact candidate = Expressions.makeFact(instantiatedLiteral.getPredicate().getName(),
									instantiatedLiteral.getTerms());
							if (instantiatedLiteral.isNegated()) {
								// negatedLiterals.add(candidate);
								negated++;

							}
							if (!facts.contains(candidate) || disjunctiveLiterals.contains(tobeinstantiatedliteral)) {
								if (!factId.containsKey(candidate)) {
									factId.put(candidate, j);
									bodyGroundedLiterals.add(j);
									j++;
								} else {
									bodyGroundedLiterals.add(factId.get(candidate));
								}
							}

						}
					}
					List<Integer> headGroundedLiterals = new ArrayList<Integer>();
					writeRewrittenGroundedRules(auxiliaryBodiedRule, facts, bindings, headGroundedLiterals,
							bodyGroundedLiterals, negated, groundingsWriter);
				}
			}
		}
		// groundingsWriter.write("0" + "\n");
		// writeFactsIDs(groundingsWriter);
		// groundingsWriter
		// .write("0" + "\n" + "B+" + "\n" + "0" + "\n" + "B-" + "\n" + "1" + "\n" + "0"
		// + "\n" + "1" + "\n");
		// groundingsWriter.close();

	}

	public static void main(final String[] args) throws IOException, ParsingException, InterruptedException {
		// ExamplesUtils.configureLogging();
		// use simple logger for the example
		KnowledgeBase originalKB = new KnowledgeBase();
		originalKB = RuleParser.parse(new FileInputStream(baseDir + "//strategic.txt"));
		File x = new File(baseDir + "//rules.txt");
		Scanner y = new Scanner(x);
		while (y.hasNextLine()) {
			StringBuilder ruleString = new StringBuilder(y.nextLine());
			if (ruleString.toString().startsWith(":")) {
				rewriteIntegrityConstraint(ruleString.toString(), originalKB);

			} else if (ruleString.toString().contains("|")) {
				originalKB.addStatement(rewriteDisjunctiveRule(ruleString.toString()));
			} else {
				originalKB.addStatement(RuleParser.parseRule(ruleString.toString()));
			}

		}
		y.close();

		nDomainpredicates.addAll(domainDifferentiationSimple(originalKB));
		domainDiffMain(originalKB, nDomainpredicates);
		KnowledgeBase RewrittenKB = RuleParser.parse(new FileInputStream(baseDir + "//strategic.txt"));
		RewrittenKB = rewriteRules(originalKB, RewrittenKB);
		try (final Reasoner reasoner = new VLogReasoner(RewrittenKB)) {
			// reasoner.setLogLevel(LogLevel.INFO);
			// reasoner.setLogFile(baseDir + "example_log.log");
			long startTime = System.nanoTime();
			ground(RewrittenKB, originalKB, baseDir + "//ali.txt", reasoner);
			String s = null;

			try {

				// run the Unix "ps -ef" command
				// using the Runtime exec method:
				Process p = Runtime.getRuntime()
						.exec("clasp ali.txt");

				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

				BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

				// read the output from the command
				System.out.println("Here is the standard output of the command:\n");
				while ((s = stdInput.readLine()) != null) {
					System.out.println(s);
				}

				// read any errors from the attempted command
				System.out.println("Here is the standard error of the command (if any):\n");
				while ((s = stdError.readLine()) != null) {
					System.out.println(s);
				}
				System.exit(0);
			} catch (IOException e) {
				System.out.println("exception happened - here's what I know: ");
				e.printStackTrace();
				System.exit(-1);
			}
			long endTime = System.nanoTime();
			System.out.println(endTime - startTime);
		}

	}
}
