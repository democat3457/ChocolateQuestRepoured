package com.teamcqr.chocolatequestrepoured.structuregen.generators;

import java.util.ArrayList;

import com.teamcqr.chocolatequestrepoured.CQRMain;
import com.teamcqr.chocolatequestrepoured.structuregen.EDungeonMobType;
import com.teamcqr.chocolatequestrepoured.structuregen.dungeons.DungeonCastle;
import com.teamcqr.chocolatequestrepoured.structuregen.generation.DungeonPartBlock;
import com.teamcqr.chocolatequestrepoured.structuregen.generation.DungeonPartEntity;
import com.teamcqr.chocolatequestrepoured.structuregen.generation.DungeonPartPlateau;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.CastleRoomSelector;
import com.teamcqr.chocolatequestrepoured.util.BlockStateGenArray;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;

/**
 * Copyright (c) 25.05.2019
 * Developed by KalgogSmash
 * GitHub: https://github.com/KalgogSmash
 */
public class GeneratorCastle extends AbstractDungeonGenerator<DungeonCastle> {

	private CastleRoomSelector roomHelper;

	public GeneratorCastle(World world, BlockPos pos, DungeonCastle dungeon) {
		super(world, pos, dungeon);
	}

	@Override
	public void preProcess() {
		this.roomHelper = new CastleRoomSelector(this.pos, this.dungeon);
		this.roomHelper.randomizeCastle();

		if (this.dungeon.doBuildSupportPlatform()) {
			for (CastleRoomSelector.SupportArea area : this.roomHelper.getSupportAreas()) {
				CQRMain.logger.info("{} {} {}", area.getNwCorner(), area.getBlocksX(), area.getBlocksZ());
				BlockPos p1 = this.pos.add(area.getNwCorner());
				BlockPos p2 = p1.add(area.getBlocksX(), 0, area.getBlocksZ());
				this.dungeonGenerator.add(new DungeonPartPlateau(world, dungeonGenerator, p1.getX(), p1.getZ(), p2.getX(), p2.getY(), p2.getZ(), this.dungeon.getSupportBlock(), this.dungeon.getSupportTopBlock(), 8));
			}
		}
	}

	@Override
	public void buildStructure() {
		BlockStateGenArray genArray = new BlockStateGenArray();
		ArrayList<String> bossUuids = new ArrayList<>();
		EDungeonMobType mobType = dungeon.getDungeonMob();
		if (mobType == EDungeonMobType.DEFAULT) {
			mobType = EDungeonMobType.getMobTypeDependingOnDistance(world, this.pos.getX(), this.pos.getZ());
		}
		this.roomHelper.generate(this.world, genArray, this.dungeon, this.pos, bossUuids, mobType);

		this.dungeonGenerator.add(new DungeonPartBlock(world, dungeonGenerator, pos, genArray.getMainMap().values(), new PlacementSettings(), mobType));
		this.dungeonGenerator.add(new DungeonPartBlock(world, dungeonGenerator, pos, genArray.getPostMap().values(), new PlacementSettings(), mobType));
		this.dungeonGenerator.add(new DungeonPartEntity(world, dungeonGenerator, pos, genArray.getEntityMap().values(), new PlacementSettings(), mobType));
	}

	@Override
	public void postProcess() {
		// Does nothing here
	}

}
