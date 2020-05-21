package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.decoration.objects;

import com.teamcqr.chocolatequestrepoured.util.BlockStateGenArray;

public class RoomDecorNone extends RoomDecorBlocksBase {
	public RoomDecorNone() {
		super();
	}

	@Override
	protected void makeSchematic() {
		this.schematic.add(new DecoBlockBase(0, 0, 0, Blocks.AIR.getDefaultState(), BlockStateGenArray.GenerationPhase.MAIN));
	}
}
