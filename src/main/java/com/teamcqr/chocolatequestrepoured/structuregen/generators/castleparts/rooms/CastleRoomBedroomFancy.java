package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.teamcqr.chocolatequestrepoured.structuregen.dungeons.DungeonCastle;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.decoration.RoomDecorTypes;

import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;

public class CastleRoomBedroomFancy extends CastleRoomGenericBase {
	private DyeColor carpetColor;

	public CastleRoomBedroomFancy(BlockPos startOffset, int sideLength, int height, int floor) {
		super(startOffset, sideLength, height, floor);
		this.roomType = EnumRoomType.BEDROOM_FANCY;
		this.maxSlotsUsed = 2;
		this.defaultCeiling = true;
		this.defaultFloor = true;

		this.decoSelector.registerEdgeDecor(RoomDecorTypes.NONE, 10);
		this.decoSelector.registerEdgeDecor(RoomDecorTypes.SHELF, 2);
		this.decoSelector.registerEdgeDecor(RoomDecorTypes.TABLE_1x1, 4);
		this.decoSelector.registerEdgeDecor(RoomDecorTypes.JUKEBOX, 1);
		this.decoSelector.registerEdgeDecor(RoomDecorTypes.FIREPLACE, 2);
		this.decoSelector.registerEdgeDecor(RoomDecorTypes.BED, 3);

		List<DyeColor> possibleColors = Arrays.asList(DyeColor.values());
		Collections.shuffle(possibleColors);
		this.carpetColor = possibleColors.get(0);
	}

	@Override
	protected BlockState getFloorBlock(DungeonCastle dungeon) {
		return Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, this.carpetColor);
	}
}
