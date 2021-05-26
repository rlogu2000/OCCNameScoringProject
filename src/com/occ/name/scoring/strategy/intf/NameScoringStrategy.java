package com.occ.name.scoring.strategy.intf;

import com.occ.name.scoring.entity.Name;
public interface NameScoringStrategy {
	
	
	public long computeScore(Name name);

}
