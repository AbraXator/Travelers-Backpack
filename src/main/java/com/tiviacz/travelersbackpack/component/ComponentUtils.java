package com.tiviacz.travelersbackpack.component;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class ComponentUtils implements EntityComponentInitializer
{
    public static final ComponentKey<ITravelersBackpackComponent> WEARABLE = ComponentRegistry.getOrCreate(new Identifier(TravelersBackpack.MODID, "travelersbackpack"), ITravelersBackpackComponent.class);

    public static ITravelersBackpackComponent getComponent(PlayerEntity player)
    {
        return WEARABLE.get(player);
    }

    public static void sync(PlayerEntity player)
    {
        if(player instanceof ServerPlayerEntity)
        {
            getComponent(player).sync();
        }
    }

    public static void syncToTracking(PlayerEntity player)
    {
        if(player instanceof ServerPlayerEntity)
        {
            getComponent(player).syncToTracking((ServerPlayerEntity)player);
        }
    }

    public static boolean isWearingBackpack(PlayerEntity player)
    {
        if(TravelersBackpack.enableTrinkets())
        {
            //return TrinketsApi.getTrinketComponent(player).getStack(SlotGroups.CHEST, Slots.CAPE).getItem() instanceof TravelersBackpackItem;
        }

        return WEARABLE.get(player).hasWearable() && WEARABLE.get(player).getWearable().getItem() instanceof TravelersBackpackItem;
       /* LazyOptional<ITravelersBackpack> cap = getCapability(player);
        ItemStack backpack = cap.lazyMap(ITravelersBackpack::getWearable).orElse(ItemStack.EMPTY);

        return cap.map(ITravelersBackpack::hasWearable).orElse(false) && backpack.getItem() instanceof TravelersBackpackItem; */
    }

    public static ItemStack getWearingBackpack(PlayerEntity player)
    {
        if(TravelersBackpack.enableTrinkets())
        {
           // return TrinketsApi.getTrinketComponent(player).getStack(SlotGroups.CHEST, Slots.CAPE);
        }

        return isWearingBackpack(player) ? WEARABLE.get(player).getWearable() : ItemStack.EMPTY;

   /*     LazyOptional<ITravelersBackpack> cap = getCapability(player);
        ItemStack backpack = cap.map(ITravelersBackpack::getWearable).orElse(ItemStack.EMPTY);

        return isWearingBackpack(player) ? backpack : ItemStack.EMPTY; */
    }

    public static void equipBackpack(PlayerEntity player, ItemStack stack)
    {
        if(!WEARABLE.get(player).hasWearable())
        {
            WEARABLE.get(player).setWearable(stack);
            player.world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (player.world.random.nextFloat() - player.world.random.nextFloat()) * 0.2F) * 0.7F);
        }

        sync(player);
        syncToTracking(player);

     /*   LazyOptional<ITravelersBackpack> cap = getCapability(player);

        if(!cap.map(ITravelersBackpack::hasWearable).orElse(false))
        {
            cap.ifPresent(inv -> inv.setWearable(stack));
            player.world.playSound(null, player.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.2F) * 0.7F);

            //Sync
            synchronise(player);
            synchroniseToOthers(player);
        } */
    }

    public static TravelersBackpackInventory getBackpackInv(PlayerEntity player)
    {
        ItemStack wearable = getWearingBackpack(player);

       /* if(TravelersBackpack.enableTrinkets())
        {
            return new TravelersBackpackInventory(TrinketsApi.getTrinketComponent(player).getStack(SlotGroups.CHEST, Slots.CAPE), player, Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID);
        } */

        if(wearable.getItem() instanceof TravelersBackpackItem)
        {
            return new TravelersBackpackInventory(wearable, player, Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID);
        }
        return null;
       /* if(TravelersBackpack.enableCurios())
        {
            return TravelersBackpackCurios.getCurioTravelersBackpackInventory(player);
        }

        ItemStack wearable = getWearingBackpack(player);

        if(wearable.getItem() instanceof TravelersBackpackItem)
        {
            return new TravelersBackpackInventory(wearable, player, Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID);
        }
        return null; */
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry)
    {
        registry.registerForPlayers(WEARABLE, TravelersBackpackComponent::new, RespawnCopyStrategy.INVENTORY);
    }
}
