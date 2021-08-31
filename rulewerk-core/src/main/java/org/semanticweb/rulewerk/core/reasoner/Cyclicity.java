package org.semanticweb.rulewerk.core.reasoner;

//TODO javadoc

public enum Cyclicity implements RulesetCyclicityProperty {
	/**
	 * Model-Faithful Cyclicity
	 */
	MFC,
	/**
	 * Restricted Model-Faithful Cyclicity
	 */
	RMFC;
	
	@Override
	public Type getType() {
		return Type.CYCLIC;
	}

//	@Override
//	public boolean isCyclic() {
//		return true;
//	}
//
//	@Override
//	public boolean isAcyclic() {
//		return false;
//	}

}
