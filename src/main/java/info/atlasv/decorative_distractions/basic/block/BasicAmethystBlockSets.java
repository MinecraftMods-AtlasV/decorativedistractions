package info.atlasv.decorative_distractions.basic.block;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.basic.item.BasicItems;
import info.atlasv.decorative_distractions.core.blocksets.Amethyst;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.*;
import java.util.stream.Collectors;

import static info.atlasv.decorative_distractions.basic.item.BasicItems.ITEMS;

public class BasicAmethystBlockSets {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(DecorativeDistractions.MODID);

    public static Collection<DeferredHolder<Block, ? extends Block>> getEntries() {
        return BLOCKS.getEntries();
    }

    public static final Map<AmethystVariant, Amethyst> CRYSTALS = new EnumMap<>(AmethystVariant.class);

    static {
        for (AmethystVariant variant : AmethystVariant.values()) {
            CRYSTALS.put(variant, new Amethyst(
                    variant.getName(),
                    "block/crystals/" + variant.getColour(),
                    BLOCKS,
                    ITEMS
            ));
        }
    }

    /*
     * Slab and stairs for the vanilla `Blocks.AMETHYST_BLOCK`.
     *
     * These are null until `registerVanillaAmethystSlabsAndStairs()` is called
     *
     * Registry names follow the same `<stem>_slab` or `<stem>_stairs` system
     * used for the custom variants:
     * - `decorative_distractions:amethyst_block_slab`
     * - `decorative_distractions:amethyst_block_stairs`
     */
    public static DeferredBlock<SlabBlock>   VANILLA_AMETHYST_SLAB;
    public static DeferredBlock<StairBlock>  VANILLA_AMETHYST_STAIRS;

    /**
     * Registers a slab and stair for the vanilla {@link Blocks#AMETHYST_BLOCK} into the
     * modules {@link #BLOCKS} and {@link BasicItems#ITEMS} registers.
     *
     * <p>Block properties are copied from vanilla {@code Blocks.AMETHYST_BLOCK} using
     * {@link BlockBehaviour.Properties#ofFullCopy}.
     */
    public static void registerVanillaAmethystSlabsAndStairs() {
        // Copy all properties from the vanilla block
        BlockBehaviour.Properties props = BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK);

        VANILLA_AMETHYST_SLAB = BLOCKS.register("amethyst_block_slab",
                () -> new SlabBlock(props));

        // StairBlock needs a base block state for shape calculating stuffs. the vanilla
        // amethyst block is already available at load so reference it
        VANILLA_AMETHYST_STAIRS = BLOCKS.register("amethyst_block_stairs",
                () -> new StairBlock(Blocks.AMETHYST_BLOCK.defaultBlockState(), props));

        // Registers the block items as items
        ITEMS.register("amethyst_block_slab",
                () -> new BlockItem(VANILLA_AMETHYST_SLAB.get(), new Item.Properties()));
        ITEMS.register("amethyst_block_stairs",
                () -> new BlockItem(VANILLA_AMETHYST_STAIRS.get(), new Item.Properties()));
    }

    // TODO: Decide whether to move this to it's own enum file
    public enum AmethystVariant {

        WHITE      ("selenite",      "white"),
        ORANGE     ("carnelian",     "orange"),
        MAGENTA    ("eudialyte",     "magenta"),
        LIGHT_BLUE ("celestite",     "light_blue"),
        YELLOW     ("citrine",       "yellow"),
        LIME       ("hiddenite",     "lime"),
        PINK       ("thulite",       "pink"),
        GRAY       ("smokey_quartz", "gray"),
        LIGHT_GRAY ("magnesite",     "light_gray"),
        CYAN       ("plancheite",    "cyan"),
        BLUE       ("azurite",       "blue"),
        BROWN      ("tigers_eye",    "brown"),
        GREEN      ("malachite",     "green"),
        RED        ("realgar",       "red"),
        BLACK      ("jet",           "black");

        private final String name;
        private final String colour;

        AmethystVariant(String name, String colour) {
            this.name   = name;
            this.colour = colour;
        }

        public String getName()   { return name; }
        public String getColour() { return colour; }
    }

    // Collects the amethyst variants for lang gen automatically
    public static final Set<String> AMETHYST_VARIANT_NAMES = Arrays.stream(AmethystVariant.values())
            .map(AmethystVariant::getName)
            .collect(Collectors.toUnmodifiableSet());

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}