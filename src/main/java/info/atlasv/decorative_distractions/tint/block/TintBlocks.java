package info.atlasv.decorative_distractions.tint.block;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.tint.TintEntry;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

/**
 * Central registration for tintable blocks and their items.
 * {@link info.atlasv.decorative_distractions.tint.client.TintClient} iterates these
 * registers at startup, so no changes are needed to change anything else there
 */
public class TintBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(DecorativeDistractions.MODID);

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(DecorativeDistractions.MODID);

    // Tint entries
    // add new tintable blocks below

    public static final TintEntry TINTED_COBBLESTONE = new TintEntry(
            "tinted_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE),
            BLOCKS,
            ITEMS
    );

    public static final TintEntry TINTED_GRASS_BLOCK = new TintEntry(
            "tinted_grass_block",
            BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK),
            BLOCKS,
            ITEMS
    );

    public static final TintEntry TINTED_STONE = new TintEntry(
            "tinted_stone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE),
            BLOCKS,
            ITEMS
    );

    // ----------------------------------------------------------------------

    private static final List<TintEntry> ENTRIES = List.of(
            TINTED_COBBLESTONE,
            TINTED_GRASS_BLOCK,
            TINTED_STONE
    );

    public static List<TintEntry> getEntries() {
        return ENTRIES;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}