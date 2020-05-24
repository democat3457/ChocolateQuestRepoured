package com.teamcqr.chocolatequestrepoured.objects.entity.ai;

import java.util.EnumSet;
import java.util.List;

import com.teamcqr.chocolatequestrepoured.objects.entity.ai.target.TargetUtil;
import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;
import com.teamcqr.chocolatequestrepoured.util.CQRConfig;
import com.teamcqr.chocolatequestrepoured.util.EntityUtil;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityAIHealingPotion extends AbstractCQREntityAI<AbstractEntityCQR> {

	protected int ticksNotHealing;
	protected boolean isHealing;

	public EntityAIHealingPotion(AbstractEntityCQR entity) {
		super(entity);
		//this.setMutexBits(3);
		setMutexFlags(EnumSet.of(Flag.MOVE));
	}

	@Override
	public boolean shouldExecute() {
		return this.entity.getHealingPotions() > 0 && this.entity.getHealth() <= Math.max(this.entity.getMaxHealth() * 0.15F, 5.0F);
	}

	@Override
	public void startExecuting() {
		this.entity.getNavigator().clearPath();
		this.ticksNotHealing = 0;
		this.isHealing = false;
	}

	@Override
	public void resetTask() {
		this.ticksNotHealing = 0;
		this.isHealing = false;
		this.entity.resetActiveHand();
		if (this.entity.isHoldingPotion()) {
			this.entity.swapWeaponAndPotionSlotItemStacks();
		}
	}

	@Override
	public void tick() {
		Entity attackTarget = this.entity.getAttackTarget();

		if (this.isHealing) {
			this.entity.swingArm(Hand.MAIN_HAND);
		} else {
			if (attackTarget == null) {
				this.startHealing();
			}
		}

		boolean flag = true;
		if (attackTarget != null) {
			int alertRadius = CQRConfig.mobs.alertRadius;
			Vec3d vec1 = this.entity.getPositionVector().add(alertRadius, alertRadius * 0.5D, alertRadius);
			Vec3d vec2 = this.entity.getPositionVector().subtract(alertRadius, alertRadius * 0.5D, alertRadius);
			AxisAlignedBB aabb = new AxisAlignedBB(vec1.x, vec1.y, vec1.z, vec2.x, vec2.y, vec2.z);
			List<Entity> possibleEnts = this.entity.world.getEntitiesInAABBexcluding(this.entity, aabb, TargetUtil.createPredicateAlly(this.entity.getFaction()));

			if (!possibleEnts.isEmpty()) {
				Entity e1 = null;
				int count = -1;
				double distance = Double.MAX_VALUE;
				for (Entity e2 : possibleEnts) {
					AxisAlignedBB aabb1 = new AxisAlignedBB(e2.getPosX() - 4, e2.getPosY() - 2, e2.getPosZ() - 4, e2.getPosX() + 4, e2.getPosY() + 2, e2.getPosZ() + 4);
					List<Entity> list = e2.world.getEntitiesInAABBexcluding(e2, aabb1, TargetUtil.createPredicateAlly(this.entity.getFaction()));
					double d = this.entity.getDistanceSq(e2);
					if (list.size() > count || (list.size() == count && d < distance)) {
						e1 = e2;
						count = list.size();
						distance = d;
					}
				}
				if (count >= 5) {
					this.entity.getNavigator().tryMoveToEntityLiving(e1, 1.0D);
					flag = false;
				}
			}

			boolean canMoveBackwards = this.canMoveBackwards();

			if (flag) {
				// No larger group in range
				this.updateRotation(attackTarget, 2.5F, 2.5F);

				if (canMoveBackwards) {
					EntityUtil.move2D(this.entity, 0.0D, -0.2D, this.entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue() * 1.5, this.entity.rotationYawHead);
				}
			}

			if (!this.isHealing) {
				if (this.entity.collidedHorizontally || !canMoveBackwards || this.ticksNotHealing > 80 || this.entity.getDistance(attackTarget) > 8.0F) {
					this.startHealing();
				} else {
					this.checkAndPerformBlock();
				}
			}
		}
	}

	private void updateRotation(Entity entity, float deltaYaw, float deltaPitch) {
		double x = entity.getPosX() - this.entity.getPosX();
		double y = entity.getPosY() - this.entity.getPosY();
		double z = entity.getPosZ() - this.entity.getPosZ();
		double d = Math.sqrt(x * x + z * z);

		float yaw = (float) Math.toDegrees(Math.atan2(-x, z));
		float pitch = (float) Math.toDegrees(Math.atan2(-y, d));
		this.entity.rotationYaw += MathHelper.clamp(MathHelper.wrapDegrees(yaw - this.entity.rotationYaw), -deltaYaw, deltaYaw);
		this.entity.rotationYaw = MathHelper.wrapDegrees(this.entity.rotationYaw);
		this.entity.rotationPitch += MathHelper.clamp(MathHelper.wrapDegrees(pitch - this.entity.rotationPitch), -deltaPitch, deltaPitch);
		this.entity.rotationPitch = MathHelper.clamp(this.entity.rotationPitch, -90.0F, 90.0F);
		this.entity.rotationYawHead = this.entity.rotationYaw;
	}

	private void checkAndPerformBlock() {
		if (!this.entity.isActiveItemStackBlocking()) {
			ItemStack offhand = this.entity.getHeldItem(Hand.OFF_HAND);

			if (offhand.getItem().isShield(offhand, this.entity)) {
				this.entity.setActiveHand(Hand.OFF_HAND);
			}
		}
	}

	private boolean canMoveBackwards() {
		double sin = -Math.sin(Math.toRadians(this.entity.rotationYaw));
		double cos = Math.cos(Math.toRadians(this.entity.rotationYaw));
		BlockPos pos = new BlockPos(this.entity.getPosX() - sin, this.entity.getPosY() - 0.001D, this.entity.getPosZ() - cos);
		BlockState state = this.entity.world.getBlockState(pos);
		return state.isTopSolid(this.entity.world, pos, this.entity);
	}

	public void startHealing() {
		if (!this.isHealing) {
			this.isHealing = true;
			if (!this.entity.isHoldingPotion()) {
				this.entity.swapWeaponAndPotionSlotItemStacks();
			}
			this.entity.resetActiveHand();
			this.entity.setActiveHand(Hand.MAIN_HAND);
		}
	}

}
