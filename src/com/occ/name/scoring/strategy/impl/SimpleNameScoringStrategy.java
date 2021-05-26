package com.occ.name.scoring.strategy.impl;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import com.occ.name.scoring.entity.Name;
import com.occ.name.scoring.strategy.intf.NameScoringStrategy;

public class SimpleNameScoringStrategy implements NameScoringStrategy {

	private static final int CHAR_A=65;
	public SimpleNameScoringStrategy() {
		
	}
	@Override
    public long computeScore(Name name) {
		AtomicLong score=new AtomicLong();
		name.getFirstName().chars().forEach(c->score.addAndGet(c-CHAR_A+1));
		Optional<String> lastName=Optional.of(name.getLastName());
    	if(lastName.isPresent())
    		lastName.get().chars().forEach(c->score.addAndGet(c-CHAR_A+1));
		return score.longValue()*name.getScore();
	}

}
