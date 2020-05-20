package org.semanticweb.rulewerk.owlapi;

/*-
 * #%L
 * Rulewerk OWL API Support
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

import org.semanticweb.rulewerk.core.exceptions.RulewerkRuntimeException;

/**
 * Exception that indicates that the translation of OWL into rules has failed
 * due to an expressive feature of OWL that cannot be captured in rules.
 *
 * @author Markus Krötzsch
 *
 */
public class OwlFeatureNotSupportedException extends RulewerkRuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = -194716185012512419L;

	/**
	 * Creates a new exception.
	 *
	 * @param cause
	 *            message explaining the error
	 */
	public OwlFeatureNotSupportedException(String cause) {
		super(cause);
	}

}
