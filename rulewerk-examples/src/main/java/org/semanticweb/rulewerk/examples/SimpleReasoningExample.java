package org.semanticweb.rulewerk.examples;

import java.io.File;

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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.QueryResult;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.LogLevel;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

/**
 * This example demonstrates the basic usage of Rulewerk for rule reasoning. We
 * are using a fixed set of rules and facts defined in Java without any external
 * sources, and we query for some of the results.
 *
 * @author Markus Kroetzsch
 *
 */
public class SimpleReasoningExample {
  final static String baseDir = "/mnt/c/Users/Ali Elhalawati/files/el";
  public  int count=1;
  public long c1=0;
  public long c2=0;
  public long c3=0;
   int support_i=0;
   int support_j=0;

  public long compute(String n,PrintWriter result,String kb_size,String query) throws ParsingException, IOException {
	  KnowledgeBase kb = new KnowledgeBase();
	//  KnowledgeBase kb2 = new KnowledgeBase();
	  File x2 = new File(baseDir + "/galen-data/ali.csv");
		Scanner y2 = new Scanner(x2);
		//int i = 0;
		while (y2.hasNextLine()) {
			String a = y2.nextLine();
			a=a.replace("\t",",");
			String []split = a.split(",");
			String b ="";
			for (String s: split) {
				b=b+"\""+s+"\""+",";
			}
			b=b+";";
			b=b.replace(",;","");
		//	System.out.println(b);
			kb.addStatement(RuleParser.parseFact("ali("+b+")."));
			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
			//i++;
		}
		y2.close();
		
		File x21 = new File(baseDir + "/galen-data/conj.csv");
		Scanner y21 = new Scanner(x21);
		//int i = 0;
		while (y21.hasNextLine()) {
			String a = y21.nextLine();
			a=a.replace("\t",",");
			String []split = a.split(",");
			String b ="";
			for (String s: split) {
				b=b+"\""+s+"\""+",";
			}
			b=b+";";
			b=b.replace(",;","");
			kb.addStatement(RuleParser.parseFact("conj("+b+")."));
			//kb2.addStatement(RuleParser.parseFact("Check("+a+")."));
			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
			//i++;
		}
		y21.close();
		
		File x211 = new File(baseDir + "/galen-data/exists.csv");
		Scanner y211 = new Scanner(x211);
		//int i = 0;
		while (y211.hasNextLine()) {
			String a = y211.nextLine();
			a=a.replace("\t",",");
			String []split = a.split(",");
			String b ="";
			for (String s: split) {
				b=b+"\""+s+"\""+",";
			}
			b=b+";";
			b=b.replace(",;","");
			kb.addStatement(RuleParser.parseFact("exists("+b+")."));
		//	kb2.addStatement(RuleParser.parseFact("Verify("+a+")."));
			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
			//i++;
		}
		y211.close();
		
		File x2111 = new File(baseDir + "/galen-data/isMainClass.csv");
		Scanner y2111 = new Scanner(x2111);
		//int i = 0;
		while (y2111.hasNextLine()) {
			String a = y2111.nextLine();
			a=a.replace("\t",",");
			String []split = a.split(",");
			String b ="";
			for (String s: split) {
				b=b+"\""+s+"\""+",";
			}
			b=b+";";
			b=b.replace(",;","");
			kb.addStatement(RuleParser.parseFact("isMainClass("+b+")."));
			//kb2.addStatement(RuleParser.parseFact("opSucc("+a+")."));
			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
			//i++;
		}
		y2111.close();
		File x21111 = new File(baseDir + "/galen-data/isSubClass.csv");
		Scanner y21111 = new Scanner(x21111);
		//int i = 0;
		while (y21111.hasNextLine()) {
			String a = y21111.nextLine();
			a=a.replace("\t",",");
			String []split = a.split(",");
			String b ="";
			for (String s: split) {
				b=b+"\""+s+"\""+",";
			}
			b=b+";";
			b=b.replace(",;","");
			kb.addStatement(RuleParser.parseFact("isSubClass("+b+")."));
			//kb2.addStatement(RuleParser.parseFact("opSucc("+a+")."));
			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
			//i++;
		}
		y21111.close();
		File x211111 = new File(baseDir + "/galen-data/subProp.csv");
		Scanner y211111 = new Scanner(x211111);
		//int i = 0;
		while (y211111.hasNextLine()) {
			String a = y211111.nextLine();
			a=a.replace("\t",",");
			String []split = a.split(",");
			String b ="";
			for (String s: split) {
				b=b+"\""+s+"\""+",";
			}
			b=b+";";
			b=b.replace(",;","");
			kb.addStatement(RuleParser.parseFact("subProp("+b+")."));
			//kb2.addStatement(RuleParser.parseFact("opSucc("+a+")."));
			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
			//i++;
		}
		y211111.close();
			
			   RuleParser.parseInto(kb, n);
			File x = new File(baseDir + "/s_rules3.txt");
			Scanner y = new Scanner(x);
			while (y.hasNextLine()) {
				String xx = y.nextLine();
				// System.out.println(xx);
				RuleParser.parseInto(kb, xx);
			}
			y.close();
	 
//			File xk = new File(baseDir + "/ground.txt");
//			Scanner yk = new Scanner(xk);
//			while (yk.hasNextLine()) {
//				String xx = yk.nextLine();
//				// System.out.println(xx);
//				RuleParser.parseInto(kb2, xx);
//			}
//			yk.close();
//			for (Rule r : kb2.getRules()) {
//				if (r.getHead().getLiterals().size()==1) {
//				String n1 = r.getHead().getLiterals().get(0).getPredicate().getName();
//				String k = "@source "+n1+"["+r.getHead().getLiterals().get(0).getArguments().size()+"]"+": load-csv(\"" + baseDir + "/idb/"+n1+".csv\") .";
//				RuleParser.parseInto(kb, k);
//			}
//			}

	    try (final Reasoner reasoner = new VLogReasoner(kb)) {
		      reasoner.setLogLevel(LogLevel.INFO);
		     reasoner.setLogFile(baseDir + "/aaa.txt");
	      long startTime2 = System.currentTimeMillis();
	      reasoner.reason();
	      //System.out.println("doneee");
	   //ExamplesUtils.printOutQueryAnswers("q07(?A,?B)", reasoner);
	  	int num = 0;
		String save = "exist"+num + ".csv";
		File file = new File(baseDir + "/exist/", save);
		while(file.exists()) {
		    save = "exist" + (num++) +".csv";
		    file = new File(baseDir + "/exist/", save); 
		}
		//FileWriter fw = new FileWriter(file);
	      reasoner.exportQueryAnswersToCsv(RuleParser.parsePositiveLiteral("prov(?X,?Y)"), baseDir + "/exist/"+save, true);
	       long endtime = System.currentTimeMillis();
	       long time = (endtime-startTime2);
	       result.print(reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("prov(?X,?Y)")).getCount()+" total sets, ");
	       int c=0;
	       QueryResultIterator queryAnswers1 = reasoner
					.answerQuery(RuleParser.parsePositiveLiteral(n.replace("iterate(?Z) :- ","").replace(").", ")")), true);
			 while(queryAnswers1.hasNext()) {
				QueryResult queryAnswer1 = queryAnswers1.next();
				
		String	 head_ann = queryAnswer1.getTerms().get(queryAnswer1.getTerms().size()-1).toString();
	       try (final QueryResultIterator queryAnswers = reasoner
					.answerQuery(RuleParser.parsePositiveLiteral("prov(?X,?Y)"), true)) {
				//System.out.println("yessssssss");
				while (queryAnswers.hasNext()) {
					//h2++;
					QueryResult queryAnswer = queryAnswers.next();
					if (queryAnswer.getTerms().toString().contains(head_ann)) {
						c++;
					}
				}
	       }
			 }
		      result.print(c+" query sets, ");
	       c1=reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("idb_edge(?X,?Y)")).getCount()
		    		  + reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("rig_edge1(?X,?Y)")).getCount()
		    			+	   reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("rig_edge2(?X,?Y)")).getCount();
	       result.print(c1+" rig_size"+", ");
	       //c2=reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("prov(?X,?Y)")).getCount();
	       //result.print(c2+" prov count"+", ");
	       count++;
	      // long facts =kb.getFacts().size();
