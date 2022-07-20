package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.compat.curios.TravelersBackpackCurios;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilityUtils
{
    public static LazyOptional<ITravelersBackpack> getCapability(final Player player)
    {
        return player.getCapability(TravelersBackpackCapability.TRAVELERS_BACKPACK_CAPABILITY, TravelersBackpackCapability.DEFAULT_FACING);
    }

    public static void synchronise(Player player)
    {
        CapabilityUtils.getCapability(player)
                .ifPresent(ITravelersBackpack::synchronise);
    }

    public static void synchroniseToOthers(Player player)
    {
        CapabilityUtils.getCapability(player)
                .ifPresent(i -> i.synchroniseToOthers(player));
    }

    public static boolean isWearingBackpack(Player player)
    {
        if(TravelersBackpack.enableCurios())
        {
            return TravelersBackpackCurios.getCurioTravelersBackpack(player).isPresent();
        }

        LazyOptional<ITravelersBackpack> cap = getCapability(player);
        ItemStack backpack = cap.lazyMap(ITravelersBackpack::getWearable).orElse(ItemStack.EMPTY);

        return cap.map(ITravelersBackpack::hasWearable).orElse(false) && backpack.getItem() instanceof TravelersBackpackItem;
    }

    public static ItemStack getWearingBackpack(Player player)
    {
        if(TravelersBackpack.enableCurios())
        {
            return TravelersBackpackCurios.getCurioTravelersBackpackStack(player);
        }

        LazyOptional<ITravelersBackpack> cap = getCapability(player);
        ItemStack backpack = cap.map(ITravelersBackpack::getWearable).orElse(ItemStack.EMPTY);

        return isWearingBackpack(player) ? backpack : ItemStack.EMPTY;
    }

    public static void equipBackpack(Player player, ItemStack stack)
    {
        LazyOptional<ITravelersBackpack> cap = getCapability(player);

        if(!cap.map(ITravelersBackpack::hasWearable).orElse(false))
        {
            cap.ifPresent(inv -> inv.setWearable(stack));
            player.level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.0F, (1.0F + (player.level.random.nextFloat() - player.level.random.nextFloat()) * 0.2F) * 0.7F);

            //Sync
            synchronise(player);
            synchroniseToOthers(player);
        }
    }

    public static TravelersBackpackContainer getBackpackInv(Player player)
    {
        if(TravelersBackpack.enableCurios())
        {
            return TravelersBackpackCurios.getCurioTravelersBackpackInventory(player);
        }

        ItemStack wearable = getWearingBackpack(player);

        if(wearable.getItem() instanceof TravelersBackpackItem)
        {
            return new TravelersBackpackContainer(wearable, player, Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID);
        }
        return null;
    }
}