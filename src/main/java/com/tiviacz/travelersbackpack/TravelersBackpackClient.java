package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackBlockEntityRenderer;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpack.handlers.KeybindHandler;
import com.tiviacz.travelersbackpack.init.ModBlockEntityTypes;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModScreenHandlerTypes;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;

@Environment(EnvType.CLIENT)
public class TravelersBackpackClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ScreenRegistry.register(ModScreenHandlerTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY, TravelersBackpackHandledScreen::new);
        ScreenRegistry.register(ModScreenHandlerTypes.TRAVELERS_BACKPACK_ITEM, TravelersBackpackHandledScreen::new);
        BlockEntityRendererRegistry.INSTANCE.register(ModBlockEntityTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY_TYPE, TravelersBackpackBlockEntityRenderer::new);
        for(Item item : ModItems.BACKPACKS)
        {
            BuiltinItemRendererRegistry.INSTANCE.register(item, (stack, mode, matrices, vertexConsumers, light, overlay)
                    -> TravelersBackpackBlockEntityRenderer.render(new TravelersBackpackInventory(stack, MinecraftClient.getInstance().player, (byte)0), null, matrices, vertexConsumers, light, overlay));
        }
        KeybindHandler.initKeybinds();
        KeybindHandler.registerListeners();
    }
}
