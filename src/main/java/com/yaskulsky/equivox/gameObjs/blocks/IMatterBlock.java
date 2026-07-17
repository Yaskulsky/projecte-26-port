package com.yaskulsky.equivox.gameObjs.blocks;

import com.yaskulsky.equivox.gameObjs.IMatterType;

public interface IMatterBlock {

	/**
	 * Gets the matter type this block is made of/is needed to break.
	 */
	IMatterType getMatterType();
}