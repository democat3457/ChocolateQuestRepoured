package com.teamcqr.chocolatequestrepoured.network.packets.handlers;

import javax.xml.ws.handler.MessageContext;

import com.teamcqr.chocolatequestrepoured.CQRMain;
import com.teamcqr.chocolatequestrepoured.network.packets.toServer.ExtendedReachAttackPacket;
import com.teamcqr.chocolatequestrepoured.objects.items.spears.ItemSpearBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

public class ExtendedReachAttackPacketHandler implements IMessageHandler<ExtendedReachAttackPacket, IMessage> {
	@Override
	public IMessage onMessage(final ExtendedReachAttackPacket message, MessageContext ctx) {
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
			if (ctx.side.isServer()) {
				PlayerEntity attackingPlayer = CQRMain.proxy.getPlayer(ctx);
				World world = CQRMain.proxy.getPlayer(ctx).world;
				Entity attackTarget = world.getEntityByID(message.getEntityId());

				if (attackTarget != null && attackingPlayer.getHeldItemMainhand().getItem() instanceof ItemSpearBase) {
					ItemSpearBase spear = (ItemSpearBase) attackingPlayer.getHeldItemMainhand().getItem();
					double distSq = attackingPlayer.getDistanceSq(attackTarget);
					double reach = message.getIsExtended() ? spear.getReachExtended() : spear.getReach();
					double reachSq = Math.pow(reach, 2.0);

					if (reachSq >= distSq) {
						attackingPlayer.attackTargetEntityWithCurrentItem(attackTarget);
					}
				}
			}
		});
		return null;
	}
}
