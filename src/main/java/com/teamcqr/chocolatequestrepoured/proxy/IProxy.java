package com.teamcqr.chocolatequestrepoured.proxy;

import javax.xml.ws.handler.MessageContext;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface IProxy {

	public void preInit();

	public void init();

	public void postInit();

	public PlayerEntity getPlayer(MessageContext ctx);

	public World getWorld(MessageContext ctx);

}
