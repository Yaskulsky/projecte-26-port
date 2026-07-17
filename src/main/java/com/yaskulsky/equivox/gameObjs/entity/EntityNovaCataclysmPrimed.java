package com.yaskulsky.equivox.gameObjs.entity;

import com.yaskulsky.equivox.gameObjs.blocks.EquivoxTNT;
import com.yaskulsky.equivox.gameObjs.registration.impl.BlockRegistryObject;
import com.yaskulsky.equivox.gameObjs.registries.PEBlocks;
import com.yaskulsky.equivox.gameObjs.registries.PEEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EntityNovaCataclysmPrimed extends EntityNovaPrimed {

	public EntityNovaCataclysmPrimed(EntityType<EntityNovaCataclysmPrimed> type, Level level) {
		super(type, level);
	}

	public EntityNovaCataclysmPrimed(Level level, double x, double y, double z, LivingEntity placer) {
		super(level, x, y, z, placer);
	}

	@Override
	protected BlockRegistryObject<EquivoxTNT, ?> getBlock() {
		return PEBlocks.NOVA_CATACLYSM;
	}

	@NotNull
	@Override
	public EntityType<?> getType() {
		return PEEntityTypes.NOVA_CATACLYSM_PRIMED.get();
	}

	@Override
	protected float getExplosionPower() {
		return 48;
	}
}