package dago.yabm_dago.blocks;

import java.util.Objects;

import dago.yabm_dago.init.Regs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SBCont extends Container{
	final IInventory tile=new Inventory(1) {
		public boolean isItemValidForSlot(int index, ItemStack stack) {
			return stack.getItem()==Items.DIAMOND_BLOCK||stack.getItem()==Items.EMERALD_BLOCK;
		};
		public int getInventoryStackLimit() {return 1;};
	};
	final BlockPos pos;
	final IWorldPosCallable woca;
	final IIntArray iarr;

	public SBCont(int windowId,PlayerInventory plinv,PacketBuffer data) {
		this(windowId,plinv,new IntArray(8), IWorldPosCallable.DUMMY,getTile(plinv,data));
	}
	
	private static SupahBeaconTile getTile(PlayerInventory plinv, PacketBuffer data) {
		Objects.requireNonNull(plinv, "playerInv cannot be null");
		Objects.requireNonNull(data, "data is gei");
		final TileEntity tileAtPos = plinv.player.world.getTileEntity(data.readBlockPos());
		if (tileAtPos instanceof SupahBeaconTile) {
			return (SupahBeaconTile) tileAtPos;
		}
		throw new IllegalStateException("TileEntity is not correct " + tileAtPos);
	}
	
	static BlockPos getPos(PacketBuffer data) {
		Objects.requireNonNull(data, "data is gei");
		return data.readBlockPos();
	}
    
	public SBCont(int id, PlayerInventory plinv,IIntArray iarr, IWorldPosCallable worldPosCallable,SupahBeaconTile pos) {
		super(Regs.SUPAHBEACONCONT.get(),id);
		assertIntArraySize(iarr, 8);
		this.iarr=iarr;
		this.woca=worldPosCallable;
		this.pos=pos.getPos();
		pos.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h->{
			this.addSlot(new SlotItemHandler(h, 0, 81, 84));
		});
		this.trackIntArray(iarr);
     	for(int k = 0; k < 3; ++k) {
     		for(int l = 0; l < 9; ++l) {
     			this.addSlot(new Slot(plinv, l + k * 9 + 9, 9 + l * 18, 111 + k * 18));
         	}
     	}
     	for(int i1 = 0; i1 < 9; ++i1) {
     		this.addSlot(new Slot(plinv, i1, 9 + i1 * 18, 169));
     	}
	}
	
	@Override
	public void onContainerClosed(PlayerEntity playerIn) {
		super.onContainerClosed(playerIn);
		if (!playerIn.world.isRemote) {
			ItemStack itemstack = this.getSlot(0).decrStackSize(this.getSlot(0).getSlotStackLimit());
			if (!itemstack.isEmpty())
				playerIn.dropItem(itemstack,false);
      	}
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return isWithinUsableDistance(this.woca, playerIn, Regs.SUPAHBEACONBLOCK.get());
	}
	
	@Override
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		this.detectAndSendChanges();
	}
	
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
	      Slot slot = this.inventorySlots.get(index);
	      if (slot != null && slot.getHasStack()) {
	    	  ItemStack stack = slot.getStack();
	    	  itemstack = stack.copy();
	    	  if(index==0) {
	    		  if (!this.mergeItemStack(stack, 1, 37, true)) return ItemStack.EMPTY;
	    		  slot.onSlotChange(stack, itemstack);
	    	  }else {
    			  if(stack.getCount()>1&&this.inventorySlots.get(0).getStack().getCount()<1) {
    				  this.mergeItemStack(stack.getItem().getDefaultInstance(), 0, 1, false);
    				  slot.putStack(stack.split(stack.getCount()-1));
    				  return ItemStack.EMPTY;
    			  }
    			  return ItemStack.EMPTY;
	    	  }
	    	  if (stack.isEmpty())
	    		  slot.putStack(ItemStack.EMPTY);
	    	  else
	    		  slot.onSlotChanged();
	    	  if (stack.getCount() == itemstack.getCount())
	    		  return ItemStack.EMPTY;
	    	  slot.onTake(playerIn, stack);
	      }

	      return itemstack;
    }
	
	public int[] getEffs() {
		return new int[] {this.iarr.get(0),this.iarr.get(1),this.iarr.get(2),this.iarr.get(3),this.iarr.get(4)};
	}
	
	public int getPlays() {return this.iarr.get(5);}
	public int getPassis() {return this.iarr.get(6);}
	public int getHostis() {return this.iarr.get(7);}
	public BlockPos getPos() {return this.pos;}
	
	public boolean hasStack() {
		return !this.inventorySlots.get(0).getStack().isEmpty();
	}

}
