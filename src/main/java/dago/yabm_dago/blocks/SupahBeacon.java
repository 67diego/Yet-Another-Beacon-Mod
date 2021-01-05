package dago.yabm_dago.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class SupahBeacon extends Block{
	
	public SupahBeacon(){
		super(Properties.create(Material.GLASS).setLightLevel(s->{return 15;}).notSolid().hardnessAndResistance(4.0f,15.0f).harvestLevel(0));
	}
	
	public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return false;
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
	
	@Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SupahBeaconTile();
    }

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand,  BlockRayTraceResult hit) {
		if (!worldIn.isRemote) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof SupahBeaconTile) {
				NetworkHooks.openGui((ServerPlayerEntity) player,(SupahBeaconTile) tileentity,pos);
			}
		}
		return ActionResultType.SUCCESS;
	}
	
}
