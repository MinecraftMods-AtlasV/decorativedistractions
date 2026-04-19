package info.atlasv.decorative_distractions.lights.block;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.lights.item.LightsItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LightsBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(DecorativeDistractions.MODID);
    public static Collection<DeferredHolder<Block, ? extends Block>> getEntries() {
        return BLOCKS.getEntries();
    }

    public static final Map<MetalVariant, Map<DyeColor, DeferredBlock<Block>>> LIGHT_PANELS =
            new EnumMap<>(MetalVariant.class);

    static {
        for (MetalVariant metal : MetalVariant.values()) {
            Map<DyeColor, DeferredBlock<Block>> colourMap = new EnumMap<>(DyeColor.class);
            for (DyeColor dye : DyeColor.values()) {
                String name = "light_" + metal.getName() + "_" + dye.getName();
                DeferredBlock<Block> deferred = registerBlock(name, () ->
                        new LightsRedstoneLightPanelBlock(
                                metal.copyProps()
                                        .noOcclusion()
                                        .lightLevel(s -> s.getValue(LightsRedstoneLightPanelBlock.LIT) ? 15 : 0)
                        )
                );
                colourMap.put(dye, deferred);
            }
            LIGHT_PANELS.put(metal, colourMap);
        }
    }

    // Helpers used by LightsBlockColors and LightsBlockStateProvider

    /**
     * Returns every registered light panel block — all 48 variants.
     * Safe to call after the DeferredRegister has fired (i.e. not in static init).
     */
    public static Collection<Block> getAllLightPanelBlocks() {
        return LIGHT_PANELS.values().stream()
                .flatMap(colourMap -> colourMap.values().stream())
                .map(DeferredBlock::get)
                .collect(Collectors.toList());
    }

    /**
     * Reverse-lookup: given a block instance, returns the DyeColor it was registered with.
     * Returns Optional.empty() if the block is not one of the light panels.
     */
    public static Optional<DyeColor> getDyeColourForBlock(Block block) {
        for (Map.Entry<MetalVariant, Map<DyeColor, DeferredBlock<Block>>> metalEntry
                : LIGHT_PANELS.entrySet()) {
            for (Map.Entry<DyeColor, DeferredBlock<Block>> dyeEntry
                    : metalEntry.getValue().entrySet()) {
                if (dyeEntry.getValue().get() == block) {
                    return Optional.of(dyeEntry.getKey());
                }
            }
        }
        return Optional.empty();
    }

    // Internal registration helpers

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, java.util.function.Supplier<T> block) {
        DeferredBlock<T> deferred = BLOCKS.register(name, block);
        registerBlockItem(name, deferred);
        return deferred;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        LightsItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    // MetalVariant enum
    // TODO: Decide whether to move to its own enum file

    public enum MetalVariant {
        COPPER("copper", Blocks.COPPER_BLOCK),
        IRON("iron",     Blocks.IRON_BLOCK),
        GOLD("gold",     Blocks.GOLD_BLOCK);

        private final String name;
        private final Block  baseBlock;

        MetalVariant(String name, Block baseBlock) {
            this.name      = name;
            this.baseBlock = baseBlock;
        }

        public String getName() {
            return name;
        }

        // Copies the vanilla metal block's properties (hardness, sound, tool requirements).
        public BlockBehaviour.Properties copyProps() {
            return BlockBehaviour.Properties.ofFullCopy(baseBlock);
        }
    }

    public static void registerModels(ItemModelProvider provider) {
        for (var metalEntry : LIGHT_PANELS.entrySet()) {
            for (var dyeEntry : metalEntry.getValue().entrySet()) {
                DeferredBlock<Block> deferredBlock = dyeEntry.getValue();
                String name = deferredBlock.getId().getPath();
                provider.withExistingParent(name,
                        ResourceLocation.fromNamespaceAndPath(DecorativeDistractions.MODID, "block/" + name));
            }
        }
    }
}