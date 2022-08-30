package org.semanticweb.rulewerk.parser.input;

/*-
 * #%L
 * Rulewerk Parser
 * %%
 * Copyright (C) 2018 - 2022 Rulewerk Developers
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

import org.semanticweb.rulewerk.core.exceptions.RulewerkException;
import org.semanticweb.rulewerk.parser.javacc.Token;

public class InputAwareParsingException extends RulewerkException {

	private static final long serialVersionUID = 2849123381757026724L;

	private final Token token;

	public InputAwareParsingException(Throwable cause, Token token) {
		super(cause);
		this.token = token;
	}

	public InputAwareParsingException(Token token, String message) {
		super(message);
		this.token = token;
	}

	public InputAwareParsingException(Token token, String message, Throwable cause) {
		super(message, cause);
		this.token = token;
	}

//	public InputAwareParsingException(String message, Throwable cause) {
//		super(message, cause);
//	}
//
//	public InputAwareParsingException(String message) {
//		super(message);
//	}
//
//	public InputAwareParsingException(Throwable cause) {
//		super(cause);
//	}

	public Token getToken() {
		return token;
	}

}
