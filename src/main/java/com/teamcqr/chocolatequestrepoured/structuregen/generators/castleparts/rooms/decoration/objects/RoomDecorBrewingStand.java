package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.decoration.objects;

import com.teamcqr.chocolatequestrepoured.util.BlockStateGenArray;

public class RoomDecorBrewingStand extends RoomDecorBlocksBase {
	public RoomDecorBrewingStand() {
		super();
	}

	@Override
	protected void makeSchematic() {
		this.schematic.add(new DecoBlockBase(0, 0, 0, Blocks.BREWING_STAND.getDefaultState(), BlockStateGenArray.GenerationPhase.MAIN));
	}
}
