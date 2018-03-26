package org.semanticweb.vlog4j.core.reasoner.exceptions;

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

import java.text.MessageFormat;
import java.util.Set;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.TermType;

/**
 * Exception thrown when attempting to add {@link Atom} facts with terms that
 * are not of type {@link TermType#CONSTANT}. A fact can only have constant
 * terms.
 * 
 * @author Irina Dragoste
 *
 */
public class FactTermTypeException extends VLog4jException {

	/**
	 * generated serial version UID
	 */
	private static final long serialVersionUID = -840382271107281366L;

	private static final String messagePattern = "Only Constant terms alowed in Fact atoms! The following non-constant terms {0} appear for fact [{1}]!";

	/**
	 * Creates an exception with a logging message for given fact and its
	 * non-constant terms.
	 * 
	 * @param nonConstantTerms
	 *            the set of terms in given {@code fact} which are not of type
	 *            {@link TermType#CONSTANT}.
	 * @param fact
	 *            fact to be added to the knowledge base.
	 */
	public FactTermTypeException(final Set<Term> nonConstantTerms, final Atom fact) {
		super(MessageFormat.format(messagePattern, nonConstantTerms, fact));
	}

}
