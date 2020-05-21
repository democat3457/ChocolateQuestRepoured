package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.decoration.objects;

import com.teamcqr.chocolatequestrepoured.util.BlockStateGenArray;
import com.teamcqr.chocolatequestrepoured.util.DungeonGenUtils;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3i;

public class DecoBlockRotating extends  DecoBlockBase {
    protected PropertyDirection directionProperty;
    protected final Direction DEFAULT_SIDE = Direction.NORTH;
    protected Direction initialFacing;

    protected DecoBlockRotating(int x, int y, int z, IBlockState initialState, PropertyDirection directionProperty, Direction initialFacing, BlockStateGenArray.GenerationPhase generationPhase) {
        super(x, y, z, initialState, generationPhase);
        this.directionProperty = directionProperty;
        this.initialFacing = initialFacing;
    }

    protected DecoBlockRotating(Vec3i offset, IBlockState initialState, PropertyDirection directionProperty, Direction initialFacing, BlockStateGenArray.GenerationPhase generationPhase) {
        super(offset, initialState, generationPhase);
        this.directionProperty = directionProperty;
        this.initialFacing = initialFacing;
    }

    @Override
    protected IBlockState getState(Direction side) {
        int rotations = DungeonGenUtils.getCWRotationsBetween(DEFAULT_SIDE, side);
        return blockState.withProperty(directionProperty, DungeonGenUtils.rotateFacingNTimesAboutY(initialFacing, rotations));
    }
}
