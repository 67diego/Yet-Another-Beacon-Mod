package com.dago.moduno.common.net;

import java.util.function.Supplier;

import com.dago.moduno.common.tile.SuperBeaconTile;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;

public class UpdateSuperBeaconConfigPacket implements IMsg<UpdateSuperBeaconConfigPacket> {
	private final int setting;
	private final BlockPos pos;

	public UpdateSuperBeaconConfigPacket() {
		this(0, BlockPos.ZERO);
	}

	public UpdateSuperBeaconConfigPacket(int i, BlockPos pos) {
		this.setting = i;
		this.pos = pos;
	}

	@Override
	public UpdateSuperBeaconConfigPacket decode(FriendlyByteBuf msg) {
		return new UpdateSuperBeaconConfigPacket(msg.readInt(), msg.readBlockPos());
	}

	@Override
	public void encode(UpdateSuperBeaconConfigPacket msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.setting);
		buf.writeBlockPos(msg.pos);
	}

	@Override
	public void handle(UpdateSuperBeaconConfigPacket msg, Supplier<Context> ctx) {
		Context context = ctx.get();
		context.enqueueWork(()->{
			ServerLevel wo = context.getSender().getLevel();
			BlockEntity be = wo.getBlockEntity(msg.pos);
			if(be instanceof SuperBeaconTile tile) {
				tile.updateSetting(msg.setting);
			}
		});
		context.setPacketHandled(true);
	}

}
