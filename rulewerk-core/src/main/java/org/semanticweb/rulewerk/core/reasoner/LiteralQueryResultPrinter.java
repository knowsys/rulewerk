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
	final PrefixDeclarationRegistry prefixDeclarationRegistry;
	final Writer writer;
	final Serializer serializer;

	public LiteralQueryResultPrinter(PositiveLiteral positiveLiteral, Writer writer,
			PrefixDeclarationRegistry prefixDeclarationRegistry) {
		this.writer = writer;
		this.serializer = new Serializer(writer, prefixDeclarationRegistry);
		this.prefixDeclarationRegistry = prefixDeclarationRegistry;

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
		if (first) {
			writer.write("true");
		}
		writer.write("\n");
	}
}
