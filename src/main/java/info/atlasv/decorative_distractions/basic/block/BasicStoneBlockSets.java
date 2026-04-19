package info.atlasv.decorative_distractions.basic.block;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.basic.item.BasicItems;
import info.atlasv.decorative_distractions.core.blocksets.Stone;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public class BasicStoneBlockSets {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(DecorativeDistractions.MODID);
    public static Collection<DeferredHolder<Block, ? extends Block>> getEntries() {
        return BLOCKS.getEntries();
    }

    public static final Map<StoneVariant, Stone> SMOOTH_VARIANTS = new EnumMap<>(StoneVariant.class);
    public static final Map<StoneVariant, Stone> SMOOTH_BRICKS_VARIANTS = new EnumMap<>(StoneVariant.class);

    // Smooth stone variant registration
    static {
        for (StoneVariant variant : StoneVariant.values()) {
            SMOOTH_VARIANTS.put(variant, new Stone(
                    "smooth_" + variant.getName(),
                    "block/smooth_" + variant.getName(),
                    BLOCKS,
                    BasicItems.ITEMS
            ));
        }
    }
    // Smooth stone bricks variant registration
    static {
        for (StoneVariant variant : StoneVariant.values()) {
            SMOOTH_BRICKS_VARIANTS.put(variant, new Stone(
                    "smooth_" + variant.getName() + "_bricks",
                    "block/smooth_" + variant.getName() + "_bricks",
                    BLOCKS,
                    BasicItems.ITEMS
            ));
        }
    }

    // TODO: Decide whether to move this to it's own enum file
    public enum StoneVariant {
        DIORITE("diorite", Blocks.DIORITE),
        ANDESITE("andesite", Blocks.ANDESITE),
        GRANITE("granite", Blocks.GRANITE),
        DEEPSLATE("deepslate", Blocks.DEEPSLATE),
        TUFF("tuff", Blocks.TUFF),
        BLACKSTONE("blackstone", Blocks.BLACKSTONE);

        private final String name;
        private final Block baseBlock;

        StoneVariant(String name, Block baseBlock) {
            this.name = name;
            this.baseBlock = baseBlock;
        }

        public String getName() {
            return name;
        }

        public BlockBehaviour.Properties copyProps() {
            return BlockBehaviour.Properties.ofFullCopy(baseBlock);
        }
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}