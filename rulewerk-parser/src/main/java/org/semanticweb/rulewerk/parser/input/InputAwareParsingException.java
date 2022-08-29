package org.semanticweb.rulewerk.parser.input;

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
