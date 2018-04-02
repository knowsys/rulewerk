package org.semanticweb.vlog4j.owlapi.reasoner;

import org.semanticweb.vlog4j.core.reasoner.Algorithm;
import org.semanticweb.vlog4j.core.reasoner.RuleRewriteStrategy;

public class VLogConfiguration {

	// VLogConfig
	private final Algorithm algorithm = Algorithm.SKOLEM_CHASE;
	private final RuleRewriteStrategy ruleRewriteStrategy = RuleRewriteStrategy.NONE;
	private final Integer timeoutAfterSeconds = null;
	// TODO what if logFile is null?
	// private final String logFile;
	// TODO logging level

	public static VLogConfiguration getDefaultConfiguration() {
		return new VLogConfiguration();
	}

}
