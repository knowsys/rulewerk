package org.semanticweb.rulewerk.examples;

import java.io.BufferedWriter;

/*-
 * #%L
 * Rulewerk Examples
 * %%
 * Copyright (C) 2028 - 2021 Rulewerk Developers
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
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.semanticweb.rulewerk.parser.ParsingException;

public class Exp {
	final static String baseDir = "/mnt/c/Users/Ali Elhalawati/files/el";
	public void RunExp(String kb_size,String query) throws ParsingException, FileNotFoundException, RDFParseException, RDFHandlerException {
		//	ArrayList<Long> a = new ArrayList<Long>();
		//ArrayList<Long> b = new ArrayList<Long>();
		HashSet<String> facts= new HashSet<String>();
		File x5 = new File(baseDir + "/"+query+".txt");

		Scanner y5 = new Scanner(x5);
		String xx = "";
		while (y5.hasNextLine()) {
			xx = y5.nextLine();
			//n--;
			// System.out.println(xx);
			// RuleParser.parseInto(kb, xx);
			String fact = query+"(";
			xx=xx.replaceAll("\\s", "");
			String[] one = xx.split(",");
			for (String k : one) {
				// System.out.println(k);
				if (k.contains("->")) {
				String[] two = k.split("->");
				fact = fact + two[1] + ",";
				}
				else {
					fact = fact+k;
				}
			}
			fact = fact + ")";
			fact = fact.replace(",)", ")");
			fact = fact.replaceAll("\\s", "");
			facts.add(fact);
		}
		
		Random rand = new Random();

		// Obtain a number between [0 - 49].
//		long n1 = 0;
//		long n2 = 0;
//		long n3 = 0;
		

		for (int i = 0; i < 1; i++) {
			int n = rand.nextInt(facts.size());
			List<String> list = new ArrayList<String>( facts );
			String fct= list.get(n);
			
			
			String fix = fct.replace(query, query+"_p").replace(")", ",?Z)");
			String fact2 = "iterate(?Z) :- " + fix+".";
		System.out.println(fact2);
			try(FileWriter fw = new FileWriter(baseDir + "/results.txt", true);
				    BufferedWriter bw = new BufferedWriter(fw);
				    PrintWriter out = new PrintWriter(bw))
				{
				SimpleReasoningExample g = new SimpleReasoningExample();
			//Grounding g2 = new Grounding();
			SimpleReasoningExample e = new SimpleReasoningExample();
			
			long a1 = g.compute2(fact2,out,kb_size,query);
			//a.add(a1);
			//n1 = n1 + a1;
			
			out.print(a1+" sets time, ");
			out.flush();
			//result.add(a.toString());
			//result.add(n1+" ");
			//long a3 = g2.compute2(fct);
			//c.add(a3);
			//n3 = n3 + a3;
			//System.out.println(a3+" gr exp"+i);
			long a2 = e.compute(fact2, out,kb_size,query);
			//b.add(a2);
			//n2 = n2 + a2;
			out.print(a2+ " sets time scc");
			out.flush();
			//result.add(b.toString());
			//result.add(n2+" ");

//System.out.println(c.toString());
//System.out.println(n3);
		
			    //out.println(result.toString().replace("[", "").replace("]", ""));
			    //more code
			   // out.println("more text");
			    //more code
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
			}
y5.close();
	}
		
	}

	public static void main(String[]args) throws IOException, ParsingException, RDFParseException, RDFHandlerException {
		Exp a = new Exp();
		a.RunExp("200k", "subClassOf");
	}
}
