package com.yaskulsky.equivox.gameObjs.items.tools;

import java.util.List;
import com.yaskulsky.equivox.api.capabilities.item.IItemCharge;
import com.yaskulsky.equivox.gameObjs.IMatterType;
import com.yaskulsky.equivox.gameObjs.PETags;
import com.yaskulsky.equivox.gameObjs.registries.PEDataComponentTypes;
import com.yaskulsky.equivox.utils.ToolHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class PEShovel extends PETool implements IItemCharge {

	public PEShovel(IMatterType matterType, int numCharges, Properties props) {
		super(matterType, BlockTags.MINEABLE_WITH_SHOVEL, numCharges, props.attributes(PETool.createAttributes(matterType, 2, -3)));
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if (player == null) {
			return InteractionResult.PASS;
		}
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState blockState = level.getBlockState(pos);
		return ToolHelper.performActions(context, blockState, ToolHelper.flattenAOE(context, blockState, 0),
				ToolHelper::dowseCampfire,
				(ctx, state) -> {
					if (state.is(PETags.Blocks.VEIN_SHOVEL)) {
						return ToolHelper.tryVeinMine(ctx.getPlayer(), ctx.getItemInHand(), ctx.getClickedPos(), ctx.getClickedFace());
					}
					return InteractionResult.PASS;
				}, (ctx, state) -> ToolHelper.digAOE(ctx.getLevel(), ctx.getPlayer(), ctx.getHand(), ctx.getItemInHand(), ctx.getClickedPos(),
						ctx.getClickedFace(), false, 0));
	}
}
