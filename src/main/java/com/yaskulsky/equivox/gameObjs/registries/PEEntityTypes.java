package com.yaskulsky.equivox.gameObjs.registries;

import com.yaskulsky.equivox.PECore;
import com.yaskulsky.equivox.gameObjs.entity.EntityFireProjectile;
import com.yaskulsky.equivox.gameObjs.entity.EntityHomingArrow;
import com.yaskulsky.equivox.gameObjs.entity.EntityLavaProjectile;
import com.yaskulsky.equivox.gameObjs.entity.EntityLensProjectile;
import com.yaskulsky.equivox.gameObjs.entity.EntityMobRandomizer;
import com.yaskulsky.equivox.gameObjs.entity.EntityNovaCataclysmPrimed;
import com.yaskulsky.equivox.gameObjs.entity.EntityNovaCatalystPrimed;
import com.yaskulsky.equivox.gameObjs.entity.EntitySWRGProjectile;
import com.yaskulsky.equivox.gameObjs.entity.EntityWaterProjectile;
import com.yaskulsky.equivox.gameObjs.registration.impl.EntityTypeDeferredRegister;
import com.yaskulsky.equivox.gameObjs.registration.impl.EntityTypeRegistryObject;
import net.minecraft.SharedConstants;

public class PEEntityTypes {

	public static final EntityTypeDeferredRegister ENTITY_TYPES = new EntityTypeDeferredRegister(PECore.MODID);

	public static final EntityTypeRegistryObject<EntityFireProjectile> FIRE_PROJECTILE = ENTITY_TYPES.registerNoGravThrowable("fire_projectile", EntityFireProjectile::new);
	public static final EntityTypeRegistryObject<EntityHomingArrow> HOMING_ARROW = ENTITY_TYPES.registerMisc("homing_arrow", EntityHomingArrow::new, builder -> builder
			//[VanillaCopy] from EntityType.ARROW
			.sized(0.5F, 0.5F)
			.eyeHeight(0.13F)
			.clientTrackingRange(4)
			.updateInterval(SharedConstants.TICKS_PER_SECOND)
	);
	public static final EntityTypeRegistryObject<EntityLavaProjectile> LAVA_PROJECTILE = ENTITY_TYPES.registerNoGravThrowable("lava_projectile", EntityLavaProjectile::new);
	public static final EntityTypeRegistryObject<EntityLensProjectile> LENS_PROJECTILE = ENTITY_TYPES.registerNoGravThrowable("lens_projectile", EntityLensProjectile::new);
	public static final EntityTypeRegistryObject<EntityMobRandomizer> MOB_RANDOMIZER = ENTITY_TYPES.registerNoGravThrowable("mob_randomizer", EntityMobRandomizer::new);
	public static final EntityTypeRegistryObject<EntityNovaCatalystPrimed> NOVA_CATALYST_PRIMED = ENTITY_TYPES.registerTnt("nova_catalyst_primed", EntityNovaCatalystPrimed::new);
	public static final EntityTypeRegistryObject<EntityNovaCataclysmPrimed> NOVA_CATACLYSM_PRIMED = ENTITY_TYPES.registerTnt("nova_cataclysm_primed", EntityNovaCataclysmPrimed::new);
	public static final EntityTypeRegistryObject<EntitySWRGProjectile> SWRG_PROJECTILE = ENTITY_TYPES.registerNoGravThrowable("swrg_projectile", EntitySWRGProjectile::new);
	public static final EntityTypeRegistryObject<EntityWaterProjectile> WATER_PROJECTILE = ENTITY_TYPES.registerNoGravThrowable("water_projectile", EntityWaterProjectile::new);
}