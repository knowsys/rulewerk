package org.semanticweb.vlog4j.core.reasoner;

/*-
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 - 2019 VLog4j Developers
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
 * Enum for different reasoning stages a {@link Reasoner} may be in, with respect to its {@link KnowledgeBase}.
 * @author Irina Dragoste
 *
 */
public enum ReasoningState {

	//TODO should we have different states for incomplete due to halting, vs incomplete due to adding facts for non-negated rules?
	/**
	 * Reasoning has not completed. Query answering yields correct, but possibly incomplete answers.
	 */
	INCOMPLETE,

	/**
	 * Query answering may give incorrect answers. Re-reasoning ({@link Reasoner#reason()}) is required, in order to obtain correct results. 
	 */
	WRONG,

	/** 
	 * Reasoning over current knowledge base is complete, and query answering yields correct and complete results.
	 */
	COMPLETE

}
