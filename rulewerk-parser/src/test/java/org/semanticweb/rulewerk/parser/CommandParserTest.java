package org.semanticweb.rulewerk.parser;

/*-
 * #%L
 * Rulewerk Parser
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

import static org.junit.Assert.*;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Argument;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.parser.javacc.JavaCCParser;

public class CommandParserTest {

	@Test
	public void parseCommand() throws ParsingException {
		String input = "@query p(?X, a):- q(?X) \"string\" abcd  p(a) <http://example.org>.";
		Command command = RuleParser.parseSyntaxFragment(input, JavaCCParser::command, "command", null);
		assertEquals("query", command.getName());
		assertEquals(5, command.getArguments().size());
		assertTrue(command.getArguments().get(0).fromRule().isPresent());
		assertTrue(command.getArguments().get(1).fromTerm().isPresent());
		assertTrue(command.getArguments().get(2).fromTerm().isPresent());
		assertTrue(command.getArguments().get(3).fromPositiveLiteral().isPresent());
		assertTrue(command.getArguments().get(4).fromTerm().isPresent());
	}
	
//	@Test
//	public void parseCommandTest() throws ParsingException {
//		String input = "@myprefix wdqs: <https://query.wikidata.org/> .";
////		String input = "@mysource diseaseId[2]: sparql(wdqs:sparql, \"disease,doid\", \"?disease wdt:P699 ?doid .\") .";
//		Command command = RuleParser.parseSyntaxFragment(input, JavaCCParser::command, "command", null);
//		for (Argument argument : command.getArguments()) {
//			System.out.println("-");
//		}
//	}
}
