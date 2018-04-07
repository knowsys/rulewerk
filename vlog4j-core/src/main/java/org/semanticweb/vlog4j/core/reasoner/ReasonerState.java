package org.semanticweb.vlog4j.core.reasoner;

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

/**
 * Enum for the states a {@link VLogReasoner} can be in. Certain operations are not
 * allowed in some states.
 * 
 * @author Irina Dragoste
 *
 */
public enum ReasonerState {
	/**
	 * State a Reasoner is in before method {@link ReasonerInterface#load()} has
	 * been called. The Reasoner cannot reason before it has been loaded. The
	 * Reasoner can only be loaded once. Reasoning and querying are not allowed in
	 * this state.
	 */
	BEFORE_LOADING("before loading"),
	/**
	 * State a Reasoner is in after method {@link ReasonerInterface#load()} has been
	 * called, and before method {@link ReasonerInterface#reason()} has been called.
	 * The Reasoner can only be loaded once. Loading in this state is ineffective.
	 * Adding rules, fact and fact data sources and setting the rule re-writing
	 * strategy are not allowed in this state.
	 */
	AFTER_LOADING("loaded"),

	/**
	 * State a Reasoner is in after method {@link ReasonerInterface#reason()} has
	 * been called. The Reasoner cannot reason again, once it reached this state.
	 * Loading and setting the reasoning algorithm this state are ineffective.
	 * Reasoning, adding rules, fact and fact data sources and setting the rule
	 * re-writing strategy are not allowed in this state.
	 */
	AFTER_REASONING("completed reasoning");

	private final String name;

	private ReasonerState(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}
