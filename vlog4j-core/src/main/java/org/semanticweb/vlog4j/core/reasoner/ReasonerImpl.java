package org.semanticweb.vlog4j.core.reasoner;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.semanticweb.vlog4j.core.model.Atom;
import org.semanticweb.vlog4j.core.model.Rule;
import org.semanticweb.vlog4j.core.model.Term;

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

public class ReasonerImpl implements Reasoner {

	@Override
	public Set<Rule> getRules() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void applyReasoning() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<Atom> query(String predicate, int arity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Atom> query(String predicate, List<Term> arguments) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateDB() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exportDB(File directoryLocation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
