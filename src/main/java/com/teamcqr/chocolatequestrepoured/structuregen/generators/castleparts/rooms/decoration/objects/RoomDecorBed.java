package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.decoration.objects;

import com.teamcqr.chocolatequestrepoured.util.BlockStateGenArray;

import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;

public class RoomDecorBed extends RoomDecorBlocksBase {
	public RoomDecorBed() {
		super();
	}

	@Override
	protected void makeSchematic() {
		BlockState head = Blocks.BED.getDefaultState().withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD);
		this.schematic.add(new DecoBlockRotating(0, 0, 0, head, BlockBed.FACING, Direction.NORTH, BlockStateGenArray.GenerationPhase.MAIN));
		BlockState foot = Blocks.BED.getDefaultState().withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT);
		this.schematic.add(new DecoBlockRotating(0, 0, 1, foot, BlockBed.FACING, Direction.NORTH, BlockStateGenArray.GenerationPhase.MAIN));
	}
}
