package org.semanticweb.rulewerk.reliances;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.logic.MartelliMontanariUnifier;
import org.semanticweb.rulewerk.logic.Substitute;
import org.semanticweb.rulewerk.logic.Unifier;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.utils.BCQA;
import org.semanticweb.rulewerk.utils.LiteralList;
import org.semanticweb.rulewerk.utils.RuleUtil;
import org.semanticweb.rulewerk.utils.Transform;

public class Reliance {

	final private static Logger logger = Logger.getLogger(Reliance.class);

	/**
	 * Defines how messages should be logged. This method can be modified to
	 * restrict the logging messages that are shown on the console or to change
	 * their formatting. See the documentation of Log4J for details on how to do
	 * this.
	 *
	 * Note: The VLog C++ backend performs its own logging. The log-level for this
	 * can be configured using
	 * {@link Reasoner#setLogLevel(org.semanticweb.rulewerk.core.reasoner.LogLevel)}.
	 * It is also possible to specify a separate log file for this part of the logs.
	 */
	public static void configureLogging() {
		// Create the appender that will write log messages to the console.
		final ConsoleAppender consoleAppender = new ConsoleAppender();
		// Define the pattern of log messages.
		// Insert the string "%c{1}:%L" to also show class name and line.
		final String pattern = "%d{yyyy-MM-dd HH:mm:ss} %-5p - %m%n";
		consoleAppender.setLayout(new PatternLayout(pattern));
		// Change to Level.ERROR for fewer messages:
		consoleAppender.setThreshold(Level.DEBUG);

		consoleAppender.activateOptions();
		Logger.getRootLogger().addAppender(consoleAppender);
	}

	/**
	 * Checker for positive reliance relation.
	 * 
	 * @param rule1
	 * @param rule2
	 * @return True if rule2 positively relies on rule1.
	 * @throws IOException
	 * @throws ParsingException
	 */
	static public boolean positively(Rule first, Rule second) throws ParsingException, IOException {
		configureLogging();
		if (RuleUtil.containsRepeatedAtoms(first)) {
			logger.debug("RPOS: Illegal argument exception: Rule first contains duplicated atoms");
			throw new IllegalArgumentException("Rule first can not contain dupplicated atoms: " + first);
		}
		if (RuleUtil.containsRepeatedAtoms(second)) {
			logger.debug("RPOS: Illegal argument exception: Rule second contains duplicated atoms");
			throw new IllegalArgumentException("Rule second can not contain dupplicated atoms: " + second);
		}
		if (!RuleUtil.isApplicable(first)) {
			logger.debug("RPOS: Rule first is not applicable");
			return false;
		}
		if (!RuleUtil.isApplicable(second)) {
			logger.debug("RPOS: Rule second is not applicable");
			return false;
		}
		logger.debug("RPOS: Rules are well written");

		Rule rule1 = Transform.exi2null(RuleUtil.renameVariablesWithSufix(first, 1));
		Rule rule2 = Transform.exi2null(RuleUtil.renameVariablesWithSufix(second, 2));

		logger.debug("RPOS: rule1: " + rule1);
		logger.debug("RPOS: rule2: " + rule2);

		List<PositiveLiteral> varphi1 = rule1.getPositiveBodyLiterals().getLiterals();
		List<PositiveLiteral> psi1 = Transform.exi2null(rule1.getHead().getLiterals()).stream()
				.map(e -> (PositiveLiteral) e).collect(Collectors.toList());
		List<PositiveLiteral> varphi2 = rule2.getPositiveBodyLiterals().getLiterals();
		List<PositiveLiteral> psi2 = Transform.exi2null(rule2.getHead().getLiterals()).stream()
				.map(e -> (PositiveLiteral) e).collect(Collectors.toList());

		return extend(varphi1, psi1, varphi2, psi2, new HashMap<PositiveLiteral, PositiveLiteral>(), 0);
	}

	private static boolean extend(List<PositiveLiteral> varphi1, List<PositiveLiteral> psi1,
			List<PositiveLiteral> varphi2, List<PositiveLiteral> psi2, Map<PositiveLiteral, PositiveLiteral> m, int idx)
			throws ParsingException, IOException {
		logger.debug("RPOS: starting extend with:\n\tvarphi1:\t" + varphi1 + "\n\tpsi1   :\t" + psi1 + "\n\tvarphi2:\t"
				+ varphi2 + "\n\tpsi2   :\t" + psi2 + "\n\tmapping:\t" + m + "\n\tindex  :\t" + idx);
		for (int i = idx; i < psi1.size(); i++) {
			for (int j = 0; j < varphi2.size(); j++) {
				if (varphi2.get(j).getPredicate().equals(psi1.get(i).getPredicate())) {
					Map<PositiveLiteral, PositiveLiteral> helper = new HashMap<>(m);
					helper.put(psi1.get(i), varphi2.get(j));
					if (worker(varphi1, psi1, varphi2, psi2, helper, (idx + 1))) {
						return true;
					}
				}
			}
		}
		return false; // should this be in the previous line?
	}

