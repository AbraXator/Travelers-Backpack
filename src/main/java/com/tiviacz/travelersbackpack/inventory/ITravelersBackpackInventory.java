package com.tiviacz.travelersbackpack.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

public interface ITravelersBackpackInventory extends ITanks
{
    void saveItems(CompoundNBT compound);

    void loadItems(CompoundNBT compound);

    void saveTime(CompoundNBT compound);

    void loadTime(CompoundNBT compound);

    void saveColor(CompoundNBT compound);

    void loadColor(CompoundNBT compound);

    void saveAbility(CompoundNBT compound);

    void loadAbility(CompoundNBT compound);

    void saveAllData(CompoundNBT compound);

    void loadAllData(CompoundNBT compound);

    CompoundNBT getTagCompound(ItemStack stack);

    int getColor();

    boolean hasColor();

    boolean getAbilityValue();

    void setAbility(boolean value);

    boolean hasTileEntity();

    boolean isSleepingBagDeployed();

    ItemStackHandler getInventory();

    ItemStackHandler getCraftingGridInventory();

    BlockPos getPosition();

    ItemStack decrStackSize(int index, int count);

    int getLastTime();

    void setLastTime(int time);

    void markLastTimeDirty();

    World getLevel();

    byte getScreenID();

    ItemStack getItemStack();

    void setChanged();
}