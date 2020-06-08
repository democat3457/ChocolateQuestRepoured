package com.teamcqr.chocolatequestrepoured.structuregen.generators.stronghold;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teamcqr.chocolatequestrepoured.CQRMain;
import com.teamcqr.chocolatequestrepoured.structuregen.EDungeonMobType;
import com.teamcqr.chocolatequestrepoured.structuregen.PlateauBuilder;
import com.teamcqr.chocolatequestrepoured.structuregen.dungeons.DungeonStrongholdOpen;
import com.teamcqr.chocolatequestrepoured.structuregen.generation.DungeonPartBlock;
import com.teamcqr.chocolatequestrepoured.structuregen.generation.DungeonPartBlockSpecial;
import com.teamcqr.chocolatequestrepoured.structuregen.generation.DungeonPartEntity;
import com.teamcqr.chocolatequestrepoured.structuregen.generation.DungeonPartPlateau;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.AbstractDungeonGenerator;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.stronghold.open.StrongholdFloorOpen;
import com.teamcqr.chocolatequestrepoured.structuregen.structurefile.AbstractBlockInfo;
import com.teamcqr.chocolatequestrepoured.structuregen.structurefile.BlockInfo;
import com.teamcqr.chocolatequestrepoured.structuregen.structurefile.CQStructure;
import com.teamcqr.chocolatequestrepoured.util.CQRConfig;
import com.teamcqr.chocolatequestrepoured.util.DungeonGenUtils;
import com.teamcqr.chocolatequestrepoured.util.data.FileIOUtil;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;

/**
 * Copyright (c) 29.04.2019
 * Developed by DerToaster98
 * GitHub: https://github.com/DerToaster98
 */
public class GeneratorStrongholdOpen extends AbstractDungeonGenerator<DungeonStrongholdOpen> {

	private List<String> blacklistedRooms = new ArrayList<>();
	private Tuple<Integer, Integer> structureBounds;

	private PlacementSettings settings = new PlacementSettings();

	private StrongholdFloorOpen[] floors;

	private int dunX;
	private int dunZ;
	
	private int entranceSizeX = 0;
	private int entranceSizeZ = 0;
	
	public GeneratorStrongholdOpen(World world, BlockPos pos, DungeonStrongholdOpen dungeon) {
		super(world, pos, dungeon);
		this.structureBounds = new Tuple<>(dungeon.getRoomSizeX(), dungeon.getRoomSizeZ());

		this.settings.setMirror(Mirror.NONE);
		this.settings.setRotation(Rotation.NONE);
		this.settings.setReplacedBlock(Blocks.STRUCTURE_VOID);
		this.settings.setIntegrity(1.0F);

		this.floors = new StrongholdFloorOpen[dungeon.getRandomFloorCount()];
		this.searchStructureBounds();
		this.computeNotFittingStructures();
	}

	private void computeNotFittingStructures() {
		for (File f : this.dungeon.getRoomFolder().listFiles(FileIOUtil.getNBTFileFilter())) {
			CQStructure struct = this.loadStructureFromFile(f);
			if (struct != null && (struct.getSize().getX() != this.structureBounds.getFirst() || struct.getSize().getZ() != this.structureBounds.getSecond())) {
				this.blacklistedRooms.add(f.getParent() + "/" + f.getName());
			}
		}
	}

	public DungeonStrongholdOpen getDungeon() {
		return this.dungeon;
	}

	private void searchStructureBounds() {

	}

