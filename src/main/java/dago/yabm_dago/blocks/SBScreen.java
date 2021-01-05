package dago.yabm_dago.blocks;

import java.awt.Rectangle;

import com.mojang.blaze3d.matrix.MatrixStack;

import dago.yabm_dago.Moduno;
import dago.yabm_dago.init.Regs;
import dago.yabm_dago.nets.Networn;
import dago.yabm_dago.nets.UpdateSBEffsPacket;
import dago.yabm_dago.nets.UpdateSBFiltersPacket;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class SBScreen extends ContainerScreen<SBCont>{
	private static final String[]NAMES= {"None","Speed","Slowness","Haste","Mining Fatigue","Strength","Instant Health","Instant Damage","Jump Boost","Nausea","Regeneration","Resistance","Fire Resistance","Water Breathing","Invisibility","Blindness","Night Vision","Hunger","Weakness","Poison","Wither","Health Boost","Absorption","Saturation","Glowing","Levitation","Luck","Bad Luck","Slow Falling","Conduit Power","Dolphin's Grace","Bad Omen","Hero of the Village"};
	private static final ResourceLocation GUI = new ResourceLocation(Moduno.modid, "textures/gui/supahbeacon.png");
	private ItemStack[]is={new ItemStack(Regs.SPRITE.get()),new ItemStack(Regs.SPRITE.get()),new ItemStack(Regs.SPRITE.get()),new ItemStack(Regs.SPRITE.get()),new ItemStack(Regs.SPRITE.get())};
	private int[]effs;
	private int plays,passis,hostis;
	private boolean started=false;
	
	public SBScreen(SBCont cont, PlayerInventory inv, ITextComponent titleIn) {
		super(cont, inv, titleIn);
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
        int relY = (this.height - 219) / 2;
		this.addButton(new Button(relX+50,relY+35,14,14,new StringTextComponent("<"),button->test(1)));
		this.addButton(new Button(relX+82,relY+35,14,14,new StringTextComponent(">"),button->test(2)));
		this.addButton(new Button(relX+136,relY+35,14,14,new StringTextComponent("<"),button->test(3)));
		this.addButton(new Button(relX+168,relY+35,14,14,new StringTextComponent(">"),button->test(4)));
		this.addButton(new Button(relX+92,relY+6,14,14,new StringTextComponent("<"),button->test(5)));
		this.addButton(new Button(relX+124,relY+6,14,14,new StringTextComponent(">"),button->test(6)));
		this.addButton(new Button(relX+67,relY+82,14,14,new StringTextComponent("<"),button->test(7)));
		this.addButton(new Button(relX+99,relY+82,14,14,new StringTextComponent(">"),button->test(8)));
		this.addButton(new Button(relX+122,relY+82,14,14,new StringTextComponent("<"),button->test(9)));
		this.addButton(new Button(relX+154,relY+82,14,14,new StringTextComponent(">"),button->test(10)));
		this.addButton(new Button(relX+176,relY+106,22,24,new StringTextComponent(" "),button->test(14))).setAlpha(0);
		this.addButton(new Button(relX+230,relY,64,20,new StringTextComponent("players"),button->test(20)));
		this.addButton(new Button(relX+230,relY+25,64,20,new StringTextComponent("passive"),button->test(21)));
		this.addButton(new Button(relX+230,relY+50,64,20,new StringTextComponent("hostile"),button->test(22)));
	}
	
    protected void drawGuiContainerForegroundLayer(MatrixStack ms,int mouseX, int mouseY) {
    	this.itemRenderer.renderItemIntoGUI(new ItemStack(Items.DIAMOND_BLOCK), 0+60, 0+84);
    	this.itemRenderer.renderItemIntoGUI(new ItemStack(Items.EMERALD_BLOCK), 0+102, 0+84);
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float partialTicks, int mx, int my) {
		if(!started) {
			started=true;
	    	CompoundNBT tmp;
			for(int i=0;i<5;i++) {
				tmp=is[i].getOrCreateTag();
				tmp.putInt("CustomModelData", this.effs[i]);
				is[i].setTag(tmp);
			}
		}
        int relX = (this.width - 230) / 2;
        int relY = (this.height - 219) / 2;
        this.minecraft.getTextureManager().bindTexture(GUI);
        this.blit(ms, relX, relY, 0, 0, 230, 219);
        this.itemRenderer.renderItemIntoGUI(is[0],relX+65,relY+34);
        this.itemRenderer.renderItemIntoGUI(is[1],relX+151,relY+34);
        this.itemRenderer.renderItemIntoGUI(is[2],relX+107,relY+5);
        this.itemRenderer.renderItemIntoGUI(is[3],relX+82,relY+81);
        this.itemRenderer.renderItemIntoGUI(is[4],relX+137,relY+81);
        test.setBounds(0, 0, 16,16);
        if(hovered(relX+65,relY+34, mx, my))renderTooltip(ms, new StringTextComponent(NAMES[is[0].getOrCreateTag().getInt("CustomModelData")]), mx,my);
        if(hovered(relX+151,relY+34, mx, my))renderTooltip(ms, new StringTextComponent(NAMES[is[1].getOrCreateTag().getInt("CustomModelData")]), mx,my);
        if(hovered(relX+107,relY+5, mx, my))renderTooltip(ms, new StringTextComponent(NAMES[is[2].getOrCreateTag().getInt("CustomModelData")]), mx,my);
        if(hovered(relX+82,relY+81, mx, my))renderTooltip(ms, new StringTextComponent(NAMES[is[3].getOrCreateTag().getInt("CustomModelData")]), mx,my);
        if(hovered(relX+137,relY+81, mx, my))renderTooltip(ms, new StringTextComponent(NAMES[is[4].getOrCreateTag().getInt("CustomModelData")]), mx,my);
        test.setBounds(0, 0, 64, 20);
        if(hovered(relX+230,relY, mx, my))renderTooltip(ms, new StringTextComponent("Apply effects: "+(this.plays==0?"none":this.plays==1?"bad":this.plays==2?"good":"all")), mx,my);
        if(hovered(relX+230,relY+25, mx, my))renderTooltip(ms, new StringTextComponent("Apply effects: "+(this.passis==0?"none":this.passis==1?"bad":this.passis==2?"good":"all")), mx,my);
        if(hovered(relX+230,relY+50, mx, my))renderTooltip(ms, new StringTextComponent("Apply effects: "+(this.hostis==0?"none":this.hostis==1?"bad":this.hostis==2?"good":"all")), mx,my);
    }
	
    Rectangle test=new Rectangle(0,0,16,16);
	private boolean hovered(int x, int y,int mx,int my) {
		test.setLocation(x, y);
		return test.contains(mx, my);
	}

    private void test(int i) {
    	CompoundNBT nbt;
    	int k=0;
    	if(i>14) {
    		Networn.INSTANCE.sendToServer(new UpdateSBFiltersPacket(i%10, this.container.getPos()));
    	}else if(i==14) {
    		if(!this.container.hasStack())return;
    		int effs[]={is[0].hasTag()?is[0].getTag().getInt("CustomModelData"):0,is[1].hasTag()?is[1].getTag().getInt("CustomModelData"):0,is[2].hasTag()?is[2].getTag().getInt("CustomModelData"):0,is[3].hasTag()?is[3].getTag().getInt("CustomModelData"):0,is[4].hasTag()?is[4].getTag().getInt("CustomModelData"):0};
    		boolean f=true;
    		for(int j=0;j<effs.length;j++)
    			if(effs[j]!=this.effs[j])f=false;
    		if(f)return;
    		Networn.INSTANCE.sendToServer(new UpdateSBEffsPacket(effs, this.container.getPos()));
    		this.minecraft.displayGuiScreen((Screen)null);
    	}else {
    		nbt = is[(i-1)/2].getOrCreateTag();
    		k=nbt.getInt("CustomModelData");
    		k+=i%2==0?1:-1;
    		if(k>32)k=0;
    		if(k<0)k=32;
    		nbt.putInt("CustomModelData", k);
    		is[(i-1)/2].setTag(nbt);
    	}
	}
}
