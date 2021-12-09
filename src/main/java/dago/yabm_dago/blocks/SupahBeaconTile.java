package dago.yabm_dago.blocks;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dago.yabm_dago.config.Config;
import dago.yabm_dago.init.Regs;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

@SuppressWarnings("unchecked")
public class SupahBeaconTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider{
	private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
	public BeamSegment mem;
    private int levels=0;
    public int act=0,plays=2,passis=0,hostis=1;//NONE,BAD,GOOD,ALL
	public int[] effs;
	IIntArray iarr=new IIntArray() {
		public int size() {return 8;}
		
		public void set(int index, int value) {
			if(index<5)SupahBeaconTile.this.effs[index]=value;
			if(index==5)SupahBeaconTile.this.plays=value;
			if(index==6)SupahBeaconTile.this.passis=value;
			if(index==7)SupahBeaconTile.this.hostis=value;
		}
		
		public int get(int index) {
			if(index<5)return SupahBeaconTile.this.effs[index];
			return index==5?SupahBeaconTile.this.plays:index==6?SupahBeaconTile.this.passis:SupahBeaconTile.this.hostis;
		}
	};
	
	public SupahBeaconTile() {
		super(Regs.SUPAHBEACONTILE.get());
		this.effs = Effect.get(0)==null?new int[]{0,0,0,0,0} : new int[]{-1,-1,-1,-1,-1};
	}
	
