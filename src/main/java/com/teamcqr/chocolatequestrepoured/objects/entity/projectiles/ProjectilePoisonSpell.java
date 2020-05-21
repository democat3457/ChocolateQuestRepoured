package com.teamcqr.chocolatequestrepoured.objects.entity.projectiles;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class ProjectilePoisonSpell extends ProjectileSpiderBall {
	private LivingEntity shooter;

	public ProjectilePoisonSpell(World worldIn) {
		super(worldIn);
	}

	public ProjectilePoisonSpell(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

	public ProjectilePoisonSpell(World worldIn, LivingEntity shooter) {
		super(worldIn, shooter);
		this.shooter = shooter;
		this.isImmuneToFire = false;
		this.damage = 5.0F;
	}

	@Override
	public boolean hasNoGravity() {
		return false;
	}

	@Override
	protected void onUpdateInAir() {
	}

	public LivingEntity getShooter() {
		return this.shooter;
	}
}