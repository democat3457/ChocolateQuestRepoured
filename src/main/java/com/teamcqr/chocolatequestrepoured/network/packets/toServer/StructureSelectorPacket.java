package com.teamcqr.chocolatequestrepoured.network.packets.toServer;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class StructureSelectorPacket implements IMessage {

	private Hand hand;

	public StructureSelectorPacket() {

	}

	public StructureSelectorPacket(Hand hand) {
		this.hand = hand;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.hand = buf.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(this.hand == Hand.MAIN_HAND ? true : false);
	}

	public Hand getHand() {
		return this.hand;
	}

}
