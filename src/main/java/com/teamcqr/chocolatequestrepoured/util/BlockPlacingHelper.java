package com.teamcqr.chocolatequestrepoured.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

import com.teamcqr.chocolatequestrepoured.CQRMain;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.util.BlockSnapshot;

public class BlockPlacingHelper {

	private static Field precipitationHeightMapField = null;

	private static int[] getPrecipitationHeightMap(Chunk chunk) {
		try {
			if (precipitationHeightMapField == null) {
				try {
					precipitationHeightMapField = Chunk.class.getDeclaredField("field_76638_b");
				} catch (NoSuchFieldException e) {
					precipitationHeightMapField = Chunk.class.getDeclaredField("precipitationHeightMap");
				}
				precipitationHeightMapField.setAccessible(true);
			}
			return (int[]) precipitationHeightMapField.get(chunk);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			CQRMain.logger.error("Failed to get value of Chunk.precipitationHeightMap field", e);
		}
		return new int[256];
	}

	private static Method relightBlockMethod = null;

	private static void relightBlock(Chunk chunk, int x, int y, int z) {
		try {
			if (relightBlockMethod == null) {
				try {
					relightBlockMethod = Chunk.class.getDeclaredMethod("func_76615_h", int.class, int.class, int.class);
				} catch (NoSuchMethodException e) {
					relightBlockMethod = Chunk.class.getDeclaredMethod("relightBlock", int.class, int.class, int.class);
				}
				relightBlockMethod.setAccessible(true);
			}
			relightBlockMethod.invoke(chunk, x, y, z);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			CQRMain.logger.error("Failed to invoke Chunk.relightBlock method", e);
		}
	}

	private static Method propagateSkylightOcclusionMethod = null;

	private static void propagateSkylightOcclusion(Chunk chunk, int x, int z) {
		try {
			if (propagateSkylightOcclusionMethod == null) {
				try {
					propagateSkylightOcclusionMethod = Chunk.class.getDeclaredMethod("func_76595_e", int.class, int.class);
				} catch (NoSuchMethodException e) {
					propagateSkylightOcclusionMethod = Chunk.class.getDeclaredMethod("propagateSkylightOcclusion", int.class, int.class);
				}
				propagateSkylightOcclusionMethod.setAccessible(true);
			}
			propagateSkylightOcclusionMethod.invoke(chunk, x, z);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			CQRMain.logger.error("Failed to invoke Chunk.propagateSkylightOcclusion method", e);
		}
	}

	public static boolean setBlockState(World world, BlockPos pos, IBlockState newState, int flags, boolean updateLight) {
		if (world.isOutsideBuildHeight(pos)) {
			return false;
		}

		if (!world.isRemote && world.getWorldInfo().getTerrainType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
			return false;
		}

		Chunk chunk = world.getChunkFromBlockCoords(pos);
		IBlockState oldState = chunk.getBlockState(pos);
		int oldLight = oldState.getLightValue(world, pos);
		int oldOpacity = oldState.getLightOpacity(world, pos);

		IBlockState iblockstate = setBlockState(world, chunk, pos, newState, updateLight);

		if (iblockstate == null) {
			return false;
		}

		if (updateLight && (newState.getLightOpacity(world, pos) != oldOpacity || newState.getLightValue(world, pos) != oldLight)) {
			world.profiler.startSection("checkLight");
			world.checkLight(pos);
			world.profiler.endSection();
		}

		if (!world.isRemote && world.captureBlockSnapshots) {
			world.capturedBlockSnapshots.add(new BlockSnapshot(world, pos.toImmutable(), oldState, flags));
		} else {
			world.markAndNotifyBlock(pos, chunk, iblockstate, newState, flags);
		}

		return true;
	}

	@Nullable
	public static IBlockState setBlockState(World world, Chunk chunk, BlockPos pos, IBlockState state, boolean updateLight) {
		int i = pos.getX() & 15;
		int j = pos.getY();
		int k = pos.getZ() & 15;
		int l = k << 4 | i;

		int[] precipitationHeightMap = getPrecipitationHeightMap(chunk);
		if (j >= precipitationHeightMap[l] - 1) {
			precipitationHeightMap[l] = -999;
		}

		int i1 = chunk.getHeightMap()[l];
		IBlockState iblockstate = chunk.getBlockState(pos);

		if (iblockstate == state) {
			return null;
		} else {
			Block block = state.getBlock();
			Block block1 = iblockstate.getBlock();
			int k1 = iblockstate.getLightOpacity(world, pos);
			ExtendedBlockStorage extendedblockstorage = chunk.getBlockStorageArray()[j >> 4];
			boolean flag = false;

			if (extendedblockstorage == Chunk.NULL_BLOCK_STORAGE) {
				if (block == Blocks.AIR) {
					return null;
				}

				extendedblockstorage = new ExtendedBlockStorage(j >> 4 << 4, world.provider.hasSkyLight());
				chunk.getBlockStorageArray()[j >> 4] = extendedblockstorage;
				flag = j >= i1;
			}

			extendedblockstorage.set(i, j & 15, k, state);

			if (!world.isRemote) {
				if (block1 != block) {
					block1.breakBlock(world, pos, iblockstate);
				}
				TileEntity te = chunk.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
				if (te != null && te.shouldRefresh(world, pos, iblockstate, state)) {
					world.removeTileEntity(pos);
				}
			} else if (block1.hasTileEntity(iblockstate)) {
				TileEntity te = chunk.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
				if (te != null && te.shouldRefresh(world, pos, iblockstate, state)) {
					world.removeTileEntity(pos);
				}
			}

			if (extendedblockstorage.get(i, j & 15, k).getBlock() != block) {
				return null;
			} else {
				if (updateLight) {
					if (flag) {
						chunk.generateSkylightMap();
					} else {
						int j1 = state.getLightOpacity(world, pos);

						if (j1 > 0) {
							if (j >= i1) {
								relightBlock(chunk, i, j + 1, k);
							}
						} else if (j == i1 - 1) {
							relightBlock(chunk, i, j, k);
						}

						if (j1 != k1 && (j1 < k1 || chunk.getLightFor(EnumSkyBlock.SKY, pos) > 0 || chunk.getLightFor(EnumSkyBlock.BLOCK, pos) > 0)) {
							propagateSkylightOcclusion(chunk, i, k);
						}
					}
				}

				if (!world.isRemote && block1 != block && (!world.captureBlockSnapshots || block.hasTileEntity(state))) {
					block.onBlockAdded(world, pos, state);
				}

				if (block.hasTileEntity(state)) {
					TileEntity tileentity1 = chunk.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);

					if (tileentity1 == null) {
						tileentity1 = block.createTileEntity(world, state);
						world.setTileEntity(pos, tileentity1);
					}

					if (tileentity1 != null) {
						tileentity1.updateContainingBlockInfo();
					}
				}

				chunk.markDirty();
				return iblockstate;
			}
		}
	}

}
