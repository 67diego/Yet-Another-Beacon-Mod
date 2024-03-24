package com.dago.moduno.common.tile;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.dago.moduno.common.Regs;
import com.dago.moduno.common.conf.Config;
import com.dago.moduno.common.container.SuperBeaconContainer;
import com.google.common.collect.Lists;

import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class SuperBeaconTile extends BlockEntity implements MenuProvider{
	public static final int ALL = 3, NONE = 0, BENEFICAL_ONLY = 2, HARMFUL_ONLY = 1;
	
	private List<MobEffect> effs = Util.make(Lists.newArrayList(), l -> {
		for(int i=0;i<5;i++) l.add(null);
	});
	private final ContainerData data = new ContainerData() {
		@Override
		public int get(int i) {
			if(i == 6) return SuperBeaconTile.this.playerConfig;
			if(i == 7) return SuperBeaconTile.this.hostileConfig;
			if(i == 8) return SuperBeaconTile.this.passiveConfig;
			return i == 0? SuperBeaconTile.this.levels: MobEffect.getIdFromNullable(SuperBeaconTile.this.effs.get(i - 1));
		}
		@Override
		public void set(int i, int v) {
			if(i == 0) {
				SuperBeaconTile.this.levels = v;
				return;
			}
			SuperBeaconTile.this.effs.set(i - 1, MobEffect.byId(v));
		}
		@Override
		public int getCount() {
			return 9;
		}
	};
	private Component name;
	
	public List<float[]> beamColors = Lists.newArrayList();
	public int playerConfig = BENEFICAL_ONLY;
	public int hostileConfig = HARMFUL_ONLY;
	public int passiveConfig = NONE;
	public int levels;

	public SuperBeaconTile(BlockPos p_155229_, BlockState p_155230_) {
		super(Regs.SUPERBEACON_TILE.get(), p_155229_, p_155230_);
	}
	
	public static void tick(Level wo, BlockPos pos, BlockState state, SuperBeaconTile tile) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();

		int j1 = tile.levels;
		if(wo.getGameTime() % 80L == 0L) {
			tile.levels = updateBase(wo, i, j, k);

			if(tile.levels > 3) {
				updateBeamColors(wo, pos, tile);
				BeaconBlockEntity.playSound(wo, pos, SoundEvents.BEACON_AMBIENT);
				applyEffects(wo, pos, tile.levels, tile.effs);
			}

			boolean flag = j1 > 3;
			if(!wo.isClientSide) {
				boolean flag1 = tile.levels > 3;
				if(!flag && flag1) {
					BeaconBlockEntity.playSound(wo, pos, SoundEvents.BEACON_ACTIVATE);

					for(ServerPlayer serverplayer: wo.getEntitiesOfClass(ServerPlayer.class, (new AABB((double) i, (double) j, (double) k, (double) i, (double) (j - 4), (double) k)).inflate(10.0D, 5.0D, 10.0D))) {
						CriteriaTriggers.CONSTRUCT_BEACON.trigger(serverplayer, tile.levels);
					}
				}else if(flag && !flag1) {
					BeaconBlockEntity.playSound(wo, pos, SoundEvents.BEACON_DEACTIVATE);
				}
			}
		}

	}
	
	private static void updateBeamColors(Level wo, BlockPos pos, SuperBeaconTile tile) {
		List<float[]> colors = Lists.newArrayList();
		for(int i=0; i<6; i++) {
			BlockPos pos1 = pos.above(i + 1);
			@Nullable
			float[] color = wo.getBlockState(pos1).getBeaconColorMultiplier(wo, pos1, pos);
			colors.add(color == null? new float[] {1.0f, 1.0f, 0.666f}: color);
		}
		tile.beamColors = colors;
	}

	private static int updateBase(Level p_155093_, int p_155094_, int p_155095_, int p_155096_) {
		int i = 0;

		for(int j = 1; j <= 4; i = j++) {
			int k = p_155095_ - j;
			if(k < p_155093_.getMinBuildHeight()) {
				break;
			}

			boolean flag = true;

			for(int l = p_155094_ - j; l <= p_155094_ + j && flag; ++l) {
				for(int i1 = p_155096_ - j; i1 <= p_155096_ + j; ++i1) {
					if(!p_155093_.getBlockState(new BlockPos(l, k, i1)).is(BlockTags.BEACON_BASE_BLOCKS)) {
						flag = false;
						break;
					}
				}
			}

			if(!flag) {
				break;
			}
		}

		return i;
	}
	
	private static void applyEffects(Level wo, BlockPos pos, int levels, List<MobEffect> efes) {
		if(wo.isClientSide) return;
		double d0 = Config.BEACON_RANGE.get();
		int i = Config.EFFECT_POWER.get() - 1;
		SuperBeaconTile tile;
		try {
			tile = (SuperBeaconTile) wo.getBlockEntity(pos);
		}catch (Exception e) {
			return;
		}

		int j = 340;
		AABB aabb = (new AABB(pos)).inflate(d0).expandTowards(0.0D, (double) wo.getHeight(), 0.0D);
		List<LivingEntity> list = wo.getEntitiesOfClass(LivingEntity.class, aabb);

		for(LivingEntity entin: list) {
			for(MobEffect effect: efes) {
				if(effect == null) continue;
				boolean isGoodEffect = effect.isBeneficial();
				boolean shouldApply = false;
				if(entin instanceof Player) {
					shouldApply = appliesToConfig(tile.playerConfig, isGoodEffect);
				}else if(isBad(entin)) {
					shouldApply = appliesToConfig(tile.hostileConfig, isGoodEffect);
				}else {
					shouldApply = appliesToConfig(tile.passiveConfig, isGoodEffect);
				}
				if(shouldApply) {
					entin.addEffect(new MobEffectInstance(effect, j, i, true, true));
				}
			}
		}
	}

	private static boolean appliesToConfig(int conf, boolean isGoodEffect) {
		return (conf == 2 && isGoodEffect)||(conf == 1 && !isGoodEffect)||conf == 3;
	}
	
	private static boolean isBad(LivingEntity entin) {
		return entin instanceof Enemy;
	}
	
	@Override
	public void setRemoved() {
		BeaconBlockEntity.playSound(this.level, this.worldPosition, SoundEvents.BEACON_DEACTIVATE);
		super.setRemoved();
	}
	
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithoutMetadata();
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		int[] arr = tag.getIntArray("effects");
		for(int i=0; i<arr.length; i++)
			this.effs.set(i, MobEffect.byId(arr[i]));
		if(tag.contains("CustomName", 8)) {
			this.name = Component.Serializer.fromJson(tag.getString("CustomName"));
		}
		this.playerConfig = tag.getInt("playerConfig");
		this.hostileConfig = tag.getInt("hostileConfig");
		this.passiveConfig = tag.getInt("passiveConfig");
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		int[] arr = new int[this.effs.size()];
		for(int i=0; i<this.effs.size(); i++) {
			arr[i] = MobEffect.getIdFromNullable(this.effs.get(i));
		}
		tag.putIntArray("effects", arr);
		if(this.name != null) {
			tag.putString("CustomName", Component.Serializer.toJson(this.name));
		}
		tag.putInt("playerConfig", playerConfig);
		tag.putInt("hostileConfig", hostileConfig);
		tag.putInt("passiveConfig", passiveConfig);
	}

	public void setCustomName(Component hoverName) {
		this.name = hoverName;
	}

	@Override
	public Component getDisplayName() {
		return this.name != null? this.name: Component.translatable("block.moduno.supahbeacon");
	}

	@Override
	public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
		return new SuperBeaconContainer(p_39954_, p_39955_, this.data, this.worldPosition);
	}

	@Override
	public AABB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	public void updateSetting(int setting) {
		switch(setting) {
		case 0:
			if(++this.playerConfig > 3)
				this.playerConfig = 0;
			break;
		case 1:
			if(++this.passiveConfig > 3)
				this.passiveConfig = 0;
			break;
		case 2:
			if(++this.hostileConfig > 3)
				this.hostileConfig = 0;
			break;
		}
	}
}
