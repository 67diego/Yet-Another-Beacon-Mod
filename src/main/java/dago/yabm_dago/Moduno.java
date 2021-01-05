package dago.yabm_dago;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dago.yabm_dago.blocks.SBScreen;
import dago.yabm_dago.blocks.SupahBeaconRenderer;
import dago.yabm_dago.config.Config;
import dago.yabm_dago.init.Regs;
import dago.yabm_dago.nets.Networn;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Moduno.modid)
public class Moduno {
	public static Moduno instance;
	public static final String modid="yabm_dago";
	private static final Logger lagger=LogManager.getLogger(modid);
	
	public Moduno() {
		final IEventBus modBus=FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::SetUp);
		Regs.ITEMS.register(modBus);
		Regs.BLOCKS.register(modBus);
		Regs.TILES.register(modBus);
		Regs.CONTS.register(modBus);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON);
		instance=this;
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	private void SetUp(final FMLCommonSetupEvent event) {
		Networn.RegisterMsg();
		lagger.info("SetUp");
	}
	
	@Mod.EventBusSubscriber(modid = modid, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class client{
		@SubscribeEvent
		public static void onClient(final FMLClientSetupEvent event) {
			ScreenManager.registerFactory(Regs.SUPAHBEACONCONT.get(), SBScreen::new);
	        RenderTypeLookup.setRenderLayer(Regs.SUPAHBEACONBLOCK.get(), RenderType.getCutout());
	        ClientRegistry.bindTileEntityRenderer(Regs.SUPAHBEACONTILE.get(), SupahBeaconRenderer::new);
		}
	}
}
