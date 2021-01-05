package dago.yabm_dago.blocks;

import java.util.ArrayList;

import com.mojang.blaze3d.matrix.MatrixStack;

import dago.yabm_dago.Moduno;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.BeaconTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SupahBeaconRenderer extends TileEntityRenderer<SupahBeaconTile>{
	private static final ResourceLocation TEXTURE_BEACON_BEAM = new ResourceLocation(Moduno.modid,"textures/entity/beacon_beam.png");
	private static final double[]posss= {-0.25,-0.25,-0.25,0.25,0.25,-0.25,0.25,0.25};
	
    public SupahBeaconRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}
    
    public boolean isGlobalRenderer(SupahBeaconTile te) {return true;}

	public void render(SupahBeaconTile sbt, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		if(sbt.mem==null)return;
		ArrayList<float[]>colors=sbt.mem.getColores();
		BlockPos mepos=sbt.getPos();
		BeaconTileEntityRenderer.renderBeamSegment(matrixStackIn, bufferIn, TEXTURE_BEACON_BEAM, partialTicks, 1, sbt.getWorld().getGameTime(), 0, 255-mepos.getY(), colors.get(0), 0.133f, 0.158f);
		for(int i=0;i<8;i+=2) {
			matrixStackIn.translate(posss[i],0,posss[i+1]);
			BeaconTileEntityRenderer.renderBeamSegment(matrixStackIn, bufferIn, TEXTURE_BEACON_BEAM, partialTicks, 1, sbt.getWorld().getGameTime(), 0, 256-mepos.getY(), colors.get(1+i/2), 0.1f, 0.125f);
			matrixStackIn.translate(-posss[i],0,-posss[i+1]);
		}
	}
}
