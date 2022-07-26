package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackItemContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TravelersBackpackInventory implements ITravelersBackpackInventory, INamedContainerProvider
{
    private final ItemStackHandler inventory = createHandler(Reference.INVENTORY_SIZE);
    private final ItemStackHandler craftingInventory = createHandler(Reference.CRAFTING_GRID_SIZE);
    private final FluidTank leftTank = createFluidHandler(TravelersBackpackConfig.tanksCapacity);
    private final FluidTank rightTank = createFluidHandler(TravelersBackpackConfig.tanksCapacity);
    private final PlayerEntity player;
    private final ItemStack stack;
    private boolean ability;
    private int lastTime;
    private final byte screenID;

    private final String INVENTORY = "Inventory";
    private final String CRAFTING_INVENTORY = "CraftingInventory";
    private final String LEFT_TANK = "LeftTank";
    private final String RIGHT_TANK = "RightTank";
    private final String COLOR = "Color";
    private final String ABILITY = "Ability";
    private final String LAST_TIME = "LastTime";

    public TravelersBackpackInventory(ItemStack stack, PlayerEntity player, byte screenID)
    {
        this.player = player;
        this.stack = stack;
        this.screenID = screenID;

        this.loadAllData(getTagCompound(stack));
    }

    @Override
    public ItemStackHandler getInventory()
    {
        return this.inventory;
    }

    @Override
    public ItemStackHandler getCraftingGridInventory()
    {
        return this.craftingInventory;
    }

    @Override
    public FluidTank getLeftTank()
    {
        return this.leftTank;
    }

    @Override
    public FluidTank getRightTank()
    {
        return this.rightTank;
    }

    @Override
    public void saveAllData(CompoundNBT compound)
    {
        this.markTankDirty();
        this.saveItems(compound);
        this.saveAbility(compound);
        this.saveTime(compound);
    }

    @Override
    public void loadAllData(CompoundNBT compound)
    {
        this.loadTanks(compound);
        this.loadItems(compound);
        this.loadAbility(compound);
        this.loadTime(compound);
    }

    @Override
    public void saveItems(CompoundNBT compound)
    {
        compound.put(INVENTORY, this.inventory.serializeNBT());
        compound.put(CRAFTING_INVENTORY, this.craftingInventory.serializeNBT());
    }

    @Override
    public void loadItems(CompoundNBT compound)
    {
        this.inventory.deserializeNBT(compound.getCompound(INVENTORY));
        this.craftingInventory.deserializeNBT(compound.getCompound(CRAFTING_INVENTORY));
    }

    @Override
    public void saveTanks(CompoundNBT compound)
    {
        compound.put(LEFT_TANK, this.leftTank.writeToNBT(new CompoundNBT()));
        compound.put(RIGHT_TANK, this.rightTank.writeToNBT(new CompoundNBT()));
    }

    @Override
    public void loadTanks(CompoundNBT compound)
    {
        this.leftTank.readFromNBT(compound.getCompound(LEFT_TANK));
        this.rightTank.readFromNBT(compound.getCompound(RIGHT_TANK));
    }

    @Override
    public void saveColor(CompoundNBT compound) {}
    @Override
    public void loadColor(CompoundNBT compound) {}

    @Override
    public void saveAbility(CompoundNBT compound)
    {
        compound.putBoolean(ABILITY, this.ability);
    }

    @Override
    public void loadAbility(CompoundNBT compound)
    {
        this.ability = compound.getBoolean(ABILITY);
    }

    @Override
    public void saveTime(CompoundNBT compound)
    {
        compound.putInt(LAST_TIME, this.lastTime);
    }

    @Override
    public void loadTime(CompoundNBT compound)
    {
        this.lastTime = compound.getInt(LAST_TIME);
    }

    @Override
    public boolean updateTankSlots()
    {
        return InventoryActions.transferContainerTank(this, getLeftTank(), Reference.BUCKET_IN_LEFT, player) || InventoryActions.transferContainerTank(this, getRightTank(), Reference.BUCKET_IN_RIGHT, player);
    }

    @Override
    public void markTankDirty()
    {
        this.saveTanks(this.getTagCompound(this.stack));
        this.sendPackets();
    }

    private void sendPackets()
    {
        if(screenID == Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID)
        {
            CapabilityUtils.synchronise(player);
            CapabilityUtils.synchroniseToOthers(player);
        }
    }

    @Override
    public boolean hasColor()
    {
        return getTagCompound(this.stack).contains(COLOR);
    }

    @Override
    public int getColor()
    {
        if(hasColor())
        {
            return getTagCompound(this.stack).getInt(COLOR);
        }
        return 0;
    }

    @Override
    public boolean getAbilityValue()
    {
        return TravelersBackpackConfig.enableBackpackAbilities ? this.ability : false;
    }

    @Override
    public void setAbility(boolean value)
    {
        this.ability = value;
    }

    @Override
    public int getLastTime()
    {
        return this.lastTime;
    }

    @Override
    public void setLastTime(int time)
    {
        this.lastTime = time;
    }

    @Override
    public void markLastTimeDirty()
    {
        this.saveTime(getTagCompound(this.stack));
        this.sendPackets();
    }

    @Override
    public CompoundNBT getTagCompound(ItemStack stack)
    {
        if(stack.getTag() == null)
        {
            CompoundNBT tag = new CompoundNBT();
            stack.setTag(tag);
        }

        return stack.getTag();
    }

    @Override
    public boolean hasTileEntity()
    {
        return false;
    }

    @Override
    public boolean isSleepingBagDeployed()
    {
        return false;
    }

    @Override
    public World getLevel()
    {
        return this.player.level;
    }

    @Override
    public BlockPos getPosition()
    {
        return this.player.blockPosition();
    }

    @Override
    public byte getScreenID()
    {
        return this.screenID;
    }

    @Override
    public ItemStack getItemStack()
    {
        return this.stack;
    }

    @Override
    public void setChanged()
    {
        this.saveAllData(this.getTagCompound(this.stack));
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent("screen.travelersbackpack.item");
    }

    public static void abilityTick(PlayerEntity player)
    {
        if(player.isAlive() && CapabilityUtils.isWearingBackpack(player))
        {
            TravelersBackpackInventory inv = BackpackUtils.getCurrentInventory(player);

            if(inv.getLastTime() > 0)
            {
                inv.setLastTime(inv.getLastTime() - 1);
                inv.markLastTimeDirty();
            }

            if(inv.getAbilityValue())
            {
                BackpackAbilities.ABILITIES.abilityTick(CapabilityUtils.getWearingBackpack(player), player, null);
            }
        }
    }

    public static void openGUI(ServerPlayerEntity serverPlayerEntity, ItemStack stack, byte screenID)
    {
        if(!serverPlayerEntity.level.isClientSide)
        {
            NetworkHooks.openGui(serverPlayerEntity, new TravelersBackpackInventory(stack, serverPlayerEntity, screenID), packetBuffer -> packetBuffer.writeByte(screenID));//packetBuffer.writeItemStack(stack, false).writeByte(screenID));
        }
    }

    @Nullable
    @Override
    public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity)
    {
        return new TravelersBackpackItemContainer(windowID, playerInventory, this);
    }

    private ItemStackHandler createHandler(int size)
    {
        return new ItemStackHandler(size)
        {
            @Override
            protected void onContentsChanged(int slot)
            {
                saveItems(getTagCompound(stack));
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack)
            {
                return !(stack.getItem() instanceof TravelersBackpackItem);
            }
        };
    }

    private FluidTank createFluidHandler(int capacity)
    {
        return new FluidTank(capacity)
        {
            @Override
            protected void onContentsChanged()
            {
                markTankDirty();
            }
        };
    }
}