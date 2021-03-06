package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.decoration;

import com.teamcqr.chocolatequestrepoured.structuregen.dungeons.DungeonCastle;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.CastleRoomBase;
import com.teamcqr.chocolatequestrepoured.util.BlockStateGenArray;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;

public interface IRoomDecor {
	boolean wouldFit(BlockPos start, EnumFacing side, HashSet<BlockPos> decoArea, HashSet<BlockPos> decoMap, CastleRoomBase room);

	void build(World world, BlockStateGenArray genArray, CastleRoomBase room, DungeonCastle dungeon, BlockPos start, EnumFacing side, HashSet<BlockPos> decoMap);
}
