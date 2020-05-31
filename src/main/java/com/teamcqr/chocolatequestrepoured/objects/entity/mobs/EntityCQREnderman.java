package com.teamcqr.chocolatequestrepoured.objects.entity.mobs;

import com.teamcqr.chocolatequestrepoured.factions.EDefaultFaction;
import com.teamcqr.chocolatequestrepoured.init.ModLoottables;
import com.teamcqr.chocolatequestrepoured.objects.entity.EBaseHealths;
import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class EntityCQREnderman extends AbstractEntityCQR {

	public EntityCQREnderman(World worldIn, EntityType<? extends EntityCQREnderman> type) {
		super(worldIn, type);
		this.stepHeight = 1.0F;
		this.setPathPriority(PathNodeType.WATER, -1.0F);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (super.attackEntityFrom(source, amount)) {
			if (source instanceof IndirectEntityDamageSource) {
				for (int i = 0; i < 64; ++i) {
					if (this.teleportRandomly()) {
						return true;
					}
				}

				return false;
			}
		}
		return super.attackEntityFrom(source, amount);
	}

	protected boolean teleportRandomly() {
		double d0 = this.getPosX() + (this.rand.nextDouble() - 0.5D) * 64.0D;
		double d1 = this.getPosY() + (double) (this.rand.nextInt(64) - 32);
		double d2 = this.getPosZ() + (this.rand.nextDouble() - 0.5D) * 64.0D;
		return this.teleportTo(d0, d1, d2);
	}

	private boolean teleportTo(double x, double y, double z) {
		EnderTeleportEvent event = new EnderTeleportEvent(this, x, y, z, 0);
		if (MinecraftForge.EVENT_BUS.post(event)) {
			return false;
		}
		boolean flag = this.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);

		if (flag) {
			this.world.playSound((PlayerEntity) null, this.prevPosX, this.prevPosY, this.prevPosZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
			this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
		}

		return flag;
	}

	@Override
	public float getBaseHealth() {
		return EBaseHealths.ENDERMAN.getValue();
	}

	@Override
	public EDefaultFaction getDefaultFaction() {
		return EDefaultFaction.ENDERMEN;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return /* this.isScreaming() ? SoundEvents.ENTITY_ENDERMEN_SCREAM : */ SoundEvents.ENTITY_ENDERMAN_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_ENDERMAN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ENDERMAN_DEATH;
	}

	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
		this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0D);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return ModLoottables.ENTITIES_ENDERMAN;
	}

	@Override
	public boolean isSitting() {
		return false;
	}

	@Override
	public boolean canMountEntity() {
		return false;
	}

	/*@Override
	public float getEyeHeight() {
		return this.height * 0.875F;
	}*/

	@Override
	public float getDefaultWidth() {
		return 0.6F;
	}

	@Override
	public float getDefaultHeight() {
		return 2.9F;
	}

}
