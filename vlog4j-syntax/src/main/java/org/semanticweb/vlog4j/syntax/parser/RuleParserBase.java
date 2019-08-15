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

import java.util.List;
import java.util.ArrayList;

import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.parser.implementation.javacc.ParseException;
import org.semanticweb.vlog4j.syntax.common.PrefixDeclarations;

import org.semanticweb.vlog4j.core.model.implementation.Expressions;

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
public class RuleParserBase {
	final protected PrefixDeclarations prefixDeclarations = new LocalPrefixDeclarations();
	final protected List<Rule> rules = new ArrayList<>();
	final protected List<PositiveLiteral> facts = new ArrayList<>();
	final protected List<PositiveLiteral> queries = new ArrayList<>();

	protected Constant createBooleanLiteral(String lexicalForm) {
		// lexicalForm is one of ['true' or 'false']
		// we remove the quotes and add data type
		lexicalForm = lexicalForm.substring(1,lexicalForm.length()-1);
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

	protected static String unescapeStr(String s) throws ParseException {
		return unescape(s, '\\', false, 1, 1);
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

			// \\u and \\U
			if (ch2 == 'u') {
				// i points to the \ so i+6 is next character
				if (i + 4 >= s.length())
					throw new ParseException("\\u escape too short, line:" + line + ", column: " + column);
				int x = hex(s, i + 1, 4, line, column);
				sb.append((char) x);
				// Jump 1 2 3 4 -- already skipped \ and u
				i = i + 4;
				column = column + 4;
				continue;
			}
			if (ch2 == 'U') {
				// i points to the \ so i+6 is next character
				if (i + 8 >= s.length())
					throw new ParseException("\\U escape too short, line:" + line + ", column: " + column);
				int x = hex(s, i + 1, 8, line, column);
				// Convert to UTF-16 codepoint pair.
				sb.append((char) x);
				// Jump 1 2 3 4 5 6 7 8 -- already skipped \ and u
				i = i + 8;
				column = column + 8;
				continue;
			}

			// Are we doing just point code escapes?
			// If so, \X-anything else is legal as a literal "\" and "X"

			if (pointCodeOnly) {
				sb.append('\\');
				sb.append(ch2);
				i = i + 1;
				continue;
			}

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

	// Line and column that started the escape
	protected static int hex(String s, int i, int len, int line, int column) throws ParseException {
//        if ( i+len >= s.length() )
//        {
//            
//        }
		int x = 0;
		for (int j = i; j < i + len; j++) {
			char ch = s.charAt(j);
			column++;
			int k = 0;
			switch (ch) {
			case '0':
				k = 0;
				break;
			case '1':
				k = 1;
				break;
			case '2':
				k = 2;
				break;
			case '3':
				k = 3;
				break;
			case '4':
				k = 4;
				break;
			case '5':
				k = 5;
				break;
			case '6':
				k = 6;
				break;
			case '7':
				k = 7;
				break;
			case '8':
				k = 8;
				break;
			case '9':
				k = 9;
				break;
			case 'A':
			case 'a':
				k = 10;
				break;
			case 'B':
			case 'b':
				k = 11;
				break;
			case 'C':
			case 'c':
				k = 12;
				break;
			case 'D':
			case 'd':
				k = 13;
				break;
			case 'E':
			case 'e':
				k = 14;
				break;
			case 'F':
			case 'f':
				k = 15;
				break;
			default:
				throw new ParseException("Illegal hex escape: " + ch + ", line:" + line + ", column: " + column);
			}
			x = (x << 4) + k;
		}
		return x;
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

	protected static String unescapePName(String s, int line, int column) throws ParseException {
		char escape = '\\';
		int idx = s.indexOf(escape);

		if (idx == -1)
			return s;

		int len = s.length();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < len; i++) {
			// Copied form unescape abobve - share!
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

			switch (ch2) {
			case '~':
			case '.':
			case '-':
			case '!':
			case '$':
			case '&':
			case '\'':
			case '(':
			case ')':
			case '*':
			case '+':
			case ',':
			case ';':
			case '=':
			case ':':
			case '/':
			case '?':
			case '#':
			case '@':
			case '%':
				sb.append(ch2);
				break;
			default:
				throw new ParseException(
						"Illegal prefix name escape: " + ch2 + ", line:" + line + ", column: " + column);
			}
		}
		return sb.toString();
	}

	public List<Rule> getRules() {
		return rules;
	}

	public List<PositiveLiteral> getFacts() {
		return facts;
	}

	public List<PositiveLiteral> getQueries() {
		return queries;
	}

}
