package org.semanticweb.vlog4j.core.reasoner;

public interface RuleStaticAnalyser {

	boolean checkProperty(KnowledgeBase knowledgeBase, AcyclicityProperty acyclicityProperty);

}
