package org.semanticweb.vlog4j.core.reasoner;

/*-
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 VLog4j Developers
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

import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;

public class LoggingTest {

	private static final Variable vx = Expressions.makeVariable("x");

	// p(?x) -> q(?x)
	private static final Atom ruleHeadQx = Expressions.makeAtom("q", vx);
	private static final Atom ruleBodyPx = Expressions.makeAtom("p", vx);
	private static final Rule rule = Expressions.makeRule(ruleHeadQx, ruleBodyPx);

	private static final Constant constantC = Expressions.makeConstant("c");
	private static final Atom factPc = Expressions.makeAtom("p", constantC);

	@Test
	public void testSetLogFile() throws EdbIdbSeparationException, IOException, ReasonerStateException {
		try (final Reasoner instance = Reasoner.getInstance()) {
			instance.addFacts(factPc);
			instance.addRules(rule);
			instance.setLogLevel(LogLevel.INFO);
			instance.setLogFile("src/test/data/log.out");
			instance.load();
			instance.reason();
		}
		// TODO test that the file was empty before the logging
		// TODO test that the logger appends to the file
		// TODO test that the log level and the log files can be set any time
	}

}
