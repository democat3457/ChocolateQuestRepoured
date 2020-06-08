package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.segments;

import java.util.Random;

public enum EnumCastleDoorType {
	RANDOM(0, 0),
	STANDARD(4, 4),
	AIR(4, 4),
	FENCE_BORDER(4, 4),
	STAIR_BORDER(4, 5),
	GRAND_ENTRY(7, 6);

	private final int width;
	private final int height;

	EnumCastleDoorType(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public static final EnumCastleDoorType[] RANDOM_LIST = { STANDARD, AIR, FENCE_BORDER, STAIR_BORDER };

	static public EnumCastleDoorType getRandomRegularType(Random random) {
		return RANDOM_LIST[random.nextInt(RANDOM_LIST.length)];
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}
}
