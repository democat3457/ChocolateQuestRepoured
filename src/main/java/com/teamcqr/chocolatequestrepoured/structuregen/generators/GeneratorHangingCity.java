package com.teamcqr.chocolatequestrepoured.structuregen.generators;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teamcqr.chocolatequestrepoured.structuregen.EDungeonMobType;
import com.teamcqr.chocolatequestrepoured.structuregen.PlateauBuilder;
import com.teamcqr.chocolatequestrepoured.structuregen.WorldDungeonGenerator;
import com.teamcqr.chocolatequestrepoured.structuregen.dungeons.DungeonFloatingNetherCity;
import com.teamcqr.chocolatequestrepoured.structuregen.generation.DungeonPartBlock;
import com.teamcqr.chocolatequestrepoured.structuregen.generation.DungeonPartEntity;
import com.teamcqr.chocolatequestrepoured.structuregen.structurefile.AbstractBlockInfo;
import com.teamcqr.chocolatequestrepoured.structuregen.structurefile.BlockInfo;
import com.teamcqr.chocolatequestrepoured.structuregen.structurefile.CQStructure;
import com.teamcqr.chocolatequestrepoured.util.DungeonGenUtils;
import com.teamcqr.chocolatequestrepoured.util.VectorUtil;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;

public class GeneratorHangingCity extends AbstractDungeonGenerator<DungeonFloatingNetherCity> {

	// DONE: Air bubble around the whole thing

	private int islandCount = 1;
	private int islandDistance = 1;
	private Map<BlockPos, CQStructure> structureMap = new HashMap<>();

	// This needs to calculate async (island blocks, chain blocks, air blocks)

	public GeneratorHangingCity(World world, BlockPos pos, DungeonFloatingNetherCity dungeon) {
		super(world, pos.up(8), dungeon);
	}

	@Override
	public void preProcess() {
		this.islandCount = DungeonGenUtils.getIntBetweenBorders(this.dungeon.getMinBuildings(), this.dungeon.getMaxBuildings(), this.random);
		this.islandDistance = DungeonGenUtils.getIntBetweenBorders(this.dungeon.getMinIslandDistance(), this.dungeon.getMaxIslandDistance(), this.random);

		// Calculates the positions and creates the island objects
		// positions are the !!CENTERS!! of the platforms, the structures positions are calculated by the platforms themselves
		// Radius = sqrt(((Longer side of building) / 2)^2 *2) +5
		// Chain start pos: diagonal go (radius / 3) * 2 -1 blocks, here start building up the chains...
		// DONE: Carve out cave -> Need to specify the height in the dungeon
		for (int i = 0; i < this.islandCount; i++) {
			BlockPos nextIslandPos = this.getNextIslandPos(this.pos, i);
			File file = this.dungeon.pickStructure();
			CQStructure structure = this.loadStructureFromFile(file);

			this.structureMap.put(nextIslandPos, structure);
			if (this.dungeon.digAirCave()) {
				int radius = structure != null ? 2 * Math.max(structure.getSize().getX(), structure.getSize().getZ()) : 16;
				BlockPos startPos = nextIslandPos.add(-radius, -this.dungeon.getYFactorHeight(), -radius);
				BlockPos endPos = nextIslandPos.add(radius, this.dungeon.getYFactorHeight(), radius);
				this.dungeonGenerator.add(PlateauBuilder.makeRandomBlob2(Blocks.AIR, startPos, endPos, 8, WorldDungeonGenerator.getSeed(world, this.pos.getX() >> 4, this.pos.getZ() >> 4), world, dungeonGenerator));
			}
		}

		CQStructure structure = this.loadStructureFromFile(this.dungeon.pickCentralStructure());
		this.structureMap.put(this.pos, structure);
		if (this.dungeon.digAirCave()) {
			int radius = structure != null ? 2 * Math.max(structure.getSize().getX(), structure.getSize().getZ()) : 16;
			BlockPos startPos = this.pos.add(-radius, -this.dungeon.getYFactorHeight(), -radius);
			BlockPos endPos = this.pos.add(radius, this.dungeon.getYFactorHeight(), radius);
			this.dungeonGenerator.add(PlateauBuilder.makeRandomBlob2(Blocks.AIR, startPos, endPos, 8, WorldDungeonGenerator.getSeed(world, this.pos.getX() >> 4, this.pos.getZ() >> 4), world, dungeonGenerator));
		}
	}

	@Override
	public void buildStructure() {
		EDungeonMobType mobType = this.dungeon.getDungeonMob();
		if (mobType == EDungeonMobType.DEFAULT) {
			mobType = EDungeonMobType.getMobTypeDependingOnDistance(this.world, this.pos.getX(), this.pos.getZ());
		}

		// Builds the platforms
		// Builds the chains
		for (Map.Entry<BlockPos, CQStructure> entry : this.structureMap.entrySet()) {
			BlockPos bp = entry.getKey();
			CQStructure structure = entry.getValue();

			this.buildBuilding(bp, structure, mobType);
		}
	}

	@Override
	public void postProcess() {
		// Not needed
	}

	// calculates a fitting position for the next island
	private BlockPos getNextIslandPos(BlockPos centerPos, int islandIndex) {
		BlockPos retPos = new BlockPos(centerPos);

		Vec3i vector = new Vec3i(0, 0, (this.islandDistance * 3D) * ((islandIndex) / 10 + 1));

		int degreeMultiplier = islandIndex;
		// int degreeMultiplier = (Math.floorDiv(islandIndex, 10) *10);
		if (this.islandCount > 10) {
			degreeMultiplier -= (((islandIndex) / 10) * 10);
		}
		double angle = this.islandCount >= 10 ? 36D : 360D / this.islandCount;
		retPos = retPos.add(VectorUtil.rotateVectorAroundY(vector, degreeMultiplier * angle));
		retPos = retPos.add(0, this.dungeon.getRandomHeightVariation(), 0);
		return retPos;
	}

