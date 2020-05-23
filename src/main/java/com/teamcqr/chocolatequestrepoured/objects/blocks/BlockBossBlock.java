package com.teamcqr.chocolatequestrepoured.objects.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockBossBlock extends Block {

	public BlockBossBlock() {
		super(Properties.create(Material.ROCK)
				.hardnessAndResistance(50.0F, 1200.0F)
				.sound(SoundType.STONE)
				.noDrops()
			);
		
		/*this.setSoundType(SoundType.STONE);
		this.setBlockUnbreakable();
		this.setResistance(Float.MAX_VALUE);*/
	}

	/*@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return Items.AIR;
	}

	@Override
	public EnumBlockRenderType getRenderType(BlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}*/

}
