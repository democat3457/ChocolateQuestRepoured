package com.teamcqr.chocolatequestrepoured.structuregen.dungeons;

import java.io.File;
import java.util.Properties;

import com.teamcqr.chocolatequestrepoured.structuregen.generators.AbstractDungeonGenerator;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.GeneratorSurface;
import com.teamcqr.chocolatequestrepoured.util.PropertyFileHelper;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Copyright (c) 29.04.2019
 * Developed by DerToaster98
 * GitHub: https://github.com/DerToaster98
 */
public class DungeonSurface extends DungeonBase {

	protected File structureFolderPath;

	public DungeonSurface(String name, Properties prop) {
		super(name, prop);

		this.structureFolderPath = PropertyFileHelper.getFileProperty(prop, "structureFolder", "test");
	}

	@Override
	public AbstractDungeonGenerator createDungeonGenerator(World world, int x, int y, int z) {
		return new GeneratorSurface(world, new BlockPos(x, y, z), this);
	}

	public File getStructureFolderPath() {
		return this.structureFolderPath;
	}

}
