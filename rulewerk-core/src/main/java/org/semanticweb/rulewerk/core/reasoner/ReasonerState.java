package org.semanticweb.rulewerk.core.reasoner;

/*
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
 * Enum for the states a {@link Reasoner} can be in. Certain operations are not
 * allowed in some states.
 *
 * @author Irina Dragoste
 *
 */
public enum ReasonerState {
	/**
	 * State a Reasoner is in before loading. Querying is not allowed in this state.
	 */
	KB_NOT_LOADED("knowledge base not loaded"),
	/**
	 * State a Reasoner is in after loading, and before method
	 * {@link Reasoner#reason()} has been called. The Reasoner can be queried.
	 */
	KB_LOADED("knowledge base loaded"),

	/**
	 * State a Reasoner is in after method {@link Reasoner#reason()} has been
	 * called.
	 */
	MATERIALISED("after reasoning"),

	/**
	 * State in which the knowledge base of an already loaded reasoner has been
	 * changed. This can occur if the knowledge base has been modified after loading
	 * (in {@link ReasonerState#KB_LOADED} state), or after reasoning (in
	 * {@link ReasonerState#MATERIALISED} state).
	 */

	KB_CHANGED("knowledge base changed"),
	/**
	 * State a Reasoner is in after method {@link Reasoner#close()} has been called.
	 * The Reasoner cannot reason again, once it reached this state. Loading,
	 * reasoning, adding rules, fact and fact data sources, setting the rule
	 * re-writing strategy, the reasoning algorithm and the reasoning timeout. are
	 * not allowed in this state.
	 */
	CLOSED("closed");

	private final String name;

	private ReasonerState(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
