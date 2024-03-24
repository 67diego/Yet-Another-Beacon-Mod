package com.dago.moduno.common.net;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public interface IMsg<T> {
	public void encode(T msg, FriendlyByteBuf buf);
	public T decode(FriendlyByteBuf msg);
	public void handle(T msg, Supplier<NetworkEvent.Context> ctx);
}
