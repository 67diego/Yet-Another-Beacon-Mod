package com.dago.moduno.client.renderer;

import java.util.List;

import com.dago.moduno.Moduno;
import com.dago.moduno.common.tile.SuperBeaconTile;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class SuperBeaconTileRenderer implements BlockEntityRenderer<SuperBeaconTile>{
	private static final ResourceLocation TEXTURE_BEACON_BEAM = new ResourceLocation(Moduno.MODID,"textures/entity/beacon_beam.png");
	private static final double[]posss= {-0.25,-0.25,-0.25,0.25,0.25,-0.25,0.25,0.25};
	
    public SuperBeaconTileRenderer(BlockEntityRendererProvider.Context p_173529_) {
	}

	@Override
	public void render(SuperBeaconTile tile, float partialTicks, PoseStack ms, MultiBufferSource buf, int p_112311_, int p_112312_) {
		if(tile.beamColors==null || tile.beamColors.isEmpty() || tile.levels < 4)return;
		List<float[]>colors=tile.beamColors;

		BeaconRenderer.renderBeaconBeam(ms, buf, TEXTURE_BEACON_BEAM, partialTicks, 1, tile.getLevel().getGameTime(), 0, 1024, colors.get(0), 0.133f, 0.158f);
		for(int i=0;i<8;i+=2) {
			ms.translate(posss[i],0,posss[i+1]);
			BeaconRenderer.renderBeaconBeam(ms, buf, TEXTURE_BEACON_BEAM, partialTicks, 1, tile.getLevel().getGameTime(), 0, 1024, colors.get(1+i/2), 0.1f, 0.125f);
			ms.translate(-posss[i],0,-posss[i+1]);
		}
		
	}

	public boolean shouldRender(SuperBeaconTile p_173531_, Vec3 p_173532_) {
		return Vec3.atCenterOf(p_173531_.getBlockPos()).multiply(1.0D, 0.0D, 1.0D).closerThan(p_173532_.multiply(1.0D, 0.0D, 1.0D), (double)this.getViewDistance());
	}
    
    @Override
    public boolean shouldRenderOffScreen(SuperBeaconTile p_112306_) {
    	return true;
    }
    
    @Override
    public int getViewDistance() {
    	return 256;
    }

}
