package com.teamcqr.chocolatequestrepoured.gui.container;

import com.teamcqr.chocolatequestrepoured.gui.container.slot.SlotSpawner;
import com.teamcqr.chocolatequestrepoured.tileentity.TileEntitySpawner;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ContainerSpawner extends Container 
{
	private final int numRows = 3;
	private final int numColumns = 3;

	public ContainerSpawner(InventoryPlayer playerInv, TileEntitySpawner te)
	{
		IItemHandler inv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
		
		for(int i = 0; i < this.numRows; ++i)
	    {
			for(int j = 0; j < this.numColumns; ++j)
			{
				this.addSlotToContainer(new SlotSpawner(inv, j + i * 3, 62 + j * 18, 17 + i * 18)
				{
					@Override
					public void onSlotChanged() 
					{
						te.markDirty();
					}
				});
			}
	    }
		
		for(int y = 0; y < 3; y++)
		{
			for(int x = 0; x < 9; x++)
			{
				this.addSlotToContainer(new Slot(playerInv, x + y*9 + 9, 8 + x*18, 84 + y*18));
			}
		}
		
		for(int x = 0; x < 9; x++)
		{
			this.addSlotToContainer(new Slot(playerInv, x, 8 + x*18, 142));
		}							
		
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return true;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < this.numRows * 9)
            {
                if (!this.mergeItemStack(itemstack1, this.numRows * 9, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, this.numRows * 9, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
	}
}