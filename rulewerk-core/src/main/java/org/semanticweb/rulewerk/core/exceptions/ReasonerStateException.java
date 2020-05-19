/*
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

package org.semanticweb.rulewerk.core.exceptions;

import java.text.MessageFormat;

import org.semanticweb.rulewerk.core.reasoner.ReasonerState;

/**
 * Thrown when an operation that is invalid in current reasoner state is
 * attempted.
 *
 * @author Irina Dragoste
 *
 */
public class ReasonerStateException extends RulewerkRuntimeException {

	/**
	 * generated serial version UID
	 */
	private static final long serialVersionUID = -5720169752588784690L;

	private static final String messagePrefix = "Invalid operation for current reasoner state: {0}! {1}";

	/**
	 * Creates an exception with a logging message for current reasoner state.
	 *
	 * @param state
	 *            the current Reasoner state.
	 * @param message
	 *            describes the attempted operation
	 */
	public ReasonerStateException(ReasonerState state, String message) {
		super(MessageFormat.format(messagePrefix, state, message));
	}

}
