package com.teamcqr.chocolatequestrepoured.objects.factories;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import com.teamcqr.chocolatequestrepoured.init.ModBlocks;
import com.teamcqr.chocolatequestrepoured.init.ModItems;
import com.teamcqr.chocolatequestrepoured.tileentity.TileEntitySpawner;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * A static utility class for generating CQR/vanilla spawners and converting them to/from the other
 * 
 * @author DerToaster, Meldexun, jdawg3636
 * @version 11 October 2019
 */
public abstract class SpawnerFactory {

	/*
	 * Creation/Modification
	 */

	/**
	 * Places a spawner in the provided world at the provided position. Spawner type (CQR/vanilla) is determined
	 * dynamically based upon the requested capabilities.
	 * 
	 * @param entities                 Entities for spawner to spawn
	 * @param multiUseSpawner          Determines spawner type. Vanilla = true; CQR = false.
	 * @param spawnerSettingsOverrides Settings to be applied if generating vanilla spawner (can be null if CQR spawner)
	 * @param world                    World in which to place spawner
	 * @param pos                      Position at which to place spawner
	 */
	public static void placeSpawner(Entity[] entities, boolean multiUseSpawner, @Nullable CompoundNBT spawnerSettingsOverrides, World world, BlockPos pos) {
		CompoundNBT[] entCompounds = new CompoundNBT[entities.length];
		for (int i = 0; i < entities.length; i++) {
			Entity ent = entities[i];
			if(ent == null) {
				continue;
			}
			entCompounds[i] = createSpawnerNBTFromEntity(ent);
		}
		placeSpawner(entCompounds, multiUseSpawner, spawnerSettingsOverrides, world, pos);
	}

	/**
	 * Places a spawner in the provided world at the provided position. Spawner type (CQR/vanilla) is determined
	 * dynamically based upon the requested capabilities.
	 * 
	 * @param entities                 Entities as NBT Tag (From Entity.writeToNBTOptional(COMPOUND) for spawner to spawn
	 * @param multiUseSpawner          Determines spawner type. Vanilla = true; CQR = false.
	 * @param spawnerSettingsOverrides Settings to be applied if generating vanilla spawner (can be null if CQR spawner)
	 * @param world                    World in which to place spawner
	 * @param pos                      Position at which to place spawner
	 */
	public static void placeSpawner(CompoundNBT[] entities, boolean multiUseSpawner, @Nullable CompoundNBT spawnerSettingsOverrides, World world, BlockPos pos) {
		world.setBlockState(pos, (multiUseSpawner == true /* && spawnerSettingsOverrides != null */) ? Blocks.SPAWNER.getDefaultState() : ModBlocks.SPAWNER.getDefaultState());

		TileEntity tileEntity = world.getTileEntity(pos);
		if (multiUseSpawner) {
			MobSpawnerTileEntity tileEntityMobSpawner = (MobSpawnerTileEntity) tileEntity;
			CompoundNBT compound = tileEntityMobSpawner.write(new CompoundNBT());
			ListNBT spawnPotentials = new ListNBT();

			// Store entity ids into NBT tag
			for (int i = 0; i < entities.length; i++) {
				if (entities[i] != null) {
					{
						// needed because in earlier versions the uuid and pos were not removed when using a soul bottle/mob to spawner on an entity
						entities[i].remove("UUIDLeast");
						entities[i].remove("UUIDMost");
						entities[i].remove("Pos");
						ListNBT passengers = entities[i].getList("Passengers", 10);
						for (INBT passenger : passengers) {
							((CompoundNBT) passenger).remove("UUIDLeast");
							((CompoundNBT) passenger).remove("UUIDMost");
							((CompoundNBT) passenger).remove("Pos");
						}
					}
					CompoundNBT spawnPotential = new CompoundNBT();
					spawnPotential.putInt("Weight", 1);
					spawnPotential.put("Entity", entities[i]);
					spawnPotentials.add(spawnPotential);
				}
			}
			compound.put("SpawnPotentials", spawnPotentials);
			compound.remove("SpawnData");

			// Store default settings into NBT
			if (spawnerSettingsOverrides != null) {
				compound.putInt("MinSpawnDelay", spawnerSettingsOverrides.getInt("MinSpawnDelay"));
				compound.putInt("MaxSpawnDelay", spawnerSettingsOverrides.getInt("MaxSpawnDelay"));
				compound.putInt("SpawnCount", spawnerSettingsOverrides.getInt("SpawnCount"));
				compound.putInt("MaxNearbyEntities", spawnerSettingsOverrides.getInt("MaxNearbyEntities"));
				compound.putInt("SpawnRange", spawnerSettingsOverrides.getInt("SpawnRange"));
				compound.putInt("RequiredPlayerRange", spawnerSettingsOverrides.getInt("RequiredPlayerRange"));
			}

			// Read data from modified nbt
			tileEntityMobSpawner.read(compound);

			tileEntityMobSpawner.markDirty();
		} else {
			TileEntitySpawner tileEntitySpawner = (TileEntitySpawner) tileEntity;

			for (int i = 0; i < entities.length && i < 9; i++) {
				if (entities[i] != null) {
					tileEntitySpawner.inventory.setStackInSlot(i, getSoulBottleItemStackForEntity(entities[i]));
				}
			}

			tileEntitySpawner.markDirty();
		}
	}

