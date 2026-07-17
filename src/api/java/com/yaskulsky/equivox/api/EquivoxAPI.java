package com.yaskulsky.equivox.api;

public final class EquivoxAPI {

	public static final String EQUIVOX_MODID = "equivox";
	/** Former ProjectE / ProjectEE mod id; registry objects alias {@code projecte:*} → {@code equivox:*}. */
	public static final String LEGACY_MODID = "projecte";
	/** Former Equivalence 1.4.x mod id; registry objects alias {@code equivalence:*} → {@code equivox:*}. */
	public static final String LEGACY_MODID_EQUIVALENCE = "equivalence";
	/** All prior mod namespaces that alias into {@link #EQUIVOX_MODID}. */
	public static final String[] LEGACY_MODIDS = { LEGACY_MODID, LEGACY_MODID_EQUIVALENCE };
	public static final long FREE_ARITHMETIC_VALUE = Long.MIN_VALUE;

	private EquivoxAPI() {
	}
}
