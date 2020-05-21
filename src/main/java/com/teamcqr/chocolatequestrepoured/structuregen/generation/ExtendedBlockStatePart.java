package com.teamcqr.chocolatequestrepoured.structuregen.generation;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.teamcqr.chocolatequestrepoured.structureprot.ProtectedRegion;
import com.teamcqr.chocolatequestrepoured.util.BlockPlacingHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class ExtendedBlockStatePart implements IStructure {

	private BlockPos pos;
	private BlockPos size;
	private ExtendedBlockState[][][] extendedstates;

	public ExtendedBlockStatePart(CompoundNBT compound) {
		this.readFromNBT(compound);
	}

	public ExtendedBlockStatePart(BlockPos pos, BlockPos size, Block[][][] blockArray) {
		this.pos = pos;
		this.size = size;
		this.extendedstates = new ExtendedBlockState[this.size.getX()][this.size.getY()][this.size.getZ()];

		for (int x = 0; x < this.size.getX() && x < blockArray.length; x++) {
			for (int y = 0; y < this.size.getY() && y < blockArray[x].length; y++) {
				for (int z = 0; z < this.size.getZ() && z < blockArray[x][y].length; z++) {
					if (blockArray[x][y][z] != null) {
						this.extendedstates[x][y][z] = new ExtendedBlockState(blockArray[x][y][z].getDefaultState(), null);
					}
				}
			}
		}
	}

	public ExtendedBlockStatePart(BlockPos pos, BlockPos size, BlockState[][][] blockStateArray) {
		this.pos = pos;
		this.size = size;
		this.extendedstates = new ExtendedBlockState[this.size.getX()][this.size.getY()][this.size.getZ()];

		for (int x = 0; x < this.size.getX() && x < blockStateArray.length; x++) {
			for (int y = 0; y < this.size.getY() && y < blockStateArray[x].length; y++) {
				for (int z = 0; z < this.size.getZ() && z < blockStateArray[x][y].length; z++) {
					if (blockStateArray[x][y][z] != null) {
						this.extendedstates[x][y][z] = new ExtendedBlockState(blockStateArray[x][y][z], null);
					}
				}
			}
		}
	}

	public ExtendedBlockStatePart(BlockPos pos, BlockPos size, ExtendedBlockState[][][] extendedBlockStateArray) {
		this.pos = pos;
		this.size = size;
		this.extendedstates = new ExtendedBlockState[this.size.getX()][this.size.getY()][this.size.getZ()];

		for (int x = 0; x < this.size.getX() && x < extendedBlockStateArray.length; x++) {
			for (int y = 0; y < this.size.getY() && y < extendedBlockStateArray[x].length; y++) {
				for (int z = 0; z < this.size.getZ() && z < extendedBlockStateArray[x][y].length; z++) {
					this.extendedstates[x][y][z] = extendedBlockStateArray[x][y][z];
				}
			}
		}
	}

	@Override
	public void generate(World world, ProtectedRegion protectedRegion) {
		BlockPlacingHelper.setBlockStates(world, this.pos, this.extendedstates, 2);
	}

	@Override
	public boolean canGenerate(World world) {
		return world.isAreaLoaded(this.pos, this.pos.add(this.size));
	}

	@Override
	public CompoundNBT writeToNBT() {
		CompoundNBT compound = new CompoundNBT();

		compound.putString("id", "extendedBlockStatePart");
		compound.put("pos", NBTUtil.writeBlockPos(this.pos));
		compound.put("size", NBTUtil.writeBlockPos(this.size));

		ListNBT nbtTagList = new ListNBT();
		for (int x = 0; x < this.size.getX(); x++) {
			for (int y = 0; y < this.size.getY(); y++) {
				for (int z = 0; z < this.size.getZ(); z++) {
					CompoundNBT tag = new CompoundNBT();
					ExtendedBlockState extendedstate = this.extendedstates[x][y][z];

					if (extendedstate != null) {
						BlockState blockstate = extendedstate.getState();
						Block block = blockstate.getBlock();
						tag.putString("block", block.getRegistryName().toString());
						tag.putInt("meta", block.getMetaFromState(blockstate));
						CompoundNBT tileentitydata = extendedstate.getTileentitydata();

						if (tileentitydata != null) {
							tag.put("nbt", tileentitydata);
						}
					}
					nbtTagList.add(tag);
				}
			}
		}
		compound.put("blocks", nbtTagList);

		return compound;
	}

	@Override
	public void readFromNBT(CompoundNBT compound) {
		this.pos = NBTUtil.readBlockPos(compound.getCompound("pos"));
		this.size = NBTUtil.readBlockPos(compound.getCompound("size"));
		this.extendedstates = new ExtendedBlockState[this.size.getX()][this.size.getY()][this.size.getZ()];

		ListNBT nbtTagList = compound.getList("blocks", 10);
		for (int x = 0; x < this.size.getX(); x++) {
			for (int y = 0; y < this.size.getY(); y++) {
				for (int z = 0; z < this.size.getZ(); z++) {
					CompoundNBT tag = nbtTagList.getCompound(x * this.size.getY() * this.size.getZ() + y * this.size.getZ() + z);

					if (tag.contains("block")) {
						Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("block")));

						if (block != null) {
							BlockState blockstate = block.getStateFromMeta(tag.getInt("meta"));
							CompoundNBT tileentitydata = tag.contains("nbt") ? tag.getCompound("nbt") : null;
							this.extendedstates[x][y][z] = new ExtendedBlockState(blockstate, tileentitydata);
						}
					}
				}
			}
		}
	}

	/**
	 * @param map BlockPos keys are coordinates in the world
	 */
	public static List<ExtendedBlockStatePart> splitBlockMap(Map<BlockPos, Block> map) {
		return ExtendedBlockStatePart.splitBlockList(new ArrayList<>(map.entrySet()));
	}

	/**
	 * @param pos Start position
	 * @param map BlockPos keys are the relative coordinates to the start position
	 */
	public static List<ExtendedBlockStatePart> splitBlockMap(BlockPos pos, Map<BlockPos, Block> map) {
		return ExtendedBlockStatePart.splitBlockList(pos, new ArrayList<>(map.entrySet()));
	}

	/**
	 * @param map BlockPos keys are coordinates in the world
	 */
	public static List<ExtendedBlockStatePart> splitBlockStateMap(Map<BlockPos, BlockState> map) {
		return ExtendedBlockStatePart.splitBlockStateList(new ArrayList<>(map.entrySet()));
	}

	/**
	 * @param pos Start position
	 * @param map BlockPos keys are the relative coordinates to the start position
	 */
	public static List<ExtendedBlockStatePart> splitBlockStateMap(BlockPos pos, Map<BlockPos, BlockState> map) {
		return ExtendedBlockStatePart.splitBlockStateList(pos, new ArrayList<>(map.entrySet()));
	}

	/**
	 * @param map BlockPos keys are coordinates in the world
	 */
	public static List<ExtendedBlockStatePart> splitExtendedBlockStateMap(Map<BlockPos, ExtendedBlockState> map) {
		return ExtendedBlockStatePart.splitExtendedBlockStateList(new ArrayList<>(map.entrySet()));
	}

	/**
	 * @param pos Start position
	 * @param map BlockPos keys are the relative coordinates to the start position
	 */
	public static List<ExtendedBlockStatePart> splitExtendedBlockStateMap(BlockPos pos, Map<BlockPos, ExtendedBlockState> map) {
		return ExtendedBlockStatePart.splitExtendedBlockStateList(pos, new ArrayList<>(map.entrySet()));
	}

	/**
	 * @param entryList BlockPos keys are coordinates in the world
	 */
	public static List<ExtendedBlockStatePart> splitBlockList(List<Map.Entry<BlockPos, Block>> entryList) {
		if (!entryList.isEmpty()) {
			List<Map.Entry<BlockPos, ExtendedBlockState>> list = new ArrayList<>(entryList.size());
			for (Map.Entry<BlockPos, Block> entry : entryList) {
				list.add(new AbstractMap.SimpleEntry<>(entry.getKey(), new ExtendedBlockState(entry.getValue().getDefaultState(), null)));
			}
			return ExtendedBlockStatePart.splitExtendedBlockStateList(list);
		}

		return Collections.emptyList();
	}

	/**
	 * @param pos       Start position
	 * @param entryList BlockPos keys are the relative coordinates to the start position
	 */
	public static List<ExtendedBlockStatePart> splitBlockList(BlockPos pos, List<Map.Entry<BlockPos, Block>> entryList) {
		if (!entryList.isEmpty()) {
			List<Map.Entry<BlockPos, ExtendedBlockState>> list = new ArrayList<>(entryList.size());
			for (Map.Entry<BlockPos, Block> entry : entryList) {
				list.add(new AbstractMap.SimpleEntry<>(entry.getKey(), new ExtendedBlockState(entry.getValue().getDefaultState(), null)));
			}
			return ExtendedBlockStatePart.splitExtendedBlockStateList(pos, list);
		}

		return Collections.emptyList();
	}

	/**
	 * @param entryList BlockPos keys are coordinates in the world
	 */
	public static List<ExtendedBlockStatePart> splitBlockStateList(List<Map.Entry<BlockPos, BlockState>> entryList) {
		if (!entryList.isEmpty()) {
			List<Map.Entry<BlockPos, ExtendedBlockState>> list = new ArrayList<>(entryList.size());
			for (Map.Entry<BlockPos, BlockState> entry : entryList) {
				list.add(new AbstractMap.SimpleEntry<>(entry.getKey(), new ExtendedBlockState(entry.getValue(), null)));
			}
			return ExtendedBlockStatePart.splitExtendedBlockStateList(list);
		}

		return Collections.emptyList();
	}

	/**
	 * @param pos       Start position
	 * @param entryList BlockPos keys are the relative coordinates to the start position
	 */
	public static List<ExtendedBlockStatePart> splitBlockStateList(BlockPos pos, List<Map.Entry<BlockPos, BlockState>> entryList) {
		if (!entryList.isEmpty()) {
			List<Map.Entry<BlockPos, ExtendedBlockState>> list = new ArrayList<>(entryList.size());
			for (Map.Entry<BlockPos, BlockState> entry : entryList) {
				list.add(new AbstractMap.SimpleEntry<>(entry.getKey(), new ExtendedBlockState(entry.getValue(), null)));
			}
			return ExtendedBlockStatePart.splitExtendedBlockStateList(pos, list);
		}

		return Collections.emptyList();
	}

	/**
	 * @param entryList BlockPos keys are coordinates in the world
	 */
	public static List<ExtendedBlockStatePart> splitExtendedBlockStateList(List<Map.Entry<BlockPos, ExtendedBlockState>> entryList) {
		if (!entryList.isEmpty()) {
			int startX = entryList.get(0).getKey().getX();
			int startY = entryList.get(0).getKey().getY();
			int startZ = entryList.get(0).getKey().getZ();
			int endX = startX;
			int endY = startY;
			int endZ = startZ;

			for (Map.Entry<BlockPos, ExtendedBlockState> entry : entryList) {
				BlockPos pos = entry.getKey();
				if (pos.getX() < startX) {
					startX = pos.getX();
				}
				if (pos.getY() < startY) {
					startY = pos.getY();
				}
				if (pos.getZ() < startZ) {
					startZ = pos.getZ();
				}
				if (pos.getX() > endX) {
					endX = pos.getX();
				}
				if (pos.getY() > endY) {
					endY = pos.getY();
				}
				if (pos.getZ() > endZ) {
					endZ = pos.getZ();
				}
			}

			ExtendedBlockState[][][] extendedstates = new ExtendedBlockState[endX - startX + 1][endY - startY + 1][endZ - startZ + 1];

			for (Map.Entry<BlockPos, ExtendedBlockState> entry : entryList) {
				BlockPos pos = entry.getKey();
				extendedstates[pos.getX() - startX][pos.getY() - startY][pos.getZ() - startZ] = entry.getValue();
			}

			return ExtendedBlockStatePart.split(new BlockPos(startX, startY, startZ), extendedstates);
		}

		return Collections.emptyList();
	}

	/**
	 * @param pos       Start position
	 * @param entryList BlockPos keys are the relative coordinates to the start position
	 */
	public static List<ExtendedBlockStatePart> splitExtendedBlockStateList(BlockPos pos, List<Map.Entry<BlockPos, ExtendedBlockState>> entryList) {
		if (!entryList.isEmpty()) {
			int endX = entryList.get(0).getKey().getX();
			int endY = entryList.get(0).getKey().getY();
			int endZ = entryList.get(0).getKey().getZ();

			for (Map.Entry<BlockPos, ExtendedBlockState> entry : entryList) {
				BlockPos position = entry.getKey();
				if (position.getX() > endX) {
					endX = position.getX();
				}
				if (position.getY() > endY) {
					endY = position.getY();
				}
				if (position.getZ() > endZ) {
					endZ = position.getZ();
				}
			}

			ExtendedBlockState[][][] extendedstates = new ExtendedBlockState[endX - pos.getX()][endY - pos.getY()][endZ - pos.getZ()];

			for (Map.Entry<BlockPos, ExtendedBlockState> entry : entryList) {
				BlockPos position = entry.getKey();
				extendedstates[position.getX()][position.getY()][position.getZ()] = entry.getValue();
			}

			return ExtendedBlockStatePart.split(pos, extendedstates);
		}

		return Collections.emptyList();
	}

	/**
	 * @param pos   Start position
	 * @param array Array keys are the relative coordinates to the start position
	 */
	public static List<ExtendedBlockStatePart> split(BlockPos pos, Block[][][] array) {
		return ExtendedBlockStatePart.split(pos, array, 16);
	}

	/**
	 * @param pos   Start position
	 * @param array Array keys are the relative coordinates to the start position
	 */
	public static List<ExtendedBlockStatePart> split(BlockPos pos, BlockState[][][] array) {
		return ExtendedBlockStatePart.split(pos, array, 16);
	}

	/**
	 * @param pos   Start position
	 * @param array Array keys are the relative coordinates to the start position
	 */
	public static List<ExtendedBlockStatePart> split(BlockPos pos, ExtendedBlockState[][][] array) {
		return ExtendedBlockStatePart.split(pos, array, 16);
	}

	/**
	 * @param pos   Start position
	 * @param array Array keys are the relative coordinates to the start position
	 * @param size  Size of the parts
	 */
	public static List<ExtendedBlockStatePart> split(BlockPos pos, Block[][][] array, int size) {
		if (array.length > 0 && array[0].length > 0 && array[0][0].length > 0) {
			ExtendedBlockState[][][] blocks = new ExtendedBlockState[array.length][array[0].length][array[0][0].length];
			for (int x = 0; x < blocks.length; x++) {
				for (int y = 0; y < blocks[x].length && y < array[x].length; y++) {
					for (int z = 0; z < blocks[x][y].length && z < array[x][y].length; z++) {
						if (array[x][y][z] != null) {
							blocks[x][y][z] = new ExtendedBlockState(array[x][y][z].getDefaultState(), null);
						}
					}
				}
			}

			return ExtendedBlockStatePart.split(pos, blocks, size);
		}

		return Collections.emptyList();
	}

	/**
	 * @param pos   Start position
	 * @param array Array keys are the relative coordinates to the start position
	 * @param size  Size of the parts
	 */
	public static List<ExtendedBlockStatePart> split(BlockPos pos, BlockState[][][] array, int size) {
		if (array.length > 0 && array[0].length > 0 && array[0][0].length > 0) {
			ExtendedBlockState[][][] blocks = new ExtendedBlockState[array.length][array[0].length][array[0][0].length];
			for (int x = 0; x < blocks.length; x++) {
				for (int y = 0; y < blocks[x].length && y < array[x].length; y++) {
					for (int z = 0; z < blocks[x][y].length && z < array[x][y].length; z++) {
						if (array[x][y][z] != null) {
							blocks[x][y][z] = new ExtendedBlockState(array[x][y][z], null);
						}
					}
				}
			}

			return ExtendedBlockStatePart.split(pos, blocks, size);
		}

		return Collections.emptyList();
	}

	/**
	 * @param pos   Start position
	 * @param array Array keys are the relative coordinates to the start position
	 * @param size  Size of the parts
	 */
	public static List<ExtendedBlockStatePart> split(BlockPos pos, ExtendedBlockState[][][] array, int size) {
		List<ExtendedBlockStatePart> list = new ArrayList<>();

		if (array.length > 0 && array[0].length > 0 && array[0][0].length > 0) {
			int xIterations = MathHelper.ceil((double) array.length / size);
			int yIterations = MathHelper.ceil((double) array[0].length / size);
			int zIterations = MathHelper.ceil((double) array[0][0].length / size);
			for (int y = 0; y < yIterations; y++) {
				for (int x = 0; x < xIterations; x++) {
					for (int z = 0; z < zIterations; z++) {
						int partOffsetX = x * size;
						int partOffsetY = y * size;
						int partOffsetZ = z * size;
						int partSizeX = x == xIterations - 1 ? array.length - partOffsetX : size;
						int partSizeY = y == yIterations - 1 ? array[0].length - partOffsetY : size;
						int partSizeZ = z == zIterations - 1 ? array[0][0].length - partOffsetZ : size;
						ExtendedBlockState[][][] blocks = new ExtendedBlockState[partSizeX][partSizeY][partSizeZ];
						boolean empty = true;

						for (int x1 = 0; x1 < partSizeX; x1++) {
							int x2 = x1 + partOffsetX;
							for (int y1 = 0; y1 < partSizeY && y1 < array[x2].length; y1++) {
								int y2 = y1 + partOffsetY;
								for (int z1 = 0; z1 < partSizeZ && z1 < array[x2][y2].length; z1++) {
									blocks[x1][y1][z1] = array[x2][y2][z1 + partOffsetZ];
									if (empty && blocks[x1][y1][z1] != null) {
										empty = false;
									}
								}
							}
						}

						if (!empty) {
							list.add(new ExtendedBlockStatePart(pos.add(partOffsetX, partOffsetY, partOffsetZ), new BlockPos(partSizeX, partSizeY, partSizeZ), blocks));
						}
					}
				}
			}
		}

		return list;
	}

	public static class ExtendedBlockState {

		private BlockState state;
		private CompoundNBT tileentitydata;

		public ExtendedBlockState(BlockState state, @Nullable CompoundNBT tileentitydata) {
			this.state = state;
			this.tileentitydata = tileentitydata;
		}

		public BlockState getState() {
			return this.state;
		}

		public CompoundNBT getTileentitydata() {
			return this.tileentitydata;
		}

	}

	@Override
	public BlockPos getPos() {
		return this.pos;
	}

	@Override
	public BlockPos getSize() {
		return this.size;
	}

}
