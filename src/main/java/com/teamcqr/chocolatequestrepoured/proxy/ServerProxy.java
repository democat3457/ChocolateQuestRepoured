package com.teamcqr.chocolatequestrepoured.proxy;

import javax.xml.ws.handler.MessageContext;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ServerProxy implements IProxy {

	@Override
	public void preInit() {

	}

	@Override
	public void init() {

	}

	@Override
	public void postInit() {

	}

	@Override
	public PlayerEntity getPlayer(MessageContext context) {
		return context.getServerHandler().player;
	}

	@Override
	public World getWorld(MessageContext context) {
		return context.getServerHandler().player.world;
	}

}
