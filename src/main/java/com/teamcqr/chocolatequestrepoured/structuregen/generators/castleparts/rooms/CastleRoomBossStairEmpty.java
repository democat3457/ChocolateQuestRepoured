package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms;

import com.teamcqr.chocolatequestrepoured.structuregen.dungeons.DungeonCastle;
import com.teamcqr.chocolatequestrepoured.util.BlockStateGenArray;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class CastleRoomBossStairEmpty extends CastleRoomDecoratedBase {
	private Direction doorSide;

	public CastleRoomBossStairEmpty(BlockPos startOffset, int sideLength, int height, Direction doorSide, int floor) {
		super(startOffset, sideLength, height, floor);
		this.roomType = EnumRoomType.STAIRCASE_BOSS;
		this.pathable = true;
		this.doorSide = doorSide;
	}

	@Override
	public void generateRoom(BlockStateGenArray genArray, DungeonCastle dungeon) {
	}

	@Override
	boolean shouldBuildEdgeDecoration() {
		return false;
	}

	@Override
	boolean shouldBuildWallDecoration() {
		return true;
	}

	@Override
	boolean shouldBuildMidDecoration() {
		return false;
	}

	@Override
	boolean shouldAddSpawners() {
		return false;
	}

	@Override
	boolean shouldAddChests() {
		return false;
	}

	@Override
	public void addInnerWall(Direction side) {
		if (!(this.doorSide.getAxis() == Direction.Axis.X && side == Direction.NORTH) && !(this.doorSide.getAxis() == Direction.Axis.Z && side == Direction.WEST)) {
			super.addInnerWall(side);
		}
	}
}
