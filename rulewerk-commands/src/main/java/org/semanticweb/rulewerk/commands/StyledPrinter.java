package org.semanticweb.rulewerk.commands;

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
 * Interface for printing given Strings to a writer using different styles.
 * 
 * @author Irina Dragoste
 *
 */
public interface StyledPrinter {

	void printNormal(String string);

	void printSection(String string);

	void printEmph(String string);

	void printCode(String string);

	void printImportant(String string);

	/**
	 * 
	 * @return the writer to print to
	 */
	Writer getWriter();

}
