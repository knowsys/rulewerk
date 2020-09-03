package org.semanticweb.rulewerk.core.reasoner;

/*-
 * #%L
 * Rulewerk Core Components
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

/**
 * Enumeration of different correctness results (for example, the correctness of
 * query answering for a reasoner).
 * 
 * @author Irina Dragoste
 *
 */
public enum Correctness {

	/**
	 * Completeness is not guaranteed, but soundness is. For example, query
	 * answering yields sound, but possibly incomplete answers.
	 */
	SOUND_BUT_INCOMPLETE("sound but possibly incomplete"),

	/**
	 * Soundness is not guaranteed. For example, query answering may give incorrect
	 * (unsound and incomplete) answers.
	 */
	INCORRECT("possibly incorrect"),

	/**
	 * Correctness is guaranteed. For example, query answering yealds are correct
	 * (sound and complete) answers.
	 */
	SOUND_AND_COMPLETE("sound and complete");

	private final String name;

	private Correctness(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}
