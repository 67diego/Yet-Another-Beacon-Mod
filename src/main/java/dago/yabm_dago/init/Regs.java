package dago.yabm_dago.init;

import dago.yabm_dago.Moduno;
import dago.yabm_dago.blocks.SBCont;
import dago.yabm_dago.blocks.SupahBeacon;
import dago.yabm_dago.blocks.SupahBeaconTile;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Regs {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Moduno.modid);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Moduno.modid);
    public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Moduno.modid);
	public static final DeferredRegister<ContainerType<?>> CONTS=DeferredRegister.create(ForgeRegistries.CONTAINERS, Moduno.modid);
	
	public static final RegistryObject<Item> SUPAHNETHERSTAR = ITEMS.register("supahnetherstar", ()->{return new Item(new Item.Properties().rarity(Rarity.EPIC).group(ItemGroup.MISC));});
	public static final RegistryObject<Item> SPRITE = ITEMS.register("sprite", ()->{return new Item(new Item.Properties());});
	
	public static final RegistryObject<Block> SUPAHBEACONBLOCK = BLOCKS.register("supahbeacon", ()->{return new SupahBeacon();});
	
	public static final RegistryObject<BlockItem> SUPAHBEACONITEM = ITEMS.register("supahbeacon", ()->{
		return new BlockItem(SUPAHBEACONBLOCK.get(), new Item.Properties().group(ItemGroup.MISC).rarity(Rarity.EPIC));
	});
	
	public static final RegistryObject<ContainerType<SBCont>> SUPAHBEACONCONT = CONTS.register("supahbeacon", ()->{
		return IForgeContainerType.create(SBCont::new);
	});
	
	public static final RegistryObject<TileEntityType<SupahBeaconTile>> SUPAHBEACONTILE = TILES.register("supahbeacon", ()->
		TileEntityType.Builder.create(SupahBeaconTile::new, SUPAHBEACONBLOCK.get()).build(null)
	);
	
}
