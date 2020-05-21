package com.teamcqr.chocolatequestrepoured.network.packets.handlers;

import javax.xml.ws.handler.MessageContext;

import com.teamcqr.chocolatequestrepoured.network.packets.toClient.DungeonSyncPacket;
import com.teamcqr.chocolatequestrepoured.objects.items.ItemDungeonPlacer;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

public class DungeonSyncPacketHandler implements IMessageHandler<DungeonSyncPacket, IMessage> {

	@Override
	public IMessage onMessage(DungeonSyncPacket message, MessageContext ctx) {
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
			if (ctx.side.isClient()) {
				ItemDungeonPlacer.fakeDungeonSet = message.getFakeDungeonList();
			}
		});
		return null;
	}

}
