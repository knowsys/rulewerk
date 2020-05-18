package org.semanticweb.rulewerk.parser;

/*-
 * #%L
 * Rulewerk Parser
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
import static org.junit.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class DirectiveHandlerTest {
	private static final String STRING = "src/test/resources/facts.rls";
	private static final URI IRI = URI.create("https://example.org");
	private static final Term TERM = Expressions.makeDatatypeConstant(STRING, IRI.toString());

	private static final DirectiveArgument STRING_ARGUMENT = DirectiveArgument.string(STRING);
	private static final DirectiveArgument IRI_ARGUMENT = DirectiveArgument.iri(IRI);
	private static final DirectiveArgument TERM_ARGUMENT = DirectiveArgument.term(TERM);

	@Test
	public void validateStringArgument_stringArgument_succeeds() throws ParsingException {
		assertEquals(STRING, DirectiveHandler.validateStringArgument(STRING_ARGUMENT, "string argument"));
	}

	@Test(expected = ParsingException.class)
	public void validateStringArgument_iriArgument_throws() throws ParsingException {
		DirectiveHandler.validateStringArgument(IRI_ARGUMENT, "string argument");
	}

	@Test(expected = ParsingException.class)
	public void validateStringArgument_termArgument_throws() throws ParsingException {
		DirectiveHandler.validateStringArgument(TERM_ARGUMENT, "string argument");
	}

	@Test
	public void validateIriArgument_iriArgument_succeeds() throws ParsingException {
		assertEquals(IRI, DirectiveHandler.validateIriArgument(IRI_ARGUMENT, "iri argument"));
	}

	@Test(expected = ParsingException.class)
	public void validateIriArgument_StringArgument_throws() throws ParsingException {
		DirectiveHandler.validateIriArgument(STRING_ARGUMENT, "iri argument");
	}

	@Test(expected = ParsingException.class)
	public void validateIriArgument_termArgument_throws() throws ParsingException {
		DirectiveHandler.validateIriArgument(TERM_ARGUMENT, "iri argument");
	}

	@Test
	public void validateTermArgument_termArgument_succeeds() throws ParsingException {
		assertEquals(TERM, DirectiveHandler.validateTermArgument(TERM_ARGUMENT, "term argument"));
	}

	@Test(expected = ParsingException.class)
	public void validateTermArgument_stringArgument_throws() throws ParsingException {
		DirectiveHandler.validateTermArgument(STRING_ARGUMENT, "term argument");
	}

	@Test(expected = ParsingException.class)
	public void validateTermArgument_iriArgument_throws() throws ParsingException {
		DirectiveHandler.validateTermArgument(IRI_ARGUMENT, "term argument");
	}

	@Test
	public void validateFilenameArgument_filename_succeeds() throws ParsingException {
		assertEquals(new File(STRING), DirectiveHandler.validateFilenameArgument(STRING_ARGUMENT, "filename argument"));
	}

	@Test
	public void validateFilenameArgument_invalidFilename_throws() throws ParsingException {
		DirectiveHandler.validateFilenameArgument(DirectiveArgument.string(STRING + "-nonexistant"),
				"filename argument");
	}

	@Test
	public void validateUrlArgument_url_succeeds() throws ParsingException, MalformedURLException {
		assertEquals(IRI.toURL(), DirectiveHandler.validateUrlArgument(IRI_ARGUMENT, "urls argument"));
	}

	@Test(expected = ParsingException.class)
	public void validateUrlArgument_invalidUrl_throws() throws ParsingException {
		DirectiveHandler.validateUrlArgument(DirectiveArgument.iri(URI.create("example://test")), "url argument");
	}

}
