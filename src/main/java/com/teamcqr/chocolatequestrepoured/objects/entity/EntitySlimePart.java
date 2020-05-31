package com.teamcqr.chocolatequestrepoured.objects.entity;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.world.World;

public class EntitySlimePart extends SlimeEntity {

	private UUID ownerUuid;

	public EntitySlimePart(World worldIn, EntityType<? extends EntitySlimePart> type) {
		super(type, worldIn);
	}

	public EntitySlimePart(World worldIn, LivingEntity owner, EntityType<? extends EntitySlimePart> type) {
		this(worldIn, type);
		this.ownerUuid = owner.getUniqueID();
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.target.taskEntries.clear();
	}

	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		IAttributeInstance iattributeinstance = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
		iattributeinstance.setBaseValue(iattributeinstance.getBaseValue() * 0.5D);
	}

	@Override
	public void tick() {
		if (this.ticksExisted > 400) {
			this.remove();
		}

		super.tick();
	}

	@Override
	protected void collideWithEntity(Entity entityIn) {
		if (entityIn instanceof LivingEntity && entityIn.getUniqueID().equals(this.ownerUuid)) {
			((LivingEntity) entityIn).heal(2.0F);
			this.remove();
		}
	}

	@Override
	protected boolean canDropLoot() {
		return false;
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putInt("ticksExisted", this.ticksExisted);
		compound.put("ownerUuid", NBTUtil.writeUniqueId(this.ownerUuid));
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.ticksExisted = compound.getInt("ticksExisted");
		this.ownerUuid = NBTUtil.readUniqueId(compound.getCompound("ownerUuid"));
	}

}
