package org.semanticweb.vlog4j.core.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.semanticweb.vlog4j.core.model.Atom;
import org.semanticweb.vlog4j.core.model.Rule;
import org.semanticweb.vlog4j.core.model.Term;
import org.semanticweb.vlog4j.core.model.Variable;

public class RuleValidator {

	/**
	 * Validates that variables in the rules obey the following principles:
	 * <ul>
	 * <li>existentially qualified variables only occur in the rule head, and not in
	 * the rule body</li>
	 * <li>all existentially qualified variables occur in the rule head</li>
	 * <li>all universally qualified variables that occur in the rule body also
	 * occur in the rule head</li>
	 * </ul>
	 * 
	 * @param rule
	 * @throws RuleVariableValidationException
	 *             <ul>
	 *             <li>if an existentially quantified variable occurs in the rule
	 *             body.</li>
	 *             <li>if an existentially quantified variable does not occur in the
	 *             rule head.</li>
	 *             <li>if a variable that is not existentially quantified occurs in
	 *             the rule head, but not in the rule body.</li>
	 *             </ul>
	 */
	public static void checkRuleVariables(Rule rule) throws RuleVariableValidationException {
		List<Variable> bodyVariables = collectVariables(rule.getBody());
		List<Variable> headVariables = collectVariables(rule.getHead());
		Set<Variable> existentiallyQuantifiedVariables = rule.getExistentiallyQuantifiedVariables();

		checkNoUniversalVariables(rule, bodyVariables, headVariables);

		checkNoExistentiallyQuantifiedVariableInBody(rule, bodyVariables);

		checkAllExistentiallyQuantifiedVariablesUsed(rule, headVariables, existentiallyQuantifiedVariables);

	}

	/**
	 * Validates that all existentially quantified variables are used in the rule
	 * head.
	 * 
	 * @param rule
	 * @param headVariables
	 * @param existentiallyQuantifiedVariables
	 * @throws RuleVariableValidationException
	 *             if an existentially quantified variable does not occur in the
	 *             rule head.
	 */
	private static void checkAllExistentiallyQuantifiedVariablesUsed(Rule rule, List<Variable> headVariables,
			Set<Variable> existentiallyQuantifiedVariables) throws RuleVariableValidationException {
		for (Variable existentiallyQuantifiedVariable : existentiallyQuantifiedVariables) {
			if (!headVariables.contains(existentiallyQuantifiedVariable)) {
				throw new RuleVariableValidationException(rule, existentiallyQuantifiedVariable,
						"Existentially quantified not used in rule head");
			}
		}
	}

	/**
	 * Validates that existentially quantified variables occur only in the rule
	 * head.
	 * 
	 * @param rule
	 * @param bodyVariables
	 * @throws RuleVariableValidationException
	 *             if an existentially quantified variable occurs in the rule body.
	 */
	private static void checkNoExistentiallyQuantifiedVariableInBody(Rule rule, List<Variable> bodyVariables)
			throws RuleVariableValidationException {
		for (Variable bodyVariable : bodyVariables) {
			if (isVariableExistentiallyQuantified(bodyVariable, rule)) {
				throw new RuleVariableValidationException(rule, bodyVariable,
						"Existentially quantified variable occurs in rule body");
			}
		}
	}

	/**
	 * Validates that all universally quantified variables (i.e. variables that are
	 * not existentially quantified) that appear in the rule head also appear in the
	 * rule body.
	 * 
	 * @param rule
	 * @param bodyVariables
	 * @param headVariables
	 * @throws RuleVariableValidationException
	 *             if a variable that is not existentially quantified occurs in the
	 *             rule head, but not in the rule body.
	 */
	private static void checkNoUniversalVariables(Rule rule, List<Variable> bodyVariables, List<Variable> headVariables)
			throws RuleVariableValidationException {
		for (Variable headVariable : headVariables) {
			if (!isVariableExistentiallyQuantified(headVariable, rule)) {
				if (!bodyVariables.contains(headVariable)) {
					throw new RuleVariableValidationException(rule, headVariable,
							"Universally quantified variable occurs only in rule head");
				}
			}
		}
	}

	private static boolean isVariableExistentiallyQuantified(Variable variable, Rule rule) {
		return rule.getExistentiallyQuantifiedVariables().contains(variable);
	}

	private static List<Variable> collectVariables(Atom[] atoms) {
		List<Variable> variables = new ArrayList<Variable>(2);
		for (Atom atom : atoms) {
			Term[] arguments = atom.getArguments();
			for (Term term : arguments) {
				if (term.isVariable()) {
					variables.add((Variable) term);
				}
			}
		}
		return variables;
	}

}
