package org.semanticweb.vlog4j.core.reasoner.exceptions;

import java.text.MessageFormat;

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

import java.util.Set;

import org.semanticweb.vlog4j.core.model.api.Predicate;

/**
 * Exception thrown when attempting to load the reasoner with a knowledge base (facts and rules) that contains predicates that
 * are both EDB (occur in facts) and IDB (occur in rule heads). Predicates that
 * occur in facts cannot appear in rule heads.
 * 
 * @author Irina Dragoste
 *
 */
public class EdbIdbSeparationException extends VLog4jException {

	/**
	 * generated serial version UID
	 */
	private static final long serialVersionUID = -6731598892649856691L;

	private static final String messagePattern = "The following predicates occur both in facts (EDBs) and rule heads (IDBs): {0}!";

	/**
	 * Creates an exception with a logging message for given predicates.
	 * @param edbIdbPredicates predicates which are both EDB (occur in facts) and IDB (occur in rule heads).
	 */
	public EdbIdbSeparationException(Set<Predicate> edbIdbPredicates) {
		super(MessageFormat.format(messagePattern, edbIdbPredicates));
	}

}
