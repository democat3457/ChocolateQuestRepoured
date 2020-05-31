package com.teamcqr.chocolatequestrepoured.objects.entity.mobs;

import com.teamcqr.chocolatequestrepoured.factions.EDefaultFaction;
import com.teamcqr.chocolatequestrepoured.objects.entity.EBaseHealths;
import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTables;

public class EntityCQRDummy extends AbstractEntityCQR {

	public EntityCQRDummy(World worldIn, EntityType<? extends EntityCQRDummy> type) {
		super(worldIn, type);
	}

	@Override
	public float getBaseHealth() {
		return EBaseHealths.DUMMY.getValue();
	}

	@Override
	public EDefaultFaction getDefaultFaction() {
		return EDefaultFaction.ALL_ALLY;
	}

	@Override
	protected void registerGoals() {

	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	protected void collideWithEntity(Entity entityIn) {

	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTables.EMPTY;
	}

}
