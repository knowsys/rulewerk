package org.semanticweb.rulewerk.examples;

/*-
 * #%L
 * Rulewerk Examples
 * %%
 * Copyright (C) 2018 - 2021 Rulewerk Developers
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
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.QueryResult;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.LogLevel;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;
import org.semanticweb.rulewerk.rdf.RdfModelConverter;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

public class Grounding {
	final static String baseDir = "/mnt/c/Users/Ali Elhalawati/files/el";
	// public static long i = 0;
	// public static long time = 0;
	static RdfModelConverter rdfModelConverter = new RdfModelConverter();
	public static int k = 1;
	public static int s = 1;
	public int h = 0;
	public static int h2 = 0;
	// public static long total = 0;
	public long total2 = 0;
	public static long total3 = 0;
	LinkedHashSet<String> heads = new LinkedHashSet<String>();
	LinkedHashSet<String> results = new LinkedHashSet<String>();
	HashSet<String> done = new HashSet<String>();
	HashSet<String> edbs = new HashSet<String>();
	public static String target = "";
	public static String first = "";

	public void finalizeOr(LinkedHashSet<String> result) throws IOException {
		// Random rand = new Random();
		// int n = rand.nextInt(20000);
		int num = 0;
		String save = "ground" + num + ".txt";
		File file = new File(baseDir + "/ground/", save);
		while (file.exists()) {
			save = "ground" + (num++) + ".txt";
			file = new File(baseDir + "/ground/", save);
		}
		FileWriter fw = new FileWriter(file);
		k++;
		for (String head : heads) {
			ArrayList<String> total = new ArrayList<String>();
			for (String r : result) {
				// System.out.println(r);
				// System.out.println(head);
				if (r.startsWith(head)) {
					// System.out.println("yesssssssss");
					total.add(r);
				}
			}
			if (total.size() > 1) {
				String rule = head + " ::=";
				for (String s : total) {
					// System.out.println(s);
					String[] m = s.split(" ::= ");
					rule = rule + m[1].replace(";", "|");
				}
				rule = rule + ";";
				rule = rule.replace("|;", ";");
				rule = rule.replaceAll("\\(<", "(").replaceAll(">\\)", ")").replaceAll(",<", ",").replaceAll(">,", ",");
				fw.write(rule + "\n");
			} else {
				String rule = total.get(0);
				rule = rule.replaceAll("\\(<", "(").replaceAll(">\\)", ")").replaceAll(",<", ",").replaceAll(">,", ",");
				fw.write(rule + "\n");
				// System.out.println("one"+rule);
			}
		}
		fw.flush();
		fw.close();
	}

//	public void ground_one(KnowledgeBase kb, Reasoner reasoner, HashSet<String> edb_predicateNames,
//			HashSet<String> Current_goalAtoms) throws ParsingException {
//		HashSet<String> Next_goalAtoms = new HashSet<String>();
//		if (!Current_goalAtoms.isEmpty()) {
//			for (String goal : Current_goalAtoms) {
//				h2++;
//				done.add(goal);
//				PositiveLiteral pl = RuleParser.parsePositiveLiteral(goal);
//				List<Term> constant = pl.getArguments();
//				for (Rule r : kb.getRules()) {
//					if (r.getHead().getLiterals().get(0).getPredicate().getName().equals(pl.getPredicate().getName())) {
//						List<Term> ruleInstance_variables = r.getBody().getLiterals().get(0).getArguments();
//						List<Term> goal_variables = r.getHead().getLiterals().get(0).getArguments();
//						List<Term> goal_bindings = new LinkedList<Term>();
//						String rule_instance = r.getBody().getLiterals().get(0).getPredicate().getName();
//						for (Term t : ruleInstance_variables) {
//							boolean match = false;
//							for (Term t2 : goal_variables) {
//								if (t.equals(t2) && !match) {
//									goal_bindings.add(constant.get(goal_variables.indexOf(t2)));
//									match = true;
//								}
//							}
//							if (!match) {
//								goal_bindings.add(t);
//							}
//						}
//						rule_instance = rule_instance + goal_bindings.toString().replace("[", "(").replace("]", ")");
//						try (final QueryResultIterator queryAnswers = reasoner
//								.answerQuery(RuleParser.parsePositiveLiteral(rule_instance), true)) {
//							// h2++;
//							// System.out.println("yessssssss");
//							if (queryAnswers.hasNext()) {
//								QueryResult queryAnswer = queryAnswers.next();
//								HashMap<Term, Term> bindings = new HashMap<Term, Term>();
//								for (int i = 0; i < queryAnswer.getTerms().size(); i++) {
//									bindings.put(ruleInstance_variables.get(i), queryAnswer.getTerms().get(i));
//								}
//								String match_body = "";
//								String match_head = goal + " ::=";
//								for (Rule rr : kb.getRules()) {
//									if (rr.getHead().getLiterals().get(0).equals(r.getBody().getLiterals().get(0))) {
//										for (Literal lit2 : rr.getBody().getLiterals()) {
//											// h++;
//											String tobeGrounded_bodyAtom = lit2.getPredicate().getName();
//											List<Term> tobeGrounded_bodyAtom_variables = lit2.getArguments();
//											List<Term> body_bindings = new LinkedList<Term>();
//											for (Term t : tobeGrounded_bodyAtom_variables) {
//												body_bindings.add(bindings.get(t));
//											}
//											tobeGrounded_bodyAtom = tobeGrounded_bodyAtom
//													+ body_bindings.toString().replace("[", "(").replace("]", ")");
//											// System.out.println(tobeGrounded_bodyAtom);
//											if (!edb_predicateNames.contains(lit2.getPredicate().getName())
//													&& !done.contains(tobeGrounded_bodyAtom)) {
//												Next_goalAtoms.add(tobeGrounded_bodyAtom);
//											}
//											match_body = match_body + "~ " + tobeGrounded_bodyAtom;
//										}
//										match_body = match_body + ";";
//										match_body = "*" + match_body;
//										match_body = match_body.replace("*~", "");
//										String satisfied_match = match_head + " " + match_body;
//										results.add(satisfied_match);
//
//									}
//
//								}
//							}
//						}
//					}
//				}
//			}
//			ground_one(kb, reasoner, edb_predicateNames, Next_goalAtoms);
//		}
//
//	}

	public void ground_two(KnowledgeBase kb, Reasoner reasoner, HashSet<String> edb_predicateNames,
			LinkedHashSet<String> Current_goalAtoms) throws ParsingException {
		LinkedHashSet<String> Next_goalAtoms = new LinkedHashSet<String>();
		if (!Current_goalAtoms.isEmpty()) {
			for (String goal : Current_goalAtoms) {
				// h++;
				if (!done.contains(goal)) {
				if (s == 1) {
					first= goal;
					s--;
				}
				done.add(goal);
				PositiveLiteral pl = RuleParser.parsePositiveLiteral(goal);
				List<Term> constant = pl.getArguments();
				for (Rule r : kb.getRules()) {
					if (r.getHead().getLiterals().get(0).getPredicate().getName().equals(pl.getPredicate().getName())) {
						List<Term> ruleInstance_variables = r.getBody().getLiterals().get(0).getArguments();
						List<Term> goal_variables = r.getHead().getLiterals().get(0).getArguments();
						List<Term> goal_bindings = new LinkedList<Term>();
						for (Term t : ruleInstance_variables) {
							boolean match = false;
							for (Term t2 : goal_variables) {
								if (t.equals(t2) && !match) {
									// System.out.println(t.toString());
									// System.out.println(t2.toString());
									goal_bindings.add(constant.get(goal_variables.indexOf(t2)));
									match = true;
								}
							}
							if (!match) {
								goal_bindings.add(t);
							}
						}
						String head_ann = "";
						QueryResultIterator queryAnswers1 = reasoner.answerQuery(pl, true);
						while (queryAnswers1.hasNext()) {
							QueryResult queryAnswer1 = queryAnswers1.next();
							head_ann = queryAnswer1.getTerms().get(queryAnswer1.getTerms().size() - 1).toString();
							head_ann = head_ann.replace("_:", "");
					//	}
							// System.out.println(head_ann);
							String rule_instance = r.getBody().getLiterals().get(0).getPredicate().getName();
						rule_instance = rule_instance + goal_bindings.toString().replace("[", "(").replace("]", ")");
						try (final QueryResultIterator queryAnswers = reasoner
								.answerQuery(RuleParser.parsePositiveLiteral(rule_instance), true)) {
							// System.out.println("yessssssss");
						//	if (queryAnswers.hasNext()) {
							while (queryAnswers.hasNext()) {
								if (goal.equals(first)) {
									target = head_ann;
									head_ann = "target";
									//s--;
								}
								// h2++;
								String body_ann = "";
								QueryResult queryAnswer = queryAnswers.next();
								
								HashMap<Term, Term> bindings = new HashMap<Term, Term>();
								for (int i = 0; i < queryAnswer.getTerms().size(); i++) {
									bindings.put(ruleInstance_variables.get(i), queryAnswer.getTerms().get(i));
								}
								String match_body = "";
								// String match_head = goal + " ::=";
								for (Rule rr : kb.getRules()) {
									if (rr.getHead().getLiterals().get(0).equals(r.getBody().getLiterals().get(0))) {
										for (Literal lit2 : rr.getBody().getLiterals()) {
											String tobeGrounded_bodyAtom = lit2.getPredicate().getName();
											List<Term> tobeGrounded_bodyAtom_variables = lit2.getArguments();
											List<Term> body_bindings = new LinkedList<Term>();
											for (Term t : tobeGrounded_bodyAtom_variables) {
												if (bindings.get(t).isConstant()) {
													body_bindings.add(bindings.get(t));
												} else {
													body_bindings.add(t);
													body_ann = bindings.get(t).toString().replace("_:", "")
															.replaceAll("\\s", "");
												}
												// System.out.println(body_ann);
											}
											tobeGrounded_bodyAtom = tobeGrounded_bodyAtom
													+ body_bindings.toString().replace("[", "(").replace("]", ")").replaceAll("\\?prov([0-9]+)", "\\?prov");
											// System.out.println(tobeGrounded_bodyAtom+" "+body_ann);
											// System.out.println(lit2.getPredicate().getName());
											if (!edb_predicateNames.contains(lit2.getPredicate().getName())
													&& !done.contains(tobeGrounded_bodyAtom)&& !body_ann.equals(target)) {
												Next_goalAtoms.add(tobeGrounded_bodyAtom);
											} else if (edb_predicateNames.contains(lit2.getPredicate().getName())) {
												// System.out.println("yesssss");
												// System.out.println(body_ann);
												edbs.add(body_ann);
											}else
											if (body_ann.equals(target)) {
												body_ann = "target";
											}
											match_body = match_body + "~ " + body_ann;
										}
										match_body = match_body + ";";
										match_body = "*" + match_body;
										match_body = match_body.replace("*~", "");
										String satisfied_match = head_ann + " ::= " + match_body;
										// System.out.println(satisfied_match);
										h++;
										results.add(satisfied_match);

									}
								}

								}
						}
						}
							}
						
					}
			ground_two(kb, reasoner, edb_predicateNames, Next_goalAtoms);
		}
			}
		}
		}

	public LinkedHashSet<String> fix(HashSet<String> ali2) {
		LinkedHashSet<String> res = new LinkedHashSet<String>();
		for (String s : results) {
			String k = "";
			String[] s1 = s.split("::=");
			s1[0] = "<" + s1[0] + ">";
			s1[0] = s1[0].replaceAll("\\s", "");
			heads.add(s1[0]);
			k = k + s1[0] + " ::=";
			String[] s2 = s1[1].split("~ ");
			for (int i = 0; i < s2.length; i++) {
				s2[i] = s2[i].replaceAll("\\s", "");
				String[] s3 = s2[i].split(";");
				// System.out.println(s2[i]);
				if (edbs.contains(s3[0])) {
					s2[i] = "\"" + s2[i] + "\"";
				} else {
					s2[i] = "<" + s2[i] + ">";
				}
				k = k + " " + s2[i];
			}
			k = k.replace(";>", ">;");
			k = k.replace(";\"", "\";").replace("< ", "<").replace("\" ", "\"");
			res.add(k);
		}
		return res;
	}

	public ArrayList<String> fix2(HashSet<String> ali2) {
		ArrayList<String> res = new ArrayList<String>();
		for (String s : results) {
			String k = "";
			String[] s1 = s.split("::=");
			s1[0] = "<" + s1[0] + ">";
			s1[0] = s1[0].replaceAll("\\s", "");
			heads.add(s1[0]);
			k = k + s1[0] + " ::=";
			String[] s2 = s1[1].split("~ ");
			for (int i = 0; i < s2.length; i++) {
				s2[i] = s2[i].replaceAll("\\s", "");
				String[] s3 = s2[i].split("\\(");
				if (ali2.contains(s3[0])) {
					s2[i] = "\"" + s2[i] + "\"";
				} else {
					s2[i] = "<" + s2[i] + ">";
				}
				k = k + " " + s2[i];
			}
			k = k.replace(";>", ">;");
			k = k.replace(";\"", "\";").replace("< ", "<").replace("\" ", "\"");
			res.add(k);
		}
		return res;
	}

	  private static Model parseRdfResource(final InputStream inputStream, final URI baseURI, final RDFFormat rdfFormat)
				throws IOException, RDFParseException, RDFHandlerException {
			final Model model = new LinkedHashModel();
			final RDFParser rdfParser = Rio.createParser(rdfFormat);
			rdfParser.setRDFHandler(new StatementCollector(model));
			rdfParser.parse(inputStream, baseURI.toString());

			return model;
		}
	public long compute(String n, PrintWriter result, String kb_size, String query)
			throws IOException, ParsingException, RDFParseException, RDFHandlerException {
		 KnowledgeBase kb = new KnowledgeBase();
		// KnowledgeBase kb2 = new KnowledgeBase();
        HashSet<String>ali2= new HashSet<String>();
    //    HashSet<Rule>rules= new HashSet<Rule>();
        ali2.add("isMainClass1_p");
		ali2.add("isSubClass1_p");
		ali2.add("subClassOf1_p");
		ali2.add("conj1_p");
		ali2.add("exists1_p");
		ali2.add("TRIPLE_p");
		ali2.add("subPropChain1_p");
		ali2.add("subProp1_p");
		ali2.add("ClassSubject_p");
		ali2.add("ClassObject_p");
		final File rdfXMLResourceFile = new File(baseDir+ "/owlapi.xrdf");
		final FileInputStream inputStreamISWC2016 = new FileInputStream(rdfXMLResourceFile);
		/* An RDF Model is obtained from parsing the RDF/XML resource. */
		final Model rdfModelISWC2016 = parseRdfResource(inputStreamISWC2016, rdfXMLResourceFile.toURI(),
				RDFFormat.RDFXML);

		/*
		 * Using rulewerk-rdf library, we convert RDF Model triples to facts, each
		 * having the ternary predicate "TRIPLE".
		 */
		final Set<Fact> tripleFactsISWC2016 = rdfModelConverter.rdfModelToFacts(rdfModelISWC2016);
		//System.out.println(tripleFactsISWC2016.toString());
		kb.addStatements(tripleFactsISWC2016);
		//System.out.println("Example triple fact from iswc-2016 dataset:");
		 File x2 = new File(baseDir + "/galen-data/subClassOf.csv");
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
				kb.addStatement(RuleParser.parseFact("subClassOf1("+b+")."));
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
				kb.addStatement(RuleParser.parseFact("conj1("+b+")."));
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
				kb.addStatement(RuleParser.parseFact("exists1("+b+")."));
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
				kb.addStatement(RuleParser.parseFact("isMainClass1("+b+")."));
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
				kb.addStatement(RuleParser.parseFact("isSubClass1("+b+")."));
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
				kb.addStatement(RuleParser.parseFact("subProp1("+b+")."));
				//kb2.addStatement(RuleParser.parseFact("opSucc("+a+")."));
				//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
				//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
				//i++;
			}
			y211111.close();
			File x = new File(baseDir + "/gr.txt");
			Scanner y = new Scanner(x);
			while (y.hasNextLine()) {
				String xx = y.nextLine();
				// System.out.println(xx);
				RuleParser.parseInto(kb, xx);
			}
			y.close();
		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.setLogLevel(LogLevel.INFO);
			// reasoner.setLogFile(baseDir + "example_log.log");
			
			  long startTime = System.currentTimeMillis();
			LinkedHashSet<String> sols = new LinkedHashSet<String>();
