package com.teamcqr.chocolatequestrepoured.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.teamcqr.chocolatequestrepoured.CQRMain;
import com.teamcqr.chocolatequestrepoured.objects.banners.BannerHelper;
import com.teamcqr.chocolatequestrepoured.objects.blocks.BlockExporterChest;
import com.teamcqr.chocolatequestrepoured.structuregen.DungeonRegistry;
import com.teamcqr.chocolatequestrepoured.structuregen.dungeons.DungeonBase;
import com.teamcqr.chocolatequestrepoured.structuregen.structurefile.CQStructure;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;

/**
 * Copyright (c) 29.04.2019<br>
 * Developed by DerToaster98<br>
 * GitHub: https://github.com/DerToaster98
 */
public class DungeonGenUtils {

	public static boolean isInsideCircle(int ix, int iz, int radius, BlockPos center) {
		BlockPos newPos = new BlockPos(center.getX() + ix, center.getY(), center.getZ() + iz);

		return isInsideSphere(newPos, center, radius);
	}

	public static boolean isInsideSphere(BlockPos pos, BlockPos center, int radius) {
		if (Math.abs(center.getDistance(pos.getX(), pos.getY(), pos.getZ())) < radius) {
			return true;
		}
		return false;
	}

	public static int getHighestYAt(Chunk chunk, int x, int z, boolean countWaterAsAir) {
		int y = 255;
		Block block = chunk.getBlockState(x, y, z).getBlock();
		while (Block.isEqualTo(block, Blocks.AIR) || (countWaterAsAir && (Block.isEqualTo(block, Blocks.WATER) || Block.isEqualTo(block, Blocks.FLOWING_WATER)))) {
			y--;
			block = chunk.getBlockState(x, y, z).getBlock();
		}

		return y;
	}

	public static boolean PercentageRandom(int number, Random rdm) {
		if (number >= 100) {
			return true;
		}
		return rdm.nextInt(100) < number;
	}

	public static boolean PercentageRandom(double number, long seed) {
		return PercentageRandom((int) (number * 100), new Random(seed));
	}

	public static int getIntBetweenBorders(int min, int max, Random rdm) {
		if (min != max && rdm != null) {
			max += 1;
			int ret = min + rdm.nextInt(max - min);
			return ret;
		}
		return min;
	}

	public static int getIntBetweenBorders(int min, int max) {
		Random rdm = new Random();
		max += 1;
		try {
			return min + rdm.nextInt(max - min);
		} catch (IllegalArgumentException ex) {
			return min;
		}
	}

	public static boolean isLootChest(Block b) {
		return b instanceof BlockExporterChest;
	}

	public static boolean isCQBanner(TileEntityBanner banner) {
		return BannerHelper.isCQBanner(banner);
	}

	public static boolean isInWallRange(World world, int chunkX, int chunkZ) {
		// Check if the wall is enabled
		if (!CQRConfig.wall.enabled) {
			return false;
		}
		// Check if the world is the overworld
		if (world.provider.getDimension() != 0) {
			return false;
		}
		// Check the coordinates
		if (chunkZ < -CQRConfig.wall.distance - 12) {
			return false;
		}
		if (chunkZ > -CQRConfig.wall.distance + 12) {
			return false;
		}

		return true;
	}

	public static boolean isFarAwayEnoughFromSpawn(World world, int chunkX, int chunkZ) {
		Chunk spawnChunk = world.getChunkFromBlockCoords(world.getSpawnPoint());
		int x = chunkX - spawnChunk.x;
		int z = chunkZ - spawnChunk.z;
		return Math.sqrt(x * x + z * z) >= CQRConfig.general.dungeonSpawnDistance;
	}

	public static boolean isFarAwayEnoughFromLocationSpecifics(World world, int chunkX, int chunkZ, int dungeonSeparation) {
		for (DungeonBase dungeon : DungeonRegistry.getInstance().getCoordinateSpecificDungeons()) {
			int x = chunkX - dungeon.getLockedPos().getX() * 16;
			int z = chunkZ - dungeon.getLockedPos().getZ() * 16;
			if (Math.sqrt(x * x + z * z) < dungeonSeparation) {
				return false;
			}
		}

		return true;
	}

