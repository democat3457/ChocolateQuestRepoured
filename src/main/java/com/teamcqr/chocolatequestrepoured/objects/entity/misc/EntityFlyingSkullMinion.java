package com.teamcqr.chocolatequestrepoured.objects.entity.misc;

import java.util.UUID;

import com.teamcqr.chocolatequestrepoured.objects.entity.ai.target.TargetUtil;
import com.teamcqr.chocolatequestrepoured.util.VectorUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityFlyingSkullMinion extends FlyingEntity {

	protected Entity summoner;
	protected Entity target;
	protected boolean attacking = false;
	protected boolean isLeftSkull = false;
	protected Vec3d direction = null;

	public EntityFlyingSkullMinion(World worldIn, EntityType<? extends EntityFlyingSkullMinion> type) {
		super(type, worldIn);
		this.setSize(0.5F, 0.5F);
		this.setNoGravity(true);
		this.setHealth(1F);
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1F);
		this.navigator = new FlyingPathNavigator(this, worldIn);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(source.isExplosion()) {
			return false;
		}
		if (source.getImmediateSource() instanceof SpectralArrowEntity) {
			Entity summonerTmp = this.summoner;
			this.summoner = source.getTrueSource();
			this.target = summonerTmp;
			this.explode(10F);
			this.remove();
			return true;
		}
		if (this.getRNG().nextInt(10) == 9) {
			Entity summonerTmp = this.summoner;
			this.summoner = source.getTrueSource();
			this.target = summonerTmp;
			this.remove();
			return true;
		}
		this.explode(1.25F);
		this.remove();
		return true;
	}

	@Override
	public PathNavigator getNavigator() {
		return this.navigator;
	}

	public void setSummoner(Entity ent) {
		this.summoner = ent;
	}

	@Override
	public void livingTick() {
		super.livingTick();
		// If we hit a wall we explode
		if (this.isEntityInsideOpaqueBlock()) {
			this.explode(1.25F);
			this.remove();
		}
		if (this.attacking) {
			if (this.target != null && this.target.isAlive()) {
				this.updateDirection();
			}
			Vec3d v = this.direction;
			v = v.normalize();
			//this.setVelocity(v.x * 0.4F, v.y * 0.25F, v.z * 0.4F);
			/*this.motionX = v.x * 0.4D;
			this.motionY = v.y * 0.25D;
			this.motionZ = v.z * 0.4D;*/
			this.setMotion(v.x * 0.4D, v.y * 0.25D, v.z * 0.4D);
			this.velocityChanged = true;
			if (this.target != null && this.target.isAlive()) {
				this.getLookController().setLookPositionWithEntity(this.target, 30, 30);
			}

		} else if (this.summoner != null) {
			Vec3d v = this.summoner.getLookVec();
			v = new Vec3d(v.x, 2.25D, v.z);
			v = v.normalize();
			v = v.scale(2.5D);
			v = VectorUtil.rotateVectorAroundY(v, this.isLeftSkull ? 270 : 90);
			Vec3d targetPos = this.summoner.getPositionVector().add(v);
			this.getLookController().setLookPositionWithEntity(this.summoner, 30, 30);
			if (this.getDistanceSq(targetPos.x, targetPos.y, targetPos.z) > 1) {
				Vec3d velo = targetPos.subtract(this.getPositionVector());
				velo = velo.normalize();
				velo = velo.scale(0.2);
				//this.setVelocity(velo.x, velo.y * 1.5, velo.z);
				/*this.motionX = velo.x;
				this.motionY = velo.y * 2.5D;
				this.motionZ = velo.z;*/
				this.setMotion(velo.x, velo.y * 2.5, velo.z);
				this.velocityChanged = true;
			}
		}
	}

	@Override
	protected void collideWithEntity(Entity entityIn) {
		if (entityIn != this.summoner) {
			super.collideWithEntity(entityIn);
			this.explode(0.75F);
		}
	}

	@Override
	public void onDeath(DamageSource cause) {
		super.onDeath(cause);
		this.explode(1.25F);
	}

	private void explode(float strengthMultiplier) {
		if (this.world != null) {
			if (this.summoner != null && this.summoner.isAlive() && this.isAlive()) {
				this.world.createExplosion(this.summoner, this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ(), 0.5F * strengthMultiplier, true, Explosion.Mode.DESTROY);
			}
			this.world.addParticle(ParticleTypes.FLAME, this.getPosition().getX(), this.getPosition().getY() + 0.02, this.getPosition().getZ(), 0.5F, 0.0F, 0.5F, 1);
			this.world.addParticle(ParticleTypes.FLAME, this.getPosition().getX(), this.getPosition().getY() + 0.02, this.getPosition().getZ(), 0.5F, 0.0F, -0.5F, 1);
			this.world.addParticle(ParticleTypes.FLAME, this.getPosition().getX(), this.getPosition().getY() + 0.02, this.getPosition().getZ(), -0.5F, 0.0F, -0.5F, 1);
			this.world.addParticle(ParticleTypes.FLAME, this.getPosition().getX(), this.getPosition().getY() + 0.02, this.getPosition().getZ(), -0.5F, 0.0F, 0.5F, 1);
		}
	}

	public void setTarget(Entity target) {
		this.target = target;
		this.updateDirection();
	}

	public void startAttacking() {
		this.attacking = true;
	}

	private void updateDirection() {
		this.direction = this.target.getPositionVector().subtract(this.getPositionVector());
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putBoolean("attacking", this.attacking);
		compound.putDouble("vX", this.direction == null ? 0D : this.direction.x);
		compound.putDouble("vY", this.direction == null ? 0D : this.direction.y);
		compound.putDouble("vZ", this.direction == null ? 0D : this.direction.z);
		if (this.summoner != null && this.summoner.isAlive()) {
			compound.put("summonerID", NBTUtil.writeUniqueId(this.summoner.getUniqueID()));
		}
		if (this.target != null && this.target.isAlive()) {
			compound.put("targetID", net.minecraft.nbt.NBTUtil.writeUniqueId(this.target.getUniqueID()));
		}
	}
	
	public boolean isAttacking() {
		return this.attacking;
	}

	public boolean hasTarget() {
		return this.target != null && this.target.isAlive();
	}

	public void setSide(boolean left) {
		this.isLeftSkull = left;
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.attacking = compound.getBoolean("attacking");
		double x, y, z;
		x = compound.getDouble("vX");
		y = compound.getDouble("vY");
		z = compound.getDouble("vZ");
		this.direction = new Vec3d(x, y, z);
		if (compound.contains("targetID")) {
			UUID id = net.minecraft.nbt.NBTUtil.readUniqueId(compound.getCompound("targetID"));
			if (this.world != null) {
				for (Entity ent : this.world.getEntitiesInAABBexcluding(this, new AxisAlignedBB(this.getPosition().add(10, 10, 10), this.getPosition().add(-10, -10, -10)), TargetUtil.PREDICATE_LIVING)) {
					if (ent.getUniqueID().equals(id)) {
						this.target = ent;
					}
				}
			}
		}
		if (compound.contains("summonerID")) {
			UUID id = net.minecraft.nbt.NBTUtil.readUniqueId(compound.getCompound("summonerID"));
			if (this.world != null) {
				for (Entity ent : this.world.getEntitiesInAABBexcluding(this, new AxisAlignedBB(this.getPosition().add(10, 10, 10), this.getPosition().add(-10, -10, -10)), TargetUtil.PREDICATE_LIVING)) {
					if (ent.getUniqueID().equals(id)) {
						this.summoner = ent;
					}
				}
			}
		}
	}

}
