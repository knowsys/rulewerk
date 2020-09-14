package org.semanticweb.rulewerk.commands;

import java.io.IOException;

/*-
 * #%L
 * Rulewerk command execution support
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

import java.io.Writer;

/**
 * Simple implementation of {@link StyledPrinter} based on an arbitrary
 * PrintWriter without any styling.
 * 
 * @author Markus Kroetzsch
 *
 */
public class SimpleStyledPrinter implements StyledPrinter {

	final Writer writer;

	public SimpleStyledPrinter(final Writer writer) {
		this.writer = writer;
	}

	@Override
	public void printNormal(String string) {
		write(string);
	}

	@Override
	public void printSection(String string) {
		write(string);
	}

	@Override
	public void printEmph(String string) {
		write(string);
	}

	@Override
	public void printCode(String string) {
		write(string);
	}

	@Override
	public void printImportant(String string) {
		write(string);
	}

	@Override
	public Writer getWriter() {
		return writer;
	}

	private void write(String string) {
		try {
			writer.write(string);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
