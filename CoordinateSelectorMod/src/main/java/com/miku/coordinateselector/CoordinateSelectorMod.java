package com.miku.coordinateselector;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CoordinateSelectorMod.MOD_ID)
public class CoordinateSelectorMod {
    public static final String MOD_ID = "coordinate_selector";

    public CoordinateSelectorMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(bus);
        bus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(ModEvents.class);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetwork::register);
    }
}
