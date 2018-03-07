package org.semanticweb.vlog4j.core.model;

/*
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 VLog4j Developers
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

import java.util.Collections;
import java.util.List;

public class Atom {

	private final String predicateName;

	private final List<Term> arguments;

	public Atom(String predicateName, List<Term> arguments) {
		this.predicateName = predicateName;
		this.arguments = Collections.unmodifiableList(arguments);
	}

	public String getPredicateName() {
		return predicateName;
	}

	/**
	 * Returns the argument list as an unmodifiableList. An
	 * {@link UnsupportedOperationException} is thrown, when an attempt to modify
	 * the list occurs.
	 * 
	 * @return an unmodifiableList representing Atom arguments
	 */
	public List<Term> getArguments() {
		return arguments;
	}

	// TODO: toString, which format?

	// TODO hashCode, equals
}
