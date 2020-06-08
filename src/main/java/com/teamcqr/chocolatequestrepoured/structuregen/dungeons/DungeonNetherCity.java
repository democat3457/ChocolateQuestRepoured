package com.teamcqr.chocolatequestrepoured.structuregen.dungeons;

import java.io.File;
import java.util.Properties;

import com.teamcqr.chocolatequestrepoured.structuregen.generators.AbstractDungeonGenerator;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.GeneratorGridCity;
import com.teamcqr.chocolatequestrepoured.util.DungeonGenUtils;
import com.teamcqr.chocolatequestrepoured.util.PropertyFileHelper;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Copyright (c) 29.04.2019
 * Developed by DerToaster98
 * GitHub: https://github.com/DerToaster98
 */
public class DungeonNetherCity extends DungeonBase {

	private int minRowsX = 5;
	private int maxRowsX = 7;
	private int minRowsZ = 5;
	private int maxRowsZ = 7;
	private int heightY = 31;
	private int posY = 31;
	private double bridgeSizeMultiplier = 1.2D;
	// private boolean singleAirPocketsForHouses = false;
	private boolean specialUseForCentralBuilding = false;
	private boolean makeSpaceForBuildings = true;
	private Block bridgeBlock = Blocks.NETHER_BRICK;
	private Block floorBlock = Blocks.LAVA;
	private Block airBlockForPocket = Blocks.AIR;

	protected File buildingFolder;
	protected File centralBuildingsFolder;

	public DungeonNetherCity(String name, Properties prop) {
		super(name, prop);

		this.minRowsX = PropertyFileHelper.getIntProperty(prop, "minRowsX", 5);
		this.maxRowsX = PropertyFileHelper.getIntProperty(prop, "maxRowsX", 7);
		this.minRowsX = PropertyFileHelper.getIntProperty(prop, "minRowsZ", 5);
		this.maxRowsZ = PropertyFileHelper.getIntProperty(prop, "maxRowsZ", 7);
		this.posY = PropertyFileHelper.getIntProperty(prop, "posY", 31);
		this.heightY = PropertyFileHelper.getIntProperty(prop, "height", 40);

		// singleAirPocketsForHouses = PropertyFileHelper.getBooleanProperty(prop, "singleAirPocketsForHouses", false);
		this.makeSpaceForBuildings = PropertyFileHelper.getBooleanProperty(prop, "createAirPocket", true);
		this.specialUseForCentralBuilding = PropertyFileHelper.getBooleanProperty(prop, "centralBuildingIsSpecial", true);

		this.bridgeSizeMultiplier = PropertyFileHelper.getDoubleProperty(prop, "bridgelengthmultiplier", 1.2D);

		this.bridgeBlock = PropertyFileHelper.getBlockProperty(prop, "streetblock", Blocks.NETHER_BRICK);
		this.floorBlock = PropertyFileHelper.getBlockProperty(prop, "floorblock", Blocks.LAVA);
		this.airBlockForPocket = PropertyFileHelper.getBlockProperty(prop, "airPocketBlock", Blocks.AIR);

		this.buildingFolder = PropertyFileHelper.getFileProperty(prop, "structureFolder", "nether_city_buildings");
		this.centralBuildingsFolder = PropertyFileHelper.getFileProperty(prop, "centralStructureFolder", "nether_city_buildings");
	}

	@Override
	public void generate(World world, int x, int z) {
		this.generate(world, x, this.posY, z);
	}

	@Override
	public AbstractDungeonGenerator createDungeonGenerator(World world, int x, int y, int z) {
		return new GeneratorGridCity(world, new BlockPos(x, y, z), this);
	}

	public int getCaveHeight() {
		return this.heightY;
	}

	public Block getBridgeBlock() {
		return this.bridgeBlock;
	}

	public Block getFloorBlock() {
		return this.floorBlock;
	}

	public Block getAirPocketBlock() {
		return this.airBlockForPocket;
	}

	public int getXRows() {
		return DungeonGenUtils.getIntBetweenBorders(this.minRowsX, this.maxRowsX);
	}

	public int getZRows() {
		return DungeonGenUtils.getIntBetweenBorders(this.minRowsZ, this.maxRowsZ);
	}

	/*
	 * public boolean useSingleAirPocketsForHouses() {
	 * return this.singleAirPocketsForHouses;
	 * }
	 */

	public boolean centralBuildingIsSpecial() {
		return this.specialUseForCentralBuilding;
	}

	public boolean makeSpaceForBuildings() {
		return this.makeSpaceForBuildings;
	}

	public File getBuildingFolder() {
		return this.buildingFolder;
	}

	public File getRandomBuilding() {
		return this.getStructureFileFromDirectory(this.getBuildingFolder());
	}

	public File getCentralBuildingFolder() {
		return this.centralBuildingsFolder;
	}

	public File getRandomCentralBuilding() {
		return this.getStructureFileFromDirectory(this.getCentralBuildingFolder());
	}

	public double getBridgeSizeMultiplier() {
		return bridgeSizeMultiplier;
	}

}
