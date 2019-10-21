package org.vlog4j.client.picocli;

/*-
 * #%L
 * VLog4j Client
 * %%
 * Copyright (C) 2018 - 2019 VLog4j Developers
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

import picocli.CommandLine.Command;

@Command(name = "testacyclicity", description = "Test if the rule set satisfies any acyclicity notion")
public class VLog4jClientTestAcyclicity implements Runnable {

//  TODO implement the following method
//	@Option(names = "--acyclicity-notion", required = false, description = "Acyclicity notion. One of:JA (Joint Acyclicity), RJA (Restricted Joint Acyclicity), RFA (Model-Faithful Acyclicity), RMFA (Restricted Model-Faithful Acyclicity). All by default.")
//	String acyclicityNotion;

//  TODO implement the following method
//	@Option(names = "--rule-file", description = "Rule file in rls syntax", required = true)
//	private String rulePath;

	@Override
	public void run() {
		System.err.println("Not implemented yet.");
		System.err.println("Exiting the program.");
	}

}
