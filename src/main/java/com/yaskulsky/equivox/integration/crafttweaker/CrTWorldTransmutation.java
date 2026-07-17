package com.yaskulsky.equivox.integration.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.yaskulsky.equivox.api.world_transmutation.IWorldTransmutation;
import com.yaskulsky.equivox.api.world_transmutation.SimpleWorldTransmutation;
import com.yaskulsky.equivox.api.world_transmutation.WorldTransmutation;
import com.yaskulsky.equivox.integration.crafttweaker.actions.WorldTransmuteAction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@Document("mods/Equivox/WorldTransmutation")
@ZenCodeType.Name("mods.equivox.WorldTransmutation")
public class CrTWorldTransmutation {

	private CrTWorldTransmutation() {
	}

	/**
	 * Adds a simple in world transmutation "recipe" that handles state data as best as it can.
	 *
	 * @param input           {@link Block} representing the input or target.
	 * @param output          {@link Block} representing the output.
	 * @param secondaryOutput Optional {@link Block} representing the output when sneaking.
	 */
	@ZenCodeType.Method
	public static void add(Block input, Block output, @ZenCodeType.Optional Block secondaryOutput) {
		CraftTweakerAPI.apply(new WorldTransmuteAction(getWorldTransmutation(input, output, secondaryOutput), true));
	}

	/**
	 * Adds an in world transmutation "recipe" for specific block states.
	 *
	 * @param input           {@link BlockState} representing the input or target state.
	 * @param output          {@link BlockState} representing the output state.
	 * @param secondaryOutput Optional {@link BlockState} representing the output state when sneaking.
	 */
	@ZenCodeType.Method
	public static void add(BlockState input, BlockState output, @ZenCodeType.Optional BlockState secondaryOutput) {
		CraftTweakerAPI.apply(new WorldTransmuteAction(getWorldTransmutation(input, output, secondaryOutput), true));
	}

	/**
	 * Removes an existing simple in world transmutation "recipe" that handles state data as best as it can.
	 *
	 * @param input           {@link Block} representing the input or target.
	 * @param output          {@link Block} representing the output.
	 * @param secondaryOutput Optional {@link Block} representing the output when sneaking.
	 */
	@ZenCodeType.Method
	public static void remove(Block input, Block output, @ZenCodeType.Optional Block secondaryOutput) {
		CraftTweakerAPI.apply(new WorldTransmuteAction(getWorldTransmutation(input, output, secondaryOutput), false));
	}

	/**
	 * Removes an existing in world transmutation "recipe" for specific block states.
	 *
	 * @param input           {@link BlockState} representing the input or target state.
	 * @param output          {@link BlockState} representing the output state.
	 * @param secondaryOutput Optional {@link BlockState} representing the output state when sneaking.
	 */
	@ZenCodeType.Method
	public static void remove(BlockState input, BlockState output, @ZenCodeType.Optional BlockState secondaryOutput) {
		CraftTweakerAPI.apply(new WorldTransmuteAction(getWorldTransmutation(input, output, secondaryOutput), false));
	}

	/**
	 * Removes all existing in world transmutation "recipes".
	 */
	@ZenCodeType.Method
	public static void removeAll() {
		CraftTweakerAPI.apply(new WorldTransmuteAction.RemoveAll());
	}

	@SuppressWarnings("deprecation")
	private static IWorldTransmutation getWorldTransmutation(Block input, Block output, @Nullable Block secondaryOutput) {
		Holder<Block> outputHolder = output.builtInRegistryHolder();
		return new SimpleWorldTransmutation(input.builtInRegistryHolder(), outputHolder, secondaryOutput == null ? outputHolder : secondaryOutput.builtInRegistryHolder());
	}

	private static IWorldTransmutation getWorldTransmutation(BlockState input, BlockState output, @Nullable BlockState secondaryOutput) {
		return WorldTransmutation.of(input, output, secondaryOutput == null ? output : secondaryOutput);
	}
}