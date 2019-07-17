package org.semanticweb.vlog4j.parser.api;

/*-
 * #%L
 * vlog4j-parser
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

import org.semanticweb.vlog4j.parser.implementation.PrologueException;

public interface Prologue {

    String getBase() throws PrologueException;

    void setBase(String base) throws PrologueException;

    String getPrefix(String prefix) throws PrologueException;

    void setPrefix(String prefix, String iri) throws PrologueException;

    String resolvePName(String prefixedName) throws PrologueException;

}
