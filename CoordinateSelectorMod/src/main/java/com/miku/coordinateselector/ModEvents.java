package com.miku.coordinateselector;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEvents {
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        BlockPos pos = event.getPos();
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(event.getLevel().getBlockState(pos).getBlock());

        if (blockId == null || !blockId.toString().equals("wormhole:portal_stabilizer")) {
            return;
        }

        Player player = event.getEntity();
        ItemStack held = player.getMainHandItem();
        if (!held.is(ModItems.MIKU_LOCATOR.get())) {
            return;
        }

        // 核心技术：阻断原版稳定器打开界面
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);

        // 仅在客户端打开专属 UI
        if (event.getLevel().isClientSide()) {
            ClientHooks.openCoordinateScreen(pos);
        }
    }
}
