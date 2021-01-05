package dago.yabm_dago.nets;

import dago.yabm_dago.Moduno;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Networn {
	public static SimpleChannel INSTANCE;
	
	public static void RegisterMsg() {
		int id=0;
		INSTANCE=NetworkRegistry.newSimpleChannel(new ResourceLocation(Moduno.modid,"nets"), ()->"1.0", s->true, s->true);
		INSTANCE.registerMessage(id++, UpdateSBEffsPacket.class, UpdateSBEffsPacket::toBytes, UpdateSBEffsPacket::new, UpdateSBEffsPacket::handle);
		INSTANCE.registerMessage(id++, UpdateSBFiltersPacket.class, UpdateSBFiltersPacket::toBytes, UpdateSBFiltersPacket::new, UpdateSBFiltersPacket::handle);
	}
}
