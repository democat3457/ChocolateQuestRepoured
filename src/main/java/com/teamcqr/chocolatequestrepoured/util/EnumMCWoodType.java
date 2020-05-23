package com.teamcqr.chocolatequestrepoured.util;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public enum EnumMCWoodType {
    OAK(Blocks.OAK_PLANKS, Blocks.OAK_STAIRS, Blocks.OAK_FENCE, Blocks.OAK_DOOR, Blocks.OAK_SLAB),
    BIRCH(Blocks.BIRCH_PLANKS, Blocks.BIRCH_STAIRS, Blocks.BIRCH_FENCE, Blocks.BIRCH_DOOR, Blocks.BIRCH_SLAB),
    SPRUCE(Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_STAIRS, Blocks.SPRUCE_FENCE, Blocks.SPRUCE_DOOR, Blocks.SPRUCE_SLAB),
    JUNGLE(Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_STAIRS, Blocks.JUNGLE_FENCE, Blocks.JUNGLE_DOOR, Blocks.JUNGLE_SLAB),
    ACACIA(Blocks.ACACIA_PLANKS, Blocks.ACACIA_STAIRS, Blocks.ACACIA_FENCE, Blocks.ACACIA_DOOR, Blocks.ACACIA_SLAB),
    DARK_OAK(Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_STAIRS, Blocks.DARK_OAK_FENCE, Blocks.DARK_OAK_DOOR, Blocks.DARK_OAK_SLAB);

    private final Block plankVariant;
    private final Block stairBlock;
    private final Block fenceBlock;
    private final Block doorBlock;
    private final Block slabBlock;

    EnumMCWoodType(Block block, Block stair, Block fence, Block door, Block slab) {
        this.plankVariant = block;
        this.stairBlock = stair;
        this.fenceBlock = fence;
        this.doorBlock = door;
        this.slabBlock = slab;
    }

    public BlockState getSlabBlockState() {
        return slabBlock.getDefaultState();
    }

    public BlockState getPlankBlockState() {
        return plankVariant.getDefaultState();
    }

    public BlockState getStairBlockState() {
        return stairBlock.getDefaultState();
    }

    public BlockState getFenceBlockState() {
        return fenceBlock.getDefaultState();
    }

    public BlockState getDoorBlockState() {
        return doorBlock.getDefaultState();
    }

    @Nullable
    public static EnumMCWoodType getTypeFromString(String str) {
        for (EnumMCWoodType type : EnumMCWoodType.values()) {
            if (type.toString().toLowerCase().equals(str.toLowerCase())) {
                return type;
            }
        }
        return null;
    }
}
