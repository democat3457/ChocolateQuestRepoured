package com.teamcqr.chocolatequestrepoured.structuregen.generation;

import com.teamcqr.chocolatequestrepoured.structureprot.ProtectedRegion;

import net.minecraft.block.state.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;

public class LightPart implements IStructure {

	private BlockPos startPos;
	private BlockPos endPos;

	public LightPart(CompoundNBT compound) {
		this.readFromNBT(compound);
	}

	public LightPart(BlockPos startPos, BlockPos endPos) {
		this.startPos = startPos;
		this.endPos = endPos;
	}

	@Override
	public void generate(World world, ProtectedRegion protectedRegion) {
		if (world.getWorldType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
			for (BlockPos.MutableBlockPos mutablePos : BlockPos.getAllInBoxMutable(this.startPos, this.endPos)) {
				world.checkLight(mutablePos);
			}
			return;
		}

		for (BlockPos.MutableBlockPos mutablePos : BlockPos.getAllInBoxMutable(this.startPos, this.endPos)) {
			world.checkLight(mutablePos);
			Chunk chunk = world.getChunkFromBlockCoords(mutablePos);
			BlockState state = chunk.getBlockState(mutablePos);
			world.markAndNotifyBlock(mutablePos, chunk, state, state, 18);
		}
	}

	@Override
	public boolean canGenerate(World world) {
		return true;
	}

	@Override
	public CompoundNBT writeToNBT() {
		CompoundNBT compound = new CompoundNBT();

		compound.setString("id", "lightPart");
		compound.setTag("startPos", NBTUtil.createPosTag(this.startPos));
		compound.setTag("endPos", NBTUtil.createPosTag(this.endPos));

		return compound;
	}

	@Override
	public void readFromNBT(CompoundNBT compound) {
		this.startPos = NBTUtil.getPosFromTag(compound.getCompoundTag("startPos"));
		this.endPos = NBTUtil.getPosFromTag(compound.getCompoundTag("endPos"));
	}

	@Override
	public BlockPos getPos() {
		return this.startPos;
	}

	@Override
	public BlockPos getSize() {
		return this.endPos.subtract(this.startPos);
	}

}
