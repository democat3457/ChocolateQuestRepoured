package com.teamcqr.chocolatequestrepoured.objects.entity.ai;

import java.util.EnumSet;

import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;

import net.minecraft.util.math.BlockPos;

public class EntityAIMoveToHome extends AbstractCQREntityAI<AbstractEntityCQR> {

	public EntityAIMoveToHome(AbstractEntityCQR entity) {
		super(entity);
		//this.setMutexBits(3);
		setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean shouldExecute() {
		if (this.entity.hasHomePositionCQR()) {
			BlockPos pos = this.entity.getHomePositionCQR();
			return this.entity.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) > 16.0D;
		}
		return false;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return this.entity.hasPath();
	}

	@Override
	public void startExecuting() {
		BlockPos pos = this.entity.getHomePositionCQR();
		this.entity.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), 1.0D);
	}

	@Override
	public void resetTask() {
		this.entity.getNavigator().clearPath();
	}

}
