package com.teamcqr.chocolatequestrepoured.objects.entity.ai;

import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;

import net.minecraft.util.math.BlockPos;

public class EntityAIFollowAttackTarget extends AbstractCQREntityAI<AbstractEntityCQR> {

	private int ticksWaiting;

	public EntityAIFollowAttackTarget(AbstractEntityCQR entity) {
		super(entity);
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		return this.entity.getAttackTarget() != null;
	}

	@Override
	public boolean shouldContinueExecuting() {
		if (this.entity.getAttackTarget() == null) {
			return false;
		}
		if (this.entity.hasPath()) {
			return true;
		}
		if (this.ticksWaiting < 100) {
			return true;
		}
		this.entity.setAttackTarget(null);
		return false;
	}

	@Override
	public void startExecuting() {
		BlockPos target = new BlockPos(this.entity.getLastPosAttackTarget());
		this.entity.getNavigator().tryMoveToXYZ(target.getX(), target.getY(), target.getZ(), 1.0D);
	}

	@Override
	public void resetTask() {
		this.entity.getNavigator().clearPath();
	}

	@Override
	public void updateTask() {
		if (this.entity.getLastTimeSeenAttackTarget() + 100 >= this.entity.ticksExisted) {
			BlockPos target = new BlockPos(this.entity.getLastPosAttackTarget());
			this.entity.getNavigator().tryMoveToXYZ(target.getX(), target.getY(), target.getZ(), 1.0D);
		}
		if (!this.entity.hasPath()) {
			this.ticksWaiting++;
		} else {
			this.ticksWaiting = 0;
		}
	}

}
