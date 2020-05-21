package com.teamcqr.chocolatequestrepoured.structuregen.generation;

import com.teamcqr.chocolatequestrepoured.structureprot.ProtectedRegion;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IStructure {

	public void generate(World world, ProtectedRegion protectedRegion);

	public boolean canGenerate(World world);

	public CompoundNBT writeToNBT();

	public void readFromNBT(CompoundNBT compound);

	public BlockPos getPos();

	public BlockPos getSize();

}
