package org.semanticweb.rulewerk.examples;

/*-
 * #%L
 * Rulewerk Examples
 * %%
 * Copyright (C) 2018 - 2022 Rulewerk Developers
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
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

public class ProofsGenerator {
	public static HashSet<String>used_literals= new HashSet<String>();
	
public static List<Rule> rewrite(List<Rule> rules) throws ParsingException {
	List<Rule> result =new LinkedList<Rule>();
	
for (Rule r:rules) {
	result.add(r);
	result.addAll(generateAnnotationRules(r));
	result.addAll(generateGraphRules(r));

}
return result;
}

public static List<Rule> generateAnnotationRules(Rule r) throws ParsingException {
	List<Rule> result = new LinkedList<Rule>();
	for (PositiveLiteral pl: r.getHead()) {
		if (!used_literals.contains(pl.toString())) {
			String new_body = pl.toString().replaceAll("!", "?");
			String new_head = new_body.replaceAll("\\)", ",!ann\\)").replaceAll("\\(", "_p(");
			String ann_rule = new_head+" :- "+new_body+".";
			result.add(RuleParser.parseRule(ann_rule));
			used_literals.add(pl.getPredicate().getName());
		}
	}
	for (Literal l: r.getBody()) {
		if (!used_literals.contains(l.toString())) {
			String new_head = l.toString().replaceAll("\\)", ",!ann\\)").replaceAll("\\(", "_p(");
			String ann_rule = new_head+" :- "+l.toString()+".";
			result.add(RuleParser.parseRule(ann_rule));
			used_literals.add(l.getPredicate().getName());
		}
}
	return result;
}

public static List<Rule> generateGraphRules(Rule r) throws ParsingException {
	List<Rule> result = new LinkedList<Rule>();
	List<String> annotations = new LinkedList<String>();
	String new_body="";
	for (int i=0;i<r.getBody().getLiterals().size();i++) {
		new_body=new_body+r.getBody().getLiterals().get(i).toString().replace(")", ", ?ann"+i+"), ").replaceAll("\\(", "_p(");
		annotations.add("?ann"+i);
	}
	new_body = new_body+"*";
	new_body=new_body.replace(", *","");
for (PositiveLiteral pl :r.getHead()) {
	String new_rule = "proof_edge(?annG, "+annotations.toString().replace("[", "").replace("]", ")")+ " :- "+ pl.toString().replace(")", ", ?annG"+")").replaceAll("\\(", "_p(")+", "+new_body+".";
	result.add(RuleParser.parseRule(new_rule));
	//System.out.println(new_rule);
}
	return result;
	
}


// main only for testing
public static void main (String[]args) throws IOException, ParsingException {
	File f = new File("C:\\Users\\Ali Elhalawati\\files\\doctor\\1m" + "\\doctorss.txt");
	FileWriter fw = new FileWriter(new File("C:\\Users\\Ali Elhalawati\\files\\doctor\\1m" + "/proofs.txt"));
	Scanner y = new Scanner(f);
	KnowledgeBase kb = new KnowledgeBase();
	while (y.hasNextLine()) {
	
		String x = y.nextLine();

		kb.addStatement(RuleParser.parseRule(x));
	}
	List<Rule> a =rewrite(kb.getRules());
	for (Rule r:a) {
		fw.write(r.toString()+"\n");
	}
	y.close();
	fw.close();
}
}
