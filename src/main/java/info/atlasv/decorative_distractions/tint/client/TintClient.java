package info.atlasv.decorative_distractions.tint.client;

import info.atlasv.decorative_distractions.tint.block.TintBlockEntity;
import info.atlasv.decorative_distractions.tint.block.TintBlocks;
import info.atlasv.decorative_distractions.tint.item.TintBlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

public class TintClient {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(TintClient::onRegisterBlockColors);
        modEventBus.addListener(TintClient::onRegisterItemColors);
    }

    // All tinted blocks, base, slab, and stair, share TintBlockEntity, so a
    // single instanceof check covers everything. Iterates BLOCKS which includes
    // all registered block variants from every TintEntry.
    private static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
        var handler = (net.minecraft.client.color.block.BlockColor) (state, reader, pos, tintIndex) -> {
            if (reader == null || pos == null) return TintBlockEntity.DEFAULT_COLOR;
            if (tintIndex == 0 && reader.getBlockEntity(pos) instanceof TintBlockEntity be) {
                return be.getColour();
            }
            return TintBlockEntity.DEFAULT_COLOR;
        };

        TintBlocks.BLOCKS.getEntries().forEach(holder ->
                event.register(handler, holder.get())
        );
    }

    // Iterates ITEMS which includes slab and stair items aswell
    private static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        var handler = (net.minecraft.client.color.item.ItemColor) (stack, tintIndex) -> {
            if (tintIndex == 0 && stack.getItem() instanceof TintBlockItem item) {
                return item.getColor(stack);
            }
            return TintBlockEntity.DEFAULT_COLOR;
        };

        TintBlocks.ITEMS.getEntries().forEach(holder ->
                event.register(handler, holder.get())
        );
    }
}