package com.teamcqr.chocolatequestrepoured.util;

import javax.annotation.Nullable;

import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;

/**
 *  Copyright (c) 20.04.2020
 *  Developed by KalgogSmash
 *  GitHub: https://github.com/KalgogSmash
 *
 *  This is an enum to map a handful of common block types to their associated blockstate in Forge 1.12.
 *  For example, chiseled stone bricks are not a Block type in 1.12, rather they are a special variant
 *  state of STONE_BRICKS that cannot be looked up by a namespace ID alone. Trying to parse through
 *  all the blocks and their property values is needlessly tedious, so this is just a shortcut so players
 *  can use these block varieties in config files as if they had their own namespace ID.
 *
 *  Note: I named the enum values after the block namespace IDs on the minecraft Wiki so that if we move to
 *  a later version of forge, the config values should just map to registry values instead of coming here.
 */
public enum EnumForgeBlockVariant {
    MOSSY_STONE_BRICKS(Blocks.MOSSY_STONE_BRICKS.getDefaultState()),
    CRACKED_STONE_BRICKS(Blocks.CRACKED_STONE_BRICKS.getDefaultState()),
    CHISELED_STONE_BRICKS(Blocks.CHISELED_STONE_BRICKS.getDefaultState()),
    CHISELED_SANDSTONE(Blocks.CHISELED_SANDSTONE.getDefaultState()),
    SMOOTH_SANDSTONE(Blocks.SMOOTH_SANDSTONE.getDefaultState()),
    SMOOTH_RED_SANDSTONE(Blocks.SMOOTH_RED_SANDSTONE.getDefaultState()),
    CHISELED_RED_SANDSTONE(Blocks.CHISELED_RED_SANDSTONE.getDefaultState()),
    PRISMARINE_BRICKS(Blocks.PRISMARINE_BRICKS.getDefaultState()),
    DARK_PRISMARINE(Blocks.DARK_PRISMARINE.getDefaultState()),
    SANDSTONE_SLAB(Blocks.SANDSTONE_SLAB.getDefaultState()),
    NETHER_BRICK_SLAB(Blocks.NETHER_BRICK_SLAB.getDefaultState()),
    QUARTZ_SLAB(Blocks.QUARTZ_SLAB.getDefaultState());


    private final BlockState blockState;
    private final String nameSpaceID;

    EnumForgeBlockVariant(BlockState blockState) {
        this.blockState = blockState;
        this.nameSpaceID = "minecraft:" + this.name().toLowerCase();
    }

    @Nullable
    static BlockState getVariantStateFromName(String name) {
        for (EnumForgeBlockVariant blockVariant : values()) {
            if (name.toLowerCase().equals(blockVariant.nameSpaceID)) {
                return blockVariant.blockState;
            }
        }
        return null;
    }
}
