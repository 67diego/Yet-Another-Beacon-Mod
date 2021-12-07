package dago.yabm_dago.blocks;

import java.awt.Rectangle;

import com.mojang.blaze3d.matrix.MatrixStack;

import dago.yabm_dago.Moduno;
import dago.yabm_dago.nets.Networn;
import dago.yabm_dago.nets.UpdateSBEffsPacket;
import dago.yabm_dago.nets.UpdateSBFiltersPacket;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

public class SBScreen extends ContainerScreen<SBCont>{
	private static final ResourceLocation GUI = new ResourceLocation(Moduno.modid, "textures/gui/supahbeacon.png");
	private int[]effs;
	private int plays,passis,hostis;
	static int maxPotId=-1;

	public SBScreen(SBCont cont, PlayerInventory inv, ITextComponent titleIn) {
		super(cont, inv, titleIn);
		//this.effs = Effect.get(0)==null?new int[]{0,0,0,0,0} : new int[]{-1,-1,-1,-1,-1};
		cont.addListener(new IContainerListener() {
			public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {}
			public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {}
			public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {
				SBScreen.this.effs=cont.getEffs();
				SBScreen.this.plays=cont.getPlays();
				SBScreen.this.passis=cont.getPassis();
				SBScreen.this.hostis=cont.getHostis();
			}			
		});
		if(maxPotId<0)
			maxPotId = ForgeRegistries.POTIONS.getKeys().size();
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}

	protected void init() {
		super.init();
        int relX = (this.width - 230) / 2;
        int relY = (this.height - 219) / 2;//-15 | +17
		this.addButton(new Button(relX+44,relY+37,14,14,new StringTextComponent("<"),button->test(1)));
		this.addButton(new Button(relX+76,relY+37,14,14,new StringTextComponent(">"),button->test(2)));
		this.addButton(new Button(relX+142,relY+37,14,14,new StringTextComponent("<"),button->test(3)));
		this.addButton(new Button(relX+174,relY+37,14,14,new StringTextComponent(">"),button->test(4)));
		this.addButton(new Button(relX+93,relY+9,14,14,new StringTextComponent("<"),button->test(5)));
		this.addButton(new Button(relX+125,relY+9,14,14,new StringTextComponent(">"),button->test(6)));
		this.addButton(new Button(relX+64,relY+81,14,14,new StringTextComponent("<"),button->test(7)));
		this.addButton(new Button(relX+96,relY+81,14,14,new StringTextComponent(">"),button->test(8)));
		this.addButton(new Button(relX+124,relY+81,14,14,new StringTextComponent("<"),button->test(9)));
		this.addButton(new Button(relX+156,relY+81,14,14,new StringTextComponent(">"),button->test(10)));
		this.addButton(new Button(relX+176,relY+106,22,24,new StringTextComponent(" "),button->test(14))).setAlpha(0);
		this.addButton(new Button(relX+230,relY,64,20,new TranslationTextComponent("desc.yabm_dago.players"),button->test(20)));
		this.addButton(new Button(relX+230,relY+25,64,20,new TranslationTextComponent("desc.yabm_dago.passive"),button->test(21)));
		this.addButton(new Button(relX+230,relY+50,64,20,new TranslationTextComponent("desc.yabm_dago.hostile"),button->test(22)));
	}
	
    protected void drawGuiContainerForegroundLayer(MatrixStack ms,int mouseX, int mouseY) {
    	this.itemRenderer.renderItemIntoGUI(new ItemStack(Items.DIAMOND_BLOCK), 0+60, 0+84);
    	this.itemRenderer.renderItemIntoGUI(new ItemStack(Items.EMERALD_BLOCK), 0+102, 0+84);
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float partialTicks, int mx, int my) {
        int relX = (this.width - 230) / 2;
        int relY = (this.height - 219) / 2;
        this.minecraft.getTextureManager().bindTexture(GUI);
        this.blit(ms, relX, relY, 0, 0, 230, 219);
        test.setBounds(0, 0, 22, 22);
        if(!this.container.hasStack())
        	this.blit(ms, relX+170, relY+107, 22, 219, 22, 23);
        else if(hovered(relX+170,relY+107, mx, my))
        	this.blit(ms, relX+170, relY+107, 44, 219, 22, 23);
        else
        	this.blit(ms, relX+170, relY+107, 0, 219, 22, 23);
        this.blit(ms, relX+170, relY+107, 66, 219, 22, 23);
        PotionSpriteUploader potionspriteuploader = this.minecraft.getPotionSpriteUploader();
        int []xOffsets = new int[] {59,157,108,79,139}, yOffsets = new int[] {36,36,8,80,80};
        test.setBounds(0, 0, 16,16);
        for(int i=0;i<5;i++) {
        	Effect effect = Effect.get(effs[i]);
        	if(effect == null) {
        		this.itemRenderer.renderItemIntoGUI(Items.STRUCTURE_VOID.getDefaultInstance(), relX+xOffsets[i], relY+yOffsets[i]);
        		continue;
        	}
        	TextureAtlasSprite textureatlassprite = potionspriteuploader.getSprite(effect);
        	this.minecraft.getTextureManager().bindTexture(textureatlassprite.getAtlasTexture().getTextureLocation());
        	blit(ms, relX+xOffsets[i], relY+yOffsets[i], this.getBlitOffset(), 16, 16, textureatlassprite);
        	if(effs[i]>=0 && hovered(relX+xOffsets[i], relY+yOffsets[i],mx,my))
        		renderTooltip(ms, effect.getDisplayName(), mx,my);
        }
        test.setBounds(0, 0, 64, 20);
        if(hovered(relX+230,relY, mx, my))renderTooltip(ms, new TranslationTextComponent("desc.yabm_dago.apply_"+this.plays), mx,my);
        if(hovered(relX+230,relY+25, mx, my))renderTooltip(ms, new TranslationTextComponent("desc.yabm_dago.apply_"+this.passis), mx,my);
        if(hovered(relX+230,relY+50, mx, my))renderTooltip(ms, new TranslationTextComponent("desc.yabm_dago.apply_"+this.hostis), mx,my);
    }
	
    Rectangle test=new Rectangle(0,0,16,16);
	private boolean hovered(int x, int y,int mx,int my) {
		test.setLocation(x, y);
		return test.contains(mx, my);
	}

    private void test(int i) {
    	int k=0;
    	if(i>14) {
    		Networn.INSTANCE.sendToServer(new UpdateSBFiltersPacket(i%10, this.container.getPos()));
    	}else if(i==14) {
    		if(!this.container.hasStack())return;
    		Networn.INSTANCE.sendToServer(new UpdateSBEffsPacket(effs, this.container.getPos()));
    		this.minecraft.displayGuiScreen((Screen)null);
    	}else {
    		k=this.effs[(i-1)/2];
    		k+=i%2==0?1:-1;
    		if(k>maxPotId)k=0;
    		if(k<0)k=maxPotId;
    		this.effs[(i-1)/2] = k;
    	}
	}
}
