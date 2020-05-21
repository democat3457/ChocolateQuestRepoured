package com.teamcqr.chocolatequestrepoured.client.render.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.HandSide;

public class LayerCQRHeldItem extends LayerHeldItem {

	public LayerCQRHeldItem(RenderLivingBase<?> livingEntityRendererIn) {
		super(livingEntityRendererIn);
	}

	@Override
	protected void translateToHand(HandSide handSide) {
		super.translateToHand(handSide);
		if (this.livingEntityRenderer.getMainModel() instanceof ModelBiped) {
			ModelBiped model = (ModelBiped) this.livingEntityRenderer.getMainModel();
			ModelRenderer armRenderer;
			if (handSide == HandSide.RIGHT) {
				armRenderer = model.bipedRightArm;
			} else {
				armRenderer = model.bipedLeftArm;
			}
			if (!armRenderer.cubeList.isEmpty()) {
				ModelBox armBox = armRenderer.cubeList.get(0);
				float x = 0.125F - 0.03125F * (armBox.posX2 - armBox.posX1);
				if (handSide == HandSide.LEFT) {
					x *= -1.0F;
				}
				float y = 0.0625F * (armBox.posY2 - armBox.posY1 - 12.0F);
				GlStateManager.translate(x, y, 0.0F);
			}
		}
	}

}
