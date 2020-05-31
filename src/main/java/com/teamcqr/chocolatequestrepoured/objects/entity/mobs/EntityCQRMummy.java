package com.teamcqr.chocolatequestrepoured.objects.entity.mobs;

import com.teamcqr.chocolatequestrepoured.factions.EDefaultFaction;
import com.teamcqr.chocolatequestrepoured.init.ModLoottables;
import com.teamcqr.chocolatequestrepoured.objects.entity.EBaseHealths;
import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class EntityCQRMummy extends AbstractEntityCQR {

	public EntityCQRMummy(World worldIn, EntityType<? extends EntityCQRMummy> type) {
		super(worldIn, type);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		boolean flag = super.attackEntityAsMob(entityIn);

		if (flag && this.getHeldItemMainhand().isEmpty() && entityIn instanceof LivingEntity) {
			int i = this.world.getDifficulty().getId();
			((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.HUNGER, 140 * i));
		}

		return flag;
	}

	@Override
	public float getBaseHealth() {
		return EBaseHealths.MUMMY.getValue();
	}

	@Override
	public EDefaultFaction getDefaultFaction() {
		return EDefaultFaction.UNDEAD;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_HUSK_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_HUSK_DEATH;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_HUSK_AMBIENT;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return ModLoottables.ENTITIES_MUMMY;
	}

	@Override
	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.UNDEAD;
	}

}
