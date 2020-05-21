package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.decoration.objects;

import com.teamcqr.chocolatequestrepoured.util.BlockStateGenArray;

import net.minecraft.block.BlockTorch;
import net.minecraft.util.Direction;

public class RoomDecorTorch extends RoomDecorBlocksBase {
	public RoomDecorTorch() {
		super();
	}

	@Override
	protected void makeSchematic() {
		this.schematic.add(new DecoBlockRotating(0, 0, 0, Blocks.TORCH.getDefaultState(), BlockTorch.FACING, Direction.SOUTH, BlockStateGenArray.GenerationPhase.POST));
	}
}
