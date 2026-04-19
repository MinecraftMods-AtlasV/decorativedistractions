package info.atlasv.decorative_distractions.lights.client;

import info.atlasv.decorative_distractions.lights.block.LightsBlocks;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

public class LightsBlockColors {
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(LightsBlockColors::registerBlockColors);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        // Collect all 48 panel blocks into an array for the bulk register call.
        Block[] panelBlocks = LightsBlocks.getAllLightPanelBlocks().toArray(new Block[0]);

        // One lambda covers all 48 blocks.
        // state.getBlock() identifies which specific block is being rendered at runtime,
        // and getDyeColourForBlock() maps it back to the DyeColor it was registered with.
        // tintindex 0 is the glass face declared in the model JSON.
        event.register((state, level, pos, tintIndex) -> {
            if (tintIndex != 0) return -1;

            return LightsBlocks.getDyeColourForBlock(state.getBlock())
                    .map(DyeColor::getTextureDiffuseColor)  // already a packed ARGB32 int
                    .orElse(0xFFFFFFFF);
        }, panelBlocks);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        // BlockItem stores its Block, so it can reuse the same dye reverse-lookup.
        var itemBlocks = LightsBlocks.getAllLightPanelBlocks().stream()
                .map(block -> block.asItem())
                .toArray(net.minecraft.world.item.Item[]::new);

        event.register((stack, tintIndex) -> {
            if (tintIndex != 0) return -1;

            // Unwrap the BlockItem back to its Block, then reuse the same lookup.
            if (stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem) {
                return LightsBlocks.getDyeColourForBlock(blockItem.getBlock())
                        .map(DyeColor::getTextureDiffuseColor)
                        .orElse(0xFFFFFFFF);
            }
            return 0xFFFFFFFF;
        }, itemBlocks);
    }
}