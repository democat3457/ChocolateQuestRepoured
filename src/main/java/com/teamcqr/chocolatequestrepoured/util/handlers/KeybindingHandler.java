package com.teamcqr.chocolatequestrepoured.util.handlers;

import com.teamcqr.chocolatequestrepoured.proxy.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber
public class KeybindingHandler {

	// Opens repu GUI
	@SubscribeEvent(receiveCanceled = true)
	public static void onKeyPress(KeyInputEvent event) {
		// Repu key
		if (ClientProxy.keybindReputationGUI.isPressed()) {
			ClientPlayerEntity player = Minecraft.getInstance().player;
			// player.openGui(CQRMain.INSTANCE, Reference.REPUTATION_GUI_ID, Minecraft.getMinecraft().world, (int)player.posX, (int)player.posY, (int)player.posZ);
		}
	}

}
