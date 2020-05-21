package com.teamcqr.chocolatequestrepoured.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public interface IRangedWeapon {

	public default int getCooldown() {
		return 60;
	}

	public SoundEvent getShootSound();

	public void shoot(World world, LivingEntity shooter, Entity target, Hand hand);

}
