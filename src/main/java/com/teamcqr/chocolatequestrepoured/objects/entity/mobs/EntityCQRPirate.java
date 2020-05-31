package com.teamcqr.chocolatequestrepoured.objects.entity.mobs;

import com.teamcqr.chocolatequestrepoured.factions.EDefaultFaction;
import com.teamcqr.chocolatequestrepoured.init.ModLoottables;
import com.teamcqr.chocolatequestrepoured.objects.entity.EBaseHealths;
import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;

import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityCQRPirate extends AbstractEntityCQR {

	public EntityCQRPirate(World worldIn, EntityType<? extends EntityCQRPirate> type) {
		super(worldIn, type);
	}

	@Override
	public float getBaseHealth() {
		return EBaseHealths.PIRATE.getValue();
	}

	@Override
	public EDefaultFaction getDefaultFaction() {
		return EDefaultFaction.PIRATE;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return ModLoottables.ENTITIES_PIRATE;
	}

	@Override
	public int getTextureCount() {
		return 3;
	}

}