	private static boolean worker(List<PositiveLiteral> varphi1, List<PositiveLiteral> psi1,
			List<PositiveLiteral> varphi2, List<PositiveLiteral> psi2, Map<PositiveLiteral, PositiveLiteral> m, int idx)
			throws ParsingException, IOException {
		logger.debug("RPOS: starting worker with:\n\tvarphi1:\t" + varphi1 + "\n\tpsi1   :\t" + psi1 + "\n\tvarphi2:\t"
				+ varphi2 + "\n\tpsi2   :\t" + psi2 + "\n\tmapping:\t" + m + "\n\tindex  :\t" + idx);

		Unifier sigma = new MartelliMontanariUnifier(m);
		if (!sigma.isSuccessful()) {
			logger.debug("RPOS: ending worker with false (not unifiable)");
			return false;
		}
		logger.debug("RPOS: sigma:\t" + sigma);
		List<PositiveLiteral> varphi21 = new ArrayList<>();
		List<PositiveLiteral> varphi22 = new ArrayList<>();

		m.forEach((key, value) -> varphi21.add(value));
		varphi2.forEach(e -> {
			if (!varphi21.contains(e))
				varphi22.add(e);
		});
		logger.debug("RPOS: varphi21:\t" + varphi21);
		logger.debug("RPOS: varphi22:\t" + varphi22);

		List<PositiveLiteral> varphi1sigma = Substitute.positiveLiterals(sigma.getSubstitution(), varphi1);
		List<PositiveLiteral> varphi22sigma = Substitute.positiveLiterals(sigma.getSubstitution(), varphi22);
		if (!LiteralList.getExistentialVariables(varphi1sigma).isEmpty()) {
			logger.debug("RPOS: ending worker with false (existentials in varphi1sigma)");
			return false;
		}
		if (!LiteralList.getExistentialVariables(varphi22sigma).isEmpty()) {
			logger.debug("RPOS: recursive call to extend");
			return extend(varphi1, psi1, varphi2, psi2, m, (idx + 1));
		}

		List<PositiveLiteral> psi1sigmaforall = Transform
				.intoPositiveLiterals(Transform.uni2cons(Substitute.positiveLiterals(sigma.getSubstitution(), psi1)));
		List<Fact> InstA = Transform.intoFacts(Transform.uni2cons(varphi1sigma));
		logger.debug("RPOS: Inst A:\t" + InstA);

		if (BCQA.query2(InstA, psi1sigmaforall)) {
			System.out.println("extending");
			return extend(varphi1, psi1, varphi2, psi2, m, (idx + 1));
		}

		logger.debug("RPOS: HERE");
		logger.debug("RPOS: HERE: "+sigma.getSubstitution());
		logger.debug("RPOS: HERE: "+varphi2);
		List<Fact> varphi2sigmaforall = Transform
				.intoFacts(Transform.uni2cons(Substitute.positiveLiterals(sigma.getSubstitution(), varphi2)));
		logger.debug("RPOS: varphi2sigmaforall:\t" + varphi2sigmaforall);
		logger.debug("RPOS: HERE2");
		if (BCQA.query1(InstA, varphi2sigmaforall)) {
			logger.debug("RPOS: ending worker with false (InstA models varphi2sigmaforall)");
			return false;
		}

		List<Fact> InstB = new ArrayList<>(InstA);
		InstB.addAll(Transform.intoFacts(psi1sigmaforall));
		logger.debug("RPOS: Inst B:\t" + InstB);

		List<PositiveLiteral> psi2sigmaforall = Transform
				.intoPositiveLiterals(Transform.uni2cons(Substitute.positiveLiterals(sigma.getSubstitution(), psi2)));
		logger.debug("RPOS: psi2sigmaforall:\t" + psi2sigmaforall);

		if (!BCQA.query2(InstB, psi2sigmaforall)) {
			logger.debug("RPOS: ending worker with true");
			return true;
		}
		logger.debug("RPOS: ending worker with false (last case)");
		return false;
	}

}