	@Override
	public void preProcess() {
		this.dunX = this.pos.getX();
		this.dunZ = this.pos.getZ();
		BlockPos initPos = this.pos;
		// initPos = initPos.subtract(new Vec3i(0,dungeon.getYOffset(),0));
		// initPos = initPos.subtract(new Vec3i(0,dungeon.getUnderGroundOffset(),0));
		
		int rgd = getDungeon().getRandomRoomCountForFloor();
		if (rgd < 2) {
			rgd = 2;
		}
		if (rgd % 2 != 0) {
			rgd++;
		}
		rgd = (new Double(Math.ceil(Math.sqrt(rgd)))).intValue();
		if(rgd % 2 == 0) {
			rgd++;
		}
		
		StrongholdFloorOpen prevFloor = null;
		for (int i = 0; i < this.floors.length; i++) {
			boolean isFirst = i == 0;
			StrongholdFloorOpen floor = null;
			if(isFirst) {
				floor = new StrongholdFloorOpen(this, rgd, ((Double)Math.floor(rgd /2)).intValue(), ((Double)Math.floor(rgd /2)).intValue());
			} else {
				floor = new StrongholdFloorOpen(this, rgd, prevFloor.getExitStairIndexes().getFirst(), prevFloor.getExitStairIndexes().getSecond());
			}
			File stair = null;
			if (isFirst) {
				stair = this.dungeon.getEntranceStair();
				if (stair == null) {
					CQRMain.logger.error("No entrance stair rooms for Stronghold Open Dungeon: {}", this.getDungeon().getDungeonName());
					return;
				}
			} else {
				stair = this.dungeon.getStairRoom();
				if (stair == null) {
					CQRMain.logger.error("No stair rooms for Stronghold Open Dungeon: {}", this.getDungeon().getDungeonName());
					return;
				}
			}
			floor.setIsFirstFloor(isFirst);
			int dY = initPos.getY() - this.loadStructureFromFile(stair).getSize().getY();
			if (dY <= (this.dungeon.getRoomSizeY() + 2)) {
				this.floors[i - 1].setExitIsBossRoom(true);
			} else {
				initPos = initPos.subtract(new Vec3i(0, this.loadStructureFromFile(stair).getSize().getY(), 0));
				if (!isFirst) {
					initPos = initPos.add(0, this.dungeon.getRoomSizeY(), 0);
				}
				if ((i + 1) == this.floors.length) {
					floor.setExitIsBossRoom(true);
				}
				
				if(isFirst) {
					floor.setEntranceStairPosition(stair, initPos.getX(), initPos.getY(), initPos.getZ());
				} else {
					floor.setEntranceStairPosition(stair, prevFloor.getExitCoordinates().getFirst(), initPos.getY(), prevFloor.getExitCoordinates().getSecond());
				}
				
				floor.calculatePositions();
				initPos = new BlockPos(floor.getExitCoordinates().getFirst(), initPos.getY(), floor.getExitCoordinates().getSecond());
			}
			prevFloor = floor;
			this.floors[i] = floor;
		}
	}

	@Override
	public void buildStructure() {
		File building = this.dungeon.getEntranceBuilding();
		EDungeonMobType mobType = dungeon.getDungeonMob();
		if (mobType == EDungeonMobType.DEFAULT) {
			mobType = EDungeonMobType.getMobTypeDependingOnDistance(world, this.pos.getX(), this.pos.getZ());
		}
		if (building == null || this.dungeon.getEntranceBuildingFolder().listFiles(FileIOUtil.getNBTFileFilter()).length <= 0) {
			CQRMain.logger.error("No entrance buildings for Open Stronghold dungeon: " + this.getDungeon().getDungeonName());
			return;
		}
		CQStructure structure = this.loadStructureFromFile(building);
		if (this.dungeon.doBuildSupportPlatform()) {
			PlateauBuilder supportBuilder = new PlateauBuilder();
			supportBuilder.load(this.dungeon.getSupportBlock(), this.dungeon.getSupportTopBlock());
			this.dungeonGenerator.add(new DungeonPartPlateau(world, dungeonGenerator, this.pos.getX() + 4 + structure.getSize().getX() / 2, this.pos.getZ() + 4 + structure.getSize().getZ() / 2, this.pos.getX() - 4 - structure.getSize().getX() / 2, this.pos.getY(), this.pos.getZ() - 4 - structure.getSize().getZ() / 2, this.dungeon.getSupportBlock(), this.dungeon.getSupportTopBlock(), 8));
		}
		entranceSizeX = structure.getSize().getX();
		entranceSizeZ = structure.getSize().getX();

		BlockPos p = DungeonGenUtils.getCentralizedPosForStructure(this.pos, structure, this.settings);
		this.dungeonGenerator.add(new DungeonPartBlock(this.world, this.dungeonGenerator, p, structure.getBlockInfoList(), this.settings, mobType));
		this.dungeonGenerator.add(new DungeonPartEntity(this.world, this.dungeonGenerator, p, structure.getEntityInfoList(), this.settings, mobType));
		this.dungeonGenerator.add(new DungeonPartBlockSpecial(this.world, this.dungeonGenerator, p, structure.getSpecialBlockInfoList(), this.settings, mobType));
		/*
		 * CQStructure stairs = new CQStructure(dungeon.getStairRoom(), dungeon, chunk.x, chunk.z, dungeon.isProtectedFromModifications());
		 * BlockPos pastePosForStair = new BlockPos(x, y - stairs.getSizeY(), z);
		 * stairs.placeBlocksInWorld(world, pastePosForStair, settings, EPosType.CENTER_XZ_LAYER);
		 */
		// Will generate the structure
		// Algorithm: while(genRooms < rooms && genFloors < maxFloors) do {
		// while(genRoomsOnFloor < roomsPerFloor) {
		// choose structure, calculate next pos and chose next structure (System: structures in different folders named to where they may attach
		// build Staircase at next position
		// genRoomsOnFloor++
		// genFloors++
		// build staircase to bossroom at next position, then build boss room

		// Structure gen information: stored in map with location and structure file
		for (StrongholdFloorOpen floor : this.floors) {
			floor.generateRooms(world, this.dungeonGenerator, mobType);
		}
	}

