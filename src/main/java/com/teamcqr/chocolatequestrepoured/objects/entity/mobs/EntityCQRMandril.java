package com.teamcqr.chocolatequestrepoured.objects.entity.mobs;

import com.teamcqr.chocolatequestrepoured.factions.EDefaultFaction;
import com.teamcqr.chocolatequestrepoured.objects.entity.EBaseHealths;
import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityCQRMandril extends AbstractEntityCQR {

	public EntityCQRMandril(World worldIn, EntityType<? extends EntityCQRMandril> type) {
		super(worldIn, type);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(9, new LeapAtTargetGoal(this, 0.6F));
	}

	@Override
	protected ResourceLocation getLootTable() {
		return null;
	}

	@Override
	public float getBaseHealth() {
		return EBaseHealths.MANDRILS.getValue();
	}

	@Override
	public EDefaultFaction getDefaultFaction() {
		return EDefaultFaction.BEASTS;
	}

	@Override
	public boolean canMountEntity() {
		return false;
	}

	@Override
	public float getDefaultWidth() {
		return 0.6F;
	}

	@Override
	public float getDefaultHeight() {
		return 1.9F;
	}

}
