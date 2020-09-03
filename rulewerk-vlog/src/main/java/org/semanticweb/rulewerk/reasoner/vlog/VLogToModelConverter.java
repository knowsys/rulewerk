package org.semanticweb.rulewerk.reasoner.vlog;

/*
 * #%L
 * Rulewerk VLog Reasoner Support
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

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.rulewerk.core.exceptions.RulewerkRuntimeException;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.api.QueryResult;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.AbstractConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.DatatypeConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.LanguageStringConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.NamedNullImpl;
import org.semanticweb.rulewerk.core.reasoner.implementation.QueryResultImpl;

/**
 * Utility class with static methods for converting from VLog internal model
 * ({@code karmaresearch.vlog} objects) to VLog API model
 * ({@code org.semanticweb.rulewerk.core.model.api}) objects.
 *
 * @author Irina Dragoste
 *
 */
class VLogToModelConverter {

	/**
	 * Converts internal VLog query results (represented as arrays of
	 * {@link karmaresearch.vlog.Term}s) into VLog model API QueryResults.
	 *
	 * @param vLogQueryResult an array of terms that represent an answer to a query.
	 * @return a QueryResult containing the corresponding {@code vLogQueryResult} as
	 *         a List of {@link Term}s.
	 */
	static QueryResult toQueryResult(karmaresearch.vlog.Term[] vLogQueryResult) {
		return new QueryResultImpl(toTermList(vLogQueryResult));
	}

	/**
	 * Converts an array of internal VLog terms ({@link karmaresearch.vlog.Term})
	 * into the corresponding list of VLog API model {@link Term}.
	 *
	 * @param vLogTerms input terms array, to be converted to a list of
	 *                  corresponding {@link Term}s.
	 * @return list of {@link Term}s, where each element corresponds to the element
	 *         in given {@code vLogTerms} at the same position.
	 */
	static List<Term> toTermList(karmaresearch.vlog.Term[] vLogTerms) {
		final List<Term> terms = new ArrayList<>(vLogTerms.length);
		for (final karmaresearch.vlog.Term vLogTerm : vLogTerms) {
			terms.add(toTerm(vLogTerm));
		}
		return terms;
	}

	/**
	 * Converts an internal VLog term ({@link karmaresearch.vlog.Term}) to a VLog
	 * API model {@link Term} of the same type and name.
	 *
	 * @param vLogTerm term to be converted
	 * @return a ({@link karmaresearch.vlog.Term}) with the same name as given
	 *         {@code vLogTerm} and of the corresponding type.
	 */
	static Term toTerm(karmaresearch.vlog.Term vLogTerm) {
		final String name = vLogTerm.getName();
		switch (vLogTerm.getTermType()) {
		case CONSTANT:
			return toConstant(name);
		case BLANK:
			return new NamedNullImpl(name);
		case VARIABLE:
			throw new IllegalArgumentException(
					"VLog variables cannot be converted without knowing if they are universally or existentially quantified.");
		default:
			throw new IllegalArgumentException("Unexpected VLog term type: " + vLogTerm.getTermType());
		}
	}

	/**
	 * Creates a {@link Constant} from the given VLog constant name.
	 *
	 * @param vLogConstantName the string name used by VLog
	 * @return {@link Constant} object
	 */
	static Constant toConstant(String vLogConstantName) {
		final Constant constant;
		if (vLogConstantName.charAt(0) == '<' && vLogConstantName.charAt(vLogConstantName.length() - 1) == '>') {
			// strip <> off of IRIs
			constant = new AbstractConstantImpl(vLogConstantName.substring(1, vLogConstantName.length() - 1));
		} else if (vLogConstantName.charAt(0) == '"') {
			if (vLogConstantName.charAt(vLogConstantName.length() - 1) == '>') {
				final int startTypeIdx = vLogConstantName.lastIndexOf('<', vLogConstantName.length() - 2);
				final String datatype = vLogConstantName.substring(startTypeIdx + 1, vLogConstantName.length() - 1);
				final String lexicalValue = vLogConstantName.substring(1, startTypeIdx - 3);
				constant = new DatatypeConstantImpl(lexicalValue, datatype);
			} else {
				final int startTypeIdx = vLogConstantName.lastIndexOf('@', vLogConstantName.length() - 2);
				if (startTypeIdx > -1) {
					final String languageTag = vLogConstantName.substring(startTypeIdx + 1, vLogConstantName.length());
					final String string = vLogConstantName.substring(1, startTypeIdx - 1);
					constant = new LanguageStringConstantImpl(string, languageTag);
				} else if (vLogConstantName.charAt(vLogConstantName.length() - 1) == '"'
						&& vLogConstantName.length() > 1) {
					// This is already an unexpceted case. Untyped strings "constant" should not
					// occur. But if they do, this is our best guess on how to interpret them.
					constant = new DatatypeConstantImpl(vLogConstantName.substring(1, vLogConstantName.length() - 1),
							PrefixDeclarationRegistry.XSD_STRING);
				} else {
					throw new RulewerkRuntimeException("VLog returned a constant name '" + vLogConstantName
							+ "' that Rulewerk cannot make sense of.");
				}
			}
		} else {
			constant = new AbstractConstantImpl(vLogConstantName);
		}
		return constant;
	}

}
