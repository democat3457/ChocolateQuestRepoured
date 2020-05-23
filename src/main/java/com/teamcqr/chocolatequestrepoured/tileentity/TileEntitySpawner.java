package com.teamcqr.chocolatequestrepoured.tileentity;

import java.util.Random;

import javax.annotation.Nullable;

import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;
import com.teamcqr.chocolatequestrepoured.structuregen.EDungeonMobType;
import com.teamcqr.chocolatequestrepoured.util.CQRConfig;
import com.teamcqr.chocolatequestrepoured.util.Reference;

import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.Difficulty;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntitySpawner extends TileEntitySyncClient implements ITickable {

	public ItemStackHandler inventory = new ItemStackHandler(9);
	private boolean spawnedInDungeon = false;
	private EDungeonMobType mobOverride = null;
	private int dungeonChunkX = 0;
	private int dungeonChunkZ = 0;
	private Mirror mirror = Mirror.NONE;
	private Rotation rot = Rotation.NONE;

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable Direction facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable Direction facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) this.inventory : super.getCapability(capability, facing);
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		this.inventory.deserializeNBT((CompoundNBT) compound.get("inventory"));
		if (compound.contains("isDungeonSpawner")) {
			this.spawnedInDungeon = compound.getBoolean("isDungeonSpawner");
		}
		if (compound.contains("overrideMob")) {
			this.mobOverride = EDungeonMobType.byString(compound.getString("overrideMob"));
		}
		if (compound.contains("dungeonChunkX") && compound.contains("dungeonChunkZ")) {
			this.dungeonChunkX = compound.getInt("dungeonChunkX");
			this.dungeonChunkZ = compound.getInt("dungeonChunkZ");
		}
		this.mirror = Mirror.values()[compound.getInt("mirror")];
		this.rot = Rotation.values()[compound.getInt("rot")];
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound = super.write(compound);
		compound.put("inventory", this.inventory.serializeNBT());
		if (this.spawnedInDungeon) {
			compound.putBoolean("isDungeonSpawner", true);
		}
		if (this.mobOverride != null) {
			compound.putString("overrideMob", this.mobOverride.name());
		}
		if (this.dungeonChunkX != 0 && this.dungeonChunkZ != 0) {
			compound.putInt("dungeonChunkX", this.dungeonChunkX);
			compound.putInt("dungeonChunkZ", this.dungeonChunkZ);
		}
		compound.putInt("mirror", this.mirror.ordinal());
		compound.putInt("rot", this.rot.ordinal());
		return compound;
	}

	@Nullable
	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(I18n.format("tile.spawner.name"));
	}

	//@Override
	public void update() {
		if (!this.world.isRemote && this.world.getDifficulty() != Difficulty.PEACEFUL && this.isNonCreativePlayerInRange(CQRConfig.general.spawnerActivationDistance)) {
			this.turnBackIntoEntity();
		}
	}
	
	@Override
	public void tick() {
		update();
	}
	
	public void forceTurnBackIntoEntity() {
		if (!this.world.isRemote && this.world.getDifficulty() != Difficulty.PEACEFUL) {
			this.turnBackIntoEntity();
		}
	}

	public void setInDungeon(int dunChunkX, int dunChunkZ, EDungeonMobType mobOverride) {
		this.spawnedInDungeon = true;
		this.mobOverride = mobOverride;
		this.dungeonChunkX = dunChunkX;
		this.dungeonChunkZ = dunChunkZ;

		this.markDirty();
	}

	protected void turnBackIntoEntity() {
		if (!this.world.isRemote) {
			for (int i = 0; i < this.inventory.getSlots(); i++) {
				ItemStack stack = this.inventory.getStackInSlot(i);

				if (!stack.isEmpty() && stack.getTag() != null) {
					CompoundNBT nbt = (CompoundNBT) stack.getTag().get("EntityIn");

					while (!stack.isEmpty()) {
						this.spawnEntityFromNBT(nbt);
						stack.shrink(1);
					}
				}
			}

			this.world.setBlockState(this.pos, Blocks.AIR.getDefaultState());
		}
	}

	protected Entity spawnEntityFromNBT(CompoundNBT nbt) {
		{
			// needed because in earlier versions the uuid and pos were not removed when using a soul bottle/mob to spawner on an entity
			nbt.remove("UUIDLeast");
			nbt.remove("UUIDMost");
			nbt.remove("Pos");
			ListNBT passengers = nbt.getList("Passengers", 10);
			for (INBT passenger : passengers) {
				((CompoundNBT) passenger).remove("UUIDLeast");
				((CompoundNBT) passenger).remove("UUIDMost");
				((CompoundNBT) passenger).remove("Pos");
			}
		}

		if (this.mobOverride != null && nbt.getString("id").equals(Reference.MODID + ":dummy")) {
			if (this.mobOverride == EDungeonMobType.DEFAULT) {
				EDungeonMobType mobType = EDungeonMobType.getMobTypeDependingOnDistance(this.world, this.pos.getX(), this.pos.getZ());
				nbt.putString("id", mobType.getEntityResourceLocation().toString());
			} else {
				nbt.putString("id", this.mobOverride.getEntityResourceLocation().toString());
			}
		}

		Entity entity = EntityList.createEntityFromNBT(nbt, this.world);

		if (entity != null) {
			Random rand = new Random();
			Vec3d pos = new Vec3d(this.pos.getX() + 0.5D, this.pos.getY(), this.pos.getZ() + 0.5D);
			double offset = entity.getWidth() < 0.96F ? 0.5D - entity.getWidth() * 0.5D : 0.02D;
			pos = pos.add(rand.nextDouble() * offset * 2.0D - offset, 0.0D, rand.nextDouble() * offset * 2.0D - offset);
			entity.setPosition(pos.x, pos.y, pos.z);

			if (entity instanceof LivingEntity) {
				if (CQRConfig.general.mobsFromCQSpawnerDontDespawn) {
					((LivingEntity) entity).enablePersistence();
				}

				if (this.spawnedInDungeon && entity instanceof AbstractEntityCQR) {
					((AbstractEntityCQR) entity).onSpawnFromCQRSpawnerInDungeon(new PlacementSettings().setMirror(this.mirror).setRotation(this.rot), this.mobOverride);
				}
			}

			this.world.addEntity(entity);

			ListNBT list = nbt.getList("Passengers", 10);
			if (!list.isEmpty()) {
				Entity rider = this.spawnEntityFromNBT(list.getCompound(0));
				rider.startRiding(entity);
			}
		}

		return entity;
	}

	protected boolean isNonCreativePlayerInRange(double range) {
		if (range > 0.0D) {
			double d = range * range;
			for (PlayerEntity player : this.world.getPlayers()) {
				if (!player.isCreative() && !player.isSpectator() && player.getDistanceSqToCenter(this.pos) < d) {
					return true;
				}
			}
		}
		return false;
	}

	public void setDungeonSpawner() {
		this.spawnedInDungeon = true;
	}

	public void setMirrorAndRotation(Mirror mirror, Rotation rotation) {
		this.mirror = mirror;
		this.rot = rotation;
	}

}
