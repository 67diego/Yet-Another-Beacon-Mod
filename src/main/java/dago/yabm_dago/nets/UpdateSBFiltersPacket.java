package dago.yabm_dago.nets;

import java.util.function.Supplier;

import dago.yabm_dago.blocks.SupahBeaconTile;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpdateSBFiltersPacket{
	private int type;
	private BlockPos pos;
	
	public UpdateSBFiltersPacket(int type,BlockPos pos) {
		this.type=type;
		this.pos=pos;
	}
	
	public UpdateSBFiltersPacket(PacketBuffer buf) {
		this.type=buf.readVarInt();
		this.pos=buf.readBlockPos();
	}

	public void toBytes(PacketBuffer buf) {
		buf.writeVarInt(this.type);
		buf.writeBlockPos(this.pos);
	}
	
	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			TileEntity te=ctx.get().getSender().getServerWorld().getTileEntity(this.pos);
			if(te instanceof SupahBeaconTile) {
				((SupahBeaconTile) te).updateFilters(this.type);
			}
		});
		ctx.get().setPacketHandled(true);
	}

	public int getType() {return type;}

}
