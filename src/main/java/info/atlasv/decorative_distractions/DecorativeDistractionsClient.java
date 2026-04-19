package info.atlasv.decorative_distractions;

import info.atlasv.decorative_distractions.lights.client.LightsBlockColors;
import info.atlasv.decorative_distractions.tint.client.GrassOnTintBlockColor;
import info.atlasv.decorative_distractions.tint.client.TintClient;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// Client-side entry point
@Mod(value = DecorativeDistractions.MODID, dist = Dist.CLIENT)
public class DecorativeDistractionsClient {
    public DecorativeDistractionsClient(IEventBus modEventBus, ModContainer container) {
        // Register client setup listener
        modEventBus.addListener(this::onClientSetup);

        // Register config screen
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        LightsBlockColors.register(modEventBus);
        TintClient.register(modEventBus);
        modEventBus.addListener(GrassOnTintBlockColor::registerColors);
    }

    // Optional: client setup logic
    private void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        DecorativeDistractions.LOGGER.info("HELLO FROM CLIENT SETUP");
        DecorativeDistractions.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
