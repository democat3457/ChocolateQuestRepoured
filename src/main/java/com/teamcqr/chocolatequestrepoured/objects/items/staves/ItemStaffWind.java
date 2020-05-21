package com.teamcqr.chocolatequestrepoured.objects.items.staves;

import com.teamcqr.chocolatequestrepoured.init.ModSounds;
import com.teamcqr.chocolatequestrepoured.util.IRangedWeapon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class ItemStaffWind extends Item implements IRangedWeapon {

	public ItemStaffWind() {
		this.setMaxDamage(2048);
		this.setMaxStackSize(1);
	}

	@Override
	public void shoot(World worldIn, LivingEntity shooter, Entity target, Hand handIn) {
		// TODO Auto-generated method stub

	}

	@Override
	public SoundEvent getShootSound() {
		return ModSounds.MAGIC;
	}

}