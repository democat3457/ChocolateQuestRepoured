package com.teamcqr.chocolatequestrepoured.objects.entity.ai;

import com.teamcqr.chocolatequestrepoured.objects.entity.EntityEquipmentExtraSlot;
import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;
import com.teamcqr.chocolatequestrepoured.util.IRangedWeapon;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class EntityAIAttackRanged extends EntityAIAttack {

	public EntityAIAttackRanged(AbstractEntityCQR entity) {
		super(entity);
	}

	@Override
	public boolean shouldExecute() {
		return this.isRangedWeapon(this.entity.getHeldItemMainhand().getItem()) && super.shouldExecute();
	}

	@Override
	public boolean shouldContinueExecuting() {
		return this.isRangedWeapon(this.entity.getHeldItemMainhand().getItem()) && super.shouldContinueExecuting();
	}

	@Override
	public void startExecuting() {
		LivingEntity attackTarget = this.entity.getAttackTarget();

		if (this.entity.getDistance(attackTarget) < 28.0D) {
			this.entity.setActiveHand(Hand.MAIN_HAND);
			this.entity.isSwingInProgress = true;
		} else {
			this.updatePath(attackTarget);
		}
	}

	@Override
	public void resetTask() {
		super.resetTask();
		this.entity.isSwingInProgress = false;
	}

	@Override
	public void tick() {
		LivingEntity attackTarget = this.entity.getAttackTarget();

		if (attackTarget != null) {
			this.entity.getLookController().setLookPositionWithEntity(attackTarget, 12.0F, 12.0F);

			double distance = this.entity.getDistance(attackTarget);
			if (distance < 28.0D || (!this.entity.hasPath() && distance < 32.0D)) {
				this.checkAndPerformAttack(this.entity.getAttackTarget());
				this.entity.getNavigator().clearPath();
				this.entity.setActiveHand(Hand.MAIN_HAND);
				this.entity.isSwingInProgress = true;
			} else {
				this.updatePath(attackTarget);
				this.entity.resetActiveHand();
				this.entity.isSwingInProgress = false;
			}
		}
	}

	@Override
	protected void checkAndPerformAttack(LivingEntity attackTarget) {
		if (this.attackTick <= 0 && this.entity.getDistance(attackTarget) <= 32.0D) {
			ItemStack stack = this.entity.getHeldItemMainhand();
			if (stack.getItem() instanceof BowItem) {
				this.attackTick = 60;
				ItemStack arrowItem = this.entity.getItemStackFromExtraSlot(EntityEquipmentExtraSlot.ARROW);
				if (arrowItem.isEmpty() || !(arrowItem.getItem() instanceof ArrowItem)) {
					arrowItem = new ItemStack(Items.ARROW, 1);
				}
				AbstractArrowEntity arrow = ((ArrowItem) arrowItem.getItem()).createArrow(this.entity.world, arrowItem, this.entity);
				arrowItem.shrink(1);

				double x = attackTarget.getPosX() - this.entity.getPosX();
				double y = attackTarget.getPosY() + (double) attackTarget.getHeight() * 0.5D - arrow.getPosY();
				double z = attackTarget.getPosZ() - this.entity.getPosZ();
				double distance = Math.sqrt(x * x + z * z);
				arrow.shoot(x, y + distance * 0.06D, z, 3.0F, 0.0F);
				double vx = arrow.getMotion().x;
				double vy = arrow.getMotion().y;
				double vz = arrow.getMotion().z;
				vx += this.entity.getMotion().x;
				vz += this.entity.getMotion().z;
				if (!this.entity.onGround) {
					vy += this.entity.getMotion().y;
				}
				arrow.setMotion(new Vec3d(vx,vy,vz));
				this.entity.world.addEntity(arrow);
				this.entity.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
			} else if (stack.getItem() instanceof IRangedWeapon) {
				IRangedWeapon weapon = (IRangedWeapon) stack.getItem();
				this.attackTick = weapon.getCooldown();
				weapon.shoot(this.entity.world, this.entity, attackTarget, Hand.MAIN_HAND);
				this.entity.playSound(weapon.getShootSound(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
			}
		}
	}

	protected boolean isRangedWeapon(Item item) {
		return item instanceof BowItem || item instanceof IRangedWeapon;
	}

}
