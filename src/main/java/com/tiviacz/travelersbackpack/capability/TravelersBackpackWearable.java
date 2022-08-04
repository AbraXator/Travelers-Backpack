package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.network.SyncBackpackCapabilityClient;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.items.ItemStackHandler;

public class TravelersBackpackWearable implements ITravelersBackpack
{
    private ItemStack wearable = ItemStack.EMPTY;
    private final Player playerEntity;
    private final TravelersBackpackContainer container;

    public TravelersBackpackWearable(final Player playerEntity)
    {
        this.playerEntity = playerEntity;
        this.container = new TravelersBackpackContainer(this.wearable, playerEntity, Reference.WEARABLE_SCREEN_ID);
    }

    @Override
    public boolean hasWearable()
    {
        return !this.wearable.isEmpty();
    }

    @Override
    public ItemStack getWearable()
    {
        return this.wearable;
    }

    @Override
    public void setWearable(ItemStack stack)
    {
        this.wearable = stack;
    }

    @Override
    public void removeWearable()
    {
        this.wearable = ItemStack.EMPTY;
        this.container.setStack(ItemStack.EMPTY);
    }

    @Override
    public TravelersBackpackContainer getContainer()
    {
        return this.container;
    }

    @Override
    public void setContents(ItemStack stack)
    {
        this.container.setStack(stack);

        if(!stack.isEmpty())
        {
            this.container.loadAllData(stack.getOrCreateTag());
        }
    }

    @Override
    public void synchronise()
    {
        if(playerEntity != null && !playerEntity.level.isClientSide)
        {
            ServerPlayer serverPlayer = (ServerPlayer)playerEntity;
            CapabilityUtils.getCapability(serverPlayer).ifPresent(cap -> TravelersBackpack.NETWORK.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncBackpackCapabilityClient(this.wearable.save(new CompoundTag()), serverPlayer.getId())));
        }
    }

    @Override
    public void synchroniseToOthers(Player player)
    {
        if(player != null && !player.level.isClientSide)
        {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            CapabilityUtils.getCapability(serverPlayer).ifPresent(cap -> TravelersBackpack.NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> serverPlayer), new ClientboundSyncCapabilityPacket(this.wearable.save(new CompoundTag()), serverPlayer.getId())));
        }
    }

    @Override
    public CompoundTag saveTag()
    {
        CompoundTag compound = new CompoundTag();

        if(hasWearable())
        {
            ItemStack wearable = getWearable();
            wearable.save(compound);
        }
        if(!hasWearable())
        {
            ItemStack wearable = ItemStack.EMPTY;
            wearable.save(compound);
        }
        return compound;
    }

    @Override
    public void loadTag(CompoundTag compoundTag)
    {
        ItemStack wearable = ItemStack.of(compoundTag);
        setWearable(wearable);
        setContents(wearable);
    }

    public static CompoundTag synchroniseMinimumData(ItemStack stack)
    {
        CompoundTag compound = new CompoundTag();
        stack.save(compound);

        if(compound.contains("tag"))
        {
            if(compound.getCompound("tag").contains("Inventory"))
            {
                CompoundTag inventoryCopy = compound.getCompound("tag").getCompound("Inventory").copy();
                compound.getCompound("tag").remove("Inventory");
                compound.getCompound("tag").put("Inventory", slimInventory(inventoryCopy));
            }

            if(compound.getCompound("tag").contains("CraftingInventory"))
            {
                compound.getCompound("tag").remove("CraftingInventory");
            }
        }
        return compound;
    }

    public static CompoundTag slimInventory(CompoundTag inventory)
    {
        ItemStackHandler tempHandler = new ItemStackHandler(Reference.INVENTORY_SIZE);
        tempHandler.deserializeNBT(inventory);

        for(int i = 0; i < tempHandler.getSlots(); i++)
        {
            if(i != 39 && i != 40)
            {
                if(!tempHandler.getStackInSlot(i).isEmpty())
                {
                    tempHandler.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        }

        return tempHandler.serializeNBT();
    }
}