	/**
	 * Places a vanilla spawner in the provided world at the provided position using the provided ResourceLocation for
	 * the entity that it should spawn.
	 */
	public static void createSimpleMultiUseSpawner(World world, BlockPos pos, ResourceLocation entityResLoc) {
		world.setBlockState(pos, Blocks.SPAWNER.getDefaultState());
		MobSpawnerTileEntity spawner = (MobSpawnerTileEntity) world.getTileEntity(pos);

		spawner.getSpawnerBaseLogic().setEntityType(ForgeRegistries.ENTITIES.getValue(entityResLoc));

		spawner.updateContainingBlockInfo();
		//Is validate the correct replacement for update?!
		spawner.validate();
	}
	
	public static MobSpawnerTileEntity getSpawnerTile(World world, ResourceLocation entity, BlockPos pos) {
		MobSpawnerTileEntity spawner = (MobSpawnerTileEntity) world.getTileEntity(pos);
		spawner.getSpawnerBaseLogic().setEntityType(ForgeRegistries.ENTITIES.getValue(entity));
		return spawner;
	}

	/**
	 * Overloaded variant of normal createSimpleMultiUseSpawner method that accepts an Entity object rather than a
	 * resource location in its parameter
	 */
	public static void createSimpleMultiUseSpawner(World world, BlockPos pos, Entity entity) {
		createSimpleMultiUseSpawner(world, pos, entity.getType().getRegistryName()/*EntityList.getKey(entity)*/);
	}

	/*
	 * CQR/Vanilla Conversion
	 */

