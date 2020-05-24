package com.teamcqr.chocolatequestrepoured.objects.entity.ai.target;

import java.util.List;

import com.google.common.base.Predicate;
import com.teamcqr.chocolatequestrepoured.factions.CQRFaction;
import com.teamcqr.chocolatequestrepoured.init.ModItems;
import com.teamcqr.chocolatequestrepoured.objects.entity.ai.AbstractCQREntityAI;
import com.teamcqr.chocolatequestrepoured.objects.entity.boss.EntityCQRNetherDragon;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.Difficulty;

public class EntityAINetherDragonNearestAttackTarget extends AbstractCQREntityAI<EntityCQRNetherDragon> {

	protected final Predicate<LivingEntity> predicate = input -> {
		if (!TargetUtil.PREDICATE_ATTACK_TARGET.apply(input)) {
			return false;
		}
		if (!EntityPredicates.IS_ALIVE.test(input)) {
			return false;
		}
		return EntityAINetherDragonNearestAttackTarget.this.isSuitableTarget(input);
	};

	public EntityAINetherDragonNearestAttackTarget(EntityCQRNetherDragon entity) {
		super(entity);
	}

	@Override
	public boolean shouldExecute() {
		if (this.entity.world.getDifficulty() == Difficulty.PEACEFUL) {
			this.entity.setAttackTarget(null);
			return false;
		}
		if (this.isStillSuitableTarget(this.entity.getAttackTarget())) {
			return false;
		}
		this.entity.setAttackTarget(null);
		return true;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return false;
	}

	@Override
	public void startExecuting() {
		AxisAlignedBB aabb = this.entity.getBoundingBox().grow(32.0D);
		List<LivingEntity> possibleTargets = this.entity.world.getEntitiesWithinAABB(LivingEntity.class, aabb, this.predicate);
		if (!possibleTargets.isEmpty()) {
			this.entity.setAttackTarget(TargetUtil.getNearestEntity(this.entity, possibleTargets));
		}
	}

	private boolean isSuitableTarget(LivingEntity possibleTarget) {
		if (possibleTarget == this.entity) {
			return false;
		}
		CQRFaction faction = this.entity.getFaction();
		if (this.entity.getHeldItemMainhand().getItem() == ModItems.STAFF_HEALING) {
			if (faction == null || (!faction.isAlly(possibleTarget) && this.entity.getLeader() != possibleTarget)) {
				return false;
			}
			if (possibleTarget.getHealth() >= possibleTarget.getMaxHealth()) {
				return false;
			}
			/*if (!this.entity.isInSightRange(possibleTarget)) {
				return false;
			}*/
			//return this.entity.getEntitySenses().canSee(possibleTarget);
			return isInHomeZone(possibleTarget);
		}
		if (faction == null || !this.entity.getFaction().isEnemy(possibleTarget) || this.entity.getLeader() == possibleTarget) {
			return false;
		}
		/*if (!this.entity.getEntitySenses().canSee(possibleTarget)) {
			return false;
		}*/
		if (this.entity.isInAttackReach(possibleTarget)) {
			return true;
		}
		/*if (this.entity.isEntityInFieldOfView(possibleTarget)) {
			return this.entity.isInSightRange(possibleTarget);
		}*/
		return !possibleTarget.isSneaking() && this.entity.getDistance(possibleTarget) < 32.0D;
	}

	private boolean isStillSuitableTarget(LivingEntity possibleTarget) {
		if (!TargetUtil.PREDICATE_ATTACK_TARGET.apply(possibleTarget)) {
			return false;
		}
		if (!EntityPredicates.IS_ALIVE.test(possibleTarget)) {
			return false;
		}
		if (possibleTarget == this.entity) {
			return false;
		}
		CQRFaction faction = this.entity.getFaction();
		if (this.entity.getHeldItemMainhand().getItem() == ModItems.STAFF_HEALING) {
			if (faction == null || (!faction.isAlly(possibleTarget) && this.entity.getLeader() != possibleTarget)) {
				return false;
			}
			if (possibleTarget.getHealth() >= possibleTarget.getMaxHealth()) {
				return false;
			}
			/*if (!this.entity.isInSightRange(possibleTarget)) {
				return false;
			}*/
			//return this.entity.getEntitySenses().canSee(possibleTarget);
			return isInHomeZone(possibleTarget);
		}
		if (faction == null || !this.entity.getFaction().isEnemy(possibleTarget) || this.entity.getLeader() == possibleTarget) {
			return false;
		}
		/*if (!this.entity.isInSightRange(possibleTarget)) {
			return false;
		}
		return this.entity.getEntitySenses().canSee(possibleTarget);*/
		return isInHomeZone(possibleTarget);
	}

	private boolean isInHomeZone(LivingEntity possibleTarget) {
		double distance = possibleTarget.getPosition().distanceSq(entity.getCirclingCenter().getX(), entity.getCirclingCenter().getY(), entity.getCirclingCenter().getZ(), false);
		distance = Math.sqrt(distance);
		return distance <= 24 + 8 * (world.getDifficulty().ordinal());
	}

}
