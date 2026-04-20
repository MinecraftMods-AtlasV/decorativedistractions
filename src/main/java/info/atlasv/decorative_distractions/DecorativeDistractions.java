package info.atlasv.decorative_distractions;

import info.atlasv.decorative_distractions.basic.block.BasicAmethystBlockSets;
import info.atlasv.decorative_distractions.basic.block.BasicStoneBlockSets;
import info.atlasv.decorative_distractions.basic.item.BasicItems;
import info.atlasv.decorative_distractions.compat.CompatLoader;
import info.atlasv.decorative_distractions.core.CreativeTabs;
import info.atlasv.decorative_distractions.lights.block.LightsBlocks;
import info.atlasv.decorative_distractions.lights.item.LightsItems;
import info.atlasv.decorative_distractions.tint.TintDataComponents;
import info.atlasv.decorative_distractions.tint.block.TintBlock;
import info.atlasv.decorative_distractions.tint.block.TintBlockEntities;
import info.atlasv.decorative_distractions.tint.block.TintBlocks;
import info.atlasv.decorative_distractions.tint.item.TintBlockItem;
import info.atlasv.decorative_distractions.tint.item.TintItems;
import info.atlasv.decorative_distractions.tint.recipe.TintRecipeSerializers;
import info.atlasv.decorative_distractions.tint.utils.GetTintCommand;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(DecorativeDistractions.MODID)
public class DecorativeDistractions {
    public static final String MODID = "decorative_distractions";
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public DecorativeDistractions(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        CompatLoader.init(modEventBus);
        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (DecorativeDistractions) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);

        // Basic Stufs
        CreativeTabs.register(modEventBus);
        BasicStoneBlockSets.register(modEventBus);
        BasicAmethystBlockSets.registerVanillaAmethystSlabsAndStairs();
        BasicAmethystBlockSets.register(modEventBus);
        BasicItems.register(modEventBus);
        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // Lights Stuffs
        LightsBlocks.register(modEventBus);
        LightsItems.register(modEventBus);

        // Tint Stuffs
        TintBlocks.register(modEventBus);
        TintBlockEntities.register(modEventBus);
        TintDataComponents.register(modEventBus);
        TintRecipeSerializers.register(modEventBus);
        TintItems.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        GetTintCommand.register(event.getDispatcher());
    }
}
