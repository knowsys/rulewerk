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
import org.semanticweb.rulewerk.commands.ClearCommandInterpreter;
import org.semanticweb.rulewerk.commands.ExportCommandInterpreter;
import org.semanticweb.rulewerk.commands.LoadCommandInterpreter;

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
	public void buildCompleterLoad_emptyLine() {
		final ArrayList<String> readWords = new ArrayList<String>();
		readWords.add("@load");

		final Set<String> candidates = this.getCompleterCandidates(readWords, "");

		assertFalse(candidates.isEmpty());
		final String tempFolderName = this.folder.getRoot().getName();
		assertTrue(candidates.contains(tempFolderName));

		assertTrue(candidates.contains(LoadCommandInterpreter.TASK_OWL));
		assertTrue(candidates.contains(LoadCommandInterpreter.TASK_RDF));
		assertTrue(candidates.contains(LoadCommandInterpreter.TASK_RLS));
	}

	@Test
	public void buildCompleterLoad_task_OWL() {
		final ArrayList<String> readWords = new ArrayList<String>();
		readWords.add("@load");
		readWords.add(LoadCommandInterpreter.TASK_OWL);

		final Set<String> candidates = this.getCompleterCandidates(readWords, "");

		assertFalse(candidates.isEmpty());
		final String tempFolderName = this.folder.getRoot().getName();
		assertTrue(candidates.contains(tempFolderName));

		assertFalse(candidates.contains(LoadCommandInterpreter.TASK_OWL));
		assertFalse(candidates.contains(LoadCommandInterpreter.TASK_RDF));
		assertFalse(candidates.contains(LoadCommandInterpreter.TASK_RLS));
	}

	@Test
	public void buildCompleterLoad_task_RDF() {
		final ArrayList<String> readWords = new ArrayList<String>();
		readWords.add("@load");
		readWords.add(LoadCommandInterpreter.TASK_RDF);

		final Set<String> candidates = this.getCompleterCandidates(readWords, "");

		assertFalse(candidates.isEmpty());
		final String tempFolderName = this.folder.getRoot().getName();
		assertTrue(candidates.contains(tempFolderName));

		assertFalse(candidates.contains(LoadCommandInterpreter.TASK_OWL));
		assertFalse(candidates.contains(LoadCommandInterpreter.TASK_RDF));
		assertFalse(candidates.contains(LoadCommandInterpreter.TASK_RLS));
	}

	@Test
	public void buildCompleterLoad_task_RLS() {
		final ArrayList<String> readWords = new ArrayList<String>();
		readWords.add("@load");
		readWords.add(LoadCommandInterpreter.TASK_RLS);

		final Set<String> candidates = this.getCompleterCandidates(readWords, "");

		assertFalse(candidates.isEmpty());
		final String tempFolderName = this.folder.getRoot().getName();
		assertTrue(candidates.contains(tempFolderName));

		assertFalse(candidates.contains(LoadCommandInterpreter.TASK_OWL));
		assertFalse(candidates.contains(LoadCommandInterpreter.TASK_RDF));
		assertFalse(candidates.contains(LoadCommandInterpreter.TASK_RLS));
	}

	@Test
	public void buildCompleterLoad_file() {
		final ArrayList<String> readWords = new ArrayList<String>();
		readWords.add("@load");
		final String tempFolderName = this.folder.getRoot().getName();
		readWords.add(tempFolderName);

		final Set<String> candidates = this.getCompleterCandidates(readWords, "");

		assertFalse(candidates.contains(LoadCommandInterpreter.TASK_OWL));
		assertFalse(candidates.contains(LoadCommandInterpreter.TASK_RDF));
		assertFalse(candidates.contains(LoadCommandInterpreter.TASK_RLS));
	}

	@Test
	public void buildCompleterExport_emptyLine() {
		final ArrayList<String> readWords = new ArrayList<String>();
		readWords.add("@export");

		final Set<String> candidates = this.getCompleterCandidates(readWords, "");

		final HashSet<String> expectedCandidates = new HashSet<>();
		expectedCandidates.add(ExportCommandInterpreter.TASK_INFERENCES);
		expectedCandidates.add(ExportCommandInterpreter.TASK_KB);

		assertEquals(expectedCandidates, candidates);
	}

	@Test
	public void buildCompleterExport_task_INFERENCES() {
		final ArrayList<String> readWords = new ArrayList<String>();
		readWords.add("@export");
		readWords.add(ExportCommandInterpreter.TASK_INFERENCES);

		final Set<String> candidates = this.getCompleterCandidates(readWords, "");

		final String tempFolderName = this.folder.getRoot().getName();
		assertTrue(candidates.contains(tempFolderName));

		assertFalse(candidates.contains(ExportCommandInterpreter.TASK_INFERENCES));
		assertFalse(candidates.contains(ExportCommandInterpreter.TASK_KB));
	}

	@Test
	public void buildCompleterExport_unknown() {
		final ArrayList<String> readWords = new ArrayList<String>();
		readWords.add("@export");
		readWords.add("unknown");

		final Set<String> candidates = this.getCompleterCandidates(readWords, "");
		assertTrue(candidates.isEmpty());
	}

	@Test
	public void buildCompleterExport_task_KB() {
		final ArrayList<String> readWords = new ArrayList<String>();
		readWords.add("@export");
		readWords.add(ExportCommandInterpreter.TASK_KB);

		final Set<String> candidates = this.getCompleterCandidates(readWords, "");

		final String tempFolderName = this.folder.getRoot().getName();
		assertTrue(candidates.contains(tempFolderName));

		assertFalse(candidates.contains(ExportCommandInterpreter.TASK_INFERENCES));
		assertFalse(candidates.contains(ExportCommandInterpreter.TASK_KB));
	}

	@Test
	public void buildCompleterClear_emptyLine() {
		final ArrayList<String> readWords = new ArrayList<String>();
		readWords.add("@clear");

		final Set<String> candidates = this.getCompleterCandidates(readWords, "");

		final HashSet<String> expectedCandidates = new HashSet<>();
		expectedCandidates.add(ClearCommandInterpreter.TASK_ALL);
		expectedCandidates.add(ClearCommandInterpreter.TASK_FACTS);
		expectedCandidates.add(ClearCommandInterpreter.TASK_INFERENCES);
		expectedCandidates.add(ClearCommandInterpreter.TASK_PREFIXES);
		expectedCandidates.add(ClearCommandInterpreter.TASK_RULES);
		expectedCandidates.add(ClearCommandInterpreter.TASK_SOURCES);

		assertEquals(expectedCandidates, candidates);
	}

	@Test
	public void buildCompleterClear_unknown() {
		final ArrayList<String> readWords = new ArrayList<String>();
		readWords.add("@clear");
		readWords.add("unknown");

		final Set<String> candidates = this.getCompleterCandidates(readWords, "");
		assertTrue(candidates.isEmpty());
	}

	@Test
	public void buildCompleterClear_task_ALL() {
		final ArrayList<String> readWords = new ArrayList<String>();
		readWords.add("@clear");
		readWords.add(ClearCommandInterpreter.TASK_ALL);

		final Set<String> candidates = this.getCompleterCandidates(readWords, "");
		assertTrue(candidates.isEmpty());
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