	public static Set<DungeonBase> getLocSpecDungeonsForChunk(World world, int chunkX, int chunkZ) {
		Set<DungeonBase> dungeons = new HashSet<DungeonBase>();

		for (DungeonBase dungeon : DungeonRegistry.getInstance().getCoordinateSpecificDungeons()) {
			Chunk chunk = world.getChunkFromBlockCoords(dungeon.getLockedPos());
			if (chunk.x == chunkX && chunk.z == chunkZ && dungeon.isDimensionAllowed(world.provider.getDimension())) {
				dungeons.add(dungeon);
			}
		}

		if (dungeons.size() > 1) {
			CQRMain.logger.warn("Found " + dungeons.size() + " coordinate specific dungeons for chunkX=" + chunkX + ", chunkZ=" + chunkZ + "!");
		}

		return dungeons;
	}

	/*
	 * Rotate a vec3i to align with the given side.
	 * Assumes that the vec3i is default +x right, +z down coordinate system
	 */
	public static Vec3i rotateVec3i(Vec3i vec, EnumFacing side) {
		if (side == EnumFacing.SOUTH) {
			return new Vec3i(-vec.getX(), vec.getY(), -vec.getZ());
		} else if (side == EnumFacing.WEST) {
			return new Vec3i(vec.getZ(), vec.getY(), -vec.getX());
		} else if (side == EnumFacing.EAST) {
			return new Vec3i(-vec.getZ(), vec.getY(), vec.getX());
		} else {
			// North side, or some other invalid side
			return vec;
		}
	}

	public static int randomBetween(Random random, int low, int high) {
		if (high <= low) {
			return low;
		} else {
			return low + random.nextInt(high - low + 1);
		}
	}

	public static int randomBetweenGaussian(Random random, int low, int high) {
		if (high <= low) {
			return low;
		} else {
			double avg = low + ((high - low) / 2d);
			double stdDev = (high - avg) / 3d; // guarantees that MOST (99.7%) results will be between low & high
			double gaussian = random.nextGaussian();
			int result = (int) (avg + (gaussian * stdDev) + 0.5); // 0.5 is added for rounding to nearest whole number
			if (result < low) {
				return low;
			} else if (result > high) {
				return high;
			} else {
				return result;
			}
		}
	}

	public static Vec3i rotateMatrixOffsetCW(Vec3i offset, int sizeX, int sizeZ, int numRotations) {
		final int maxXIndex = sizeX - 1;
		final int maxZIndex = sizeZ - 1;

		if (numRotations % 4 == 0) {
			return new Vec3i(offset.getX(), offset.getY(), offset.getZ());
		} else if (numRotations % 4 == 1) {
			return new Vec3i(maxZIndex - offset.getZ(), offset.getY(), offset.getX());
		} else if (numRotations % 4 == 2) {
			return new Vec3i(maxXIndex - offset.getX(), offset.getY(), maxZIndex - offset.getZ());
		} else {
			return new Vec3i(offset.getZ(), offset.getY(), maxXIndex - offset.getX());
		}
	}

	public static int getCWRotationsBetween(EnumFacing start, EnumFacing end) {
		int rotations = 0;
		if (start.getAxis().isHorizontal() && end.getAxis().isHorizontal()) {
			while (start != end) {
				start = start.rotateY();
				rotations++;
			}
		}
		return rotations;
	}

	public static EnumFacing rotateFacingNTimesAboutY(EnumFacing facing, int n) {
		for (int i = 0; i < n; i++) {
			facing = facing.rotateY();
		}
		return facing;
	}