	private IItemHandler createHandler() {
        return new ItemStackHandler(1) {
            protected void onContentsChanged(int slot) {markDirty();}
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            	return stack.getItem() == Items.DIAMOND_BLOCK||stack.getItem() == Items.EMERALD_BLOCK;
            }
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (stack.getItem() != Items.DIAMOND_BLOCK&&stack.getItem() != Items.EMERALD_BLOCK) return stack;
                return super.insertItem(slot, stack, simulate);
            }
            protected int getStackLimit(int slot, ItemStack stack) {
            	return 1;
            }
        };
    }

	@Override
	public ITextComponent getDisplayName() {
		return new StringTextComponent(getType().getRegistryName().getPath());
	}
	
	@Override
	public void tick() {
		boolean f=false;
	    int i = this.pos.getX();
	    int j = this.pos.getY();
	    int k = this.pos.getZ();
	    if (this.world.getGameTime() % 80L == 0L) {
			boolean f1=this.levels>3;
		    this.checkBeaconLevel(i, j, k);
		    if(this.levels>=4) {
		    	f=true;
				if(act==2) addEffectsToPlayers();
		    }
		    if(!f1&&f) {
		    	this.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE);
		    	if(!this.world.isRemote()) this.world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), 2);
		    }
		    else if(f1&&!f) this.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE);
	    }
	    if (this.world.getGameTime() % 40L == 0L) {
	    	ArrayList<float[]> cols=new ArrayList<>();
	    	for(int p=1;p<6;p++) {
	    		BlockPos popos=new BlockPos(i, j+p, k);
	    		BlockState blockstate = this.world.getBlockState(popos);
	    		float[]col=blockstate.getBeaconColorMultiplier(this.world, popos, getPos());
	    		cols.add(col==null?new float[] {1,1,0.5f}:col);
	    	}
	    	this.mem=new BeamSegment(cols);
	    	if(this.levels<4) this.mem=null;
	    }
		if(!this.world.isRemote()&&this.act==1&&this.levels>=4) {
	        handler.ifPresent(h -> {
	        	this.act=2;
	        	h.extractItem(0, 1, false);
	            markDirty();
	        });
	        this.act=this.act<2?0:2;
	        this.world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), 2);
		}
	}

	private void addEffectsToPlayers() {
		if (this.levels >= 4) {
			double d0 = Config.BEACONRANGE.get();
			AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.pos)).grow(d0);
			List<LivingEntity> list = this.world.getEntitiesWithinAABB(LivingEntity.class, axisalignedbb);
			for(LivingEntity pla : list) {
				for(int i=0;i<5;i++) {
					Effect effect = Effect.get(this.effs[i]);
					if(effect == null) continue;
					boolean goodEffect = effect.isBeneficial();
					boolean shouldApply = false;
					if(pla instanceof PlayerEntity) {
						if((this.plays == 2 && goodEffect)||(this.plays == 1 && !goodEffect)||this.plays == 3)
							shouldApply = true;
					}else if(isBad(pla)) {
						if((this.hostis == 2 && goodEffect)||(this.hostis == 1 && !goodEffect)||this.hostis == 3)
							shouldApply = true;
					}else {
						if((this.passis == 2 && goodEffect)||(this.passis == 1 && !goodEffect)||this.passis == 3)
							shouldApply = true;
					}
					if(shouldApply) {
						EffectInstance newEffectInstance = new EffectInstance(effect, 320, Config.EFFECTPAWA.get()-1, true, true);
						if(pla.getActivePotionEffect(effect)==null)
							pla.addPotionEffect(new EffectInstance(effect, 320, Config.EFFECTPAWA.get()-1, true, true));
						else { //if(pla.isPotionApplicable(newEffectInstance))
							EffectInstance effectInstance = pla.getActivePotionEffect(effect);
							//net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.living.PotionEvent.PotionAddedEvent(pla, effectInstance, newEffectInstance));
							effectInstance.combine(newEffectInstance);
						}
					}
				}
			}
		}
	}
	
	boolean isBad(LivingEntity entin) {
		return entin instanceof MonsterEntity||entin instanceof SlimeEntity||entin instanceof PhantomEntity;
	}

   private void checkBeaconLevel(int beaconXIn, int beaconYIn, int beaconZIn) {
      this.levels = 0;
      for(int i = 1; i <= 4; this.levels = i++) {
         int j = beaconYIn - i;
         if (j < 0) break;
         boolean flag = true;
         for(int k = beaconXIn - i; k <= beaconXIn + i && flag; ++k) {
            for(int l = beaconZIn - i; l <= beaconZIn + i; ++l) {
               if (!this.world.getBlockState(new BlockPos(k, j, l)).isIn(BlockTags.BEACON_BASE_BLOCKS)) {
                  flag = false;
                  break;
               }
            }
         }
         if (!flag) break;
      }
   }
	
	@Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }
	
	@Override
	public void remove() {
		this.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE);
	    super.remove();
	}

	public void playSound(SoundEvent p_205736_1_) {
	   this.world.playSound((PlayerEntity)null, this.pos, p_205736_1_, SoundCategory.BLOCKS, 1.0F, 1.0F);
	}

    @Override
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public int getLevels() {
       return this.levels;
    }
	
    public Container createMenu(int id, PlayerInventory plinv, PlayerEntity playerEntity) {
		return new SBCont(id, plinv, this.iarr, IWorldPosCallable.of(this.world, this.getPos()), this);
    }
	
	@Override
	public void read(BlockState state, CompoundNBT tag) {
        CompoundNBT invTag = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(invTag));
        this.effs = tag.getIntArray("effs");
        for(int i=0;i<this.effs.length;i++)
        	this.iarr.set(i, this.effs[i]);
        this.act=tag.getInt("act");
        this.plays=tag.getInt("plays");
        this.passis=tag.getInt("passis");
        this.hostis=tag.getInt("hostis");
		super.read(state, tag);
	}

	@Override
    public CompoundNBT write(CompoundNBT tag) {
        handler.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("inv", compound);
        });
        tag.putIntArray("effs", effs);
        tag.putInt("act",act);
        tag.putInt("plays",plays);
        tag.putInt("passis",passis);
        tag.putInt("hostis",hostis);
        super.write(tag);
        return tag;
    }
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket(){
		CompoundNBT tag = new CompoundNBT();
		tag.putInt("act", this.act);
		tag.putIntArray("effs", this.effs);
        tag.putInt("plays",plays);
        tag.putInt("passis",passis);
        tag.putInt("hostis",hostis);
		return new SUpdateTileEntityPacket(getPos(), 0, tag);
	}
	
	@Override
	public void onDataPacket(NetworkManager net,SUpdateTileEntityPacket pkt){
		CompoundNBT nbt = pkt.getNbtCompound();
		this.act=nbt.getInt("act");
		this.effs=nbt.getIntArray("effs");
		this.plays=nbt.getInt("plays");
		this.passis=nbt.getInt("passis");
		this.hostis=nbt.getInt("hostis");
	}

    public static class BeamSegment {
    	final ArrayList<float[]> cols;

        public BeamSegment(ArrayList<float[]> cols) {
        	this.cols=cols;
        }

        public ArrayList<float[]> getColores() {
            return this.cols;
        }
    }
    
	public void updateFilters(int i) {
		switch(i) {
		case 0:
			if(++this.plays>3)this.plays=0;
			break;
		case 1:
			if(++this.passis>3)this.passis=0;
			break;
		case 2:
			if(++this.hostis>3)this.hostis=0;
			break;
		}
	}

}
