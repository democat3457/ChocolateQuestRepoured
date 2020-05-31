package com.teamcqr.chocolatequestrepoured.objects.entity.bases;

import com.teamcqr.chocolatequestrepoured.util.CQRConfig;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;

public abstract class AbstractEntityCQRBoss extends AbstractEntityCQR {

	protected String assignedRegionID = null;

	protected final ServerBossInfo bossInfoServer = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.NOTCHED_10);

	public AbstractEntityCQRBoss(World worldIn, EntityType<? extends AbstractEntityCQR> type) {
		super(worldIn, type);
		this.experienceValue = 50;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount, boolean sentFromPart) {
		int nearbyPlayerCount = 0;
		for (PlayerEntity player : this.world.getPlayers()) {
			if (this.getDistanceSq(player) < 100.0D * 100.0D) {
				nearbyPlayerCount++;
			}
		}
		for (int i = 0; i < nearbyPlayerCount - 1; i++) {
			amount *= 1.0F - CQRConfig.mobs.bossDamageReductionPerPlayer;
		}
		return super.attackEntityFrom(source, amount, sentFromPart);
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		if (compound.contains("assignedRegion")) {
			this.assignedRegionID = compound.getString("assignedRegion");
		}
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		if (this.assignedRegionID != null) {
			compound.putString("assignedRegion", this.assignedRegionID);
		}
	}

	public void assignRegion(String regionID) {
		if (regionID != null) {
			this.assignedRegionID = regionID;
		}
	}

	@Override
	public boolean isNonBoss() {
		return false;
	}

	@Override
	public void livingTick() {
		super.livingTick();

		this.bossInfoServer.setPercent(this.getHealth() / this.getMaxHealth());
	}

	@Override
	public void addTrackingPlayer(ServerPlayerEntity player) {
		super.addTrackingPlayer(player);
		this.bossInfoServer.addPlayer(player);
	}

	@Override
	public void removeTrackingPlayer(ServerPlayerEntity player) {
		super.removeTrackingPlayer(player);
		this.bossInfoServer.removePlayer(player);
	}

	@Override
	public void onDeath(DamageSource cause) {
		super.onDeath(cause);

		// TOOD: Destroy protected region
	}

	@Override
	public void setCustomName(ITextComponent name) {
		super.setCustomName(name);
		this.bossInfoServer.setName(this.getDisplayName());
	}

	@Override
	protected void onDeathUpdate() {
		if (this.usesEnderDragonDeath()) {
			if (this.isSitting()) {
				this.setSitting(false);
			}
			// super.onDeathUpdate();
			++this.deathTicks;
			if (this.deathTicks >= 180 && this.deathTicks <= MAX_DEATH_TICKS) {
				float f = (this.rand.nextFloat() - 0.5F) * 8.0F;
				float f1 = (this.rand.nextFloat() - 0.5F) * 4.0F;
				float f2 = (this.rand.nextFloat() - 0.5F) * 8.0F;
				this.world.addParticle(this.getDeathAnimParticles(), this.getPosX() + (double) f, this.getPosY() + 2.0D + (double) f1, this.getPosZ() + (double) f2, 0.0D, 0.0D, 0.0D);
			}
			this.setNoGravity(true);
			this.move(MoverType.SELF, new Vec3d(0, 10 / MAX_DEATH_TICKS / 3, 0));
			if (this.deathTicks == MAX_DEATH_TICKS && !this.world.isRemote) {
				this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), this.getFinalDeathSound(), SoundCategory.MASTER, 1, 1, false);
				this.remove();
				
				onFinalDeath();
				
				if (this.doesExplodeOnDeath()) {
					this.world.createExplosion(this, this.getPosX(), this.getPosY(), this.getPosZ(), 8.0F, true, Mode.NONE);
				}
			}
		} else {
			super.onDeathUpdate();
		}
	}
	
	protected void onFinalDeath() {
		
	}
	
	protected SoundEvent getFinalDeathSound() {
		return this.getDeathSound();
	}

	protected boolean doesExplodeOnDeath() {
		return false;
	}

	protected boolean usesEnderDragonDeath() {
		return false;
	}

	protected IParticleData getDeathAnimParticles() {
		return ParticleTypes.EXPLOSION;
	}

	@Override
	public boolean canTameEntity() {
		return false;
	}

	@Override
	public boolean canMountEntity() {
		return false;
	}

}
