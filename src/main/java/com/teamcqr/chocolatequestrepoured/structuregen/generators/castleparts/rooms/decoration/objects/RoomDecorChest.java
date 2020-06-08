package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.decoration.objects;

import java.util.HashSet;

import com.teamcqr.chocolatequestrepoured.CQRMain;
import com.teamcqr.chocolatequestrepoured.structuregen.WorldDungeonGenerator;
import com.teamcqr.chocolatequestrepoured.structuregen.dungeons.DungeonCastle;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.CastleRoomBase;
import com.teamcqr.chocolatequestrepoured.util.BlockStateGenArray;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RoomDecorChest extends RoomDecorBlocksBase {
	public RoomDecorChest() {
		super();
	}

	@Override
	protected void makeSchematic() {
		this.schematic.add(new DecoBlockRotating(0, 0, 0, Blocks.CHEST.getDefaultState(), BlockChest.FACING, Direction.SOUTH, BlockStateGenArray.GenerationPhase.MAIN));
	}

	@Override
	public void build(World world, BlockStateGenArray genArray, CastleRoomBase room, DungeonCastle dungeon, BlockPos start, Direction side, HashSet<BlockPos> decoMap) {
		//super.build(world, genArray, room, dungeon, start, side, decoMap);

		ResourceLocation[] chestIDs = room.getChestIDs();
		if (chestIDs != null && chestIDs.length > 0) {
			Block chestBlock = Blocks.CHEST;
			BlockState state = this.schematic.get(0).getState(side);
			TileEntityChest chest = (TileEntityChest) chestBlock.createTileEntity(world, state);
			if (chest != null) {
				ResourceLocation resLoc = chestIDs[dungeon.getRandom().nextInt(chestIDs.length)];
				if (resLoc != null) {
					long seed = WorldDungeonGenerator.getSeed(world, start.getX() + start.getY(), start.getZ() + start.getY());
					chest.setLootTable(resLoc, seed);
				}
				CompoundNBT nbt = chest.writeToNBT(new CompoundNBT());
				genArray.forceAddBlockState(start, state, nbt, BlockStateGenArray.GenerationPhase.MAIN);
				decoMap.add(start);
			}
		} else {
			CQRMain.logger.warn("Placed a chest but could not find a loot table for Room Type {}", room.getRoomType());
		}
	}
}
