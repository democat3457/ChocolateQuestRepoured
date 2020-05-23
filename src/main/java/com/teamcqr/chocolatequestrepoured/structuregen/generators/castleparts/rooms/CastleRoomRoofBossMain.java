package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teamcqr.chocolatequestrepoured.init.ModBlocks;
import com.teamcqr.chocolatequestrepoured.init.ModLoottables;
import com.teamcqr.chocolatequestrepoured.objects.factories.GearedMobFactory;
import com.teamcqr.chocolatequestrepoured.objects.factories.SpawnerFactory;
import com.teamcqr.chocolatequestrepoured.structuregen.dungeons.DungeonCastle;
import com.teamcqr.chocolatequestrepoured.tileentity.TileEntitySpawner;
import com.teamcqr.chocolatequestrepoured.util.BlockStateGenArray;
import com.teamcqr.chocolatequestrepoured.util.DungeonGenUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class CastleRoomRoofBossMain extends CastleRoomBase {
	private Vec3i bossBuildOffset = new Vec3i(0, 0, 0);
	private static final int BOSS_ROOM_STATIC_SIZE = 17;
	private DungeonCastle dungeon;

	public CastleRoomRoofBossMain(BlockPos startOffset, int sideLength, int height, int floor) {
		super(startOffset, sideLength, height, floor);
		this.roomType = EnumRoomType.ROOF_BOSS_MAIN;
		this.pathable = false;
	}

	public void setBossBuildOffset(Vec3i bossBuildOffset) {
		this.bossBuildOffset = bossBuildOffset;
	}

	public int getStaticSize() {
		return BOSS_ROOM_STATIC_SIZE;
	}

	@Override
	protected void generateWalls(BlockStateGenArray genArray, DungeonCastle dungeon) {
	}

	@Override
	public void generateRoom(BlockStateGenArray genArray, DungeonCastle dungeon) {
		BlockPos nwCorner = this.getBossRoomBuildStartPosition();
		BlockPos pos;
		BlockState blockToBuild;
		this.dungeon = dungeon;

		for (int x = 0; x < BOSS_ROOM_STATIC_SIZE; x++) {
			for (int y = 0; y < 8; y++) {
				for (int z = 0; z < BOSS_ROOM_STATIC_SIZE; z++) {
					blockToBuild = this.getBlockToBuild(x, y, z);
					pos = nwCorner.add(x, y, z);

					genArray.addBlockState(pos, blockToBuild, BlockStateGenArray.GenerationPhase.MAIN);
				}
			}
		}
	}

	@Override
	public void decorate(World world, BlockStateGenArray genArray, DungeonCastle dungeon, GearedMobFactory mobFactory)
	{
		// Have to add torches last because they won't place unless the wall next to them is already built
		this.placeTorches(this.getBossRoomBuildStartPosition(), genArray);

		this.placeChests(world, this.getBossRoomBuildStartPosition(), genArray);
	}

	@Override
	public void placeBoss(World world, BlockStateGenArray genArray, DungeonCastle dungeon, ResourceLocation bossResourceLocation, ArrayList<String> bossUuids) {
		BlockPos pos =  this.getBossRoomBuildStartPosition().add(BOSS_ROOM_STATIC_SIZE / 2, 1, BOSS_ROOM_STATIC_SIZE / 2);
		if(bossResourceLocation == null) {
			
			EntityArmorStand indicator = new EntityArmorStand(world);
			indicator.setCustomNameTag("Oops! We haven't added this boss yet! Treat yourself to some free loot!");
			indicator.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			indicator.setEntityInvulnerable(true);
			indicator.setInvisible(true);
			indicator.setAlwaysRenderNameTag(true);
			indicator.setSilent(true);
			indicator.setNoGravity(true);
			CompoundNBT indicatorNbt = indicator.writeToNBT(new CompoundNBT());

			genArray.addEntity(pos, EntityList.getKey(indicator), indicatorNbt);
			
			return;
		}
		
		Entity mobEntity = EntityList.createEntityByIDFromName(bossResourceLocation, world);

		//SpawnerFactory.placeSpawner(new Entity[] { mobEntity }, false, null, world, pos);

		if (mobEntity != null) {
			Block spawnerBlock = ModBlocks.SPAWNER;
			BlockState state = spawnerBlock.getDefaultState();
			TileEntitySpawner spawner = (TileEntitySpawner)spawnerBlock.createTileEntity(world, state);
			if (spawner != null) {
				spawner.inventory.setStackInSlot(0, SpawnerFactory.getSoulBottleItemStackForEntity(mobEntity));
				CompoundNBT spawnerCompound = spawner.writeToNBT(new CompoundNBT());
				genArray.addBlockState(pos, state, spawnerCompound, BlockStateGenArray.GenerationPhase.POST);

				bossUuids.add(mobEntity.getUniqueID().toString());
			}
		}
	}

	private void placeTorches(BlockPos nwCorner, BlockStateGenArray genArray) {
		BlockState torchBase = Blocks.TORCH.getDefaultState();
		genArray.addBlockState(nwCorner.add(10, 3, 2), torchBase.withProperty(BlockTorch.FACING, Direction.SOUTH), BlockStateGenArray.GenerationPhase.POST);
		genArray.addBlockState(nwCorner.add(6, 3, 2), torchBase.withProperty(BlockTorch.FACING, Direction.SOUTH), BlockStateGenArray.GenerationPhase.POST);
		genArray.addBlockState(nwCorner.add(6, 3, 14), torchBase.withProperty(BlockTorch.FACING, Direction.NORTH), BlockStateGenArray.GenerationPhase.POST);
		genArray.addBlockState(nwCorner.add(10, 3, 14), torchBase.withProperty(BlockTorch.FACING, Direction.NORTH), BlockStateGenArray.GenerationPhase.POST);
		genArray.addBlockState(nwCorner.add(2, 3, 6), torchBase.withProperty(BlockTorch.FACING, Direction.EAST), BlockStateGenArray.GenerationPhase.POST);
		genArray.addBlockState(nwCorner.add(2, 3, 10), torchBase.withProperty(BlockTorch.FACING, Direction.EAST), BlockStateGenArray.GenerationPhase.POST);
		genArray.addBlockState(nwCorner.add(14, 3, 6), torchBase.withProperty(BlockTorch.FACING, Direction.WEST), BlockStateGenArray.GenerationPhase.POST);
		genArray.addBlockState(nwCorner.add(14, 3, 10), torchBase.withProperty(BlockTorch.FACING, Direction.WEST), BlockStateGenArray.GenerationPhase.POST);
	}

	private void placeChests(World world, BlockPos nwCorner, BlockStateGenArray genArray) {
		int numChestsTotal = DungeonGenUtils.randomBetweenGaussian(this.random, 4, 8);
		int numTreasureChests = DungeonGenUtils.randomBetween(this.random, 2, 4);
		int treasureChestsPlaced = 0;
		HashMap<BlockPos, Direction> possibleChestLocs = new HashMap<>();
		possibleChestLocs.put(nwCorner.add(1, 5, 7), Direction.WEST);
		possibleChestLocs.put(nwCorner.add(1, 5, 9), Direction.WEST);
		possibleChestLocs.put(nwCorner.add(15, 5, 7), Direction.EAST);
		possibleChestLocs.put(nwCorner.add(15, 5, 9), Direction.EAST);
		possibleChestLocs.put(nwCorner.add(7, 5, 1), Direction.NORTH);
		possibleChestLocs.put(nwCorner.add(9, 5, 1), Direction.NORTH);
		possibleChestLocs.put(nwCorner.add(7, 5, 15), Direction.SOUTH);
		possibleChestLocs.put(nwCorner.add(9, 5, 15), Direction.SOUTH);
		List<Map.Entry<BlockPos, Direction>> locList = new ArrayList<>(possibleChestLocs.entrySet());
		Collections.shuffle(locList, this.random);

		for (int i = 0; i < numChestsTotal; i++) {
			ResourceLocation lootTable;

			if (treasureChestsPlaced < numTreasureChests) {
				lootTable = ModLoottables.CHESTS_TREASURE;
				++treasureChestsPlaced;
			}
			else
			{
				if (DungeonGenUtils.PercentageRandom(50, random))
				{
					lootTable = ModLoottables.CHESTS_MATERIAL;
				}
				else
				{
					lootTable = ModLoottables.CHESTS_EQUIPMENT;
				}
			}
			genArray.addChestWithLootTable(world, locList.get(i).getKey(), locList.get(i).getValue().getOpposite(), lootTable, BlockStateGenArray.GenerationPhase.POST);
		}
	}

	private BlockPos getBossRoomBuildStartPosition() {
		return this.getNonWallStartPos().add(this.bossBuildOffset);
	}

	private BlockState getBlockToBuild(int x, int y, int z) {
		BlockState blockToBuild = Blocks.AIR.getDefaultState();
		if (y == 0 || y == 7) {
			if (this.floorDesignBlock(x, z)) {
				blockToBuild = Blocks.CONCRETE.getDefaultState();
			} else {
				blockToBuild = dungeon.getMainBlockState();
			}
		} else if (x == 0 || z == 0 || x == 16 || z == 16) {
			blockToBuild = this.getOuterEdgeBlock(x, y, z);
		} else if (x == 1 || x == 15 || z == 1 || z == 15) {
			blockToBuild = this.getInnerRing1Block(x, y, z);
		} else if (x == 2 || x == 14 || z == 2 || z == 14) {
			blockToBuild = this.getInnerRing2Block(x, y, z);
		} else if (x == 3 || x == 13 || z == 3 || z == 13) {
			blockToBuild = this.getInnerRing3Block(x, y, z);
		}

		return blockToBuild;
	}

	private boolean floorDesignBlock(int x, int z) {
		final int[][] floorPattern = new int[][] {
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0 },
				{ 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
				{ 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 0, 0 },
				{ 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0 },
				{ 0, 0, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0 },
				{ 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
				{ 0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };

		return this.checkPatternIndex(x, z, floorPattern);
	}

	private boolean checkPatternIndex(int x, int z, int[][] pattern) {
		if (pattern != null && z >= 0 && z <= pattern.length && x >= 0 && x <= pattern[0].length) {
			return pattern[x][z] == 1;
		} else {
			return false;
		}
	}

	private BlockState getOuterEdgeBlock(int x, int y, int z) {
		if (x == 0 || x == 16) {
			if (z == 0 || z == 3 || z == 6 || z == 10 || z == 13 || z == 16) {
				return dungeon.getMainBlockState();
			} else if (z >= 7 && z <= 9) {
				if (y >= 1 && y <= 3) {
					return Blocks.AIR.getDefaultState();
				} else if (y == 4) {
					if (z == 7 || z == 9) {
						Direction doorFrameFacing = (z == 7) ? Direction.NORTH : Direction.SOUTH;
						return dungeon.getStairBlockState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP).withProperty(BlockStairs.FACING, doorFrameFacing);
					} else {
						return Blocks.AIR.getDefaultState();
					}
				}
			} else {
				if (y == 6) {
					return dungeon.getMainBlockState();
				} else if (y == 2 || y == 3 || y == 4) {
					return Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockColored.COLOR, DyeColor.RED);
				} else if (y == 1) {
					Direction windowBotFacing = (x == 0) ? Direction.WEST : Direction.EAST;
					return dungeon.getStairBlockState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.BOTTOM).withProperty(BlockStairs.FACING, windowBotFacing);
				} else if (y == 5) {
					Direction windowTopFacing = (x == 0) ? Direction.EAST : Direction.WEST;
					return dungeon.getStairBlockState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP).withProperty(BlockStairs.FACING, windowTopFacing);
				}
			}
		} else if (z == 0 || z == 16) {
			if (x == 3 || x == 6 || x == 10 || x == 13) {
				return dungeon.getMainBlockState();
			} else if (x >= 7 && x <= 9) {
				if (y >= 1 && y <= 3) {
					return Blocks.AIR.getDefaultState();
				} else if (y == 4) {
					if (x == 7 || x == 9) {
						Direction doorFrameFacing = (x == 7) ? Direction.WEST : Direction.EAST;
						return dungeon.getStairBlockState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP).withProperty(BlockStairs.FACING, doorFrameFacing);
					} else {
						return Blocks.AIR.getDefaultState();
					}
				}
			} else {
				if (y == 6) {
					return dungeon.getMainBlockState();
				} else if (y == 2 || y == 3 || y == 4) {
					return Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockColored.COLOR, DyeColor.RED);
				} else if (y == 1) {
					Direction windowBotFacing = (z == 0) ? Direction.NORTH : Direction.SOUTH;
					return dungeon.getStairBlockState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.BOTTOM).withProperty(BlockStairs.FACING, windowBotFacing);
				} else if (y == 5) {
					Direction windowTopFacing = (z == 0) ? Direction.SOUTH : Direction.NORTH;
					return dungeon.getStairBlockState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP).withProperty(BlockStairs.FACING, windowTopFacing);
				}
			}
		}

		return dungeon.getMainBlockState();
	}

	private BlockState getInnerRing1Block(int x, int y, int z) {
		final BlockState detailBlock = dungeon.getFancyBlockState();

		if (x == 1 || x == 15) {
			if (z == 3 || z == 6 || z == 10 || z == 13) {
				return detailBlock;
			} else if ((z == 1 || z == 2 || z == 14 || z == 15) && y == 1) {
				return Blocks.LAVA.getDefaultState();
			} else if (z >= 7 && z <= 9) {
				if (y == 3 && (z == 7 || z == 9)) {
					return dungeon.getSlabBlockState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP);
				} else if (y == 4) {
					return detailBlock;
				} else if (y == 5 && z == 8) {
					Direction frameTopStairFacing = (x == 1) ? Direction.WEST : Direction.EAST;
					return dungeon.getStairBlockState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.BOTTOM).withProperty(BlockStairs.FACING, frameTopStairFacing);
				}
			}
		} else if (z == 1 || z == 15) {
			if (x == 3 || x == 6 || x == 10 || x == 13) {
				return detailBlock;
			} else if ((x == 2 || x == 14) && y == 1) {
				return Blocks.LAVA.getDefaultState();
			} else if (x >= 7 && x <= 9) {
				if (y == 3 && (x == 7 || x == 9)) {
					return dungeon.getSlabBlockState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP);
				} else if (y == 4) {
					return detailBlock;
				} else if (y == 5 && x == 8) {
					Direction frameTopStairFacing = (z == 1) ? Direction.NORTH : Direction.SOUTH;
					return dungeon.getStairBlockState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.BOTTOM).withProperty(BlockStairs.FACING, frameTopStairFacing);
				}
			}
		}

		return Blocks.AIR.getDefaultState();
	}

	private BlockState getInnerRing2Block(int x, int y, int z) {
		if (x == 2 || x == 14) {
			if ((z == 2 || z == 14) && y == 1) {
				return Blocks.LAVA.getDefaultState();
			} else if (z == 3 || z == 13) {
				if (y == 1 || y == 6) {
					Direction stairFacing = (z == 3) ? Direction.NORTH : Direction.SOUTH;
					BlockStairs.EnumHalf stairHalf = (y == 1) ? BlockStairs.EnumHalf.TOP : BlockStairs.EnumHalf.BOTTOM;
					return dungeon.getStairBlockState().withProperty(BlockStairs.FACING, stairFacing).withProperty(BlockStairs.HALF, stairHalf);
				} else if (y >= 2 && y <= 5) {
					return Blocks.IRON_BARS.getDefaultState();
				}
			}
		} else if (z == 2 || z == 14) {
			// Lava case covered by previous conditionals

			if (x == 3 || x == 13) {
				if (y == 1 || y == 6) {
					Direction stairFacing = (x == 3) ? Direction.WEST : Direction.EAST;
					BlockStairs.EnumHalf stairHalf = (y == 1) ? BlockStairs.EnumHalf.TOP : BlockStairs.EnumHalf.BOTTOM;
					return dungeon.getStairBlockState().withProperty(BlockStairs.FACING, stairFacing).withProperty(BlockStairs.HALF, stairHalf);
				} else if (y >= 2 && y <= 5) {
					return Blocks.IRON_BARS.getDefaultState();
				}
			}
		}

		return Blocks.AIR.getDefaultState();
	}

	private BlockState getInnerRing3Block(int x, int y, int z) {
		if ((x == 3 || x == 13) && z == 3) {
			if (y >= 2 & y <= 5) {
				return Blocks.IRON_BARS.getDefaultState();
			} else if (y == 1 || y == 6) {
				BlockStairs.EnumHalf stairHalf = (y == 1) ? BlockStairs.EnumHalf.TOP : BlockStairs.EnumHalf.BOTTOM;
				Direction stairFacing = (x == 3) ? Direction.WEST : Direction.NORTH;
				return dungeon.getStairBlockState().withProperty(BlockStairs.HALF, stairHalf).withProperty(BlockStairs.FACING, stairFacing);
			}
		} else if ((x == 3 || x == 13) && z == 13) {
			if (y >= 2 & y <= 5) {
				return Blocks.IRON_BARS.getDefaultState();
			} else if (y == 1 || y == 6) {
				BlockStairs.EnumHalf stairHalf = (y == 1) ? BlockStairs.EnumHalf.TOP : BlockStairs.EnumHalf.BOTTOM;
				Direction stairFacing = (x == 3) ? Direction.WEST : Direction.SOUTH;
				return dungeon.getStairBlockState().withProperty(BlockStairs.HALF, stairHalf).withProperty(BlockStairs.FACING, stairFacing);
			}
		}

		return Blocks.AIR.getDefaultState();
	}
}
