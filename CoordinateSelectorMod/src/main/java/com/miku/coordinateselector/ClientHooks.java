package com.miku.coordinateselector;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public class ClientHooks {
    public static void openCoordinateScreen(BlockPos pos) {
        Minecraft.getInstance().setScreen(new CoordinateInputScreen(pos));
    }
}
