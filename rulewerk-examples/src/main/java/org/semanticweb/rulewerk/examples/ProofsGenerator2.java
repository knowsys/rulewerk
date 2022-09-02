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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.LogLevel;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

public class ProofsGenerator2 {
	public HashSet<String>used_literals= new HashSet<String>();
	public HashSet<Rule>graph_rules= new HashSet<Rule>();
	public HashMap<String,String>IRI_rewriting= new HashMap<String,String>();
	
	
public List<Rule> rewrite(List<Rule> rules) throws ParsingException {
	List<Rule> result =new LinkedList<Rule>();
	
for (Rule r:rules) {
	result.add(r);
	result.addAll(generateAnnotationRules(r));
	result.addAll(generateGraphRules(r));

}
return result;
}

public List<Rule> generateAnnotationRules(Rule r) throws ParsingException {
	List<Rule> result = new LinkedList<Rule>();
	for (PositiveLiteral pl: r.getHead()) {
		if (!used_literals.contains(pl.getPredicate().getName())) {
			String new_body = pl.getPredicate().getName()+"(";
			for (int i=0;i<pl.getPredicate().getArity();i++) {
				new_body=new_body+"?a"+i+", ";
			}
			new_body=new_body+"*";
			new_body=new_body.replace(", *", ")");
			String new_head = new_body.replaceAll("\\)", ",!ann\\)").replaceAll("\\(", "_p(");
			String ann_rule = new_head+" :- "+new_body+".";
			String new_body2 = new_body.replaceAll("\\)", ",?ann\\)").replaceAll("\\(", "_p(")+", iterate(?ann).";
			String new_head2 = "G_"+new_body.replaceAll("\\)", ",?ann\\)").replaceAll("\\(", "_p(");
			String new_rule2 = new_head2+" :- "+new_body2;
			if (!graph_rules.contains(RuleParser.parseRule(new_rule2))) {
				result.add(RuleParser.parseRule(new_rule2));
			graph_rules.add(RuleParser.parseRule(new_rule2));
			}
			result.add(RuleParser.parseRule(ann_rule));
			used_literals.add(pl.getPredicate().getName());
		}
	}
	for (Literal l: r.getBody()) {
		if (!used_literals.contains(l.getPredicate().getName())) {
			String new_body = l.getPredicate().getName()+"(";
			for (int i=0;i<l.getPredicate().getArity();i++) {
				new_body=new_body+"?a"+i+", ";
			}
			new_body=new_body+"*";
			new_body=new_body.replace(", *", ")");
			String new_head = new_body.replaceAll("\\)", ",!ann\\)").replaceAll("\\(", "_p(");
			String ann_rule = new_head+" :- "+new_body+".";
			String new_body2 = new_body.replaceAll("\\)", ",?ann\\)").replaceAll("\\(", "_p(")+", iterate(?ann).";
			String new_head2 = "G_"+new_body.replaceAll("\\)", ",?ann\\)").replaceAll("\\(", "_p(");
			String new_rule2 = new_head2+" :- "+new_body2;
			if (!graph_rules.contains(RuleParser.parseRule(new_rule2))) {
			result.add(RuleParser.parseRule(new_rule2));
			graph_rules.add(RuleParser.parseRule(new_rule2));
			}
			result.add(RuleParser.parseRule(ann_rule));
			used_literals.add(l.getPredicate().getName());
		}
}
	return result;
}

public List<Rule> generateGraphRules(Rule r) throws ParsingException {
	List<Rule> result = new LinkedList<Rule>();
	List<String> annotations = new LinkedList<String>();
	String new_body="";
	for (int i=0;i<r.getBody().getLiterals().size();i++) {
		new_body=new_body+r.getBody().getLiterals().get(i).toString().replace(")", ", ?ann"+i+"), ").replaceAll("\\(", "_p(");
		annotations.add("?ann"+i);
	}
	new_body = new_body+"*";
	new_body=new_body.replace(", *","");
	new_body=new_body+", iterate(?annG)";
for (PositiveLiteral pl :r.getHead()) {
	String new_head = "proof_edge(!aux_node, ?annG, "+annotations.toString().replace("[", "").replace("]", ")");
	String current_head =new_head;
	for (String ann:annotations) {
		new_head = new_head+", iterate("+ann+")";
	}
	String new_rule = new_head + " :- "+ pl.toString().replace(")", ", ?annG"+")").replaceAll("\\(", "_p(")+", "+new_body+".";
	result.add(RuleParser.parseRule(new_rule));
	result.addAll(unfoldingGraphRules(annotations, current_head));
}
	return result;
	
}

public List<Rule> unfoldingGraphRules(List<String> annotations, String new_body) throws ParsingException {
	List<Rule> result = new LinkedList<Rule>();
	new_body=new_body.replace("!", "?");
	String new_head="edge(?aux_node, ?annG)";
	for (String annotation:annotations) {
		new_head = new_head + ", edge("+annotation+", ?aux_node)";
	}
	Rule new_rule = RuleParser.parseRule(new_head+" :- " + new_body+".");
	result.add(new_rule);
	
	return result;
	
	
}

public List<Rule> replaceIrI(List<Rule>rules) throws ParsingException{
	int i=0;
	List<Rule>result= new LinkedList<Rule>();
	for (Rule r:rules) {
		String new_rule =r.toString();
		for (PositiveLiteral l:r.getHead().getLiterals()) {
			String possible_iri = l.getPredicate().getName();
			if (possible_iri.startsWith("http:")) {
				if (!IRI_rewriting.containsKey("<"+possible_iri+">")) {
					new_rule=new_rule.replaceAll("<"+possible_iri+">", "IRI"+i);
					IRI_rewriting.put("<"+possible_iri+">", "IRI"+i);
					i++;
				}
			else {
				new_rule=new_rule.replaceAll(possible_iri, IRI_rewriting.get("<"+possible_iri+">"));
			}
			}
		}
		for (Literal l:r.getBody().getLiterals()) {
			String possible_iri = l.getPredicate().getName();
			if (possible_iri.startsWith("http:")) {
				if (!IRI_rewriting.containsKey("<"+possible_iri+">")) {
					new_rule=new_rule.replaceAll("<"+possible_iri+">", "IRI"+i);
					IRI_rewriting.put("<"+possible_iri+">", "IRI"+i);
					i++;
				}
			else {
				
				new_rule=new_rule.replaceAll("<"+possible_iri+">", IRI_rewriting.get("<"+possible_iri+">"));
			}
			}
		}
		result.add(RuleParser.parseRule(new_rule));
	}
	return result;
}

public HashMap<String,String> exportConclusions(KnowledgeBase kb, String conclusion,String path) throws ParsingException, IOException {
	KnowledgeBase kb2 = new KnowledgeBase();
	kb2.addStatements(kb.getFacts());
	List<Rule> new_rules=replaceIrI(kb.getRules());
	kb2.addStatements(rewrite(new_rules));
	String[]cut=conclusion.split("\\(");
	if (IRI_rewriting.containsKey(cut[0])) {
		conclusion=conclusion.replace(cut[0], IRI_rewriting.get(cut[0]));
	}
	kb2.addStatement(RuleParser.parseRule("iterate(?Z) :- " + conclusion.replace("(", "_p(").replace(")", ",?Z).")));
    try (final Reasoner reasoner = new VLogReasoner(kb2)) {
	      reasoner.setLogLevel(LogLevel.INFO);
    reasoner.reason();
   //reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("G_TRIPLE_p(?X, ?P, ?O, ?ann)"), path+"/ali.csv", true);
    reasoner.exportQueryAnswersToCsv(RuleParser.parsePositiveLiteral("edge(?x,?y)"), path+"/edge.csv", true);
    for (Rule r : graph_rules) {
    	if (reasoner.countQueryAnswers(r.getHead().getLiterals().get(0), true).getCount()>0)
    	reasoner.exportQueryAnswersToCsv(r.getHead().getLiterals().get(0), path+"/"+r.getHead().getLiterals().get(0).getPredicate().getName()+".csv", true);
    }
}
	return IRI_rewriting;
}


// main only for testing
//public static void main (String[]args) throws IOException, ParsingException {
//	final String baseDir = "/mnt/c/Users/Ali Elhalawati/files/el";
//	ProofsGenerator2 al = new ProofsGenerator2();
////	File f = new File("/mnt/c/Users/Ali Elhalawati/files/el/elk_optimized.rls");
////	FileWriter fw = new FileWriter(new File(baseDir + "/proofs.txt"));
////	Scanner yf = new Scanner(f);
//	KnowledgeBase kb = new KnowledgeBase();
////	while (yf.hasNextLine()) {
////	
////		String x = yf.nextLine();
////
////		kb.addStatement(RuleParser.parseRule(x));
////	}
////	List<Rule> aa =rewrite(kb.getRules());
////	for (Rule r:aa) {
////		fw.write(r.toString()+"\n");
////	}
////	yf.close();
////	fw.close();
////	 File x2 = new File(baseDir + "/galen-data/ali.csv");
////		Scanner y2 = new Scanner(x2);
////		//int i = 0;
////		while (y2.hasNextLine()) {
////			String a = y2.nextLine();
////			a=a.replace("\t",",");
////			String []split = a.split(",");
////			String b ="";
////			for (String s: split) {
////				b=b+"\""+s+"\""+",";
////			}
////			b=b+";";
////			b=b.replace(",;","");
////		//	System.out.println(b);
////			kb.addStatement(RuleParser.parseFact("ali("+b+")."));
////			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
////			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
////			//i++;
////		}
////		y2.close();
////		
////		File x21 = new File(baseDir + "/galen-data/conj.csv");
////		Scanner y21 = new Scanner(x21);
////		//int i = 0;
////		while (y21.hasNextLine()) {
////			String a = y21.nextLine();
////			a=a.replace("\t",",");
////			String []split = a.split(",");
////			String b ="";
////			for (String s: split) {
////				b=b+"\""+s+"\""+",";
////			}
////			b=b+";";
////			b=b.replace(",;","");
////			kb.addStatement(RuleParser.parseFact("conj("+b+")."));
////			//kb2.addStatement(RuleParser.parseFact("Check("+a+")."));
////			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
////			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
////			//i++;
////		}
////		y21.close();
////		
////		File x211 = new File(baseDir + "/galen-data/exists.csv");
////		Scanner y211 = new Scanner(x211);
////		//int i = 0;
////		while (y211.hasNextLine()) {
////			String a = y211.nextLine();
////			a=a.replace("\t",",");
////			String []split = a.split(",");
////			String b ="";
////			for (String s: split) {
////				b=b+"\""+s+"\""+",";
////			}
////			b=b+";";
////			b=b.replace(",;","");
////			kb.addStatement(RuleParser.parseFact("exists("+b+")."));
////		//	kb2.addStatement(RuleParser.parseFact("Verify("+a+")."));
////			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
////			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
////			//i++;
////		}
////		y211.close();
////		
////		File x2111 = new File(baseDir + "/galen-data/isMainClass.csv");
////		Scanner y2111 = new Scanner(x2111);
////		//int i = 0;
////		while (y2111.hasNextLine()) {
////			String a = y2111.nextLine();
////			a=a.replace("\t",",");
////			String []split = a.split(",");
////			String b ="";
////			for (String s: split) {
////				b=b+"\""+s+"\""+",";
////			}
////			b=b+";";
////			b=b.replace(",;","");
////			kb.addStatement(RuleParser.parseFact("isMainClass("+b+")."));
////			//kb2.addStatement(RuleParser.parseFact("opSucc("+a+")."));
////			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
////			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
////			//i++;
////		}
////		y2111.close();
////		File x21111 = new File(baseDir + "/galen-data/isSubClass.csv");
////		Scanner y21111 = new Scanner(x21111);
////		//int i = 0;
////		while (y21111.hasNextLine()) {
////			String a = y21111.nextLine();
////			a=a.replace("\t",",");
////			String []split = a.split(",");
////			String b ="";
////			for (String s: split) {
////				b=b+"\""+s+"\""+",";
////			}
////			b=b+";";
////			b=b.replace(",;","");
////			kb.addStatement(RuleParser.parseFact("isSubClass("+b+")."));
////			//kb2.addStatement(RuleParser.parseFact("opSucc("+a+")."));
////			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
////			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
////			//i++;
////		}
////		y21111.close();
////		File x211111 = new File(baseDir + "/galen-data/subProp.csv");
////		Scanner y211111 = new Scanner(x211111);
////		//int i = 0;
////		while (y211111.hasNextLine()) {
////			String a = y211111.nextLine();
////			a=a.replace("\t",",");
////			String []split = a.split(",");
////			String b ="";
////			for (String s: split) {
////				b=b+"\""+s+"\""+",";
////			}
////			b=b+";";
////			b=b.replace(",;","");
////			kb.addStatement(RuleParser.parseFact("subProp("+b+")."));
////			//kb2.addStatement(RuleParser.parseFact("opSucc("+a+")."));
////			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
////			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
////			//i++;
////		}
////		y211111.close();
//		File x = new File(baseDir + "/elk-calculus.rls");
//		Scanner y = new Scanner(x);
//		while (y.hasNextLine()) {
//			String xx = y.nextLine();
//			// System.out.println(xx);
//			RuleParser.parseInto(kb, xx);
//		}
//		y.close();
//		
//		al.exportConclusions(kb,"<http://rulewerk.semantic-web.org/inferred/subClassOf>(<http://simpleTest#Concept3>,<http://simpleTest#Concept4>)",baseDir+"/ground");
//		
//}
}
