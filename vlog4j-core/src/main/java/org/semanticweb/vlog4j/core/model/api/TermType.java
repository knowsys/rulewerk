package org.semanticweb.vlog4j.core.model.api;

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
 * Enumeration listing the different types of terms ({@link #CONSTANT},
 * {@link #BLANK}, and {@link #VARIABLE}).
 *
 * @author david.carral@tu-dresden.de
 *
 */
public enum TermType {
	/**
	 * A constant is an entity used to represent named domain elements.
	 */
	CONSTANT,
	/**
	 * A blank is an entity used to represent anonymous domain elements introduced
	 * during the reasoning process to satisfy existential restrictions.
	 */
	BLANK,
	/**
	 * A variable is a parameter that stands for an arbitrary domain element.
	 */
	VARIABLE
}
