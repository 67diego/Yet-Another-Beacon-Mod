package dago.yabm_dago.config;

import dago.yabm_dago.Moduno;
import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
	public static ForgeConfigSpec COMMON;
	public static ForgeConfigSpec.IntValue EFFECTPAWA;
	public static ForgeConfigSpec.IntValue BEACONRANGE;
	
	static{
		final ForgeConfigSpec.Builder com=new ForgeConfigSpec.Builder();
		com.comment(Moduno.modid+" config").push("Super Beacon");
		EFFECTPAWA=com.comment("Level of the effects given by the super beacon\ndefault: 3").defineInRange("effect_level", 3,1,255);
		BEACONRANGE=com.comment("Range of the super beacon area of effect\ndefault: 100").defineInRange("range", 100,50,256);
		com.pop();
		COMMON=com.build();
	}
}
