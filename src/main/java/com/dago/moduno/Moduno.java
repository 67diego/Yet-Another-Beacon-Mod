package com.dago.moduno;

import org.slf4j.Logger;

import com.dago.moduno.client.renderer.SuperBeaconTileRenderer;
import com.dago.moduno.client.screen.SuperBeaconScreen;
import com.dago.moduno.common.Regs;
import com.dago.moduno.common.conf.Config;
import com.dago.moduno.common.net.Networn;
import com.mojang.logging.LogUtils;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Moduno.MODID)
public class Moduno {
    public static final String MODID = "moduno";
    public static final Logger LOG = LogUtils.getLogger();

    public Moduno(){
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::setUp);
        Regs.register(modBus);

        ModLoadingContext.get().registerConfig(Type.COMMON, Config.COMMON);
        MinecraftForge.EVENT_BUS.register(this);
    }

	private void setUp(FMLCommonSetupEvent event) {
		Networn.registerNet();
	}
	
	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ModEvents{
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
			event.enqueueWork(() -> {
				MenuScreens.register(Regs.SUPERBEACON_CONT.get(), SuperBeaconScreen::new);
			});
		}

		@SubscribeEvent
		public static void onTileEntityRendererRegistry(EntityRenderersEvent.RegisterRenderers event) {
			event.registerBlockEntityRenderer(Regs.SUPERBEACON_TILE.get(), SuperBeaconTileRenderer::new);
		}
	}

}
