package org.semanticweb.vlog4j.parser.api;

/*-
 * #%L
 * vlog4j-parser
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.semanticweb.vlog4j.parser.implementation.javacc.JavaCCRuleParser;


public class RuleParser extends JavaCCRuleParser {

	public RuleParser(InputStream stream) {
		super(stream, "UTF-8");
	}
	
	public RuleParser(InputStream stream, String encoding) {
		super(stream, encoding);
	}

	public RuleParser(String rules) {
		super(new ByteArrayInputStream(rules.getBytes()), "UTF-8");
	}
}
