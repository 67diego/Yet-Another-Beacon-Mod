package com.dago.moduno.common.block;

import com.dago.moduno.common.Regs;
import com.dago.moduno.common.tile.SuperBeaconTile;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class SuperBeaconBlock extends BaseEntityBlock implements BeaconBeamBlock{

	public SuperBeaconBlock(Properties p_49224_) {
		super(p_49224_);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
		return new SuperBeaconTile(p_153215_, p_153216_);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
		return createTickerHelper(p_153214_, Regs.SUPERBEACON_TILE.get(), SuperBeaconTile::tick);
	}
	
	@Override
	public InteractionResult use(BlockState p_60503_, Level wo, BlockPos p_60505_, Player p_60506_, InteractionHand p_60507_, BlockHitResult p_60508_) {
		if(wo.isClientSide) return InteractionResult.SUCCESS;
		BlockEntity be = wo.getBlockEntity(p_60505_);
		if(be instanceof SuperBeaconTile t) {
			NetworkHooks.openScreen((ServerPlayer) p_60506_, t, buf -> {
				buf.writeBlockPos(p_60505_);
			});
		}
		return InteractionResult.CONSUME;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState p_49232_) {
		return RenderShape.MODEL;
	}
	
	@Override
	public void setPlacedBy(Level wo, BlockPos p_49848_, BlockState p_49849_, LivingEntity p_49850_, ItemStack p_49851_) {
		if(p_49851_.hasCustomHoverName()) {
			BlockEntity be = wo.getBlockEntity(p_49848_);
			if(be instanceof SuperBeaconTile t) {
				t.setCustomName(p_49851_.getHoverName());
			}
		}
	}

	@Override
	public DyeColor getColor() {
		return DyeColor.WHITE;
	}

}
