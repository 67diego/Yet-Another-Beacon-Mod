package com.dago.moduno.common.container;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import com.dago.moduno.common.Regs;
import com.dago.moduno.common.conf.Config;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class SuperBeaconContainer extends AbstractContainerMenu{
	private final ContainerLevelAccess access;
	private final ContainerData beaconData;
	private final Container beacon = new SimpleContainer(1) {
		@Override
		public boolean canPlaceItem(int p_18952_, ItemStack p_18953_) {
			return p_18953_.is(Regs.SUPER_BEACON_PAYMENT_ITEMS);
		};
		@Override
		public int getMaxStackSize() { return 1; };
	};
	private final Slot paymentSlot;
	private BlockPos pos;

	public SuperBeaconContainer(int id, Inventory plinv, FriendlyByteBuf data) {
		this(id, plinv, new SimpleContainerData(9), data.readBlockPos());
	}
	
	public SuperBeaconContainer(int id, Inventory plinv, ContainerData p_39041_, BlockPos pos) {
		super(Regs.SUPERBEACON_CONT.get(), id);
		this.beaconData = p_39041_;
		this.pos = pos;
		this.access = ContainerLevelAccess.create(plinv.player.level, pos);
		this.paymentSlot = new Slot(this.beacon, 0, 107, 110) {
			@Override
			public boolean mayPlace(ItemStack p_40231_) { return SuperBeaconContainer.this.beaconData.get(0) > 3 && p_40231_.is(Regs.SUPER_BEACON_PAYMENT_ITEMS); }
			@Override
			public int getMaxStackSize() { return 1; }
		};
		this.addSlot(this.paymentSlot);
		this.addDataSlots(this.beaconData);

		int i = 35;
		int j = 137;

		for(int k = 0; k < 3; ++k) {
			for(int l = 0; l < 9; ++l) {
				this.addSlot(new Slot(plinv, l + k * 9 + 9, i + l * 18, j + k * 18));
			}
		}

		for(int i1 = 0; i1 < 9; ++i1) {
			this.addSlot(new Slot(plinv, i1, i + i1 * 18, j + 58));
		}
	}
	
	@Override
	public void removed(Player p_38940_) {
		super.removed(p_38940_);
		if(!p_38940_.level.isClientSide) {
			ItemStack its = this.paymentSlot.remove(1);
			if(!its.isEmpty()) {
				p_38940_.drop(its, false);
			}
		}
	}

	@Override
	public boolean stillValid(Player p_38874_) {
		return stillValid(this.access, p_38874_, Regs.SUPERBEACON_BLOCK.get());
	}
	
	@Override
	public void setData(int p_38855_, int p_38856_) {
		super.setData(p_38855_, p_38856_);
		this.broadcastChanges();
	}

	@Override
	public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(p_38942_);
		if(slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if(p_38942_ == 0) {
				if(!this.moveItemStackTo(itemstack1, 1, 37, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(itemstack1, itemstack);
			}else if(this.moveItemStackTo(itemstack1, 0, 1, false)) { // Forge Fix Shift Clicking in beacons with stacks larger then 1.
				return ItemStack.EMPTY;
			}else if(p_38942_ >= 1 && p_38942_ < 28) {
				if(!this.moveItemStackTo(itemstack1, 28, 37, false)) {
					return ItemStack.EMPTY;
				}
			}else if(p_38942_ >= 28 && p_38942_ < 37) {
				if(!this.moveItemStackTo(itemstack1, 1, 28, false)) {
					return ItemStack.EMPTY;
				}
			}else if(!this.moveItemStackTo(itemstack1, 1, 37, false)) {
				return ItemStack.EMPTY;
			}

			if(itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			}else {
				slot.setChanged();
			}

			if(itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(p_38941_, itemstack1);
		}

		return itemstack;
	}
	
	public int getLevels() {
		return this.beaconData.get(0);
	}
	
	public List<@Nullable MobEffect> getAvailableEffects(){
		return Config.BEACON_EFFECTS.get().stream().map(ResourceLocation::new).map(ForgeRegistries.MOB_EFFECTS::getValue).filter(Objects::nonNull).toList();
	}
	
	public List<MobEffect> getEffects(){
		List<MobEffect> effs = Lists.newArrayList();
		for(int i=1; i<6; i++) {
			effs.add(MobEffect.byId(this.beaconData.get(i)));
		}
		return effs;
	}
	
	public void updateEffects(List<Optional<MobEffect>> effs) {
		if(this.paymentSlot.hasItem()) {
			for(int i=0; i<effs.size(); i++) {
				this.beaconData.set(i, effs.get(i).map(MobEffect::getId).orElse(-1));
			}
			this.paymentSlot.remove(1);
			this.access.execute(Level::blockEntityChanged);
		}
	}

	public void updateEffects(int[] effects) {
		if(this.paymentSlot.hasItem()) {
			for(int i=0; i<effects.length; i++) {
				this.beaconData.set(i + 1, effects[i]);
			}
			this.paymentSlot.remove(1);
			this.access.execute(Level::blockEntityChanged);
		}
	}
	
	public int getPlayerConfig() {
		return this.beaconData.get(6);
	}
	
	public void setPlayerConfig(int v) {
		this.beaconData.set(6, v);
	}
	
	public int getHostileConfig() {
		return this.beaconData.get(7);
	}
	
	public void setHostileConfig(int v) {
		this.beaconData.set(7, v);
	}
	
	public int getPassiveConfig() {
		return this.beaconData.get(8);
	}
	
	public void setPassiveConfig(int v) {
		this.beaconData.set(8, v);
	}
	
	public boolean hasPayment() {
		return !this.beacon.getItem(0).isEmpty();
	}

	public BlockPos getPos() {
		return pos;
	}

}