	public static BlockPos getMinPos(BlockPos pos1, BlockPos pos2) {
		return new BlockPos(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
	}

	public static BlockPos getMaxPos(BlockPos pos1, BlockPos pos2) {
		return new BlockPos(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
	}

	public static BlockPos getValidMinPos(BlockPos pos1, BlockPos pos2) {
		return new BlockPos(Math.max(Math.min(pos1.getX(), pos2.getX()), -30000000), Math.max(Math.min(pos1.getY(), pos2.getY()), 0), Math.max(Math.min(pos1.getZ(), pos2.getZ()), -30000000));
	}

	public static BlockPos getValidMaxPos(BlockPos pos1, BlockPos pos2) {
		return new BlockPos(Math.min(Math.max(pos1.getX(), pos2.getX()), 30000000), Math.min(Math.max(pos1.getY(), pos2.getY()), 255), Math.min(Math.max(pos1.getZ(), pos2.getZ()), 30000000));
	}

	public static BlockPos getTransformedStartPos(BlockPos startPos, BlockPos size, PlacementSettings settings) {
		if (settings.getMirror() == Mirror.NONE && settings.getRotation() == Rotation.NONE) {
			return startPos;
		}
		BlockPos pos = Template.transformedBlockPos(settings, size);
		int x = startPos.getX();
		int y = startPos.getY();
		int z = startPos.getZ();
		boolean flag = false;
		if (pos.getX() < 0) {
			x -= pos.getX() + 1;
			flag = true;
		}
		if (pos.getZ() < 0) {
			z -= pos.getZ() + 1;
			flag = true;
		}
		return flag ? new BlockPos(x, y, z) : startPos;
	}

	public static int getYForPos(World world, int x, int z, boolean ignoreWater) {
		int y = 255;
		Material material = world.getBlockState(new BlockPos(x, y, z)).getMaterial();
		while (y > 0 && (material == Material.AIR || material == Material.WOOD || material == Material.LEAVES || material == Material.PLANTS || (ignoreWater && material == Material.WATER))) {
			material = world.getBlockState(new BlockPos(x, --y, z)).getMaterial();
		}
		return y;
	}

	public static Vec3d transformedVec3d(Vec3d vec, PlacementSettings settings) {
		return transformedVec3d(vec, settings.getMirror(), settings.getRotation());
	}

	public static Vec3d transformedVec3d(Vec3d vec, Mirror mirror, Rotation rotation) {
		double i = vec.x;
		double j = vec.y;
		double k = vec.z;
		boolean flag = true;

		switch (mirror) {
		case LEFT_RIGHT:
			k = 1.0D - k;
			break;
		case FRONT_BACK:
			i = 1.0D - i;
			break;
		default:
			flag = false;
		}

		switch (rotation) {
		case COUNTERCLOCKWISE_90:
			return new Vec3d(k, j, 1.0D - i);
		case CLOCKWISE_90:
			return new Vec3d(1.0D - k, j, i);
		case CLOCKWISE_180:
			return new Vec3d(1.0D - i, j, 1.0D - k);
		default:
			return flag ? new Vec3d(i, j, k) : vec;
		}
	}

	public static NBTTagList writePosToList(BlockPos pos) {
		NBTTagList nbtTagList = new NBTTagList();
		nbtTagList.appendTag(new NBTTagInt(pos.getX()));
		nbtTagList.appendTag(new NBTTagInt(pos.getY()));
		nbtTagList.appendTag(new NBTTagInt(pos.getZ()));
		return nbtTagList;
	}

	public static BlockPos readPosFromList(NBTTagList nbtTagList) {
		return new BlockPos(nbtTagList.getIntAt(0), nbtTagList.getIntAt(1), nbtTagList.getIntAt(2));
	}

	public static NBTTagList writeVecToList(Vec3d vec) {
		NBTTagList nbtTagList = new NBTTagList();
		nbtTagList.appendTag(new NBTTagDouble(vec.x));
		nbtTagList.appendTag(new NBTTagDouble(vec.y));
		nbtTagList.appendTag(new NBTTagDouble(vec.z));
		return nbtTagList;
	}

	public static Vec3d readVecFromList(NBTTagList nbtTagList) {
		return new Vec3d(nbtTagList.getDoubleAt(0), nbtTagList.getDoubleAt(1), nbtTagList.getDoubleAt(2));
	}

	public static NBTTagList writeUUIDToList(UUID uuid) {
		NBTTagList nbtTagList = new NBTTagList();
		nbtTagList.appendTag(new NBTTagLong(uuid.getMostSignificantBits()));
		nbtTagList.appendTag(new NBTTagLong(uuid.getLeastSignificantBits()));
		return nbtTagList;
	}

	public static UUID readUUIDFromList(NBTTagList nbtTagList) {
		NBTBase nbtM = nbtTagList.get(0);
		NBTBase nbtL = nbtTagList.get(1);
		return new UUID(nbtM instanceof NBTTagLong ? ((NBTTagLong) nbtM).getLong() : 0, nbtM instanceof NBTTagLong ? ((NBTTagLong) nbtL).getLong() : 0);
	}

	/**
	 * Returns the passed position with the half of the transformed structure sizeX and sizeZ subtracted.
	 */
	public static BlockPos getCentralizedPosForStructure(BlockPos pos, CQStructure structure, PlacementSettings settings) {
		BlockPos transformedSize = Template.transformedBlockPos(settings, structure.getSize());
		return pos.add(-(transformedSize.getX() >> 1), 0, -(transformedSize.getZ() >> 1));
	}

}
