package com.teamcqr.chocolatequestrepoured.util.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

class DataEntryDungeon {

	private String dungeonName = "missingNo";
	private BlockPos pos = new BlockPos(0, 0, 0);

	public DataEntryDungeon(String name, BlockPos pos) {
		this.dungeonName = name;
		this.pos = pos;
	}

	public CompoundNBT getNBT() {
		CompoundNBT compound = new CompoundNBT();
		compound.putString("name", this.dungeonName);
		compound.put("position", NBTUtil.writeBlockPos(this.pos));

		return compound;
	}

}
