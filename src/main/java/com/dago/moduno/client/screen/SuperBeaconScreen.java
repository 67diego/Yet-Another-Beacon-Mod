package com.dago.moduno.client.screen;

import java.util.List;

import org.apache.commons.compress.utils.Lists;

import com.dago.moduno.Moduno;
import com.dago.moduno.common.Regs;
import com.dago.moduno.common.container.SuperBeaconContainer;
import com.dago.moduno.common.net.Networn;
import com.dago.moduno.common.net.UpdateSuperBeaconConfigPacket;
import com.dago.moduno.common.net.UpdateSuperBeaconEffectsPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class SuperBeaconScreen extends AbstractContainerScreen<SuperBeaconContainer>{
	private static final ResourceLocation GUI = new ResourceLocation(Moduno.MODID, "textures/gui/supahbeacon.png");
	private List<EffectWidget> efwidgets = Lists.newArrayList();
	private List<MobEffect> selectedEffects;
	private int plays, passis, hostis;
	private Button playerSettingsButton;
	private Button hostileSettingsButton;
	private Button passiveSettingsButton;
	private Button submitButton;

	public SuperBeaconScreen(SuperBeaconContainer cont, Inventory p_97742_, Component p_97743_) {
		super(cont, p_97742_, p_97743_);
		cont.addSlotListener(new ContainerListener() {
			@Override
			public void slotChanged(AbstractContainerMenu p_39315_, int p_39316_, ItemStack p_39317_) {}
			@Override
			public void dataChanged(AbstractContainerMenu p_150524_, int p_150525_, int p_150526_) {
				SuperBeaconScreen.this.selectedEffects=cont.getEffects();
				SuperBeaconScreen.this.plays=cont.getPlayerConfig();
				SuperBeaconScreen.this.hostis=cont.getHostileConfig();
				SuperBeaconScreen.this.passis=cont.getPassiveConfig();
			}
		});
		this.imageWidth = 230;
		this.imageHeight = 219;
	}
	
	@Override
	protected void init() {
		super.init();
		int relX = (this.width - 230) / 2;
        int relY = (this.height - 219) / 2;//-15 | +17
		this.addRenderableWidget(new Button(relX+44,relY+37,14,14,Component.literal("<"),button->changeSettings(1)));
		this.addRenderableWidget(new Button(relX+76,relY+37,14,14,Component.literal(">"),button->changeSettings(2)));
		this.addRenderableWidget(new Button(relX+142,relY+37,14,14,Component.literal("<"),button->changeSettings(3)));
		this.addRenderableWidget(new Button(relX+174,relY+37,14,14,Component.literal(">"),button->changeSettings(4)));
		this.addRenderableWidget(new Button(relX+93,relY+9,14,14,Component.literal("<"),button->changeSettings(5)));
		this.addRenderableWidget(new Button(relX+125,relY+9,14,14,Component.literal(">"),button->changeSettings(6)));
		this.addRenderableWidget(new Button(relX+64,relY+81,14,14,Component.literal("<"),button->changeSettings(7)));
		this.addRenderableWidget(new Button(relX+96,relY+81,14,14,Component.literal(">"),button->changeSettings(8)));
		this.addRenderableWidget(new Button(relX+124,relY+81,14,14,Component.literal("<"),button->changeSettings(9)));
		this.addRenderableWidget(new Button(relX+156,relY+81,14,14,Component.literal(">"),button->changeSettings(10)));
		this.submitButton = new Button(relX+176,relY+106,22,24,Component.literal(" "),button->changeSettings(14));
		this.submitButton.setAlpha(0);
		this.addRenderableWidget(this.submitButton).setAlpha(0);
		this.playerSettingsButton = new Button(relX+230,relY,64,20,Component.translatable("desc.moduno.players"),button->changeSettings(20));
		this.addRenderableWidget(this.playerSettingsButton);
		this.passiveSettingsButton = new Button(relX+230,relY+25,64,20,Component.translatable("desc.moduno.passive"),button->changeSettings(21));
		this.addRenderableWidget(this.passiveSettingsButton);
		this.hostileSettingsButton = new Button(relX+230,relY+50,64,20,Component.translatable("desc.moduno.hostile"),button->changeSettings(22));
		this.addRenderableWidget(this.hostileSettingsButton);
		
		int []xOffsets = new int[] {59,157,108,79,139}, yOffsets = new int[] {36,36,8,80,80};
		for(int i=0; i<5; i++) {
			EffectWidget effectWidget = new EffectWidget(relX + xOffsets[i], relY + yOffsets[i], i);
			this.addRenderableWidget(effectWidget);
			this.efwidgets.add(effectWidget);
		}
	}
	
	@Override
	protected void containerTick() {
		super.containerTick();
		if(this.submitButton != null) {
			this.submitButton.active = this.menu.hasPayment() && !this.selectedEffects.equals(this.menu.getEffects());
		}
	}
	
	@Override
	public void render(PoseStack ms, int mx, int my, float partialTicks) {
		renderBackground(ms);
		super.render(ms, mx, my, partialTicks);
		renderTooltip(ms, mx, my);
	}

	@Override
	protected void renderBg(PoseStack ms, float partialTicks, int mx, int my) {
		int relX = (this.width - 230) / 2;
        int relY = (this.height - 219) / 2;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        this.blit(ms, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
        if(!this.menu.hasPayment() || !this.submitButton.active) {
        	this.blit(ms, relX+170, relY+107, 22, 219, 22, 23);
        }else if(this.submitButton.isHoveredOrFocused()) {
        	this.blit(ms, relX+170, relY+107, 44, 219, 22, 23);
        }else {
        	this.blit(ms, relX+170, relY+107, 0, 219, 22, 23);
        }
    	blit(ms, relX+170, relY+107, 500, 66, 219, 32, 32, 256, 256);
        
        if(!this.menu.hasPayment()) {
        	this.itemRenderer.blitOffset = 1.0F;
        	ItemStack[] items = Ingredient.of(Regs.SUPER_BEACON_PAYMENT_ITEMS).getItems();
        	this.itemRenderer.renderAndDecorateItem(items[(int) (System.currentTimeMillis() / 2000 % items.length)], relX+107, relY+110);

        	int x1 = relX+107, x2 = x1 + 16, y1 = relY+110, y2 = y1 + 16, z = 200;
        	int color = 0x88a0a0a0;
        	Matrix4f m4 = ms.last().pose();
        	if(x1 < x2) {
        		int i = x1;
        		x1 = x2;
        		x2 = i;
        	}
        	
        	if(y1 < y2) {
        		int j = y1;
        		y1 = y2;
        		y2 = j;
        	}
        	
        	float f3 = (float) (color >> 24 & 255) / 255.0F;
        	float f = (float) (color >> 16 & 255) / 255.0F;
        	float f1 = (float) (color >> 8 & 255) / 255.0F;
        	float f2 = (float) (color & 255) / 255.0F;
        	BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        	RenderSystem.enableBlend();
        	RenderSystem.disableTexture();
        	RenderSystem.defaultBlendFunc();
        	RenderSystem.setShader(GameRenderer::getPositionColorShader);
        	bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        	bufferbuilder.vertex(m4, (float) x1, (float) y2, z).color(f, f1, f2, f3).endVertex();
        	bufferbuilder.vertex(m4, (float) x2, (float) y2, z).color(f, f1, f2, f3).endVertex();
        	bufferbuilder.vertex(m4, (float) x2, (float) y1, z).color(f, f1, f2, f3).endVertex();
        	bufferbuilder.vertex(m4, (float) x1, (float) y1, z).color(f, f1, f2, f3).endVertex();
        	BufferUploader.drawWithShader(bufferbuilder.end());
        	RenderSystem.enableTexture();
        	RenderSystem.disableBlend();

        	this.itemRenderer.blitOffset = 0.0F;
        }
		
		if(this.playerSettingsButton.isHoveredOrFocused())
			renderTooltip(ms, Component.translatable("desc.moduno.apply_"+this.plays), mx,my);
		if(this.hostileSettingsButton.isHoveredOrFocused())
			renderTooltip(ms, Component.translatable("desc.moduno.apply_"+this.hostis), mx,my);
		if(this.passiveSettingsButton.isHoveredOrFocused())
			renderTooltip(ms, Component.translatable("desc.moduno.apply_"+this.passis), mx,my);
	}
	
	private void changeSettings(int i) {
    	if(i>14) {
    		Networn.INSTANCE.sendToServer(new UpdateSuperBeaconConfigPacket(i%10, this.menu.getPos()));
    	}else if(i==14) {
    		if(!this.menu.hasPayment())return;
    		Networn.INSTANCE.sendToServer(new UpdateSuperBeaconEffectsPacket(selectedEffects, this.menu.getPos()));
    		SuperBeaconScreen.this.minecraft.player.closeContainer();
    	}else { // effect pagination
    		List<MobEffect> availableEffects = this.menu.getAvailableEffects();
    		int efin = (i - 1)/2;
    		MobEffect selEff = this.selectedEffects.get(efin);

    		int indexOf;
    		if(selEff == null || (indexOf = availableEffects.indexOf(selEff)) == -1) {
    			this.selectedEffects.set(efin, availableEffects.get(i%2 == 0? 0: availableEffects.size() - 1));
    		}else {
    			int d = i%2 == 0? 1: -1;
    			int index = indexOf + d;
    			if(index < -1) index = availableEffects.size() - 1;
    			if(index >= availableEffects.size()) index = -1;
    			this.selectedEffects.set(efin, index < 0? null: availableEffects.get(index));
    		}
    	}
	}

	@Override
	protected void renderLabels(PoseStack p_97808_, int p_97809_, int p_97810_) {
		for(EffectWidget ew: this.efwidgets) {
			if(ew.isHovered()) {
				MobEffect effect = this.selectedEffects.get(ew.effectIndex);
				if(effect == null) continue;
				renderTooltip(p_97808_, effect.getDisplayName(), p_97809_ - this.leftPos, p_97810_ - this.topPos);
			}
		}
		if(this.submitButton.isHoveredOrFocused() && this.submitButton.active) {
			renderTooltip(p_97808_, CommonComponents.GUI_DONE, p_97809_ - this.leftPos, p_97810_ - this.topPos);
		}
	}
	
	private class EffectWidget extends AbstractWidget{
		private int effectIndex;

		public EffectWidget(int x, int y, int effect) {
			super(x, y, 16, 16, CommonComponents.EMPTY);
			this.effectIndex = effect;
		}
		
		public boolean isHovered() {
			return this.isHovered;
		}
		
		@Override
		public void renderButton(PoseStack ms, int p_93677_, int p_93678_, float p_93679_) {
			MobEffect effect = SuperBeaconScreen.this.selectedEffects.get(this.effectIndex);
			if(effect == null) {
				SuperBeaconScreen.this.itemRenderer.renderAndDecorateItem(Items.STRUCTURE_VOID.getDefaultInstance(), this.x, this.y);
				return;
			}
			Minecraft mc = Minecraft.getInstance();
			MobEffectTextureManager textures = mc.getMobEffectTextures();
			TextureAtlasSprite atlasSprite = textures.get(effect);
			
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, atlasSprite.atlas().location());
			ms.pushPose();
			ms.translate((double)this.x - 1, (double)this.y - 1, 0.0D);
			blit(ms, 0, 0, this.getBlitOffset(), 18, 18, atlasSprite);
			ms.popPose();
		}

		@Override
		public void updateNarration(NarrationElementOutput p_169152_) {
			this.defaultButtonNarrationText(p_169152_);
		}
		
	}

}
