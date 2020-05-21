package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.decoration.objects;

import com.teamcqr.chocolatequestrepoured.util.BlockStateGenArray;

public class RoomDecorPlanks extends RoomDecorBlocksBase {
	public RoomDecorPlanks() {
		super();
	}

	@Override
	protected void makeSchematic() {
		this.schematic.add(new DecoBlockBase(0, 0, 0, Blocks.PLANKS.getDefaultState(), BlockStateGenArray.GenerationPhase.MAIN));
	}
}
