package org.semanticweb.vlog4j.parser.javacc;

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

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.PrefixDeclarations;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.parser.LocalPrefixDeclarations;

/**
 * Basic methods used in the JavaCC-generated parser.
 * 
 * Implementation of some string escaping methods adapted from Apache Jena,
 * released under Apache 2.0 license terms.
 * 
 * @see <a href=
 *      "https://github.com/apache/jena/blob/master/jena-core/src/main/java/org/apache/jena/n3/turtle/ParserBase.java">https://github.com/apache/jena/blob/master/jena-core/src/main/java/org/apache/jena/n3/turtle/ParserBase.java</a>
 * 
 * @author Markus Kroetzsch
 * @author Larry Gonzalez
 * @author Jena developers, Apache Software Foundation (ASF)
 *
 */
public class JavaCCParserBase {
	final protected PrefixDeclarations prefixDeclarations = new LocalPrefixDeclarations();

	final protected List<Rule> rules = new ArrayList<>();
	final protected List<PositiveLiteral> facts = new ArrayList<>();

	/**
	 * "Local" variable to remember (universal) body variables during parsing.
	 */
	final protected HashSet<String> bodyVars = new HashSet<String>();
	/**
	 * "Local" variable to remember existential head variables during parsing.
	 */
	final protected HashSet<String> headExiVars = new HashSet<String>();;
	/**
	 * "Local" variable to remember universal head variables during parsing.
	 */
	final protected HashSet<String> headUniVars = new HashSet<String>();;

	/**
	 * Defines the context for parsing sub-formulas.
	 * 
	 * @author Markus Kroetzsch
	 *
	 */
	public enum FormulaContext {
		/**
		 * Formula is to be interpreted in the context of a rule head (positive
		 * occurrence).
		 */
		HEAD,
		/**
		 * Formula is to be interpreted in the context of a rule body (negative
		 * occurrence).
		 */
		BODY
	}

	protected Constant createBooleanLiteral(String lexicalForm) {
		// lexicalForm is one of ['true' or 'false']
		// we remove the quotes and add data type
		lexicalForm = lexicalForm.substring(1, lexicalForm.length() - 1);
		return Expressions.makeConstant(lexicalForm + "^^<" + PrefixDeclarations.XSD_BOOLEAN + ">");
	}

	protected Constant createIntegerLiteral(String lexicalForm) {
		return Expressions.makeConstant(lexicalForm + "^^<" + PrefixDeclarations.XSD_INTEGER + ">");
	}

	protected Constant createDecimalLiteral(String lexicalForm) {
		return Expressions.makeConstant(lexicalForm + "^^<" + PrefixDeclarations.XSD_DECIMAL + ">");
	}

	protected Constant createDoubleLiteral(String lexicalForm) {
		return Expressions.makeConstant(lexicalForm + "^^<" + PrefixDeclarations.XSD_DOUBLE + ">");
	}

	protected static String unescapeStr(String s, int line, int column) throws ParseException {
		return unescape(s, '\\', false, line, column);
	}

	protected static String unescape(String s, char escape, boolean pointCodeOnly, int line, int column)
			throws ParseException {
		int i = s.indexOf(escape);

		if (i == -1)
			return s;

		// Dump the initial part straight into the string buffer
		StringBuilder sb = new StringBuilder(s.substring(0, i));

		for (; i < s.length(); i++) {
			char ch = s.charAt(i);
			// Keep line and column numbers.
			switch (ch) {
			case '\n':
			case '\r':
				line++;
				column = 1;
				break;
			default:
				column++;
				break;
			}

			if (ch != escape) {
				sb.append(ch);
				continue;
			}

			// Escape
			if (i >= s.length() - 1)
				throw new ParseException("Illegal escape at end of string, line:" + line + ", column: " + column);
			char ch2 = s.charAt(i + 1);
			column = column + 1;
			i = i + 1;

			// Not just codepoints. Must be a legal escape.
			char ch3 = 0;
			switch (ch2) {
			case 'n':
				ch3 = '\n';
				break;
			case 't':
				ch3 = '\t';
				break;
			case 'r':
				ch3 = '\r';
				break;
			case 'b':
				ch3 = '\b';
				break;
			case 'f':
				ch3 = '\f';
				break;
			case '\'':
				ch3 = '\'';
				break;
			case '\"':
				ch3 = '\"';
				break;
			case '\\':
				ch3 = '\\';
				break;
			default:
				throw new ParseException("Unknown escape: \\" + ch2 + ", line:" + line + ", column: " + column);
			}
			sb.append(ch3);
		}
		return sb.toString();
	}

	/** Remove first and last characters (e.g. ' or "") from a string */
	protected static String stripQuotes(String s) {
		return s.substring(1, s.length() - 1);
	}

	/** Remove first 3 and last 3 characters (e.g. ''' or """) from a string */
	protected static String stripQuotes3(String s) {
		return s.substring(3, s.length() - 3);
	}

	/** remove the first n charcacters from the string */
	protected static String stripChars(String s, int n) {
		return s.substring(n, s.length());
	}

	protected String strRDFLiteral(String data, String lang, String dt) {
		// https://www.w3.org/TR/turtle/#grammar-production-String RDFLiteral
		String ret = "\"" + data + "\"";
		if (dt != null) {
			return ret += "^^" + dt;
			// return ret += "^^<" + dt+">";
		}
		if (lang != null) {
			// dt = "http://www.w3.org/1999/02/22-rdf-syntax-ns#langString"
			return ret += "@" + lang;
		}
		// return ret + "^^http://www.w3.org/2001/XMLSchema#string";
		return ret + "^^<http://www.w3.org/2001/XMLSchema#string>";
	}

	/**
	 * Reset the local set variables used when parsing a rule.
	 */
	protected void resetVariableSets() {
		this.bodyVars.clear();
		this.headExiVars.clear();
		this.headUniVars.clear();
	}

	public List<Rule> getRules() {
		return rules;
	}

	public List<PositiveLiteral> getFacts() {
		return facts;
	}

}
