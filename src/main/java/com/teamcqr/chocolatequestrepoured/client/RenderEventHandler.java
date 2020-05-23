package com.teamcqr.chocolatequestrepoured.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.teamcqr.chocolatequestrepoured.objects.items.guns.ItemMusket;
import com.teamcqr.chocolatequestrepoured.objects.items.guns.ItemMusketKnife;
import com.teamcqr.chocolatequestrepoured.objects.items.guns.ItemRevolver;

import net.minecraft.item.Item;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.relauncher.Side;

@OnlyIn(Side.CLIENT)
@EventBusSubscriber(value = Side.CLIENT)
public class RenderEventHandler {

	@SubscribeEvent
	public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
		Item itemMain = event.getPlayerEntity().getHeldItemMainhand().getItem();
		Item itemOff = event.getPlayerEntity().getHeldItemOffhand().getItem();

		if (itemMain instanceof ItemRevolver || itemOff instanceof ItemRevolver || itemOff instanceof ItemMusketKnife || itemMain instanceof ItemMusketKnife) {
			GlStateManager.pushMatrix();
		}

		if (itemMain instanceof ItemMusket || itemMain instanceof ItemMusketKnife) {
			if (event.getPlayerEntity().getPrimaryHand() == HandSide.LEFT) {
				event.getRenderer().getMainModel().leftArmPose = ArmPose.BOW_AND_ARROW;
			} else {
				event.getRenderer().getMainModel().rightArmPose = ArmPose.BOW_AND_ARROW;
			}
		} else if (itemMain instanceof ItemRevolver) {
			if (event.getPlayerEntity().getPrimaryHand() == HandSide.LEFT) {
				event.getRenderer().getMainModel().bipedLeftArm.rotateAngleX -= new Float(Math.toRadians(90));
			} else {
				event.getRenderer().getMainModel().bipedRightArm.rotateAngleX -= new Float(Math.toRadians(90));
			}
		}
		if (itemOff instanceof ItemMusket || itemOff instanceof ItemMusketKnife) {
			if (!(event.getPlayerEntity().getPrimaryHand() == HandSide.LEFT)) {
				event.getRenderer().getMainModel().leftArmPose = ArmPose.BOW_AND_ARROW;
			} else {
				event.getRenderer().getMainModel().rightArmPose = ArmPose.BOW_AND_ARROW;
			}
		} else if (itemOff instanceof ItemRevolver) {
			if (!(event.getPlayerEntity().getPrimaryHand() == HandSide.LEFT)) {
				event.getRenderer().getMainModel().bipedLeftArm.rotateAngleX -= new Float(Math.toRadians(90));
			} else {
				event.getRenderer().getMainModel().bipedRightArm.rotateAngleX -= new Float(Math.toRadians(90));
			}
		}
	}

	@SubscribeEvent
	public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
		Item itemMain = event.getPlayerEntity().getHeldItemMainhand().getItem();
		Item itemOff = event.getPlayerEntity().getHeldItemOffhand().getItem();
		if (itemMain instanceof ItemRevolver && !(itemMain instanceof ItemMusket || itemMain instanceof ItemMusketKnife)) {
			if (event.getPlayerEntity().getPrimaryHand() == HandSide.LEFT) {
				event.getRenderer().getMainModel().bipedLeftArm.rotateAngleX -= new Float(Math.toRadians(90));
				event.getRenderer().getMainModel().bipedLeftArm.postRender(1F);
			} else {
				event.getRenderer().getMainModel().bipedRightArm.rotateAngleX -= new Float(Math.toRadians(90));
				event.getRenderer().getMainModel().bipedRightArm.postRender(1F);
			}
		} else if (itemMain instanceof ItemRevolver) {
			if (!(event.getPlayerEntity().getPrimaryHand() == HandSide.LEFT)) {
				event.getRenderer().getMainModel().leftArmPose = ArmPose.BOW_AND_ARROW;
				event.getRenderer().getMainModel().bipedLeftArm.postRender(1F);
			} else {
				event.getRenderer().getMainModel().rightArmPose = ArmPose.BOW_AND_ARROW;
				event.getRenderer().getMainModel().bipedRightArm.postRender(1F);
			}
		}
		if (itemOff instanceof ItemRevolver && !(itemOff instanceof ItemMusket  || itemOff instanceof ItemMusketKnife)) {
			if (!(event.getPlayerEntity().getPrimaryHand() == HandSide.LEFT)) {
				event.getRenderer().getMainModel().bipedLeftArm.rotateAngleX -= new Float(Math.toRadians(90));
				event.getRenderer().getMainModel().bipedLeftArm.postRender(1F);
			} else {
				event.getRenderer().getMainModel().bipedRightArm.rotateAngleX -= new Float(Math.toRadians(90));
				event.getRenderer().getMainModel().bipedRightArm.postRender(1F);
			}
		} else if (itemOff instanceof ItemRevolver) {
			if (!(event.getPlayerEntity().getPrimaryHand() == HandSide.LEFT)) {
				event.getRenderer().getMainModel().leftArmPose = ArmPose.BOW_AND_ARROW;
				event.getRenderer().getMainModel().bipedLeftArm.postRender(1F);
			} else {
				event.getRenderer().getMainModel().rightArmPose = ArmPose.BOW_AND_ARROW;
				event.getRenderer().getMainModel().bipedRightArm.postRender(1F);
			}
		}

		if (itemMain instanceof ItemRevolver || itemOff instanceof ItemRevolver || itemOff instanceof ItemMusketKnife || itemMain instanceof ItemMusketKnife) {
			GlStateManager.popMatrix();
		}
	}

}