//	       result.print(facts+", ");
//	       result.print(reasoner.getInferences().count()-facts+", ");
	       
	       return (time);
  }
  }
  
  public KnowledgeBase reduce_graph(KnowledgeBase kb, HashSet<Fact>rig_edge1,HashSet<Fact>rig_edge2,HashSet<Fact>idb_edge, HashSet<Fact>scc, HashSet<Fact>graph_edb,HashSet<Fact>non_supports) {
	  HashSet<Term>checked= new HashSet<Term>();
	  HashSet<Term>embedded_idb= new HashSet<Term>();
	  HashSet<Fact>new_rig1= new HashSet<Fact>();
	  HashSet<Fact>new_rig2= new HashSet<Fact>();
	  HashSet<Fact>new_idb= new HashSet<Fact>();
	  HashSet<Fact>new_scc= new HashSet<Fact>(scc);
	  Term scc_node = null ;
	  if (!new_scc.isEmpty()) {
		  Fact f = scc.iterator().next();
			scc_node=  f.getArguments().get(0);
		//	System.out.println(scc_node);
			new_scc.remove(f);
			checked.add(scc_node);
			for (Fact f2:scc) {
				if (f2.getArguments().get(0).equals(scc_node)) {
					checked.add(f2.getArguments().get(1));
					new_scc.remove(f2);
				}
			}
			for (Fact f2:scc) {
				if (checked.contains(f2.getArguments().get(0))) {
					new_scc.remove(f2);
				}
			}
		  HashSet<Term>done= new HashSet<Term>();
		  String support="out"+support_i;
		  String set="set"+support_i;
		  support_i++;
//		  System.out.println(Expressions.makeFact("set", Expressions.makeAbstractConstant(set)));
//		  System.out.println(Expressions.makeFact("prov2",scc_node, Expressions.makeAbstractConstant(set)));
		  kb.addStatement(Expressions.makeFact("set", Expressions.makeAbstractConstant(set)));
		  kb.addStatement(Expressions.makeFact("prov2",scc_node, Expressions.makeAbstractConstant(set)));
		  kb.addStatement(Expressions.makeFact("scc_node",scc_node));
		  boolean created =false;
		  for (Fact f2:idb_edge) {
			  List<Term> args=f2.getArguments();
			  if (!checked.contains(args.get(0))){
				  if (checked.contains(args.get(1))) {
				  if(!done.contains(args.get(0))) {
				  new_idb.add(Expressions.makeFact("idb_edge", args.get(0),scc_node));
				  done.add(args.get(0));
			  }
				  }
				  else {
					  new_idb.add(f2);
				  }
			  }
			  else if (!checked.contains(args.get(1))) {
				  if(!done.contains(args.get(1))) {
					//  System.out.println("I entereddddddddddd");
					  if (!created) {
						  new_rig1.add(Expressions.makeFact("rig_edge1", scc_node,Expressions.makeAbstractConstant(support)));
						  new_rig2.add(Expressions.makeFact("rig_edge2", scc_node,Expressions.makeAbstractConstant(support)));
						  created=true;
					  }
					  new_idb.add(Expressions.makeFact("idb_edge",Expressions.makeAbstractConstant(support),args.get(1)));
					  done.add(args.get(1));
				  }
			  }
		  }
		  done.clear();
		  for (Fact f2:rig_edge1) {
			  List<Term> args=f2.getArguments();
			  if (!checked.contains(args.get(0))){
				  if (checked.contains(args.get(1))) {
					  if (!done.contains(args.get(0))&&(!embedded_idb.contains(args.get(0)))) {
					  if (graph_edb.contains(Expressions.makeFact("edb", args.get(0)))) {
				//		  System.out.println(Expressions.makeFact("SU",args.get(0),Expressions.makeAbstractConstant(set),Expressions.makeAbstractConstant(set)));
						  kb.addStatement(Expressions.makeFact("SU",args.get(0),Expressions.makeAbstractConstant(set),Expressions.makeAbstractConstant(set)));
					  }else {
						  String support2="in"+support_j;
			  new_rig1.add(Expressions.makeFact("rig_edge1", args.get(0),Expressions.makeAbstractConstant(support2)));
			  new_rig2.add(Expressions.makeFact("rig_edge2", args.get(0),Expressions.makeAbstractConstant(support2)));
			  new_idb.add(Expressions.makeFact("idb_edge",Expressions.makeAbstractConstant(support2),scc_node));
			  support_j++;
					  }
					  embedded_idb.add(args.get(0));
				  done.add(args.get(0));
				  }
				  }
				  else {
				  new_rig1.add(f2);
			  }
			  }
			  else if (!checked.contains(args.get(1))) {
				  if(!done.contains(args.get(1))) {
					  if (!non_supports.contains(Expressions.makeFact("scc", args.get(1),args.get(1)))) {
					  new_rig1.add(Expressions.makeFact("rig_edge1",scc_node,args.get(1)));
					  }else {
						  if (!created) {
							  new_rig1.add(Expressions.makeFact("rig_edge1", scc_node,Expressions.makeAbstractConstant(support)));
							  new_rig2.add(Expressions.makeFact("rig_edge2", scc_node,Expressions.makeAbstractConstant(support)));
							  created=true;
						  }
						  new_rig1.add(Expressions.makeFact("rig_edge1",Expressions.makeAbstractConstant(support),args.get(1)));
						  
					  }
					  done.add(args.get(1));
				  }
			  }
		  }
		  done.clear();
		  for (Fact f2:rig_edge2) {
			  List<Term> args=f2.getArguments();
			  if (!checked.contains(args.get(0))){
				  if (checked.contains(args.get(1))) {
					  if (!done.contains(args.get(0))&&(!embedded_idb.contains(args.get(0)))) {
					  if (graph_edb.contains(Expressions.makeFact("edb", args.get(0)))) {
//						  System.out.println(Expressions.makeFact("SU",args.get(0),Expressions.makeAbstractConstant(set),Expressions.makeAbstractConstant(set)));
						  kb.addStatement(Expressions.makeFact("SU",args.get(0),Expressions.makeAbstractConstant(set),Expressions.makeAbstractConstant(set)));
					  }else {
						  String support2="in"+support_j;
			  new_rig1.add(Expressions.makeFact("rig_edge1", args.get(0),Expressions.makeAbstractConstant(support2)));
			  new_rig2.add(Expressions.makeFact("rig_edge2", args.get(0),Expressions.makeAbstractConstant(support2)));
			  new_idb.add(Expressions.makeFact("idb_edge",Expressions.makeAbstractConstant(support2),scc_node));
			  support_j++;
					  }
					  embedded_idb.add(args.get(0));
				  done.add(args.get(0));
				  }
				  }
				  else {
					  new_rig2.add(f2);
				  }
			  }
			  else if (!checked.contains(args.get(1))) {
				  if(!done.contains(args.get(1))) {
					  if (!non_supports.contains(Expressions.makeFact("scc", args.get(1),args.get(1)))) {
					  new_rig2.add(Expressions.makeFact("rig_edge2",scc_node,args.get(1)));
					  }else {
						  if (!created) {
							  new_rig1.add(Expressions.makeFact("rig_edge1", scc_node,Expressions.makeAbstractConstant(support)));
							  new_rig2.add(Expressions.makeFact("rig_edge2", scc_node,Expressions.makeAbstractConstant(support)));
							  created=true;
						  }
						  new_rig2.add(Expressions.makeFact("rig_edge2",Expressions.makeAbstractConstant(support),args.get(1)));
						  
					  }
					  done.add(args.get(1));
				  }
			  }
		  }
		  reduce_graph(kb, new_rig1, new_rig2, new_idb, new_scc, graph_edb, non_supports);
		  
	  }
//	for (Fact f:new_rig1) {
//		System.out.println(f.toString());
//	}
//	for (Fact f:new_rig2) {
//		System.out.println(f.toString());
//	}
//	for (Fact f:new_idb) {
//		System.out.println(f.toString());
//	}
	kb.addStatements(new_rig1);
	kb.addStatements(new_rig2);
	kb.addStatements(new_idb);
	kb.addStatements(graph_edb);
	return kb;
  }

  public long compute2(String n,PrintWriter result,String kb_size,String query) throws ParsingException, IOException {
	  KnowledgeBase kb = new KnowledgeBase();
	  File x2 = new File(baseDir + "/galen-data/ali.csv");
		Scanner y2 = new Scanner(x2);
		//int i = 0;
		while (y2.hasNextLine()) {
			String a = y2.nextLine();
			a=a.replace("\t",",");
			String []split = a.split(",");
			String b ="";
			for (String s: split) {
				b=b+"\""+s+"\""+",";
			}
			b=b+";";
			b=b.replace(",;","");
		//	System.out.println(b);
			kb.addStatement(RuleParser.parseFact("ali("+b+")."));
			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
			//i++;
		}
		y2.close();
		
		File x21 = new File(baseDir + "/galen-data/conj.csv");
		Scanner y21 = new Scanner(x21);
		//int i = 0;
		while (y21.hasNextLine()) {
			String a = y21.nextLine();
			a=a.replace("\t",",");
			String []split = a.split(",");
			String b ="";
			for (String s: split) {
				b=b+"\""+s+"\""+",";
			}
			b=b+";";
			b=b.replace(",;","");
			kb.addStatement(RuleParser.parseFact("conj("+b+")."));
			//kb2.addStatement(RuleParser.parseFact("Check("+a+")."));
			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
			//i++;
		}
		y21.close();
		
		File x211 = new File(baseDir + "/galen-data/exists.csv");
		Scanner y211 = new Scanner(x211);
		//int i = 0;
		while (y211.hasNextLine()) {
			String a = y211.nextLine();
			a=a.replace("\t",",");
			String []split = a.split(",");
			String b ="";
			for (String s: split) {
				b=b+"\""+s+"\""+",";
			}
			b=b+";";
			b=b.replace(",;","");
			kb.addStatement(RuleParser.parseFact("exists("+b+")."));
		//	kb2.addStatement(RuleParser.parseFact("Verify("+a+")."));
			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
			//i++;
		}
		y211.close();
		
		File x2111 = new File(baseDir + "/galen-data/isMainClass.csv");
		Scanner y2111 = new Scanner(x2111);
		//int i = 0;
		while (y2111.hasNextLine()) {
			String a = y2111.nextLine();
			a=a.replace("\t",",");
			String []split = a.split(",");
			String b ="";
			for (String s: split) {
				b=b+"\""+s+"\""+",";
			}
			b=b+";";
			b=b.replace(",;","");
			kb.addStatement(RuleParser.parseFact("isMainClass("+b+")."));
			//kb2.addStatement(RuleParser.parseFact("opSucc("+a+")."));
			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
			//i++;
		}
		y2111.close();
		File x21111 = new File(baseDir + "/galen-data/isSubClass.csv");
		Scanner y21111 = new Scanner(x21111);
		//int i = 0;
		while (y21111.hasNextLine()) {
			String a = y21111.nextLine();
			a=a.replace("\t",",");
			String []split = a.split(",");
			String b ="";
			for (String s: split) {
				b=b+"\""+s+"\""+",";
			}
			b=b+";";
			b=b.replace(",;","");
			kb.addStatement(RuleParser.parseFact("isSubClass("+b+")."));
			//kb2.addStatement(RuleParser.parseFact("opSucc("+a+")."));
			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
			//i++;
		}
		y21111.close();
		File x211111 = new File(baseDir + "/galen-data/subProp.csv");
		Scanner y211111 = new Scanner(x211111);
		//int i = 0;
		while (y211111.hasNextLine()) {
			String a = y211111.nextLine();
			a=a.replace("\t",",");
			String []split = a.split(",");
			String b ="";
			for (String s: split) {
				b=b+"\""+s+"\""+",";
			}
			b=b+";";
			b=b.replace(",;","");
			kb.addStatement(RuleParser.parseFact("subProp("+b+")."));
			//kb2.addStatement(RuleParser.parseFact("opSucc("+a+")."));
			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
			//i++;
		}
		y211111.close();
		

		File x = new File(baseDir + "/s_rules.txt");
		Scanner y = new Scanner(x);
		while (y.hasNextLine()) {
			String xx = y.nextLine();
			// System.out.println(xx);
			RuleParser.parseInto(kb, xx);
		}
		y.close();
		 RuleParser.parseInto(kb, n);
	     HashSet<Fact>rig_edge1 = new HashSet<Fact>();
	     HashSet<Fact>rig_edge2 = new HashSet<Fact>();
	     HashSet<Fact>idb_edge = new HashSet<Fact>();
	     HashSet<Fact>scc = new HashSet<Fact>();
	     HashSet<Fact>non_supports = new HashSet<Fact>();
	     HashSet<Fact>graph_edb = new HashSet<Fact>();
	     long startTime2 = System.currentTimeMillis();
	    try (final Reasoner reasoner = new VLogReasoner(kb)) {
		      reasoner.setLogLevel(LogLevel.INFO);
		     reasoner.setLogFile(baseDir + "/aaa.txt");
	      
	      reasoner.reason();
	      System.out.println("first reason doneeeeeeeeeeeee");
//	      HashSet<String>a= new HashSet<String>();
//	      for (Rule r :kb.getRules()) {
//	    	  if (r.getHead().getLiterals().size()==1&&(!a.contains(r.getHead().getLiterals().get(0).getPredicate().getName()))) {
	    	//	  reasoner.exportQueryAnswersToCsv(r.getHead().getLiterals().get(0), baseDir+"/idb/"+r.getHead().getLiterals().get(0).getPredicate().getName()+".csv", true);
//	    		  a.add(r.getHead().getLiterals().get(0).getPredicate().getName());
//	    	  }
//	      }
//	      ExamplesUtils.printOutQueryAnswers("prov(?var1,?k) ",reasoner);
//		     ExamplesUtils.printOutQueryAnswers("scc(?x4,?x3)",reasoner);
//	   //   ExamplesUtils.printOutQueryAnswers("getSU(?v1,?k) ",reasoner);
//    ExamplesUtils.printOutQueryAnswers("getU(?var1,?k) ",reasoner);
//	      ExamplesUtils.printOutQueryAnswers("SU(?x,?U,?V) ",reasoner);
//	      ExamplesUtils.printOutQueryAnswers("edge_p(?x,?U,?Z) ",reasoner);
//	     // ExamplesUtils.printOutQueryAnswers("iterate(?x) ",reasoner);
//	      ExamplesUtils.printOutQueryAnswers("good(?x)",reasoner);
//	      ExamplesUtils.printOutQueryAnswers("edge_p(?x,?y,?z) ",reasoner);
//	      ExamplesUtils.printOutQueryAnswers("reaches2_p(?x,?y,?z) ",reasoner);

	     QueryResultIterator queryAnswers1 = reasoner.answerQuery(RuleParser.parsePositiveLiteral("rig_edge1(?x,?y)"), true);
			while (queryAnswers1.hasNext()) {
				QueryResult queryAnswer1 = queryAnswers1.next();
				rig_edge1.add(Expressions.makeFact("rig_edge1", Expressions.makeAbstractConstant(queryAnswer1.getTerms().get(0).toString().replace("_:", "")),Expressions.makeAbstractConstant(queryAnswer1.getTerms().get(1).toString().replace("_:", ""))));
			}
			
			QueryResultIterator queryAnswers2 = reasoner.answerQuery(RuleParser.parsePositiveLiteral("rig_edge2(?x,?y)"), true);
			while (queryAnswers2.hasNext()) {
				QueryResult queryAnswer2 = queryAnswers2.next();
				rig_edge2.add(Expressions.makeFact("rig_edge2", Expressions.makeAbstractConstant(queryAnswer2.getTerms().get(0).toString().replace("_:", "")),Expressions.makeAbstractConstant(queryAnswer2.getTerms().get(1).toString().replace("_:", ""))));
			}
			
			QueryResultIterator queryAnswers3 = reasoner.answerQuery(RuleParser.parsePositiveLiteral("idb_edge(?x,?y)"), true);
			while (queryAnswers3.hasNext()) {
				QueryResult queryAnswer3 = queryAnswers3.next();
				idb_edge.add(Expressions.makeFact("idb_edge", Expressions.makeAbstractConstant(queryAnswer3.getTerms().get(0).toString().replace("_:", "")),Expressions.makeAbstractConstant(queryAnswer3.getTerms().get(1).toString().replace("_:", ""))));
			}
			QueryResultIterator queryAnswers4 = reasoner.answerQuery(RuleParser.parsePositiveLiteral("scc(?x,?y)"), true);
			while (queryAnswers4.hasNext()) {
				QueryResult queryAnswer4 = queryAnswers4.next();
				List<Term> answers = queryAnswer4.getTerms();
				if (!answers.get(0).getName().equals(answers.get(1).getName())) {
				scc.add(Expressions.makeFact("scc", Expressions.makeAbstractConstant(queryAnswer4.getTerms().get(0).toString().replace("_:", "")),Expressions.makeAbstractConstant(queryAnswer4.getTerms().get(1).toString().replace("_:", ""))));
				}else {
					non_supports.add(Expressions.makeFact("scc", queryAnswer4.getTerms()));
				}
			}

			QueryResultIterator queryAnswers5 = reasoner.answerQuery(RuleParser.parsePositiveLiteral("graph_edb(?x)"), true);
			while (queryAnswers5.hasNext()) {
				QueryResult queryAnswer5 = queryAnswers5.next();
				graph_edb.add(Expressions.makeFact("edb", Expressions.makeAbstractConstant(queryAnswer5.getTerms().get(0).toString().replace("_:", ""))));
			}
	    }
	    System.out.println("doneeeeeeeee query");
//	      ExamplesUtils.printOutQueryAnswers("rig_edge1(?x,?y) ",reasoner);
//	      ExamplesUtils.printOutQueryAnswers("rig_edge2(?x,?y) ",reasoner);
//	      ExamplesUtils.printOutQueryAnswers("idb_edge(?x,?y) ",reasoner);
//			for (Fact f: rig_edge1) {
//				System.out.println(f);
//			}
//			for (Fact f: rig_edge2) {
//				System.out.println(f);
//			}
//			for (Fact f: idb_edge) {
//				System.out.println(f);
//			}
			KnowledgeBase kb3= new KnowledgeBase();
			reduce_graph(kb3,rig_edge1,rig_edge2,idb_edge, scc, graph_edb,non_supports);
			File xz = new File(baseDir + "/s_rules2.txt");
			Scanner yz = new Scanner(xz);
			while (yz.hasNextLine()) {
				String xxz = yz.nextLine();
				// System.out.println(xx);
				RuleParser.parseInto(kb3, xxz);
			}
			yz.close();
			try (final Reasoner reasoner = new VLogReasoner(kb3)) {
			      reasoner.setLogLevel(LogLevel.INFO);
			     reasoner.setLogFile(baseDir + "/aaa.txt");
		   //   System.out.println(kb3.getRules());
		      reasoner.reason();
			  	int num = 0;
				String save = "exist2"+num + ".csv";
				File file = new File(baseDir + "/exist2/", save);
				while(file.exists()) {
				    save = "exist2" + (num++) +".csv";
				    file = new File(baseDir + "/exist2/", save); 
				}
				//FileWriter fw = new FileWriter(file);
			      reasoner.exportQueryAnswersToCsv(RuleParser.parsePositiveLiteral("prov(?X,?Y)"), baseDir + "/exist2/"+save, true);
			       long endtime = System.currentTimeMillis();
			       long time = (endtime-startTime2);
			       result.print(reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("prov(?X,?Y)")).getCount()+" total sets, ");
			       int c=0;
			       QueryResultIterator queryAnswers1 = reasoner
							.answerQuery(RuleParser.parsePositiveLiteral(n.replace("iterate(?Z) :- ","").replace(").", ")")), true);
					 while(queryAnswers1.hasNext()) {
						QueryResult queryAnswer1 = queryAnswers1.next();
						
				String	 head_ann = queryAnswer1.getTerms().get(queryAnswer1.getTerms().size()-1).toString();
			       try (final QueryResultIterator queryAnswers = reasoner
							.answerQuery(RuleParser.parsePositiveLiteral("prov(?X,?Y)"), true)) {
						//System.out.println("yessssssss");
						while (queryAnswers.hasNext()) {
							//h2++;
							QueryResult queryAnswer = queryAnswers.next();
							if (queryAnswer.getTerms().toString().contains(head_ann)) {
								c++;
							}
						}
			       }
					 }
				      result.print(c+" query sets, ");
			       c1=reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("idb_edge(?X,?Y)")).getCount()
				    		  + reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("rig_edge1(?X,?Y)")).getCount()
				    			+	   reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("rig_edge2(?X,?Y)")).getCount();
			       result.print(c1+" rig_size"+", ");
			       //c2=reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("prov(?X,?Y)")).getCount();
			       //result.print(c2+" prov count"+", ");
			       count++;
			      // long facts =kb.getFacts().size();
