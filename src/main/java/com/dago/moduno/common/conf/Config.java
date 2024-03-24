package com.dago.moduno.common.conf;

import java.util.List;

import net.minecraft.core.Registry;
import net.minecraftforge.common.ForgeConfigSpec;

@SuppressWarnings("deprecation")
public class Config {
	public static ForgeConfigSpec COMMON;
	public static ForgeConfigSpec.IntValue EFFECT_POWER;
	public static ForgeConfigSpec.IntValue BEACON_RANGE;
	public static ForgeConfigSpec.ConfigValue<List<String>> BEACON_EFFECTS;

	static{
		final ForgeConfigSpec.Builder common = new ForgeConfigSpec.Builder();
		common.comment("Yet Another Beacon Mod config").push("Super Beacon");
		EFFECT_POWER = common.comment("Level of the effects given by the super beacon\ndefault: 3").defineInRange("effect_level", 3, 1, 128);
		BEACON_RANGE = common.comment("Range of the super beacon area of effect\ndefault: 100").defineInRange("beacon_range", 100, 50, 256);
		BEACON_EFFECTS = common.comment("List of effects that the super beacon can grant").define("available_effects", Registry.MOB_EFFECT.stream().map(e -> Registry.MOB_EFFECT.getKey(e).toString()).toList());
		common.pop();
		COMMON = common.build();
	}
}
