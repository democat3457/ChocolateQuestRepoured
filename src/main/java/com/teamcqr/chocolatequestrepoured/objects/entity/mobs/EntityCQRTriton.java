package com.teamcqr.chocolatequestrepoured.objects.entity.mobs;

import com.teamcqr.chocolatequestrepoured.factions.EDefaultFaction;
import com.teamcqr.chocolatequestrepoured.init.ModLoottables;
import com.teamcqr.chocolatequestrepoured.objects.entity.EBaseHealths;
import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityCQRTriton extends AbstractEntityCQR {

	public EntityCQRTriton(World worldIn, EntityType<? extends EntityCQRTriton> type) {
		super(worldIn, type);
	}

	@Override
	public float getBaseHealth() {
		return EBaseHealths.TRITON.getValue();
	}

	@Override
	public EDefaultFaction getDefaultFaction() {
		return EDefaultFaction.TRITONS;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return ModLoottables.ENTITIES_TRITON;
	}

	@Override
	public boolean isSitting() {
		return false;
	}

	@Override
	public int getTextureCount() {
		return 2;
	}

	@Override
	public boolean canMountEntity() {
		return false;
	}
	
	@Override
	protected float getWaterSlowDown() {
		return 0.0F;
	}
	
	@Override
	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.WATER;
	}

}
