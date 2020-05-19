package org.semanticweb.rulewerk.graal;

/*-
 * #%L
 * Rulewerk Graal Import Components
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
 * An exception to signify that a conversion from <a href="http://graphik-team.github.io/graal/">Graal</a> data structures to Rulewerk data structures could not
 * be made.
 *
 * @author Adrian Bielefeldt
 *
 */
public class GraalConvertException extends RulewerkRuntimeException {

	/**
	 * generated serial version UID
	 */
	private static final long serialVersionUID = -3228005099627492816L;

	public GraalConvertException(final String message) {
		super(message);
	}

	public GraalConvertException(final String message, final Throwable exception) {
		super(message, exception);
	}
}
