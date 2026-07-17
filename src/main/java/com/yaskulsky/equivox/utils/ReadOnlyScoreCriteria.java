package com.yaskulsky.equivox.utils;

import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ReadOnlyScoreCriteria extends ObjectiveCriteria {

	// Expose protected superclass ctor
	public ReadOnlyScoreCriteria(String name) {
		super(name, true, RenderType.INTEGER);
	}
}