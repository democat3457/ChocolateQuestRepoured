package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms;

import com.teamcqr.chocolatequestrepoured.structuregen.dungeons.DungeonCastle;
import com.teamcqr.chocolatequestrepoured.util.BlockStateGenArray;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class CastleRoomBossLandingEmpty extends CastleRoomDecoratedBase {
	private Direction doorSide;

	public CastleRoomBossLandingEmpty(BlockPos startOffset, int sideLength, int height, Direction doorSide, int floor) {
		super(startOffset, sideLength, height, floor);
		this.roomType = EnumRoomType.LANDING_BOSS;
		this.pathable = false;
		this.doorSide = doorSide;
		this.defaultCeiling = true;
	}

	@Override
	public void generateRoom(BlockStateGenArray genArray, DungeonCastle dungeon) {
	}

	@Override
	public void addInnerWall(Direction side) {
		if (!(this.doorSide.getAxis() == Direction.Axis.X && side == Direction.SOUTH) && !(this.doorSide.getAxis() == Direction.Axis.Z && side == Direction.EAST) && !(side == this.doorSide)) {
			super.addInnerWall(side);
		}
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
		return true;
	}

	@Override
	boolean shouldAddChests() {
		return false;
	}
}
