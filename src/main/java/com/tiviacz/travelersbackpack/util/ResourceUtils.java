package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ResourceUtils
{
    public static final List<ResourceLocation> TEXTURE_RESOURCE_LOCATIONS = new ArrayList<>();

    //Store resource locations for backpacks, to avoid tick by tick new resource locations creation.
    //Then get each texture by index from ModItems#BACKPACKS.
    //Any new approach is appreciated D:

    public static ResourceLocation getBackpackTexture(Item item)
    {
        return TEXTURE_RESOURCE_LOCATIONS.get(ModItems.BACKPACKS.indexOf(item));
    }

    public static void createTextureLocations()
    {
        TEXTURE_RESOURCE_LOCATIONS.clear();

        for(String name : Reference.BACKPACK_NAMES)
        {
            ResourceLocation res = new ResourceLocation(TravelersBackpack.MODID, "textures/model/" + name.toLowerCase(Locale.ENGLISH) + ".png");
            TEXTURE_RESOURCE_LOCATIONS.add(res);
        }
    }
}