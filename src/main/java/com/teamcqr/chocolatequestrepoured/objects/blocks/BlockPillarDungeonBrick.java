package com.teamcqr.chocolatequestrepoured.objects.blocks;

import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockPillarDungeonBrick extends RotatedPillarBlock {

	public BlockPillarDungeonBrick() {
		/*super(Material.ROCK);

		this.setSoundType(SoundType.STONE);
		this.setBlockUnbreakable();
		this.setHardness(Float.MAX_VALUE);*/
		super(Properties.create(Material.ROCK)
				.hardnessAndResistance(50.0F, 1200.0F).sound(SoundType.STONE)
				.noDrops()
				.sound(SoundType.METAL)
			);
	}

}