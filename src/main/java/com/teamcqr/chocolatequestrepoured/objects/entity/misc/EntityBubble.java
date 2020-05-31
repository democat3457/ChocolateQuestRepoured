package com.teamcqr.chocolatequestrepoured.objects.entity.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityBubble extends LivingEntity {

	private static final int FLY_TIME_MAX = 160;

	private int flyTicks = 0;

	public EntityBubble(World worldIn, EntityType<? extends EntityBubble> type) {
		super(type, worldIn);
		this.isImmuneToFire = true;
		this.setNoGravity(true);
	}

	@Override
	protected void registerData() {

	}

	@Override
	public void tick() {
		super.tick();

		if (!this.world.isRemote) {
			if (!this.isBeingRidden() || this.isInLava() || (this.collidedVertically && !this.onGround) || this.flyTicks > FLY_TIME_MAX) {
				if (this.isBeingRidden()) {
					Entity entity = this.getPassengers().get(0);
					entity.dismountRidingEntity();
					entity.setPositionAndUpdate(this.getPosX(), this.getPosY() + 0.5D * (double) (this.getHeight() - entity.getHeight()), this.getPosZ());
				}
				this.remove();
				return;
			}

			this.flyTicks++;
		}

		this.move(MoverType.SELF, new Vec3d(0.0D, 0.05D, 0.0D));
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		this.flyTicks += 40;
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		this.flyTicks = compound.getInt("flyTicks");
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		compound.putInt("flyTicks", this.flyTicks);
	}

	@Override
	public double getMountedYOffset() {
		if (this.isBeingRidden()) {
			Entity entity = this.getPassengers().get(0);
			return 0.5D * (double) (this.getHeight() - entity.getHeight()) - entity.getYOffset();
		}
		return 0.0D;
	}

	@Override
	protected void addPassenger(Entity passenger) {
		super.addPassenger(passenger);
		float size = Math.max(passenger.getWidth(), passenger.getWidth()) + 0.1F;
		this.setSize(size, size);
	}

	@Override
	protected boolean canFitPassenger(Entity passenger) {
		return !this.isBeingRidden();
	}

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	@Override
	public boolean shouldDismountInWater(Entity rider) {
		return false;
	}

	@Override
	public Iterable<ItemStack> getArmorInventoryList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack getItemStackFromSlot(EquipmentSlotType slotIn) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {
	}

	@Override
	public HandSide getPrimaryHand() {
		return HandSide.LEFT;
	}

}
