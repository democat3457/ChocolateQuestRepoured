package com.teamcqr.chocolatequestrepoured.objects.entity.boss;

import java.util.ArrayList;
import java.util.List;

import com.teamcqr.chocolatequestrepoured.factions.CQRFaction;
import com.teamcqr.chocolatequestrepoured.factions.EDefaultFaction;
import com.teamcqr.chocolatequestrepoured.init.ModBlocks;
import com.teamcqr.chocolatequestrepoured.init.ModLoottables;
import com.teamcqr.chocolatequestrepoured.objects.entity.EBaseHealths;
import com.teamcqr.chocolatequestrepoured.objects.entity.ai.spells.EntityAIArmorSpell;
import com.teamcqr.chocolatequestrepoured.objects.entity.ai.spells.EntityAIFangAttack;
import com.teamcqr.chocolatequestrepoured.objects.entity.ai.spells.EntityAIShootPoisonProjectiles;
import com.teamcqr.chocolatequestrepoured.objects.entity.ai.spells.EntityAISummonMinionSpell;
import com.teamcqr.chocolatequestrepoured.objects.entity.bases.ISummoner;
import com.teamcqr.chocolatequestrepoured.objects.entity.misc.EntitySummoningCircle.ECircleTexture;
import com.teamcqr.chocolatequestrepoured.util.Reference;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityCQRLich extends AbstractEntityCQRMageBase implements ISummoner {

	protected List<Entity> summonedMinions = new ArrayList<>();
	protected BlockPos currentPhylacteryPosition = null;

	public EntityCQRLich(World worldIn, EntityType<? extends EntityCQRLich> type) {
		super(worldIn, type);
	}

	@Override
	public void livingTick() {
		super.livingTick();
		List<Entity> tmp = new ArrayList<>();
		for (Entity ent : this.summonedMinions) {
			if (ent == null || !ent.isAlive()) {
				tmp.add(ent);
			}
		}
		for (Entity e : tmp) {
			this.summonedMinions.remove(e);
		}
		// Phylactery
		if (this.currentPhylacteryPosition != null) {
			if (this.world.getBlockState(this.currentPhylacteryPosition).getBlock() == ModBlocks.PHYLACTERY) {
				this.setMagicArmorActive(true);
			} else {
				this.currentPhylacteryPosition = null;
				this.setMagicArmorActive(false);
			}
		}
	}

	public void setCurrentPhylacteryBlock(BlockPos pos) {
		this.setMagicArmorActive(true);
		this.currentPhylacteryPosition = pos;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.spellHandler.addSpell(0, new EntityAIArmorSpell(this, 400, 40));
		this.spellHandler.addSpell(1, new EntityAISummonMinionSpell(this, 400, 40, new ResourceLocation(Reference.MODID, "zombie"), ECircleTexture.ZOMBIE, true, 12, 4, new Vec3d(0,0,0)));
		this.spellHandler.addSpell(2, new EntityAIFangAttack(this, 400, 40));
		this.spellHandler.addSpell(3, new EntityAIShootPoisonProjectiles(this, 400, 40));
	}

	@Override
	public void onDeath(DamageSource cause) {
		// Kill minions
		for (Entity e : this.summonedMinions) {
			if (e != null && e.isAlive()) {
				if (e instanceof LivingEntity) {
					((LivingEntity) e).onDeath(cause);
				}
				if (e != null) {
					e.remove();
				}
			}
		}
		this.summonedMinions.clear();

		super.onDeath(cause);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return ModLoottables.ENTITIES_LICH;
	}

	@Override
	public float getBaseHealth() {
		return EBaseHealths.LICH.getValue();
	}

	@Override
	public EDefaultFaction getDefaultFaction() {
		return EDefaultFaction.UNDEAD;
	}

	@Override
	public CQRFaction getSummonerFaction() {
		return this.getFaction();
	}

	@Override
	public List<Entity> getSummonedEntities() {
		return this.summonedMinions;
	}

	@Override
	public LivingEntity getSummoner() {
		return this;
	}

	@Override
	public void addSummonedEntityToList(Entity summoned) {
		this.summonedMinions.add(summoned);
	}

	@Override
	protected void updateCooldownForMagicArmor() {
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		if (this.currentPhylacteryPosition != null) {
			compound.put("currentPhylactery", NBTUtil.writeBlockPos(this.currentPhylacteryPosition));
		}
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		if (compound.contains("currentPhylactery")) {
			this.currentPhylacteryPosition = NBTUtil.readBlockPos(compound.getCompound("currentPhylactery"));
		}
	}

	@Override
	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.UNDEAD;
	}
	
	public boolean hasPhylactery() {
		return (this.currentPhylacteryPosition != null &&
			(this.world.getBlockState(this.currentPhylacteryPosition).getBlock() == ModBlocks.PHYLACTERY)); 
	}

}
