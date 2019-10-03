package org.semanticweb.vlog4j.parser.javacc;

import java.util.ArrayList;

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

import java.util.HashSet;
import java.util.List;

import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.DataSource;
import org.semanticweb.vlog4j.core.model.api.PrefixDeclarations;
import org.semanticweb.vlog4j.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.vlog4j.core.model.implementation.DatatypeConstantImpl;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.model.implementation.LanguageStringConstantImpl;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.parser.LocalPrefixDeclarations;
import org.semanticweb.vlog4j.parser.ParserConfiguration;
import org.semanticweb.vlog4j.core.model.api.Predicate;

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
	final PrefixDeclarations prefixDeclarations = new LocalPrefixDeclarations();

	KnowledgeBase knowledgeBase;
    ParserConfiguration parserConfiguration;

	/**
	 * "Local" variable to remember (universal) body variables during parsing.
	 */
	final HashSet<String> bodyVars = new HashSet<String>();
	/**
	 * "Local" variable to remember existential head variables during parsing.
	 */
	final HashSet<String> headExiVars = new HashSet<String>();;
	/**
	 * "Local" variable to remember universal head variables during parsing.
	 */
	final HashSet<String> headUniVars = new HashSet<String>();;

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

	public JavaCCParserBase() {
		this.knowledgeBase = new KnowledgeBase();
        this.parserConfiguration = new ParserConfiguration();
	}

	Constant createIntegerConstant(String lexicalForm) {
		return Expressions.makeDatatypeConstant(lexicalForm, PrefixDeclarations.XSD_INTEGER);
	}

	Constant createDecimalConstant(String lexicalForm) {
		return Expressions.makeDatatypeConstant(lexicalForm, PrefixDeclarations.XSD_DECIMAL);
	}

	Constant createDoubleConstant(String lexicalForm) {
		return Expressions.makeDatatypeConstant(lexicalForm, PrefixDeclarations.XSD_DOUBLE);
	}

	void addDataSource(String predicateName, int arity, DataSource dataSource) {
		Predicate predicate = Expressions.makePredicate(predicateName, arity);
		knowledgeBase.addStatement(new DataSourceDeclarationImpl(predicate, dataSource));
	}

    static String[] collectStrings(String str, String[] rest) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add(str);

        for (String next : rest) {
            strings.add(next);
        }

        return strings.toArray(rest);
    }

	static String unescapeStr(String s, int line, int column) throws ParseException {
		return unescape(s, '\\', false, line, column);
	}

	static String unescape(String s, char escape, boolean pointCodeOnly, int line, int column) throws ParseException {
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
	static String stripQuotes(String s) {
		return s.substring(1, s.length() - 1);
	}

	/** Remove first 3 and last 3 characters (e.g. ''' or """) from a string */
	static String stripQuotes3(String s) {
		return s.substring(3, s.length() - 3);
	}

	/** remove the first n charcacters from the string */
	static String stripChars(String s, int n) {
		return s.substring(n, s.length());
	}

	/**
	 * Creates a suitable {@link Constant} from the parsed data.
	 * 
	 * @param string      the string data (unescaped)
	 * @param languageTag the language tag, or null if not present
	 * @param datatype    the datatype, or null if not provided
	 * @return suitable constant
	 */
	Constant createDataConstant(String string, String languageTag, String datatype) {
		// https://www.w3.org/TR/turtle/#grammar-production-String RDFLiteral
		if (datatype != null) {
			return new DatatypeConstantImpl(string, datatype);
		} else if (languageTag != null) {
			return new LanguageStringConstantImpl(string, languageTag);
		} else {
			return new DatatypeConstantImpl(string, "http://www.w3.org/2001/XMLSchema#string");
		}
	}

	/**
	 * Reset the local set variables used when parsing a rule.
	 */
	void resetVariableSets() {
		this.bodyVars.clear();
		this.headExiVars.clear();
		this.headUniVars.clear();
	}

	public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
		this.knowledgeBase = knowledgeBase;
	}

	public KnowledgeBase getKnowledgeBase() {
		return knowledgeBase;
	}

    public void setParserConfiguration(ParserConfiguration parserConfiguration) {
        this.parserConfiguration = parserConfiguration;
    }

    public ParserConfiguration getParserConfiguration() {
        return parserConfiguration;
    }


}
