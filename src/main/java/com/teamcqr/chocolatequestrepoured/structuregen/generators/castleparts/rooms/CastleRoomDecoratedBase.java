package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms;

import java.util.ArrayList;
import java.util.HashMap;

import com.teamcqr.chocolatequestrepoured.init.ModBlocks;
import com.teamcqr.chocolatequestrepoured.objects.factories.GearedMobFactory;
import com.teamcqr.chocolatequestrepoured.objects.factories.SpawnerFactory;
import com.teamcqr.chocolatequestrepoured.structuregen.dungeons.DungeonCastle;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.decoration.DecorationSelector;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.decoration.IRoomDecor;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.decoration.RoomDecorTypes;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.decoration.objects.RoomDecorChest;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.decoration.objects.RoomDecorNone;
import com.teamcqr.chocolatequestrepoured.tileentity.TileEntitySpawner;
import com.teamcqr.chocolatequestrepoured.util.BlockStateGenArray;
import com.teamcqr.chocolatequestrepoured.util.DungeonGenUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class CastleRoomDecoratedBase extends CastleRoomBase {
    protected static final int MAX_DECO_ATTEMPTS = 3;
    protected static final int LIGHT_LEVEL = 4;
    protected DecorationSelector decoSelector;
    protected HashMap<BlockPos, Direction> possibleChestLocs;

    CastleRoomDecoratedBase(BlockPos startOffset, int sideLength, int height, int floor) {
        super(startOffset, sideLength, height, floor);
        this.decoSelector = new DecorationSelector(this.random);
        this.possibleChestLocs = new HashMap<>();
    }

    @Override
    public void decorate(World world, BlockStateGenArray genArray, DungeonCastle dungeon, GearedMobFactory mobFactory) {
        this.setupDecoration(genArray);

        if (this.shouldBuildEdgeDecoration()) {
            this.addEdgeDecoration(world, genArray, dungeon);
        }
        if (this.shouldBuildWallDecoration()) {
            this.addWallDecoration(world, genArray, dungeon);
        }
        if (this.shouldBuildMidDecoration()) {
            this.addMidDecoration(world, genArray, dungeon);
        }
        if (this.shouldAddSpawners()) {
            this.addSpawners(world, genArray, dungeon, mobFactory);
        }
        if (this.shouldAddChests()) {
            this.addChests(world, genArray, dungeon);
        }

        this.fillEmptySpaceWithAir(genArray);
    }

    abstract boolean shouldBuildEdgeDecoration();
    abstract boolean shouldBuildWallDecoration();
    abstract boolean shouldBuildMidDecoration();
    abstract boolean shouldAddSpawners();
    abstract boolean shouldAddChests();

    protected void addEdgeDecoration(World world, BlockStateGenArray genArray, DungeonCastle dungeon) {
        if (this.decoSelector.edgeDecorRegistered()) {
            for (Direction side : Direction.HORIZONTALS) {
                if (this.hasWallOnSide(side) || this.adjacentRoomHasWall(side)) {
                    ArrayList<BlockPos> edge = this.getDecorationEdge(side);
                    for (BlockPos pos : edge) {
                        if (this.usedDecoPositions.contains(pos)) {
                            // This position is already decorated, so keep going
                            continue;
                        }

                        int attempts = 0;

                        while (attempts < MAX_DECO_ATTEMPTS) {
                            IRoomDecor decor = this.decoSelector.randomEdgeDecor();
                            if (decor.wouldFit(pos, side, this.possibleDecoPositions, this.usedDecoPositions, this)) {
                                decor.build(world, genArray, this, dungeon, pos, side, this.usedDecoPositions);

                                // If we added air here then this is a candidate spot for a chest
                                if (decor instanceof RoomDecorNone) {
                                    this.usedDecoPositions.add(pos);
                                    this.possibleChestLocs.put(pos, side);
                                }
                                break;
                            }
                            ++attempts;
                        }
                        if (attempts >= MAX_DECO_ATTEMPTS) {
                            genArray.addBlockState(pos, Blocks.AIR.getDefaultState(), BlockStateGenArray.GenerationPhase.MAIN);
                            this.usedDecoPositions.add(pos);
                            this.possibleChestLocs.put(pos, side);
                        }
                    }
                }
            }
        }
    }

    protected void addMidDecoration(World world, BlockStateGenArray genArray, DungeonCastle dungeon) {
        if (this.decoSelector.midDecorRegistered()) {
            ArrayList<BlockPos> area = this.getDecorationMiddle();
            for (BlockPos pos : area) {
                if (this.usedDecoPositions.contains(pos)) {
                    // This position is already decorated, so keep going
                    continue;
                }

                int attempts = 0;

                while (attempts < MAX_DECO_ATTEMPTS) {
                    IRoomDecor decor = this.decoSelector.randomMidDecor();
                    Direction side = Direction.NORTH; //Direction.HORIZONTALS[random.nextInt(Direction.HORIZONTALS.length)];
                    if (decor.wouldFit(pos, side, this.possibleDecoPositions, this.usedDecoPositions, this)) {
                        decor.build(world, genArray, this, dungeon, pos, side, this.usedDecoPositions);

                        break;
                    }
                    ++attempts;
                }
                if (attempts >= MAX_DECO_ATTEMPTS) {
                    genArray.addBlockState(pos, Blocks.AIR.getDefaultState(), BlockStateGenArray.GenerationPhase.MAIN);
                    this.usedDecoPositions.add(pos);
                }
            }
        }
    }

    protected void addSpawners(World world, BlockStateGenArray genArray, DungeonCastle dungeon, GearedMobFactory mobFactory) {
        ArrayList<BlockPos> spawnPositions = this.getDecorationLayer(0);
        spawnPositions.removeAll(this.usedDecoPositions);

        int spawnerCount = dungeon.randomizeRoomSpawnerCount();

        for (int i = 0; (i < spawnerCount && !spawnPositions.isEmpty()); i++) {
            BlockPos pos = spawnPositions.get(this.random.nextInt(spawnPositions.size()));

            Entity mobEntity = mobFactory.getGearedEntityByFloor(this.floor, world);

            Block spawnerBlock = ModBlocks.SPAWNER;
            BlockState state = spawnerBlock.getDefaultState();
            TileEntitySpawner spawner = (TileEntitySpawner)spawnerBlock.createTileEntity(world, state);

            if (spawner != null)
            {
                spawner.inventory.setStackInSlot(0, SpawnerFactory.getSoulBottleItemStackForEntity(mobEntity));

                CompoundNBT spawnerCompound = spawner.write(new CompoundNBT());
                genArray.addBlockState(pos, state, spawnerCompound, BlockStateGenArray.GenerationPhase.POST);

                this.usedDecoPositions.add(pos);
                spawnPositions.remove(pos);
            }
        }
    }

    protected void addWallDecoration(World world, BlockStateGenArray genArray, DungeonCastle dungeon) {
        int torchPercent = LIGHT_LEVEL * 3;

        for (Direction side : Direction.HORIZONTALS) {
            if (this.hasWallOnSide(side) || this.adjacentRoomHasWall(side)) {
                ArrayList<BlockPos> edge = this.getWallDecorationEdge(side);
                for (BlockPos pos : edge) {
                    if (this.usedDecoPositions.contains(pos)) {
                        // This position is already decorated, so keep going
                        continue;
                    }
                    if ((RoomDecorTypes.TORCH.wouldFit(pos, side, this.possibleDecoPositions, this.usedDecoPositions, this)) &&
                            (DungeonGenUtils.PercentageRandom(torchPercent, this.random))) {
                        RoomDecorTypes.TORCH.build(world, genArray, this, dungeon, pos, side, this.usedDecoPositions);
                    } else if ((RoomDecorTypes.UNLIT_TORCH.wouldFit(pos, side, this.possibleDecoPositions, this.usedDecoPositions, this)) &&
                            (DungeonGenUtils.PercentageRandom(5, this.random))) {
                        RoomDecorTypes.UNLIT_TORCH.build(world, genArray, this, dungeon, pos, side, this.usedDecoPositions);
                    } else if ((RoomDecorTypes.PAINTING.wouldFit(pos, side, this.possibleDecoPositions, this.usedDecoPositions)) &&
                            (DungeonGenUtils.PercentageRandom(dungeon.getPaintingChance(), this.random))) {
                        RoomDecorTypes.PAINTING.buildRandom(world, pos, genArray, side, this.possibleDecoPositions, this.usedDecoPositions);
                    }
                }
            }
        }
    }

    protected void addChests(World world, BlockStateGenArray genArray, DungeonCastle dungeon) {
        if (this.getChestIDs() != null && !this.possibleChestLocs.isEmpty()) {
            if (DungeonGenUtils.PercentageRandom(50, this.random)) {
                IRoomDecor chest = new RoomDecorChest();
                BlockPos pos = (BlockPos) this.possibleChestLocs.keySet().toArray()[this.random.nextInt(this.possibleChestLocs.size())];
                chest.build(world, genArray, this, dungeon, pos, this.possibleChestLocs.get(pos), this.usedDecoPositions);
            }
        }
    }
}