//			long startTime1 = System.currentTimeMillis();
			reasoner.reason();
			long endTime1 = System.currentTimeMillis();
			total2 = (endTime1 - startTime);		
			result.print("\n" + total2 + " reason time, ");
			//System.out.println("done");
			// ArrayList<String>result=ground(kb, reasoner);
			// HashSet<Fact> hf= new HashSet<Fact>(kb.getFacts());

			/*
			 * ali2.add("treatment"); ali2.add("physician"); ali2.add("medprescription");
			 * ali2.add("hospital");
			 */
			// for (Fact f:kb.getFacts()) {
			// ali2.add(f.toString());
			// }
			
//			 long startTime2 = System.currentTimeMillis();
			long startTime2 = System.currentTimeMillis();
			sols.add(n);
			ground_two(kb, reasoner, ali2, sols);
		//	System.out.println("done2");
			// System.out.println(time+" query time");
			// time=0;
			// long endTime2 = System.currentTimeMillis();
//			 System.out.println(endTime2-startTime2+" grounding time");
			// total+=endTime2-startTime;
			// System.out.println(total+" grounding time");
			LinkedHashSet<String> fixed = fix(ali2);
			// for (String s:results) {
			// System.out.println(s);
			// }
			finalizeOr(fixed);
			long endTime2 = System.currentTimeMillis();
			result.print(h + " goals, ");
			// h = 0;
