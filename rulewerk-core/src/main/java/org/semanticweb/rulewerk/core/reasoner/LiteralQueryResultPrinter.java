package org.semanticweb.rulewerk.core.reasoner;

import java.io.IOException;

/*-
 * #%L
 * Rulewerk Core Components
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

import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.api.QueryResult;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.TermType;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.model.implementation.Serializer;

/**
 * Class for writing {@link QueryResult} objects in pretty print.
 * 
 * @author Markus Kroetzsch
 *
 */
public class LiteralQueryResultPrinter {

	final LinkedHashMap<UniversalVariable, Integer> firstIndex = new LinkedHashMap<>();
	final Writer writer;
	final Serializer serializer;

	int resultCount = 0;

	/**
	 * Constructor.
	 * 
	 * @param positiveLiteral           the query pattern for which query results
	 *                                  are to be printed
	 * @param writer                    the object to write the output to
	 * @param prefixDeclarationRegistry information on prefixes used to compute IRI
	 *                                  abbreviations; can be null
	 */
	public LiteralQueryResultPrinter(PositiveLiteral positiveLiteral, Writer writer,
			PrefixDeclarationRegistry prefixDeclarationRegistry) {
		this.writer = writer;
		if (prefixDeclarationRegistry == null) {
			this.serializer = new Serializer(writer);
		} else {
			this.serializer = new Serializer(writer, prefixDeclarationRegistry);
		}

		int i = 0;
		for (Term term : positiveLiteral.getArguments()) {
			if (term.getType() == TermType.UNIVERSAL_VARIABLE) {
				UniversalVariable variable = (UniversalVariable) term;
				if (!firstIndex.containsKey(variable)) {
					firstIndex.put(variable, i);
				}
			}
			i++;
		}
	}

	/**
	 * Writes a {@link QueryResult} to the specified writer. Nothing is written for
	 * results of Boolean queries (not even a linebreak).
	 * 
	 * @param queryResult the {@link QueryResult} to write; this result must be
	 *                    based on the query literal specified in the constructor
	 * @throws IOException if a problem occurred in writing
	 */
	public void write(QueryResult queryResult) throws IOException {
		boolean first = true;
		for (Entry<UniversalVariable, Integer> entry : firstIndex.entrySet()) {
			if (first) {
				first = false;
			} else {
				writer.write(", ");
			}
			serializer.writeUniversalVariable(entry.getKey());
			writer.write(" -> ");
			serializer.writeTerm(queryResult.getTerms().get(entry.getValue()));
		}
		resultCount++;
		if (!first) {
			writer.write("\n");
		}
	}

	/**
	 * Returns the number of results written so far.
	 * 
	 * @return number of results
	 */
	public int getResultCount() {
		return resultCount;
	}

	/**
	 * Returns true if the query has had any results.
	 * 
	 * @return true if query result is not empty
	 */
	public boolean hadResults() {
		return resultCount != 0;
	}

	/**
	 * Returns true if the query is boolean, i.e., has no answer variables.
	 * 
	 * @return true if query is boolean
	 */
	public boolean isBooleanQuery() {
		return firstIndex.size() == 0;
	}

}
