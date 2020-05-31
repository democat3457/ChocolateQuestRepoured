package com.teamcqr.chocolatequestrepoured.objects.entity.bases;

import javax.annotation.Nullable;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class EntityCQRMountBase extends AnimalEntity {

	public EntityCQRMountBase(World worldIn, EntityType<? extends EntityCQRMountBase> type) {
		super(type, worldIn);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(1, new PanicGoal(this, 0.9D));
		this.goalSelector.addGoal(6, new RandomWalkingGoal(this, 0.6D));
		this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
	}

	@Override
	protected boolean canBeRidden(Entity entityIn) {
		return entityIn instanceof AbstractEntityCQR || entityIn instanceof PlayerEntity;
	}

	@Override
	public AgeableEntity createChild(AgeableEntity ageable) {
		return null;
	}

	@Override
	@Nullable
	public Entity getControllingPassenger() {
		return this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);
	}

	@Override
	public boolean canBeSteered() {
		Entity entity = this.getControllingPassenger();

		return entity != null && (entity instanceof AbstractEntityCQR || entity instanceof PlayerEntity);
	}

	@Override
	public boolean processInteract(PlayerEntity player, Hand hand) {
		if (!super.processInteract(player, hand)) {
			if (!this.isBeingRidden()) {
				if (!this.world.isRemote) {
					player.startRiding(this);
				}

				return true;
			}

		}
		return false;

	}

	@Override
	public void travel(Vec3d p_213352_1_) {
		travel(p_213352_1_.x, p_213352_1_.y, p_213352_1_.z);
	}
	
	//@Override
	public void travel(double x, double y, double z) {
		if (this.isBeingRidden() && this.canBeSteered()) {
			LivingEntity entity = (LivingEntity) this.getControllingPassenger();// this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
			this.rotationYaw = entity.rotationYaw;
			this.prevRotationYaw = this.rotationYaw;
			this.rotationPitch = entity.rotationPitch * 0.5F;
			this.setRotation(this.rotationYaw, this.rotationPitch);
			this.renderYawOffset = this.rotationYaw;
			this.rotationYawHead = this.rotationYaw;
			this.stepHeight = 1.0F;
			this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;

			double v = 0.0;
			if (this.isInWater() || this.isInLava()) {
				v = y * 0.5;
			}
			if (this.canPassengerSteer()) {
				double f = (float) this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue() * 0.5F;

				this.setAIMoveSpeed((float) f);
				super.travel(new Vec3d(entity.moveStrafing * f, v, entity.moveForward * f));
			} else {
				this.setMotion(Vec3d.ZERO);
			}

			this.prevLimbSwingAmount = this.limbSwingAmount;
			double d1 = this.getPosX() - this.prevPosX;
			double d0 = this.getPosZ() - this.prevPosZ;
			float f1 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;

			if (f1 > 1.0F) {
				f1 = 1.0F;
			}

			this.limbSwingAmount += (f1 - this.limbSwingAmount) * 0.4F;
			this.limbSwing += this.limbSwingAmount;
		} else {
			this.stepHeight = 0.5F;
			this.jumpMovementFactor = 0.02F;
			super.travel(new Vec3d(x, y, z));
		}
	}

	@Override
	protected abstract ResourceLocation getLootTable();

}