//			System.out.println(h2 + " number of queries");
			// h2 = 0;
			return ((endTime2 - startTime2)+total2);
		}

	}

	public long compute2(String n) throws IOException, ParsingException {
		KnowledgeBase kb = new KnowledgeBase();
		HashSet<String> ali2 = new HashSet<String>();
		HashSet<String> ali3 = new HashSet<String>();
		ali2.add("isMainClass");
		ali2.add("isSubClass");
		ali2.add("conj");
		ali2.add("exists");
		ali2.add("ali");
		ali2.add("subPropChain");
		ali2.add("subProp");
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
		File x = new File(baseDir + "/ground.txt");
		Scanner y = new Scanner(x);
		while (y.hasNextLine()) {
			String xx = y.nextLine();
			// System.out.println(xx);
			RuleParser.parseInto(kb, xx);
		}
		y.close();
		// System.out.println(fact);

		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.setLogLevel(LogLevel.INFO);
			// reasoner.setLogFile(baseDir + "example_log.log");
			HashSet<String> sols = new HashSet<String>();
			long startTime = System.currentTimeMillis();
//			long startTime1 = System.currentTimeMillis();
			reasoner.reason();
			long endTime1 = System.currentTimeMillis();
			total3 += (endTime1 - startTime);
			System.out.println(total3 + " reasoning time2");
			// ArrayList<String>result=ground(kb, reasoner);
			// HashSet<Fact> hf= new HashSet<Fact>(kb.getFacts());

			/*
			 * ali2.add("treatment"); ali2.add("physician"); ali2.add("medprescription");
			 * ali2.add("hospital");
			 */
			// for (Fact f:kb.getFacts()) {
			// ali2.add(f.toString());
			// }
			sols.add(n);
//			 long startTime2 = System.currentTimeMillis();
			long startTime2 = System.currentTimeMillis();
		//	ground_one(kb, reasoner, ali3, sols);
			// System.out.println(time+" query time");
			// time=0;
			// long endTime2 = System.currentTimeMillis();
//			 System.out.println(endTime2-startTime2+" grounding time");
			// total+=endTime2-startTime;
			// System.out.println(total+" grounding time");
			ArrayList<String> fixed = fix2(ali3);
			// for (String s:results) {
			// System.out.println(s);
			// }
			//finalizeOr(fixed);
			long endTime2 = System.currentTimeMillis();
			System.out.println(h2 + " number of goals2");
			// h = 0;
//			System.out.println(h2 + " number of queries");
			// h2 = 0;
			return (endTime2 - startTime2);
		}

	}

}
