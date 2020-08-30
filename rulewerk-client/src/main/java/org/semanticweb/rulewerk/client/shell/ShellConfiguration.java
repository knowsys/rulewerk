package org.semanticweb.rulewerk.client.shell;

import java.io.IOException;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.semanticweb.rulewerk.commands.Interpreter;

public interface ShellConfiguration {

	LineReader buildLineReader(Terminal terminal, Interpreter interpreter);

	Terminal buildTerminal() throws IOException;

	String buildPrompt(Terminal terminal);

}