//			       result.print(facts+", ");
//			       result.print(reasoner.getInferences().count()-facts+", ");
			       System.out.println("yesss");
			       return (time);
  }
  }
  public long compute3(String n) throws ParsingException, IOException {
	  KnowledgeBase kb = new KnowledgeBase();
		File x2 = new File(baseDir + "/galen-data/ali.csv");
		Scanner y2 = new Scanner(x2);
		//int i = 0;
		while (y2.hasNextLine()) {
			String a = y2.nextLine();
			a=a.replace("\t",",");
			String []split = a.split(",");
			String b ="";
			for (String s: split) {
				b=b+"\""+s+"\""+",";
			}
			b=b+";";
			b=b.replace(",;","");
			//System.out.println(b);
			kb.addStatement(RuleParser.parseFact("ali("+b+")."));
			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
			//i++;
		}
		y2.close();
		
		File x21 = new File(baseDir + "/galen-data/conj.csv");
		Scanner y21 = new Scanner(x21);
		//int i = 0;
		while (y21.hasNextLine()) {
			String a = y21.nextLine();
			a=a.replace("\t",",");
			String []split = a.split(",");
			String b ="";
			for (String s: split) {
				b=b+"\""+s+"\""+",";
			}
			b=b+";";
			b=b.replace(",;","");
			kb.addStatement(RuleParser.parseFact("conj("+b+")."));
			//kb2.addStatement(RuleParser.parseFact("Check("+a+")."));
			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
			//i++;
		}
		y21.close();
		
		File x211 = new File(baseDir + "/galen-data/exists.csv");
		Scanner y211 = new Scanner(x211);
		//int i = 0;
		while (y211.hasNextLine()) {
			String a = y211.nextLine();
			a=a.replace("\t",",");
			String []split = a.split(",");
			String b ="";
			for (String s: split) {
				b=b+"\""+s+"\""+",";
			}
			b=b+";";
			b=b.replace(",;","");
			kb.addStatement(RuleParser.parseFact("exists("+b+")."));
		//	kb2.addStatement(RuleParser.parseFact("Verify("+a+")."));
			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
			//i++;
		}
		y211.close();
		
		File x2111 = new File(baseDir + "/galen-data/isMainClass.csv");
		Scanner y2111 = new Scanner(x2111);
		//int i = 0;
		while (y2111.hasNextLine()) {
			String a = y2111.nextLine();
			a=a.replace("\t",",");
			String []split = a.split(",");
			String b ="";
			for (String s: split) {
				b=b+"\""+s+"\""+",";
			}
			b=b+";";
			b=b.replace(",;","");
			kb.addStatement(RuleParser.parseFact("isMainClass("+b+")."));
			//kb2.addStatement(RuleParser.parseFact("opSucc("+a+")."));
			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
			//i++;
		}
		y2111.close();
		File x21111 = new File(baseDir + "/galen-data/isSubClass.csv");
		Scanner y21111 = new Scanner(x21111);
		//int i = 0;
		while (y21111.hasNextLine()) {
			String a = y21111.nextLine();
			a=a.replace("\t",",");
			String []split = a.split(",");
			String b ="";
			for (String s: split) {
				b=b+"\""+s+"\""+",";
			}
			b=b+";";
			b=b.replace(",;","");
			kb.addStatement(RuleParser.parseFact("isSubClass("+b+")."));
			//kb2.addStatement(RuleParser.parseFact("opSucc("+a+")."));
			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
			//i++;
		}
		y21111.close();
		File x211111 = new File(baseDir + "/galen-data/subProp.csv");
		Scanner y211111 = new Scanner(x211111);
		//int i = 0;
		while (y211111.hasNextLine()) {
			String a = y211111.nextLine();
			a=a.replace("\t",",");
			String []split = a.split(",");
			String b ="";
			for (String s: split) {
				b=b+"\""+s+"\""+",";
			}
			b=b+";";
			b=b.replace(",;","");
			kb.addStatement(RuleParser.parseFact("subProp("+b+")."));
			//kb2.addStatement(RuleParser.parseFact("opSucc("+a+")."));
			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
			//i++;
		}
		y211111.close();
		

		File x = new File(baseDir + "/rules.txt");
		Scanner y = new Scanner(x);
		while (y.hasNextLine()) {
			String xx = y.nextLine();
			// System.out.println(xx);
			RuleParser.parseInto(kb, xx);
		}
		y.close();
	    try (final Reasoner reasoner = new VLogReasoner(kb)) {
		      reasoner.setLogLevel(LogLevel.INFO);
		     reasoner.setLogFile(baseDir + "/aaa.txt");
	      long startTime2 = System.currentTimeMillis();
	      reasoner.reason();
//	      HashSet<String>a= new HashSet<String>();
//	      for (Rule r :kb.getRules()) {
//	    	  if (r.getHead().getLiterals().size()==1&&(!a.contains(r.getHead().getLiterals().get(0).getPredicate().getName()))) {
	    	//	  reasoner.exportQueryAnswersToCsv(r.getHead().getLiterals().get(0), baseDir+"/idb/"+r.getHead().getLiterals().get(0).getPredicate().getName()+".csv", true);
//	    		  a.add(r.getHead().getLiterals().get(0).getPredicate().getName());
//	    	  }
//	      }
//	      ExamplesUtils.printOutQueryAnswers("prov(?var1,?k) ",reasoner);
//		     ExamplesUtils.printOutQueryAnswers("prov(?x4,?x3)",reasoner);
//	   //   ExamplesUtils.printOutQueryAnswers("getSU(?v1,?k) ",reasoner);
//	  //    ExamplesUtils.printOutQueryAnswers("getU(?var1,?k) ",reasoner);
//	      ExamplesUtils.printOutQueryAnswers("SU(?x,?U,?V) ",reasoner);
//	      ExamplesUtils.printOutQueryAnswers("edge_p(?x,?U,?Z) ",reasoner);
//	     // ExamplesUtils.printOutQueryAnswers("iterate(?x) ",reasoner);
//	      ExamplesUtils.printOutQueryAnswers("good(?x)",reasoner);
//	      ExamplesUtils.printOutQueryAnswers("reaches(?x,?x) ",reasoner);
//	      ExamplesUtils.printOutQueryAnswers("rig_edge1(?x,?y) ",reasoner);
//	      ExamplesUtils.printOutQueryAnswers("rig_edge2(?x,?y) ",reasoner);
//	      ExamplesUtils.printOutQueryAnswers("idb_edge(?x,?y) ",reasoner);

	     ExamplesUtils.printOutQueryAnswers("subClassOf(?A,?B)",reasoner);
	       long endtime = System.currentTimeMillis();
//		      result.print(reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("prov(?X,?Y)")).getCount()+" total sets, ");
//		      result.print(reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("prov_count(?X,?Y)")).getCount()+" query sets, ");
//	       c1=reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("idb_edge(?X,?Y)")).getCount()
//		    		  + reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("rig_edge1(?X,?Y)")).getCount()
//		    			+	   reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("rig_edge2(?X,?Y)")).getCount();
//	       result.print(c1+" rig_size"+", ");
	       //c2=reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("prov(?X,?Y)")).getCount();
	       //result.print(c2+" prov count"+", ");
	       count++;
	      // long facts =kb.getFacts().size();
//	       result.print(facts+", ");
//	       result.print(reasoner.getInferences().count()-facts+", ");
	       return ((endtime-startTime2)-c3);
  }
  }
