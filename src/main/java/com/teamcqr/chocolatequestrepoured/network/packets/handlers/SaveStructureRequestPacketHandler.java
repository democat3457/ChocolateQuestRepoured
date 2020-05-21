package com.teamcqr.chocolatequestrepoured.network.packets.handlers;

import javax.xml.ws.handler.MessageContext;

import com.teamcqr.chocolatequestrepoured.CQRMain;
import com.teamcqr.chocolatequestrepoured.network.packets.toServer.SaveStructureRequestPacket;
import com.teamcqr.chocolatequestrepoured.structuregen.structurefile.CQStructure;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

public class SaveStructureRequestPacketHandler implements IMessageHandler<SaveStructureRequestPacket, IMessage> {

	@Override
	public IMessage onMessage(SaveStructureRequestPacket message, MessageContext ctx) {
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
			if (ctx.side.isServer()) {
				PlayerEntity player = CQRMain.proxy.getPlayer(ctx);
				World world = CQRMain.proxy.getWorld(ctx);
				CQStructure structure = new CQStructure(message.getName());
				structure.takeBlocksFromWorld(world, message.getStartPos(), message.getEndPos(), message.usePartMode(), message.ignoreEntities());
				structure.writeToFile(player);
			}
		});
		return null;
	}

}
