package com.yaskulsky.equivox.api.capabilities;

import com.yaskulsky.equivox.api.EquivoxAPI;
import com.yaskulsky.equivox.api.capabilities.block_entity.IEmcStorage;
import com.yaskulsky.equivox.api.capabilities.item.IAlchBagItem;
import com.yaskulsky.equivox.api.capabilities.item.IAlchChestItem;
import com.yaskulsky.equivox.api.capabilities.item.IExtraFunction;
import com.yaskulsky.equivox.api.capabilities.item.IItemCharge;
import com.yaskulsky.equivox.api.capabilities.item.IItemEmcHolder;
import com.yaskulsky.equivox.api.capabilities.item.IModeChanger;
import com.yaskulsky.equivox.api.capabilities.item.IPedestalItem;
import com.yaskulsky.equivox.api.capabilities.item.IProjectileShooter;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

public class PECapabilities {

	private PECapabilities() {
	}

	private static Identifier rl(String path) {
		return Identifier.fromNamespaceAndPath(EquivoxAPI.EQUIVOX_MODID, path);
	}

	/**
	 * The capability object for IEmcStorage
	 */
	public static final BlockCapability<IEmcStorage, @Nullable Direction> EMC_STORAGE_CAPABILITY = BlockCapability.createSided(rl("emc_storage"), IEmcStorage.class);

	/**
	 * The capability object for IAlchBagProvider
	 */
	public static final EntityCapability<IAlchBagProvider, Void> ALCH_BAG_CAPABILITY = EntityCapability.createVoid(rl("alchemical_bag"), IAlchBagProvider.class);

	/**
	 * The capability object for IKnowledgeProvider
	 */
	public static final EntityCapability<IKnowledgeProvider, Void> KNOWLEDGE_CAPABILITY = EntityCapability.createVoid(rl("knowledge"), IKnowledgeProvider.class);

	/**
	 * The capability object for IAlchBagItem
	 */
	public static final ItemCapability<IAlchBagItem, Void> ALCH_BAG_ITEM_CAPABILITY = ItemCapability.createVoid(rl("alchemical_bag"), IAlchBagItem.class);

	/**
	 * The capability object for IAlchChestItem
	 */
	public static final ItemCapability<IAlchChestItem, Void> ALCH_CHEST_ITEM_CAPABILITY = ItemCapability.createVoid(rl("alchemical_chest"), IAlchChestItem.class);

	/**
	 * The capability object for IExtraFunction
	 */
	public static final ItemCapability<IExtraFunction, Void> EXTRA_FUNCTION_ITEM_CAPABILITY = ItemCapability.createVoid(rl("extra_function"), IExtraFunction.class);

	/**
	 * The capability object for IItemCharge
	 */
	public static final ItemCapability<IItemCharge, Void> CHARGE_ITEM_CAPABILITY = ItemCapability.createVoid(rl("charge"), IItemCharge.class);

	/**
	 * The capability object for IItemEmcHolder
	 */
	public static final ItemCapability<IItemEmcHolder, Void> EMC_HOLDER_ITEM_CAPABILITY = ItemCapability.createVoid(rl("emc_holder"), IItemEmcHolder.class);

	/**
	 * The capability object for IModeChanger
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static final ItemCapability<IModeChanger<?>, Void> MODE_CHANGER_ITEM_CAPABILITY = ItemCapability.createVoid(rl("mode_changer"), (Class) IModeChanger.class);

	/**
	 * The capability object for IPedestalItem
	 */
	public static final ItemCapability<IPedestalItem, Void> PEDESTAL_ITEM_CAPABILITY = ItemCapability.createVoid(rl("pedestal"), IPedestalItem.class);

	/**
	 * The capability object for IProjectileShooter
	 */
	public static final ItemCapability<IProjectileShooter, Void> PROJECTILE_SHOOTER_ITEM_CAPABILITY = ItemCapability.createVoid(rl("projectile_shooter"), IProjectileShooter.class);
}