package com.teamcqr.chocolatequestrepoured.structuregen.generation;

import java.util.Collection;
import java.util.Collections;

import com.teamcqr.chocolatequestrepoured.structuregen.EDungeonMobType;
import com.teamcqr.chocolatequestrepoured.structuregen.structurefile.AbstractBlockInfo;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;

public class DungeonPartBlockSpecial extends DungeonPartBlock {

	public DungeonPartBlockSpecial(World world, DungeonGenerator dungeonGenerator) {
		this(world, dungeonGenerator, BlockPos.ORIGIN, Collections.emptyList(), new PlacementSettings(), EDungeonMobType.DEFAULT);
	}

	public DungeonPartBlockSpecial(World world, DungeonGenerator dungeonGenerator, BlockPos partPos, Collection<AbstractBlockInfo> blocks, PlacementSettings settings, EDungeonMobType dungeonMobType) {
		super(world, dungeonGenerator, partPos, blocks, settings, dungeonMobType);
	}

	@Override
	public String getId() {
		return DUNGEON_PART_BLOCK_SPECIAL_ID;
	}

	@Override
	public void generateNext() {
		while (!this.blockInfoList.isEmpty()) {
			AbstractBlockInfo blockInfo = this.blockInfoList.removeFirst();
			blockInfo.generate(this.world, this.dungeonGenerator.getPos(), this.partPos, this.settings, this.dungeonMobType, this.dungeonGenerator.getProtectedRegion());
		}
	}

}
