package com.teamcqr.chocolatequestrepoured.structuregen.structurefile;

import com.teamcqr.chocolatequestrepoured.CQRMain;
import com.teamcqr.chocolatequestrepoured.init.ModLoottables;
import com.teamcqr.chocolatequestrepoured.objects.blocks.BlockExporterChest;
import com.teamcqr.chocolatequestrepoured.structuregen.EDungeonMobType;
import com.teamcqr.chocolatequestrepoured.structuregen.WorldDungeonGenerator;
import com.teamcqr.chocolatequestrepoured.structureprot.ProtectedRegion;
import com.teamcqr.chocolatequestrepoured.util.BlockPlacingHelper;
import com.teamcqr.chocolatequestrepoured.util.CQRConfig;
import com.teamcqr.chocolatequestrepoured.util.Reference;

import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.storage.loot.LootTableList;

public class BlockInfoLootChest extends AbstractBlockInfo {

	public static final int ID = 4;

	protected ResourceLocation lootTable = LootTableList.EMPTY;
	protected EnumFacing facing = EnumFacing.NORTH;

	public BlockInfoLootChest(BlockPos pos, IBlockState state) {
		super(pos);
		if (state.getBlock() instanceof BlockExporterChest) {
			this.lootTable = ((BlockExporterChest) state.getBlock()).lootTable;
			this.facing = state.getValue(BlockChest.FACING);
		}
	}

	public BlockInfoLootChest(BlockPos pos, ResourceLocation lootTable, EnumFacing facing) {
		super(pos);
		this.lootTable = lootTable;
		this.facing = facing;
	}

	public BlockInfoLootChest(BlockPos pos, NBTTagIntArray nbtTagIntArray) {
		super(pos);
		this.readFromNBT(nbtTagIntArray, null, null);
	}

	@Override
	public void generate(World world, BlockPos dungeonPos, BlockPos dungeonPartPos, PlacementSettings settings, EDungeonMobType dungeonMob, ProtectedRegion protectedRegion) {
		BlockPos transformedPos = dungeonPartPos.add(Template.transformedBlockPos(settings, this.pos));
		IBlockState iblockstate = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, this.facing).withMirror(settings.getMirror()).withRotation(settings.getRotation());
		BlockPlacingHelper.setBlockState2(world, transformedPos, iblockstate, 18, CQRConfig.advanced.instantLightUpdates);
		TileEntity tileEntity = world.getTileEntity(transformedPos);

		if (tileEntity instanceof TileEntityChest) {
			long seed = WorldDungeonGenerator.getSeed(world, transformedPos.getX(), transformedPos.getZ());
			((TileEntityChest) tileEntity).setLootTable(this.lootTable, seed);
		} else {
			CQRMain.logger.warn("Failed to place loot chest at {}", transformedPos);
		}
	}

	@Override
	public int getId() {
		return ID;
	}

	@Override
	public NBTTagIntArray writeToNBT(BlockStatePalette blockStatePalette, NBTTagList compoundTagList) {
		int[] ints = new int[3];
		ints[0] = this.getId();
		ints[1] = this.getIdFromLootTable(this.lootTable);
		ints[2] = this.facing.getHorizontalIndex();
		return new NBTTagIntArray(ints);
	}

	@Override
	public void readFromNBT(NBTTagIntArray nbtTagIntArray, BlockStatePalette blockStatePalette, NBTTagList compoundTagList) {
		int[] ints = nbtTagIntArray.getIntArray();
		this.lootTable = this.getLootTableFromId(ints[1]);
		this.facing = EnumFacing.getHorizontal(ints[2]);
	}

	private int getIdFromLootTable(ResourceLocation lootTable) {
		if (lootTable.equals(ModLoottables.CHESTS_FOOD)) {
			return 0;
		} else if (lootTable.equals(ModLoottables.CHESTS_EQUIPMENT)) {
			return 1;
		} else if (lootTable.equals(ModLoottables.CHESTS_TREASURE)) {
			return 2;
		} else if (lootTable.equals(ModLoottables.CHESTS_MATERIAL)) {
			return 3;
		} else if (lootTable.equals(LootTableList.CHESTS_ABANDONED_MINESHAFT)) {
			return 4;
		} else if (lootTable.equals(LootTableList.CHESTS_DESERT_PYRAMID)) {
			return 5;
		} else if (lootTable.equals(LootTableList.CHESTS_END_CITY_TREASURE)) {
			return 6;
		} else if (lootTable.equals(LootTableList.CHESTS_IGLOO_CHEST)) {
			return 7;
		} else if (lootTable.equals(LootTableList.CHESTS_JUNGLE_TEMPLE)) {
			return 8;
		} else if (lootTable.equals(LootTableList.CHESTS_JUNGLE_TEMPLE_DISPENSER)) {
			return 9;
		} else if (lootTable.equals(LootTableList.CHESTS_NETHER_BRIDGE)) {
			return 10;
		} else if (lootTable.equals(LootTableList.CHESTS_SPAWN_BONUS_CHEST)) {
			return 11;
		} else if (lootTable.equals(LootTableList.CHESTS_STRONGHOLD_CORRIDOR)) {
			return 12;
		} else if (lootTable.equals(LootTableList.CHESTS_STRONGHOLD_CROSSING)) {
			return 13;
		} else if (lootTable.equals(LootTableList.CHESTS_STRONGHOLD_LIBRARY)) {
			return 14;
		} else if (lootTable.equals(LootTableList.CHESTS_VILLAGE_BLACKSMITH)) {
			return 15;
		} else if (lootTable.equals(LootTableList.CHESTS_WOODLAND_MANSION)) {
			return 16;
		} else {
			try {
				return Integer.parseInt(lootTable.toString().substring(18, lootTable.toString().length()));
			} catch (Exception e) {
				return 0;
			}
		}
	}

	private ResourceLocation getLootTableFromId(int id) {
		switch (id) {
		case 0:
			return ModLoottables.CHESTS_FOOD;
		case 1:
			return ModLoottables.CHESTS_EQUIPMENT;
		case 2:
			return ModLoottables.CHESTS_TREASURE;
		case 3:
			return ModLoottables.CHESTS_MATERIAL;
		case 4:
			return LootTableList.CHESTS_ABANDONED_MINESHAFT;
		case 5:
			return LootTableList.CHESTS_DESERT_PYRAMID;
		case 6:
			return LootTableList.CHESTS_END_CITY_TREASURE;
		case 7:
			return LootTableList.CHESTS_IGLOO_CHEST;
		case 8:
			return LootTableList.CHESTS_JUNGLE_TEMPLE;
		case 9:
			return LootTableList.CHESTS_JUNGLE_TEMPLE_DISPENSER;
		case 10:
			return LootTableList.CHESTS_NETHER_BRIDGE;
		case 11:
			return LootTableList.CHESTS_SPAWN_BONUS_CHEST;
		case 12:
			return LootTableList.CHESTS_STRONGHOLD_CORRIDOR;
		case 13:
			return LootTableList.CHESTS_STRONGHOLD_CROSSING;
		case 14:
			return LootTableList.CHESTS_STRONGHOLD_LIBRARY;
		case 15:
			return LootTableList.CHESTS_VILLAGE_BLACKSMITH;
		case 16:
			return LootTableList.CHESTS_WOODLAND_MANSION;
		default:
			break;
		}
		if (id >= 17 && id <= 30) {
			return new ResourceLocation(Reference.MODID, "custom_" + (id - 16));
		}
		return ModLoottables.CHESTS_FOOD;
	}

}
