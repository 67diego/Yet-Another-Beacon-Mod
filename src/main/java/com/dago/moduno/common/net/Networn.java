package com.dago.moduno.common.net;

import com.dago.moduno.Moduno;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class Networn {
	public static SimpleChannel INSTANCE;
	static int id = 0;
	
	public static void registerNet() {
		INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Moduno.MODID, "networn"), ()->"1.0", s->true, s->true);
		regPacket(UpdateSuperBeaconConfigPacket.class, new UpdateSuperBeaconConfigPacket());
		regPacket(UpdateSuperBeaconEffectsPacket.class, new UpdateSuperBeaconEffectsPacket());
	}
	
	static <T> void regPacket(Class<T> clazz,IMsg<T> msg) {
		INSTANCE.registerMessage(id++, clazz, msg::encode, msg::decode, msg::handle);
	}

}
