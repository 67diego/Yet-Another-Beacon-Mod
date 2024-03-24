package com.dago.moduno.common;

import com.dago.moduno.Moduno;
import com.dago.moduno.common.block.SuperBeaconBlock;
import com.dago.moduno.common.container.SuperBeaconContainer;
import com.dago.moduno.common.tile.SuperBeaconTile;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SimpleFoiledItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Regs {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Moduno.MODID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Moduno.MODID);
	public static final DeferredRegister<MenuType<?>> CONTS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Moduno.MODID);
	public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Moduno.MODID);

	public static final RegistryObject<Block> SUPERBEACON_BLOCK = BLOCKS.register("supahbeacon", () -> new SuperBeaconBlock(BlockBehaviour.Properties.copy(Blocks.BEACON)));
	
	public static final RegistryObject<BlockItem> SUPERBEACON_ITEM = ITEMS.register("supahbeacon", () -> new BlockItem(SUPERBEACON_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC).rarity(Rarity.EPIC)));
	public static final RegistryObject<SimpleFoiledItem> SUPER_NETHER_STAR = ITEMS.register("supahnetherstar", () -> new SimpleFoiledItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC).rarity(Rarity.EPIC)) {
		@Override
		public boolean canBeHurtBy(DamageSource ds) {
			if(ds.isExplosion()) return false;
			return !this.isFireResistant() || !ds.isFire();
		}
	});
	
	public static final RegistryObject<MenuType<SuperBeaconContainer>> SUPERBEACON_CONT = CONTS.register("supahbeacon",
		() -> IForgeMenuType.create(SuperBeaconContainer::new)
	);
	
	public static final RegistryObject<BlockEntityType<SuperBeaconTile>> SUPERBEACON_TILE = TILES.register("supahbeacon",
		() -> BlockEntityType.Builder.of(SuperBeaconTile::new, SUPERBEACON_BLOCK.get()).build(null)
	);
	
	public static final TagKey<Item> SUPER_BEACON_PAYMENT_ITEMS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Moduno.MODID, "super_beacon_payment_items"));

	public static void register(IEventBus modBus) {
		BLOCKS.register(modBus);
		ITEMS.register(modBus);
		CONTS.register(modBus);
		TILES.register(modBus);
	}

}
