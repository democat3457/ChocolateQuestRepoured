package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.decoration.objects;

import com.teamcqr.chocolatequestrepoured.util.BlockStateGenArray;

import net.minecraft.block.BlockFurnace;
import net.minecraft.util.Direction;

public class RoomDecorFurnace extends RoomDecorBlocksBase {
	public RoomDecorFurnace() {
		super();
	}

	@Override
	protected void makeSchematic() {
		this.schematic.add(new DecoBlockRotating(0, 0, 0, Blocks.FURNACE.getDefaultState(), BlockFurnace.FACING, Direction.SOUTH, BlockStateGenArray.GenerationPhase.MAIN));
	}
}
