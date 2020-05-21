package com.teamcqr.chocolatequestrepoured.objects.entity.misc;

import java.util.UUID;
import java.util.function.Consumer;

import com.google.common.base.Predicates;
import com.teamcqr.chocolatequestrepoured.objects.entity.Capes;
import com.teamcqr.chocolatequestrepoured.objects.entity.EntityEquipmentExtraSlot;
import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;
import com.teamcqr.chocolatequestrepoured.objects.entity.boss.EntityCQRWalkerKing;
import com.teamcqr.chocolatequestrepoured.objects.entity.mobs.EntityCQRWalker;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityWalkerKingIllusion extends EntityCQRWalker {

	private EntityCQRWalkerKing parent;
	private int ttl = 1200;
	private int searchTicksForParent = 20;
	private int damageCounter = 0;
	private UUID parentUUID = null;

	public EntityWalkerKingIllusion(World worldIn) {
		super(worldIn);
	}

	public EntityWalkerKingIllusion(int ttl, EntityCQRWalkerKing parent, World world) {
		this(world);
		this.parent = parent;
		this.ttl = ttl;
		this.parentUUID = parent.getUniqueID();

		cloneParentEquipment(parent);
	}

	private void cloneParentEquipment(AbstractEntityCQR parent) {
		this.setItemStackToExtraSlot(EntityEquipmentExtraSlot.POTION, parent.getItemStackFromExtraSlot(EntityEquipmentExtraSlot.POTION));
		this.setItemStackToSlot(EquipmentSlotType.CHEST, parent.getItemStackFromSlot(EquipmentSlotType.CHEST));
		this.setItemStackToSlot(EquipmentSlotType.HEAD, parent.getItemStackFromSlot(EquipmentSlotType.HEAD));
		this.setItemStackToSlot(EquipmentSlotType.LEGS, parent.getItemStackFromSlot(EquipmentSlotType.LEGS));
		this.setItemStackToSlot(EquipmentSlotType.FEET, parent.getItemStackFromSlot(EquipmentSlotType.FEET));
		this.setItemStackToSlot(EquipmentSlotType.OFFHAND, parent.getItemStackFromSlot(EquipmentSlotType.OFFHAND));
		this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(parent.getItemStackFromSlot(EquipmentSlotType.MAINHAND).getItem(), 1));
	}

	@Override
	protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
	}

	@Override
	protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
	}

	@Override
	protected int getExperiencePoints(PlayerEntity player) {
		return 0;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return new ResourceLocation("enpty");
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		if (parent != null) {
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(parent.getMaxHealth());
			this.setHealth(parent.getHealth());
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return attackEntityFrom(source, amount, false);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount, boolean sentFromPart) {
		// return super.attackEntityFrom(source, amount, sentFromPart);
		if (!world.isRemote && damageCounter >= 2 * (world.getDifficulty().ordinal() <= 0 ? 1 : world.getDifficulty().ordinal())) {
			setDead();
		}
		damageCounter++;
		return true;
	}

	@Override
	public void setDead() {
		if(world.isRemote) {
			playDeathEffect();
		} 
		super.setDead();
	}

	private void playDeathEffect() {
		if (world.isRemote) {
			for (int i = 0; i < 15; i++) {
				world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, posX, posY, posZ, 0.0, 0.025, 0.0);
				world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, posX, posY, posZ, 0.025, 0.01, 0.025);
				world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, posX, posY, posZ, 0.025, 0.01, -0.025);
				world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, posX, posY, posZ, -0.025, 0.01, 0.025);
				world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, posX, posY, posZ, -0.025, 0.01, -0.025);
			}
			world.playSound(posX, posY, posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.AMBIENT, 2, 0.75F, true);
		}
	}

	@Override
	public void onEntityUpdate() {
		if(!world.isRemote) {
			if (ttl < 0) {
				setDead();
				return;
			}
			// Search parent
			if (parent == null && parentUUID != null) {
				if (searchTicksForParent > 0) {
					if (!world.isRemote) {
						world.getEntitiesInAABBexcluding(this, new AxisAlignedBB(getPosition().add(-10, -10, -10), getPosition().add(10, 10, 10)), Predicates.instanceOf(EntityCQRWalkerKing.class)).forEach(new Consumer<Entity>() {

							@Override
							public void accept(Entity t) {
								if (t.getUniqueID().equals(parentUUID)) {
									parent = (EntityCQRWalkerKing) t;
								}
							}
						});
						;
						searchTicksForParent--;
					}
				} else {
					setDead();
					return;
				}
			}
			if (parent == null || parent.isDead) {
				setDead();
				return;
			}
			super.onEntityUpdate();
			this.setHealth(parent.getHealth());

			if (parent.getAttackTarget() != null || getAttackTarget() != null) {
				ttl--;
			} else {
				ttl -= 10;
			}
		} else {
			super.onEntityUpdate();
		}
	}

	@Override
	public ResourceLocation getResourceLocationOfCape() {
		return Capes.CAPE_WALKER;
	}

	@Override
	public boolean hasCape() {
		return true;
	}

	@Override
	public void writeEntityToNBT(CompoundNBT compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("ttl", ttl);
		compound.setTag("illusionParent", NBTUtil.createUUIDTag(parentUUID));
	}

	@Override
	public void readEntityFromNBT(CompoundNBT compound) {
		super.readEntityFromNBT(compound);
		ttl = compound.getInteger("ttl");
		parentUUID = NBTUtil.getUUIDFromTag(compound.getCompoundTag("illusionParent"));
	}

}