	/**
	 * Converts the CQR spawner at the provided World/BlockPos to a vanilla spawner
	 * 
	 * @param spawnerSettings
	 */
	public static void convertCQSpawnerToVanillaSpawner(World world, BlockPos pos, @Nullable CompoundNBT spawnerSettings) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntitySpawner) {
			TileEntitySpawner spawner = (TileEntitySpawner) tile;

			// Entity[] entities = new Entity[spawner.inventory.getSlots()];
			CompoundNBT[] entities = new CompoundNBT[spawner.inventory.getSlots()];
			// Random rand = new Random();

			for (int i = 0; i < entities.length; i++) {
				ItemStack stack = spawner.inventory.extractItem(i, spawner.inventory.getStackInSlot(i).getCount(), false);// getStackInSlot(i);
				if (stack != null && !stack.isEmpty() && stack.getCount() >= 1) {
					try {
						CompoundNBT tag = stack.getTag();

						// CompoundNBT entityTag = (CompoundNBT)tag.getTag("EntityIn");
						entities[i] = tag.getCompound("EntityIn");
						/*
						 * entities[i] = createEntityFromNBTWithoutSpawningIt(entityTag, world);
						 * 
						 * entities[i].setUniqueId(MathHelper.getRandomUUID(rand));
						 */
					} catch (NullPointerException ignored) {
					}
				} else {
					entities[i] = null;
				}
			}
			world.setBlockState(pos, Blocks.AIR.getDefaultState());

			placeSpawner(entities, true, spawnerSettings, world, pos);
		}
	}

	/**
	 * Converts the vanilla spawner at the provided World/BlockPos to a CQR spawner
	 */
	public static void convertVanillaSpawnerToCQSpawner(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof MobSpawnerTileEntity) {
			MobSpawnerTileEntity spawnerMultiUseTile = (MobSpawnerTileEntity) tile;

			List<WeightedSpawnerEntity> spawnerEntries = new ArrayList<WeightedSpawnerEntity>();
			spawnerEntries = ObfuscationReflectionHelper.getPrivateValue(MobSpawnerBaseLogic.class, spawnerMultiUseTile.getSpawnerBaseLogic(), 1 /* It is an array index of getDeclaredFields() */);
			if (spawnerEntries != null && !spawnerEntries.isEmpty()) {
				Iterator<WeightedSpawnerEntity> iterator = spawnerEntries.iterator();

				// Entity[] entities = new Entity[9];
				CompoundNBT[] entityCompound = new CompoundNBT[9];

				int entriesRead = 0;
				while (entriesRead < 9 && iterator.hasNext()) {
					/*
					 * Entity entity = createEntityFromNBTWithoutSpawningIt(iterator.next().getNbt(), world);
					 * entities[entriesRead] = entity;
					 */
					entityCompound[entriesRead] = iterator.next().getNbt();
					entriesRead++;
				}
				// placeSpawner(entities, false, null, world, pos);
				placeSpawner(entityCompound, false, null, world, pos);
			}
		}
	}

	/*
	 * Miscellaneous
	 */

	/**
	 * Converts provided NBT data into an entity in the provided world without actually spawning it
	 * 
	 * @return Generated entity object
	 */
	public static Entity createEntityFromNBTWithoutSpawningIt(CompoundNBT tag, World worldIn) {
		Entity entity = EntityType.loadEntityUnchecked(tag, worldIn).get();//EntityList.createEntityFromNBT(tag, worldIn);
		entity.read(tag);

		return entity;
	}

	public static CompoundNBT createSpawnerNBTFromEntity(Entity entity) {
		CompoundNBT entityCompound = new CompoundNBT();
		entity.writeUnlessPassenger(entityCompound);
		entityCompound.remove("UUIDLeast");
		entityCompound.remove("UUIDMost");
		entityCompound.remove("Pos");
		ListNBT passengerList = entityCompound.getList("Passengers", 10);
		for (INBT passengerTag : passengerList) {
			((CompoundNBT) passengerTag).remove("UUIDLeast");
			((CompoundNBT) passengerTag).remove("UUIDMost");
			((CompoundNBT) passengerTag).remove("Pos");
		}

		return entityCompound;
	}

	/**
	 * Used internally for the placeSpawner method
	 */
	public static ItemStack getSoulBottleItemStackForEntity(Entity entity) {
		if (entity == null) {
			return null;
		}
		CompoundNBT entityTag = new CompoundNBT();
		if (entity.writeUnlessPassenger(entityTag)) {
			return getSoulBottleItemStackForEntity(entityTag);
		}
		return null;

	}

	public static ItemStack getSoulBottleItemStackForEntity(CompoundNBT entityTag) {
		ItemStack bottle = new ItemStack(ModItems.SOUL_BOTTLE);
		bottle.setCount(1);
		CompoundNBT mobToSpawnerItem = new CompoundNBT();

		mobToSpawnerItem.put("EntityIn", entityTag);
		bottle.setTag(mobToSpawnerItem);
		return bottle;
	}

}
