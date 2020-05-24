package com.teamcqr.chocolatequestrepoured.objects.entity.ai;

import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;
import com.teamcqr.chocolatequestrepoured.util.IRangedWeapon;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class EntityAIAttackRangedStrafing extends EntityAIAttack {

    private final double moveSpeedAmp;
    private int attackCooldown;
    private final float maxAttackDistance;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;
	
	public EntityAIAttackRangedStrafing(AbstractEntityCQR entity, double moveSpeedAmp, int attackCooldown, float maxAttackDistance) {
		super(entity);
		this.moveSpeedAmp = moveSpeedAmp;
        this.attackCooldown = attackCooldown;
        this.maxAttackDistance = maxAttackDistance * maxAttackDistance;
	}
	
	public void setAttackCooldown(int value)
    {
        this.attackCooldown = value;
    }
	
	public boolean shouldExecute()
    {
        return super.shouldExecute() && this.entity.getAttackTarget() == null ? false : this.isWeaponEquipped();
    }

    protected boolean isWeaponEquipped()
    {
    	Item item = entity.getHeldItemMainhand().getItem();
        return item instanceof BowItem || item instanceof IRangedWeapon;
    }
    
    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return (this.shouldExecute() || !this.entity.getNavigator().noPath()) && this.isWeaponEquipped();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        super.startExecuting();
        this.entity.isSwingInProgress = true;
		this.entity.setActiveHand(Hand.MAIN_HAND);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        super.resetTask();
        this.seeTime = 0;
        this.attackTime = -1;
        this.entity.resetActiveHand();
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick()
    {
        LivingEntity entitylivingbase = this.entity.getAttackTarget();

        if (entitylivingbase != null)
        {
            double d0 = this.entity.getDistanceSq(entitylivingbase.getPosX(), entitylivingbase.getBoundingBox().minY, entitylivingbase.getPosZ());
            boolean flag = this.entity.getEntitySenses().canSee(entitylivingbase);
            boolean flag1 = this.seeTime > 0;

            attackTime--;
            
            if (flag != flag1)
            {
                this.seeTime = 0;
            }

            if (flag)
            {
                ++this.seeTime;
            }
            else
            {
                --this.seeTime;
            }

            if (d0 <= (double)this.maxAttackDistance && this.seeTime >= 20)
            {
                this.entity.getNavigator().clearPath();
                ++this.strafingTime;
            }
            else
            {
                this.entity.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.moveSpeedAmp);
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20)
            {
                if ((double)this.entity.getRNG().nextFloat() < 0.3D)
                {
                    this.strafingClockwise = !this.strafingClockwise;
                }

                if ((double)this.entity.getRNG().nextFloat() < 0.3D)
                {
                    this.strafingBackwards = !this.strafingBackwards;
                }

                this.strafingTime = 0;
            }

            if (this.strafingTime > -1)
            {
                if (d0 > (double)(this.maxAttackDistance * 0.75F))
                {
                    this.strafingBackwards = false;
                }
                else if (d0 < (double)(this.maxAttackDistance * 0.25F))
                {
                    this.strafingBackwards = true;
                }

                this.entity.getMoveHelper().strafe((float) (this.strafingBackwards ? -0.5F * moveSpeedAmp : 0.5F* moveSpeedAmp) , this.strafingClockwise ? 0.5F : -0.5F);
                this.entity.faceEntity(entitylivingbase, 30.0F, 30.0F);
            }
            else
            {
                this.entity.getLookController().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
            }

           // if (this.entity.isHandActive())
           // {
                if (!flag && this.seeTime < -60)
                {
                    this.entity.resetActiveHand();
                }
                else if (flag)
                {
                    if (attackTime <= 0)
                    {
                        this.entity.resetActiveHand();
                        performAttack(entitylivingbase);
                        this.attackTime = this.attackCooldown;
                    }
                }
            // }
           // else if (--this.attackTime <= 0 && this.seeTime >= -60)
           // {
             //   this.entity.setActiveHand(Hand.MAIN_HAND);
           // }
        }
    }

	private void performAttack(LivingEntity attackTarget) {
		ItemStack stack = this.entity.getHeldItemMainhand();
		this.entity.isSwingInProgress = false;
		if (stack.getItem() instanceof BowItem) {
			ArrowEntity arrow = new ArrowEntity(this.entity.world, this.entity);
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
			weapon.shoot(this.entity.world, this.entity, attackTarget, Hand.MAIN_HAND);
			attackCooldown = weapon.getCooldown();
			this.entity.playSound(weapon.getShootSound(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
		}
	}

}
