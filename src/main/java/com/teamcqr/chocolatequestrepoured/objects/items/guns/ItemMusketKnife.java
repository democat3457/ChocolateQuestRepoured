package com.teamcqr.chocolatequestrepoured.objects.items.guns;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.Multimap;
import com.teamcqr.chocolatequestrepoured.init.ModItems;
import com.teamcqr.chocolatequestrepoured.init.ModSounds;
import com.teamcqr.chocolatequestrepoured.objects.entity.projectiles.ProjectileBullet;

import net.java.games.input.Keyboard;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.relauncher.Side;

public class ItemMusketKnife extends ItemSword {

	public ItemMusketKnife(ToolMaterial material) {
		super(material);
		this.setMaxDamage(300);
		this.setMaxStackSize(1);
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		Multimap<String, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);
		this.replaceModifier(modifiers, SharedMonsterAttributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER, -0.8F);
		return modifiers;
	}

	protected void replaceModifier(Multimap<String, AttributeModifier> modifierMultimap, IAttribute attribute, UUID id, double value) {
		Collection<AttributeModifier> modifiers = modifierMultimap.get(attribute.getName());
		Optional<AttributeModifier> modifierOptional = modifiers.stream().filter(attributeModifier -> attributeModifier.getID().equals(id)).findFirst();

		if (modifierOptional.isPresent()) {
			AttributeModifier modifier = modifierOptional.get();
			modifiers.remove(modifier);
			modifiers.add(new AttributeModifier(modifier.getID(), modifier.getName(), modifier.getAmount() + value, modifier.getOperation()));
		}
	}

	/*
	 * @Override
	 * public int getMaxItemUseDuration(ItemStack stack) {
	 * return 72000;
	 * }
	 * 
	 * @Override
	 * public EnumAction getItemUseAction(ItemStack stack) {
	 * return EnumAction.NONE;
	 * }
	 */

	@Override
	@OnlyIn(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TextFormatting.BLUE + "7.5 " + I18n.format("description.bullet_damage.name"));
		tooltip.add(TextFormatting.RED + "-60 " + I18n.format("description.fire_rate.name"));
		tooltip.add(TextFormatting.RED + "-10" + "% " + I18n.format("description.accuracy.name"));
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			tooltip.add(TextFormatting.BLUE + I18n.format("description.gun.name"));
		} else {
			tooltip.add(TextFormatting.BLUE + I18n.format("description.click_shift.name"));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		boolean flag = !this.findAmmo(playerIn).isEmpty();

		if (!playerIn.capabilities.isCreativeMode && !flag && this.getBulletStack(stack, playerIn) == ItemStack.EMPTY) {
			if (flag) {
				this.shoot(stack, worldIn, playerIn);
			}
			return flag ? new ActionResult(EnumActionResult.PASS, stack) : new ActionResult(EnumActionResult.FAIL, stack);
		}

		else {
			this.shoot(stack, worldIn, playerIn);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
	}

	public void shoot(ItemStack stack, World worldIn, PlayerEntity player) {
		boolean flag = player.capabilities.isCreativeMode;
		ItemStack itemstack = this.findAmmo(player);

		if (!itemstack.isEmpty() || flag) {
			if (!worldIn.isRemote) {
				if (flag && itemstack.isEmpty()) {
					ProjectileBullet bulletE = new ProjectileBullet(worldIn, player, 1);
					bulletE.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 3.5F, 2F);
					player.getCooldownTracker().setCooldown(stack.getItem(), 30);
					worldIn.spawnEntity(bulletE);
				} else {
					ProjectileBullet bulletE = new ProjectileBullet(worldIn, player, this.getBulletType(itemstack));
					bulletE.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 3.5F, 2F);
					player.getCooldownTracker().setCooldown(stack.getItem(), 30);
					worldIn.spawnEntity(bulletE);
					stack.damageItem(1, player);
				}
			}

			worldIn.playSound(player.posX, player.posY + player.getEyeHeight(), player.posZ, ModSounds.GUN_SHOOT, SoundCategory.MASTER, 1.0F, 0.9F + itemRand.nextFloat() * 0.2F, false);
			player.rotationPitch -= worldIn.rand.nextFloat() * 10;

			if (!flag) {
				itemstack.shrink(1);

				if (itemstack.isEmpty()) {
					player.inventory.deleteStack(itemstack);
				}
			}
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!worldIn.isRemote) {
			if (entityIn instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) entityIn;

				if (player.getHeldItemMainhand() == stack) {
					if (!player.getHeldItemOffhand().isEmpty()) {
						if (!player.inventory.addItemStackToInventory(player.getHeldItemOffhand())) {
							player.entityDropItem(player.getHeldItemOffhand(), 0F);
						}

						if (!player.capabilities.isCreativeMode) {
							player.setItemStackToSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
						}
					}
				}
			}
		}
	}

	/*
	 * @Override
	 * public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
	 * if (entityLiving instanceof PlayerEntity) {
	 * PlayerEntity player = (PlayerEntity) entityLiving;
	 * boolean flag = player.capabilities.isCreativeMode;
	 * ItemStack itemstack = findAmmo(player);
	 * 
	 * if (!itemstack.isEmpty() || flag) {
	 * if (!worldIn.isRemote) {
	 * if (flag && itemstack.isEmpty()) {
	 * ProjectileBullet bulletE = new ProjectileBullet(worldIn, player, 1);
	 * bulletE.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 3.5F, 2F);
	 * player.getCooldownTracker().setCooldown(player.getHeldItem(player.getActiveHand()).getItem(),
	 * 30);
	 * worldIn.spawnEntity(bulletE);
	 * } else {
	 * ProjectileBullet bulletE = new ProjectileBullet(worldIn, player, getBulletType(itemstack));
	 * bulletE.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 3.5F, 2F);
	 * player.getCooldownTracker().setCooldown(player.getHeldItem(player.getActiveHand()).getItem(),
	 * 30);
	 * worldIn.spawnEntity(bulletE);
	 * stack.damageItem(1, player);
	 * }
	 * }
	 * 
	 * worldIn.playSound(player.posX, player.posY, player.posZ, SoundsHandler.GUN_SHOOT, SoundCategory.MASTER,
	 * 1.0F, 1.0F, false);
	 * entityLiving.rotationPitch -= worldIn.rand.nextFloat() * 10;
	 * 
	 * if (!flag) {
	 * itemstack.shrink(1);
	 * 
	 * if (itemstack.isEmpty()) {
	 * player.inventory.deleteStack(itemstack);
	 * }
	 * }
	 * }
	 * }
	 * }
	 */

	protected boolean isBullet(ItemStack stack) {
		return stack.getItem() instanceof ItemBullet;
	}

	protected ItemStack findAmmo(PlayerEntity player) {
		if (this.isBullet(player.getHeldItem(Hand.OFF_HAND))) {
			return player.getHeldItem(Hand.OFF_HAND);
		} else if (this.isBullet(player.getHeldItem(Hand.MAIN_HAND))) {
			return player.getHeldItem(Hand.MAIN_HAND);
		} else {
			for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
				ItemStack itemstack = player.inventory.getStackInSlot(i);

				if (this.isBullet(itemstack)) {
					return itemstack;
				}
			}

			return ItemStack.EMPTY;
		}
	}

	protected ItemStack getBulletStack(ItemStack stack, PlayerEntity player) {
		if (stack.getItem() == ModItems.BULLET_IRON) {
			return new ItemStack(ModItems.BULLET_IRON);
		}

		if (stack.getItem() == ModItems.BULLET_GOLD) {
			return new ItemStack(ModItems.BULLET_GOLD);
		}

		if (stack.getItem() == ModItems.BULLET_DIAMOND) {
			return new ItemStack(ModItems.BULLET_DIAMOND);
		}

		if (stack.getItem() == ModItems.BULLET_FIRE) {
			return new ItemStack(ModItems.BULLET_FIRE);
		} else {
			System.out.println("IT'S A BUG!!!! IF YOU SEE THIS REPORT IT TO MOD'S AUTHOR");
			return ItemStack.EMPTY; // #SHOULD NEVER HAPPEN
		}
	}

	protected int getBulletType(ItemStack stack) {
		if (stack.getItem() == ModItems.BULLET_IRON) {
			return 1;
		}

		if (stack.getItem() == ModItems.BULLET_GOLD) {
			return 2;
		}

		if (stack.getItem() == ModItems.BULLET_DIAMOND) {
			return 3;
		}

		if (stack.getItem() == ModItems.BULLET_FIRE) {
			return 4;
		}

		else {
			System.out.println("IT'S A BUG!!!! IF YOU SEE THIS REPORT IT TO MOD'S AUTHOR");
			return 0; // #SHOULD NEVER HAPPEN
		}
	}

}
