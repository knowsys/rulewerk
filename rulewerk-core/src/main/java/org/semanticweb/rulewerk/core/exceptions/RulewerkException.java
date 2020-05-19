package org.semanticweb.rulewerk.core.exceptions;

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

/**
 * Top-level checked exception for Rulewerk system.
 * @author Irina Dragoste
 *
 */
public class RulewerkException extends Exception {

	/**
	 * generated serial version UID
	 */
	private static final long serialVersionUID = 8305375071519734590L;

	public RulewerkException(Throwable cause) {
		super(cause);
	}

	public RulewerkException(String message, Throwable cause) {
		super(message, cause);
	}

	public RulewerkException(String message) {
		super(message);
	}

	public RulewerkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
