package com.teamcqr.chocolatequestrepoured.network.packets.handlers;

import javax.xml.ws.handler.MessageContext;

import com.teamcqr.chocolatequestrepoured.CQRMain;
import com.teamcqr.chocolatequestrepoured.network.packets.toServer.StructureSelectorPacket;
import com.teamcqr.chocolatequestrepoured.objects.items.ItemStructureSelector;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

public class StructureSelectorPacketHandler implements IMessageHandler<StructureSelectorPacket, IMessage> {

	@Override
	public IMessage onMessage(StructureSelectorPacket message, MessageContext ctx) {
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
			PlayerEntity player = CQRMain.proxy.getPlayer(ctx);
			ItemStack stack = player.getHeldItem(message.getHand());

			if (stack.getItem() instanceof ItemStructureSelector) {
				((ItemStructureSelector) stack.getItem()).setFirstPos(stack, new BlockPos(player));
			}
		});
		return null;
	}

}
