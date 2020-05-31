package com.teamcqr.chocolatequestrepoured.structuregen.structurefile;

import com.teamcqr.chocolatequestrepoured.init.ModLoottables;
import com.teamcqr.chocolatequestrepoured.util.Reference;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraftforge.common.util.Constants;

/**
 * Copyright (c) 29.04.2019
 * Developed by DerToaster98
 * GitHub: https://github.com/DerToaster98
 */
public class LootChestInfo {

	private BlockPos position;
	private Direction facing;
	private ResourceLocation lootTable;

	public LootChestInfo(BlockPos position, Direction facing, ResourceLocation lootTable) {
		this.position = position;
		this.facing = facing;
		this.lootTable = lootTable;
	}

	public LootChestInfo(CompoundNBT compound) {
		this.position = NBTUtil.readBlockPos(compound.getCompound("position"));
		this.facing = Direction.getHorizontal(compound.getInt("facing"));
		if (compound.contains("loottable", Constants.NBT.TAG_STRING)) {
			this.lootTable = new ResourceLocation(compound.getString("loottable"));
		} else {
			this.lootTable = this.getOldLootTable(compound);
		}
	}

	public CompoundNBT getAsNBTTag() {
		CompoundNBT compound = new CompoundNBT();

		compound.put("position", NBTUtil.writeBlockPos(this.position));
		compound.putInt("facing", this.facing.getHorizontalIndex());
		compound.putString("loottable", this.lootTable.toString());

		return compound;
	}

	public BlockPos getPosition() {
		return this.position;
	}

	public Direction getFacing() {
		return this.facing;
	}

	public ResourceLocation getLootTable() {
		return this.lootTable;
	}

	private ResourceLocation getOldLootTable(CompoundNBT compound) {
		switch (compound.getInt("loottable")) {
		case 0:
			return ModLoottables.CHESTS_TREASURE;
		case 1:
			return ModLoottables.CHESTS_EQUIPMENT;
		case 2:
			return ModLoottables.CHESTS_FOOD;
		case 3:
			return ModLoottables.CHESTS_MATERIAL;
		case 4:
			return LootTables.CHESTS_ABANDONED_MINESHAFT;
		case 5:
			return LootTables.CHESTS_DESERT_PYRAMID;
		case 6:
			return LootTables.CHESTS_END_CITY_TREASURE;
		case 7:
			return LootTables.CHESTS_IGLOO_CHEST;
		case 8:
			return LootTables.CHESTS_JUNGLE_TEMPLE;
		case 9:
			return LootTables.CHESTS_JUNGLE_TEMPLE_DISPENSER;
		case 10:
			return LootTables.CHESTS_NETHER_BRIDGE;
		case 11:
			return LootTables.CHESTS_SPAWN_BONUS_CHEST;
		case 12:
			return LootTables.CHESTS_STRONGHOLD_CORRIDOR;
		case 13:
			return LootTables.CHESTS_STRONGHOLD_CROSSING;
		case 14:
			return LootTables.CHESTS_STRONGHOLD_LIBRARY;
		case 15:
			return LootTables.CHESTS_VILLAGE_BLACKSMITH;
		case 16:
			return LootTables.CHESTS_WOODLAND_MANSION;
		default:
			return new ResourceLocation(Reference.MODID, "custom_" + (compound.getInt("loottable") - 16));
		}
	}

}