//  
//  public static void main(final String[] args) throws IOException, ParsingException {
//	  SimpleReasoningExample  g = new SimpleReasoningExample();
//	  g.compute3("");
//	  
//  }
  //	  //compute();

     //ExamplesUtils.configureLogging(); // use simple logger for the example
   
      // System.out.println("eddy ya geddy");
      //ExamplesUtils.printOutQueryAnswers("reaches(?A,?B)", reasoner);
      //ProcessBuilder processBuilder = new ProcessBuilder();

      // -- Linux --
//try {
//      // Run a shell command
//      //processBuilder.command("bash", "-c", "mvn exec:java -Dexec.mainClass=org.semanticweb.rulewerk.examples.SimpleReasoningExample -e>ali.txt");
//      Process process = Runtime.getRuntime().exec("mvn exec:java -Dexec.mainClass=org.semanticweb.rulewerk.examples.Grounding -e");
//      //Process process2 = Runtime.getRuntime().exec("mvn exec:java -Dexec.mainClass=org.semanticweb.rulewerk.examples.SimpleReasoningExample -e>ali2.txt");
//      StringBuilder output = new StringBuilder();
//
//      BufferedReader reader = new BufferedReader(
//              new InputStreamReader(process.getInputStream()));
//
//      String line;
//      while ((line = reader.readLine()) != null) {
//          output.append(line + "\n");
//      }
//      int exitVal = process.waitFor();
//      if (exitVal == 0) {
//          System.out.println("Success!");
//          System.out.println(output);
//          System.exit(0);
//      } else {
//          //abnormal...
//      }
//
//  } catch (IOException e) {
//      e.printStackTrace();
//  } catch (InterruptedException e) {
//      e.printStackTrace();
//  }
//    }
//
//}
}
     // reasoner.exportQueryAnswersToCsv(Expressions.makePositiveLiteral("n", Expressions.makeUniversalVariable("x")),baseDir + "/555.csv",true);
      //reasoner.answerQuery(Expressions.makePositiveLiteral("empty", Expressions.makeUniversalVariable("x")),true);
      //reasoner.answerQuery(Expressions.makePositiveLiteral("set", Expressions.makeUniversalVariable("x")),true);
   

