package com.teamcqr.chocolatequestrepoured.objects.entity.ai;

import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;
import com.teamcqr.chocolatequestrepoured.objects.items.swords.ItemDagger;

import net.minecraft.entity.LivingEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;

public class EntityAIBackstab extends EntityAIAttack {

	public EntityAIBackstab(AbstractEntityCQR entity) {
		super(entity);
	}

	@Override
	public boolean shouldExecute() {
		return this.entity.getHeldItemMainhand().getItem() instanceof ItemDagger && super.shouldExecute();
	}

	@Override
	public boolean shouldContinueExecuting() {
		return this.entity.getHeldItemMainhand().getItem() instanceof ItemDagger && super.shouldContinueExecuting();
	}

	@Override
	public void resetTask() {
		super.resetTask();
		this.entity.setSneaking(false);
	}

	@Override
	public void tick() {
		super.tick();

		LivingEntity attackTarget = this.entity.getAttackTarget();

		if (attackTarget instanceof AbstractEntityCQR) {
			AbstractEntityCQR target = (AbstractEntityCQR) attackTarget;
			boolean flag = this.entity.getDistance(target) < 20.0D && target.getEntitySenses().canSee(this.entity) && !target.isEntityInFieldOfView(this.entity);
			this.entity.setSneaking(flag);
		}
	}

	@Override
	protected void updatePath(LivingEntity target) {
		double distance = Math.min(4.0D, this.entity.getDistance(target.getPosX(), target.getPosY(), target.getPosZ()) * 0.5D);
		double rad = Math.toRadians(target.rotationYaw);
		double sin = Math.sin(rad);
		double cos = Math.cos(rad);
		PathNavigator navigator = this.entity.getNavigator();
		Path path = null;
		for (int i = 4; path == null && i >= 0; i--) {
			double d = distance * (double) i / 4.0D;
			path = navigator.getPathToPos(target.getPosX() + sin * d, target.getPosY(), target.getPosZ() - cos * d, 1);
		}
		navigator.setPath(path, 1.0D);
	}

	@Override
	protected void checkAndPerformBlock() {

	}

}
