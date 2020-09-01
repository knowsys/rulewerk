package org.semanticweb.rulewerk.client.shell;

/*-
 * #%L
 * Rulewerk Client
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jline.builtins.Completers.TreeCompleter;
import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

public class DefaultShellConfigurationTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder(new File("."));

	public static final List<String> SHELL_COMMANDS = Arrays.asList("help", "load", "assert", "retract", "addsource",
			"delsource", "setprefix", "clear",
			"reason", "query", "export", "showkb", "exit");

	@Test
	public void buildPromptProvider() {
		final AttributedString promptProvider = new DefaultShellConfiguration().getDefaultPromptStyle();
		assertEquals("rulewerk> ", promptProvider.toString());
	}

	@Test
	public void buildPrompt() {
		final Terminal terminal = Mockito.mock(Terminal.class);
		Mockito.when(terminal.getType()).thenReturn(Terminal.TYPE_DUMB);
		final String string = new DefaultShellConfiguration().buildPrompt(terminal);
		assertTrue(string.length() >= 10);
	}

	@Test
	public void buildCompleterEmptyLine() {
		final ArrayList<String> readWords = new ArrayList<String>();

		final Set<String> candidates = this.getCompleterCandidates(readWords, "");
		final Set<String> expectedCandidates = SHELL_COMMANDS.stream().map(c -> "@" + c).collect(Collectors.toSet());
		assertEquals(expectedCandidates, candidates);
	}

	@Test
	public void buildCompleterHelp() {
		final ArrayList<String> readWords = new ArrayList<String>();
		readWords.add("@help");

		final Set<String> candidates = this.getCompleterCandidates(readWords, "");
		final Set<String> expectedCandidates = new HashSet<String>(SHELL_COMMANDS);
		assertEquals(expectedCandidates, candidates);
	}

	@Test
	public void buildCompleterLoad() {
		final ArrayList<String> readWords = new ArrayList<String>();
		readWords.add("@load");

		final Set<String> candidates = this.getCompleterCandidates(readWords, "");

		assertFalse(candidates.isEmpty());
		final String tempFolderName = this.folder.getRoot().getName();
		assertTrue(candidates.contains(tempFolderName));
	}

	private Set<String> getCompleterCandidates(final ArrayList<String> readWords, final String wordToComplete) {
		final List<Candidate> candidates = new ArrayList<>();

		final TreeCompleter completer = new DefaultShellConfiguration().buildCompleter(SHELL_COMMANDS);
		final LineReader reader = Mockito.mock(LineReader.class);

		final ParsedLine parsedLine = this.makeParsedLine(readWords, wordToComplete);
		completer.complete(reader, parsedLine, candidates);
		return candidates.stream().map(c -> c.value()).collect(Collectors.toSet());
	}


	private ParsedLine makeParsedLine(final List<String> readWords, final String wordToComplete) {
		final ParsedLine parsedLine = new ParsedLine() {

			@Override
			public List<String> words() {
				return readWords;
			}

			@Override
			public int wordIndex() {
				return readWords.size();
			}

			@Override
			public int wordCursor() {
				return this.word().length();
			}

			@Override
			public String word() {
				return wordToComplete;
			}

			@Override
			public String line() {
				// Only used by PipedlineCompleter
				return null;
			}

			@Override
			public int cursor() {
				return this.line().length();
			}
		};
		return parsedLine;
	}

}
