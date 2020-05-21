package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.decoration.objects;

import com.teamcqr.chocolatequestrepoured.util.BlockStateGenArray;

import net.minecraft.block.state.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3i;

public class DecoBlockBase {
    public Vec3i offset;
    public BlockState blockState;
    public BlockStateGenArray.GenerationPhase genPhase;

    protected DecoBlockBase(int x, int y, int z, BlockState block, BlockStateGenArray.GenerationPhase generationPhase) {
        this.offset = new Vec3i(x, y, z);
        this.blockState = block;
        this.genPhase = generationPhase;
    }

    protected DecoBlockBase(Vec3i offset, BlockState block, BlockStateGenArray.GenerationPhase generationPhase) {
        this.offset = offset;
        this.blockState = block;
        this.genPhase = generationPhase;
    }

    protected BlockState getState(Direction side) {
        return blockState;
    }

    public BlockStateGenArray.GenerationPhase getGenPhase() {
        return genPhase;
    }
}
