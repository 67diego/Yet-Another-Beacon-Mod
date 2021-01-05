package dago.yabm_dago.nets;

import java.util.function.Supplier;

import dago.yabm_dago.blocks.SupahBeaconTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpdateSBEffsPacket{
	private int[]effs;
	private BlockPos pos;
	
	public UpdateSBEffsPacket(int[]effs,BlockPos pos) {
		this.effs=effs;
		this.pos=pos;
	}
	
	public UpdateSBEffsPacket(PacketBuffer buf) {
		this.effs=buf.readVarIntArray(8);
		this.pos=buf.readBlockPos();
	}

	public void toBytes(PacketBuffer buf) {
		buf.writeVarIntArray(this.effs);
		buf.writeBlockPos(this.pos);
	}
	
	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			TileEntity te=ctx.get().getSender().getServerWorld().getTileEntity(this.pos);
			if(te instanceof SupahBeaconTile) {
				((SupahBeaconTile) te).effs=this.effs;
				((SupahBeaconTile) te).act=1;
				te.getWorld().playSound((PlayerEntity)null, te.getPos(), SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.BLOCKS, 1, 1);
			}
		});
		ctx.get().setPacketHandled(true);
	}
	
	public int[] getEffs() {
		return effs;
	}

}
