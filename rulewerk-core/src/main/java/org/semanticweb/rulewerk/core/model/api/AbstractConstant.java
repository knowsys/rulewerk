package org.semanticweb.rulewerk.core.model.api;

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
 * Interface for abstract constants, i.e. for constants that represent an
 * abstract domain element (in contrast to a specific value of a concrete
 * datatype). Such terms are of type {@link TermType#ABSTRACT_CONSTANT}.
 *
 * @author Markus Kroetzsch
 */
public interface AbstractConstant extends Constant {

	@Override
	default TermType getType() {
		return TermType.ABSTRACT_CONSTANT;
	}

}
