package com.teamcqr.chocolatequestrepoured.objects.entity.mobs;

import com.teamcqr.chocolatequestrepoured.factions.EDefaultFaction;
import com.teamcqr.chocolatequestrepoured.init.ModLoottables;
import com.teamcqr.chocolatequestrepoured.objects.entity.EBaseHealths;
import com.teamcqr.chocolatequestrepoured.objects.entity.ai.EntityAISpellWalker;
import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class EntityCQRWalker extends AbstractEntityCQR {

	public EntityCQRWalker(World worldIn, EntityType<? extends EntityCQRWalker> type) {
		super(worldIn, type);
	}

	@Override
	public float getBaseHealth() {
		return EBaseHealths.WALKER.getValue();
	}

	@Override
	public EDefaultFaction getDefaultFaction() {
		return EDefaultFaction.WALKERS;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_ENDERMAN_AMBIENT;
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
	protected ResourceLocation getLootTable() {
		return ModLoottables.ENTITIES_WALKER;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(source.getImmediateSource() != null && source.getImmediateSource() instanceof SpectralArrowEntity) {
			amount *= 2;
		}
		return super.attackEntityFrom(source, amount);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.spellHandler.addSpell(0, new EntityAISpellWalker(this));
	}

}
