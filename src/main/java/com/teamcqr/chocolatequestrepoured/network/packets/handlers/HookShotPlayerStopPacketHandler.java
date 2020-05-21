package com.teamcqr.chocolatequestrepoured.network.packets.handlers;

import javax.xml.ws.handler.MessageContext;

import com.teamcqr.chocolatequestrepoured.CQRMain;
import com.teamcqr.chocolatequestrepoured.network.packets.toClient.HookShotPlayerStopPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.relauncher.Side;

public class HookShotPlayerStopPacketHandler implements IMessageHandler<HookShotPlayerStopPacket, IMessage> {
    @Override
    public IMessage onMessage(final HookShotPlayerStopPacket message, MessageContext ctx) {
        if (ctx.side != Side.CLIENT) {
            return null;
        }

        Minecraft minecraft = Minecraft.getMinecraft();

        minecraft.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                HookShotPlayerStopPacketHandler.this.processMessage(message, ctx);
            }
        });

        return null;
    }

    private void processMessage(final HookShotPlayerStopPacket message, MessageContext ctx) {
        PlayerEntity player = CQRMain.proxy.getPlayer(ctx);
        player.setVelocity(0, 0, 0);
        player.velocityChanged = true;
    }
}
