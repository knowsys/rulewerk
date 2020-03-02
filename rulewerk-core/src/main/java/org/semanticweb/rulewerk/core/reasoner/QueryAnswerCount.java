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
 * Container for correctness and number of query answers, i.e. the number of
 * facts that the query maps to.
 * 
 * Depending on the state of the reasoning (materialisation) and its
 * {@link KnowledgeBase}, the answers can have a different {@link Correctness}
 * <ul>
 * <li>If {@link Correctness#SOUND_AND_COMPLETE}, materialisation over current
 * knowledge base has completed, and the query answers are guaranteed to be
 * correct.</li>
 * <li>If {@link Correctness#SOUND_BUT_INCOMPLETE}, the results are guaranteed
 * to be sound, but may be incomplete. This can happen
 * <ul>
 * <li>when materialisation has not completed ({@link Reasoner#reason()} returns
 * {@code false}),</li>
 * <li>or when the knowledge base was modified after reasoning, and the
 * materialisation does not reflect the current knowledge base.
 * Re-materialisation ({@link Reasoner#reason()}) is required in order to obtain
 * complete query answers with respect to the current knowledge base.</li>
 * </ul>
 * </li>
 * <li>If {@link Correctness#INCORRECT}, the results may be incomplete, and some
 * results may be unsound. This can happen when the knowledge base was modified
 * and the reasoner materialisation is no longer consistent with the current
 * knowledge base. Re-materialisation ({@link Reasoner#reason()}) is required,
 * in order to obtain correct query answers.
 * </ul>
 * 
 * @author Larry González
 *
 */
public interface QueryAnswerCount {

	/**
	 * Returns the correctness of the query result.
	 * <ul>
	 * <li>If {@link Correctness#SOUND_AND_COMPLETE}, the query results are
	 * guaranteed to be correct.</li>
	 * <li>If {@link Correctness#SOUND_BUT_INCOMPLETE}, the results are guaranteed
	 * to be sound, but may be incomplete.</li>
	 * <li>If {@link Correctness#INCORRECT}, the results may be incomplete, and some
	 * results may be unsound.
	 * </ul>
	 * 
	 * @return query result correctness
	 */
	Correctness getCorrectness();

	/**
	 * 
	 * @return number of query answers, i.e., the number of facts that the query
	 *         maps to.
	 */
	long getCount();

}