	@Override
	public void postProcess() {
		// build all the structures in the map
		for (StrongholdFloorOpen floor : this.floors) {
			if (floor == null) {
				CQRMain.logger.error("Floor is null! Not generating it!");
			} else {
				try {
					floor.buildWalls(world, this.dungeonGenerator);
				} catch (NullPointerException ex) {
					CQRMain.logger.error("Error whilst trying to construct wall in open stronghold at: X {}  Y {}  Z {}", this.pos.getX(), this.pos.getY(), this.pos.getZ());
				}
			}
		}

		if (this.dungeon.isCoverBlockEnabled()) {
			Map<BlockPos, IBlockState> stateMap = new HashMap<>();
			
			int startX = this.pos.getX() - entranceSizeX / 3 - CQRConfig.general.supportHillWallSize / 2;
			int startZ = this.pos.getZ() - entranceSizeZ / 3 - CQRConfig.general.supportHillWallSize / 2;

			int endX = this.pos.getX() + entranceSizeX + entranceSizeX / 3 + CQRConfig.general.supportHillWallSize / 2;
			int endZ = this.pos.getZ() + entranceSizeZ + entranceSizeZ / 3 + CQRConfig.general.supportHillWallSize / 2;

			for (int iX = startX; iX <= endX; iX++) {
				for (int iZ = startZ; iZ <= endZ; iZ++) {
					BlockPos pos = new BlockPos(iX, world.getTopSolidOrLiquidBlock(new BlockPos(iX, 0, iZ)).getY(), iZ);
					if (!Block.isEqualTo(world.getBlockState(pos.subtract(new Vec3i(0, 1, 0))).getBlock(), this.dungeon.getCoverBlock())) {
						stateMap.put(pos, this.dungeon.getCoverBlock().getDefaultState());
					}
				}
			}
			List<AbstractBlockInfo> blockInfoList = new ArrayList<>();
			for (Map.Entry<BlockPos, IBlockState> entry : stateMap.entrySet()) {
				blockInfoList.add(new BlockInfo(entry.getKey().subtract(this.pos), entry.getValue(), null));
			}
			this.dungeonGenerator.add(new DungeonPartBlock(world, dungeonGenerator, pos, blockInfoList, new PlacementSettings(), EDungeonMobType.ZOMBIE));
		}
	}

	public int getDunX() {
		return this.dunX;
	}

	public int getDunZ() {
		return this.dunZ;
	}

	public PlacementSettings getPlacementSettings() {
		return this.settings;
	}

	public BlockPos getPos() {
		return this.pos;
	}

}
