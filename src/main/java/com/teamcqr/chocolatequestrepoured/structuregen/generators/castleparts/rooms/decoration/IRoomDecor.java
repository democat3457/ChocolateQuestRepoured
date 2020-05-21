package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.decoration;

import java.util.HashSet;

import com.teamcqr.chocolatequestrepoured.structuregen.dungeons.DungeonCastle;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.CastleRoomBase;
import com.teamcqr.chocolatequestrepoured.util.BlockStateGenArray;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IRoomDecor {
	boolean wouldFit(BlockPos start, Direction side, HashSet<BlockPos> decoArea, HashSet<BlockPos> decoMap, CastleRoomBase room);

	void build(World world, BlockStateGenArray genArray, CastleRoomBase room, DungeonCastle dungeon, BlockPos start, Direction side, HashSet<BlockPos> decoMap);
}
