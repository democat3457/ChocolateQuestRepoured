package com.teamcqr.chocolatequestrepoured.objects.entity.mobs;

import com.teamcqr.chocolatequestrepoured.factions.EDefaultFaction;
import com.teamcqr.chocolatequestrepoured.init.ModLoottables;
import com.teamcqr.chocolatequestrepoured.objects.entity.EBaseHealths;
import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityCQRSpectre extends AbstractEntityCQR {

	public EntityCQRSpectre(World worldIn, EntityType<? extends EntityCQRSpectre> type) {
		super(worldIn, type);
	}

	@Override
	public float getBaseHealth() {
		return EBaseHealths.SPECTRE.getValue();
	}

	@Override
	public EDefaultFaction getDefaultFaction() {
		return EDefaultFaction.UNDEAD;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return ModLoottables.ENTITIES_SPECTRE;
	}

	@Override
	public int getTextureCount() {
		return 3;
	}

	@Override
	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.UNDEAD;
	}

}
