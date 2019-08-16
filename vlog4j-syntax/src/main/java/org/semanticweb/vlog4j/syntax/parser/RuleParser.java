package org.semanticweb.vlog4j.syntax.parser;

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
import java.util.List;

import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.parser.implementation.javacc.JavaCCRuleParser;
import org.semanticweb.vlog4j.parser.implementation.javacc.ParseException;
import org.semanticweb.vlog4j.syntax.common.PrefixDeclarationException;

/**
 * Class to access VLog parsing functionality.
 * 
 * @FIXME Support parsing from multiple files (into one KB).
 * 
 * @author Markus Kroetzsch
 *
 */
public class RuleParser {

	JavaCCRuleParser parser;

	public void parse(InputStream stream, String encoding) throws ParsingException {
		parser = new JavaCCRuleParser(stream, encoding);
		doParse();
	}
	
	public void parse(InputStream stream) throws ParsingException {
		parse(stream, "UTF-8");
	}

	public void parse(String input) throws ParsingException {
		InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		parse(inputStream, "UTF-8");
	}

	void doParse() throws ParsingException {
		try {
			parser.parse();
		} catch (ParseException | PrefixDeclarationException e) {
			throw new ParsingException(e.getMessage(), e);
		}
	}

	public List<Rule> getRules() {
		return parser.getRules();
	}

	public List<PositiveLiteral> getFacts() {
		return parser.getFacts();
	}
}
