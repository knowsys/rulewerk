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
import java.util.Scanner;

public class Annotation {
	final static String baseDir = "C:\\Users\\Ali Elhalawati\\files\\bench\\rvcheck";
	public static void main (String[]args) throws IOException {
		File x2 = new File(baseDir + "\\rules.small.dl");
		FileWriter fw = new FileWriter(new File(baseDir + "\\rules.txt"));
		FileWriter fw2 = new FileWriter(new File(baseDir + "\\anotations.txt"));
		Scanner y2 = new Scanner(x2);
		int i = 0;
		while (y2.hasNextLine()) {
			String a = y2.nextLine();
			fw.write(a.replace(".", ", Rule("+i+").")+"\n");
			fw2.write("Rule("+i+")."+"\n");
			
			i++;
			
			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
			//i++;
		}
		fw.flush();
		fw.close();
		fw2.flush();
		fw2.close();
		y2.close();
}
}
