package org.semanticweb.rulewerk.owlapi;

/*-
 * #%L
 * Rulewerk OWL API Support
 * %%
 * Copyright (C) 2018 - 2021 Rulewerk Developers
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

import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.ExistentialVariableImpl;
import org.semanticweb.rulewerk.core.model.implementation.UniversalVariableImpl;
import org.semanticweb.rulewerk.core.reasoner.implementation.Skolemization;

/**
 * Factory for retrieving Terms in the context of a translation from an OWL
 * Axiom to rules (and facts).
 * 
 * @author dragoste
 *
 */
public class ConverterTermFactory {

	private Skolemization skolemization = new Skolemization();

	final Variable frontierVariable = new UniversalVariableImpl("X");

	private int freshVariableCounter = 0;

	/**
	 * Changes the renaming function for blank node IDs. Blank nodes with the same
	 * local ID will be represented differently before and after this function is
	 * called, but will retain a constant interpretation otherwise.
	 */
	public void startNewBlankNodeContext() {
		this.skolemization = new Skolemization();
	}

	void resetFreshVariableCounter() {
		this.freshVariableCounter = 0;
	}

	/**
	 * Returns a fresh universal variable, which can be used as auxiliary variable
	 * in the current axiom's translation.
	 *
	 * @return a variable
	 */
	Variable getFreshUniversalVariable() {
		this.freshVariableCounter++;
		return new UniversalVariableImpl("Y" + this.freshVariableCounter);
	}

	/**
	 * Returns a fresh existential variable, which can be used as auxiliary variable
	 * in the current axiom's translation.
	 *
	 * @return a variable
	 */
	Variable getFreshExistentialVariable() {
		this.freshVariableCounter++;
		return new ExistentialVariableImpl("Y" + this.freshVariableCounter);
	}

	Skolemization getSkolemization() {
		return this.skolemization;
	}

}
