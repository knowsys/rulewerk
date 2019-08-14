package org.semanticweb.vlog4j.syntax.common;

/*-
 * #%L
 * vlog4j-syntax
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

import org.semanticweb.vlog4j.syntax.parser.PrologueException;

/**
 * Registry that manages prefixes and base namespace declarations as used for
 * parsing and serialising inputs.
 * 
 * @author Markus Kroetzsch
 */
public interface PrefixDeclarations {

	/**
	 * Returns the relevant base namespace. This should always return a result,
	 * possibly using a local default value if no base was declared.
	 * 
	 * @return string of an absolute base IRI
	 */
	String getBase();

	/**
	 * Sets the base namespace to the given value. This should only be done once,
	 * and not after the base namespace was assumed to be an implicit default value.
	 * 
	 * @param base the new base namespace
	 * @throws PrologueException
	 */
	void setBase(String base) throws PrologueException;

	String getPrefix(String prefix) throws PrologueException;

	void setPrefix(String prefix, String iri) throws PrologueException;

	String resolvePrefixedName(String prefixedName) throws PrologueException;

	String absolutize(String prefixedName) throws PrologueException;

}
