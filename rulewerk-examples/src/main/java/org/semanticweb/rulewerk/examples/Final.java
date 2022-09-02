package org.semanticweb.rulewerk.examples;

import java.io.File;
import java.io.FileWriter;

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


import java.io.IOException;
import java.util.Scanner;

import org.semanticweb.rulewerk.parser.ParsingException;

public class Final {
	final static String baseDir = "/mnt/d/el";
	public static void main(String[] args) throws ParsingException, IOException {
		FileWriter fw = new FileWriter(new File(baseDir + "/ground/count.txt"));
		FileWriter fw2 = new FileWriter(new File(baseDir + "/ground/sets.txt"));
for (int k=0;k<60;k++) {
		File x2 = new File(baseDir + "/ground/ali"+k+".txt");
		if (x2.exists()) {
		Scanner y2 = new Scanner(x2);
		Boolean b =false;
		// int i = 0;

		while (y2.hasNextLine()) {
			String a = y2.nextLine();
			//System.out.println(a);
			if (a.startsWith("target == ")) {
				int count = 0;
				for (int i = 0; i < a.length(); i++) {
					//System.out.println(a.charAt(i));
				    if (a.charAt(i) == '{') {
				        count++;
				    }
				}
				fw.write(count-1 + "\n");
				b=true;
			}
			
			// kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
			// kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
			// i++;
		}
		if (!b) {
			fw.write(0 + "\n");
		}
		
		y2.close();
		}
}
		fw.close();
		fw2.close();
		

	}

}