	// Constructs an Island in this shape:
	/*
	 * Dec Rad
	 * # # # # # # # # # # # # # # # # # # # # 0 10
	 * # # # # # # # # # # # # # # # # # # 1 9
	 * # # # # # # # # # # # # # # 2 7
	 * # # # # # # # # 3 4
	 * 
	 */
	private void buildBuilding(BlockPos centeredPos, CQStructure structure, EDungeonMobType mobType) {
		int longestSide = structure != null ? Math.max(structure.getSize().getX(), structure.getSize().getZ()) : 16;
		int radius = (int) (0.7071D * (double) longestSide) + 5;

		this.buildPlatform(centeredPos, radius, mobType);
		if (structure != null) {
			PlacementSettings settings = new PlacementSettings();
			BlockPos p = DungeonGenUtils.getCentralizedPosForStructure(centeredPos.up(), structure, settings);
			this.dungeonGenerator.add(new DungeonPartBlock(world, dungeonGenerator, p, structure.getBlockInfoList(), settings, mobType));
			this.dungeonGenerator.add(new DungeonPartBlock(world, dungeonGenerator, p, structure.getSpecialBlockInfoList(), settings, mobType));
			this.dungeonGenerator.add(new DungeonPartEntity(world, dungeonGenerator, p, structure.getEntityInfoList(), settings, mobType));
		}
	}

	private void buildPlatform(BlockPos center, int radius, EDungeonMobType mobType) {
		Map<BlockPos, IBlockState> stateMap = new HashMap<>();
		int decrementor = 0;
		int rad = (int) (1.5D * radius);
		while (decrementor < (rad / 2)) {
			rad -= decrementor;

			for (int iX = -rad; iX <= rad; iX++) {
				for (int iZ = -rad; iZ <= rad; iZ++) {
					if (DungeonGenUtils.isInsideCircle(iX, iZ, rad, center)) {
						stateMap.put((center.add(iX, -decrementor, iZ)), dungeon.getIslandBlock().getDefaultState());
					}
				}
			}

			decrementor++;
		}

		if (this.dungeon.doBuildChains()) {
			this.buildChain(center.add(radius * 0.9, -2, radius * 0.9), 0, stateMap);
			this.buildChain(center.add(-radius * 0.9, -2, -radius * 0.9), 0, stateMap);
			this.buildChain(center.add(-radius * 0.9, -2, radius * 0.9), 1, stateMap);
			this.buildChain(center.add(radius * 0.9, -2, -radius * 0.9), 1, stateMap);
		}

		List<AbstractBlockInfo> blockInfoList = new ArrayList<>();
		for (Map.Entry<BlockPos, IBlockState> entry : stateMap.entrySet()) {
			blockInfoList.add(new BlockInfo(entry.getKey().subtract(center), entry.getValue(), null));
		}
		this.dungeonGenerator.add(new DungeonPartBlock(world, dungeonGenerator, center, blockInfoList, new PlacementSettings(), mobType));
	}

	private void buildChain(BlockPos pos, int iOffset, Map<BlockPos, IBlockState> stateMap) {
		/*
		 * Chain from side:
		 * #
		 * # # #
		 * # # #
		 * # #
		 * # #
		 * # #
		 * # # #
		 * # # #
		 * #
		 */
		int deltaYPerChainSegment = 5;

		int maxY = DungeonGenUtils.getHighestYAt(world.getChunkFromBlockCoords(pos), pos.getX(), pos.getZ(), true);
		maxY = maxY >= 255 ? 255 : maxY;
		int chainCount = (maxY - pos.getY()) / deltaYPerChainSegment;
		for (int i = 0; i < chainCount; i++) {
			// Check the direction of the chain
			int yOffset = i * deltaYPerChainSegment;
			BlockPos startPos = pos.add(0, yOffset, 0);
			if ((i + iOffset) % 2 > 0) {
				this.buildChainSegment(startPos, startPos.north(), startPos.south(), startPos.north(2).up(), startPos.south(2).up(), stateMap);
			} else {
				this.buildChainSegment(startPos, startPos.east(), startPos.west(), startPos.east(2).up(), startPos.west(2).up(), stateMap);
			}
		}
	}

	private void buildChainSegment(BlockPos lowerCenter, BlockPos lowerLeft, BlockPos lowerRight, BlockPos lowerBoundL, BlockPos lowerBoundR, Map<BlockPos, IBlockState> stateMap) {
		stateMap.put(lowerCenter, this.dungeon.getChainBlock().getDefaultState());
		stateMap.put(lowerCenter.add(0, 6, 0), this.dungeon.getChainBlock().getDefaultState());

		stateMap.put(lowerLeft, this.dungeon.getChainBlock().getDefaultState());
		stateMap.put(lowerLeft.add(0, 6, 0), this.dungeon.getChainBlock().getDefaultState());

		stateMap.put(lowerRight, this.dungeon.getChainBlock().getDefaultState());
		stateMap.put(lowerRight.add(0, 6, 0), this.dungeon.getChainBlock().getDefaultState());

		for (int i = 0; i < 5; i++) {
			stateMap.put(lowerBoundL.add(0, i, 0), this.dungeon.getChainBlock().getDefaultState());
			stateMap.put(lowerBoundR.add(0, i, 0), this.dungeon.getChainBlock().getDefaultState());
		}
	}

}
