package org.semanticweb.vlog4j.owlapi;

/*-
 * #%L
 * VLog4j OWL API Support
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

import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;
import org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;
import org.semanticweb.vlog4j.core.reasoner.Algorithm;
import org.semanticweb.vlog4j.core.reasoner.RuleRewriteStrategy;

public class VLogReasonerConfiguration implements OWLReasonerConfiguration {

	private static final long serialVersionUID = 1903592737429847888L;

	private final OWLReasonerConfiguration owlConfig;

	// VLogConfig
	private final Algorithm algorithm = Algorithm.SKOLEM_CHASE;
	private final RuleRewriteStrategy ruleRewriteStrategy = RuleRewriteStrategy.NONE;
	private final Integer timeoutAfterSeconds = null;
	// TODO what if logFile is null?
	// private final String logFile;
	// TODO logging level

	public VLogReasonerConfiguration(OWLReasonerConfiguration owlConfig) {
		super();
		this.owlConfig = owlConfig;
	}

	@Override
	public ReasonerProgressMonitor getProgressMonitor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getTimeOut() {
		// FIXME correlate with our internal time out; treat Long.MAX_VALUE case; should
		// be long instead of int.
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FreshEntityPolicy getFreshEntityPolicy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
		// TODO Auto-generated method stub
		return null;
	}

}
