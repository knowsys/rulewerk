package org.semanticweb.vlog4j.core.model.validation;

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

public class EntityNameValidator {

	public static void validBlankNameCheck(final String nullName) throws BlankNameValidationException {
		// TODO Auto-generated method stub
		if (false) {
			throw new BlankNameValidationException("Not a valid null name: " + nullName);
		}
	}

	public static void validConstantNameCheck(final String constantName) throws ConstantNameValidationException {
		// TODO Auto-generated method stub
		if (false) {
			throw new ConstantNameValidationException("Not a valid constant name: " + constantName);
		}
	}

	public static void validVariableNameCheck(final String variableName) throws VariableNameValidationException {
		// TODO Auto-generated method stub
		if (false) {
			throw new VariableNameValidationException("Not a valid variable name: " + variableName);
		}
	}

	public static void validPredicateNameCheck(final String predicateName) throws PredicateNameValidationException {
		// TODO Auto-generated method stub
		if (false) {
			throw new PredicateNameValidationException("Not a predicate null name: " + predicateName);
		}
	}

}
