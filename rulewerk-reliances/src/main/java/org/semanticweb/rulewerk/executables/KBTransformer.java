package org.semanticweb.rulewerk.executables;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;
import org.semanticweb.rulewerk.reliances.Reliance;

public class KBTransformer {

	private Rule addHeadAtom(Rule rule, PositiveLiteral literal) {
		List<PositiveLiteral> head = new ArrayList<>();
		rule.getHead().getLiterals().forEach(pl -> head.add(pl));
		head.add(literal);
		return new RuleImpl(new ConjunctionImpl<PositiveLiteral>(head), rule.getBody());
	}

	private Rule addBodyAtom(Rule rule, Literal literal) {
		List<Literal> body = new ArrayList<>();
		rule.getBody().getLiterals().forEach(l -> body.add(l));
		body.add(literal);
		return new RuleImpl(rule.getHead(), new ConjunctionImpl<Literal>(body));
	}

	private void saveStringToFile(String outputPath, String data) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(outputPath);
		out.println(data);
		out.close();
	}

	private String readFile(String inputPath) throws IOException {
		File file = new File(inputPath);
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fis.read(data);
		fis.close();
		return new String(data, "UTF-8");
	}

	private void addFactsToKB(String inputDataPath, String outputDataPath, String newFacts) throws IOException {
		String data = readFile(inputDataPath);
		data += "\n" + newFacts;
		saveStringToFile(outputDataPath, data);
	}

	// I have to make sure that the rules and the data are in separated files
	public void transform(String inputRulePath, String inputDataPath, String outputRulePath, String outputDataPath)
			throws ParsingException, IOException {
		KnowledgeBase kb = new KnowledgeBase();
		RuleParser.parseInto(kb, new FileInputStream(inputRulePath));

		HashMap<Integer, Rule> rules = new HashMap<>();
		kb.getRules().forEach(rule -> rules.put(rules.size(), rule));

//		System.out.println("Rules used in this example:");
//		for (int i = 0; i < rules.size(); i++) {
//			System.out.println(i + ": " + rules.get(i));
//		}

		List<int[]> positiveDependency = new ArrayList<>();
		for (int i = 0; i < rules.size(); i++) {
			for (int j = 0; j < rules.size(); j++) {
				if (Reliance.positively(rules.get(i), rules.get(j))) {
					positiveDependency.add(new int[] { i, j });
				}
			}
		}

		String newFacts = "";
		for (int i = 0; i < positiveDependency.size(); i++) {
			String name = "newPredicateName";
			Fact fact = RuleParser.parseFact(name + i + "(1).");
			PositiveLiteral literal1 = RuleParser.parsePositiveLiteral(name + i + "(1)");
			Literal literal2 = RuleParser.parseLiteral("~" + name + i + "(2)");

			int[] pair = positiveDependency.get(i);
			if (pair[0] != pair[1]) {
				newFacts += fact + "\n";
				Rule r1 = addHeadAtom(rules.get(pair[0]), literal1);
				System.out.println(r1);
				rules.replace(pair[0], addHeadAtom(rules.get(pair[0]), literal1));
				rules.replace(pair[1], addBodyAtom(rules.get(pair[1]), literal2));
			}
		}

		String newRules = "";
		for (int i = 0; i < rules.size(); i++) {
			newRules += rules.get(i) + "\n";
		}

		saveStringToFile(outputRulePath, newRules);
		addFactsToKB(inputDataPath, outputDataPath, newFacts);

	}
}
