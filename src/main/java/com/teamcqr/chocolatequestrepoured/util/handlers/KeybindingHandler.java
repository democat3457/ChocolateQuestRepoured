package com.teamcqr.chocolatequestrepoured.util.handlers;

import com.teamcqr.chocolatequestrepoured.proxy.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.PlayerEntitySP;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(value = Side.CLIENT)
public class KeybindingHandler {

	// Opens repu GUI
	@SubscribeEvent(receiveCanceled = true)
	public static void onKeyPress(KeyInputEvent event) {
		// Repu key
		if (ClientProxy.keybindReputationGUI.isPressed()) {
			PlayerEntitySP player = Minecraft.getMinecraft().player;
			// player.openGui(CQRMain.INSTANCE, Reference.REPUTATION_GUI_ID, Minecraft.getMinecraft().world, (int)player.posX, (int)player.posY, (int)player.posZ);
		}
	}

}
