package com.dago.moduno.common.net;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.dago.moduno.common.container.SuperBeaconContainer;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.network.NetworkEvent.Context;

public class UpdateSuperBeaconEffectsPacket implements IMsg<UpdateSuperBeaconEffectsPacket>{
	private final int[] effects;
	private final BlockPos pos;

	public UpdateSuperBeaconEffectsPacket() {
		this(new int[0], BlockPos.ZERO);
	}
	
	public UpdateSuperBeaconEffectsPacket(List<MobEffect> effs, BlockPos pos) {
		this(Util.make(new int[effs.size()], arr -> {
			for(int i=0; i<effs.size(); i++) {
				arr[i] = MobEffect.getIdFromNullable(effs.get(i));
			}
		}), pos);
	}

	public UpdateSuperBeaconEffectsPacket(int[] effects, BlockPos pos) {
		this.effects = effects;
		this.pos = pos;
		System.out.println(Arrays.toString(effects));
	}

	@Override
	public void encode(UpdateSuperBeaconEffectsPacket msg, FriendlyByteBuf buf) {
		buf.writeVarIntArray(msg.effects);
		buf.writeBlockPos(msg.pos);
	}

	@Override
	public UpdateSuperBeaconEffectsPacket decode(FriendlyByteBuf msg) {
		return new UpdateSuperBeaconEffectsPacket(msg.readVarIntArray(6), msg.readBlockPos());
	}

	@Override
	public void handle(UpdateSuperBeaconEffectsPacket msg, Supplier<Context> sup) {
		Context ctx = sup.get();
		System.out.println(Arrays.toString(msg.effects));
		ctx.enqueueWork(()->{
			if(ctx.getSender().containerMenu instanceof SuperBeaconContainer menu) {
				menu.updateEffects(msg.effects);
			}
		});
		ctx.setPacketHandled(true);
	}

}
