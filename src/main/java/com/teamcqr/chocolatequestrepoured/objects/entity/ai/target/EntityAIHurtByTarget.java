package com.teamcqr.chocolatequestrepoured.objects.entity.ai.target;

import java.util.List;

import com.google.common.base.Predicate;
import com.teamcqr.chocolatequestrepoured.objects.entity.ai.AbstractCQREntityAI;
import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;
import com.teamcqr.chocolatequestrepoured.objects.items.staves.ItemStaffHealing;
import com.teamcqr.chocolatequestrepoured.util.CQRConfig;

import net.minecraft.entity.LivingEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;

public class EntityAIHurtByTarget extends AbstractCQREntityAI<AbstractEntityCQR> {

	protected final Predicate<LivingEntity> predicateAlly = input -> {
		if (!TargetUtil.PREDICATE_ATTACK_TARGET.apply(input)) {
			return false;
		}
		if (!EntityPredicates.IS_ALIVE.test(input)) {
			return false;
		}
		return EntityAIHurtByTarget.this.isSuitableAlly(input);
	};
	protected LivingEntity attackTarget;
	protected int prevRevengeTimer;

	public EntityAIHurtByTarget(AbstractEntityCQR entity) {
		super(entity);
	}

	@Override
	public boolean shouldExecute() {
		if (this.entity.world.getDifficulty() == Difficulty.PEACEFUL) {
			return false;
		}
		if (this.entity.getRevengeTimer() == this.prevRevengeTimer) {
			return false;
		}
		LivingEntity revengeTarget = this.entity.getRevengeTarget();
		if (!TargetUtil.PREDICATE_ATTACK_TARGET.apply(revengeTarget)) {
			return false;
		}
		if (!revengeTarget.isAlive()) {
			return false;
		}
		if (this.entity.getFaction().isAlly(revengeTarget)) {
			return false;
		}
		if (!this.entity.isInSightRange(revengeTarget)) {
			return false;
		}
		this.attackTarget = revengeTarget;
		return true;
	}

	@Override
	public void startExecuting() {
		this.prevRevengeTimer = this.entity.getRevengeTimer();
		this.trySetAttackTarget(this.entity);
		this.callForHelp();
	}

	protected void callForHelp() {
		double radius = CQRConfig.mobs.alertRadius;
		Vec3d eyeVec = this.entity.getEyePosition(1.0F);
		Vec3d vec1 = eyeVec.subtract(radius, radius * 0.5D, radius);
		Vec3d vec2 = eyeVec.add(radius, radius * 0.5D, radius);
		AxisAlignedBB aabb = new AxisAlignedBB(vec1.x, vec1.y, vec1.z, vec2.x, vec2.y, vec2.z);
		List<LivingEntity> allies = this.entity.world.getEntitiesWithinAABB(LivingEntity.class, aabb, this.predicateAlly);
		for (LivingEntity ally : allies) {
			this.trySetAttackTarget(ally);
		}
	}

	protected boolean isSuitableAlly(LivingEntity possibleAlly) {
		if (possibleAlly == this.entity) {
			return false;
		}
		if (!this.entity.getFaction().isAlly(possibleAlly)) {
			return false;
		}
		Path path = possibleAlly.getNavigator().getPathToLivingEntity(this.entity);
		return path != null && path.getCurrentPathLength() <= 20;
	}

	protected boolean trySetAttackTarget(LivingEntity entityLiving) {
		if (entityLiving.getHeldItemMainhand().getItem() instanceof ItemStaffHealing) {
			return false;
		}
		LivingEntity oldAttackTarget = entityLiving.getAttackTarget();
		if (oldAttackTarget != null && entityLiving.getEntitySenses().canSee(oldAttackTarget) && entityLiving.getDistance(oldAttackTarget) < entityLiving.getDistance(this.attackTarget)) {
			return false;
		}
		entityLiving.setAttackTarget(this.attackTarget);
		return true;
	}

}
