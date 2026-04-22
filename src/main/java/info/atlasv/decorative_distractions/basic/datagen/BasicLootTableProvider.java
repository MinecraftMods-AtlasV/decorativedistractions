package info.atlasv.decorative_distractions.basic.datagen;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.basic.block.BasicStoneBlockSets;
import info.atlasv.decorative_distractions.core.blocksets.Stone;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;
import java.util.stream.Collectors;

public class BasicLootTableProvider extends BlockLootSubProvider {

    protected BasicLootTableProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        // Register loot tables for every Stone set in both maps.
        // Iterating the same way the sets were registered means no block is missed
        // if new StoneVariants are added in the future.
        for (BasicStoneBlockSets.StoneVariant variant : BasicStoneBlockSets.StoneVariant.values()) {
            registerStoneSet(BasicStoneBlockSets.SMOOTH_VARIANTS.get(variant));
            registerStoneSet(BasicStoneBlockSets.SMOOTH_BRICKS_VARIANTS.get(variant));
        }
    }

    /**
     * Registers drop-self loot tables for all six variants in a Stone set.
     * Slabs use createSlabItemTable so that a double slabs correctly drops 2 items.
     */
    private void registerStoneSet(Stone stone) {
        dropSelf(stone.block.get());
        dropSelf(stone.stairs.get());
        add(stone.slab.get(), this::createSlabItemTable);
        dropSelf(stone.wall.get());
        dropSelf(stone.pressurePlate.get());
        dropSelf(stone.button.get());
    }

    /**
     * Tells the framework which blocks this provider is responsible for.
     * Any block registered in BasicStoneBlockSets.BLOCKS that is missing a loot
     * table entry will cause datagen to throw, catching accidental omissions
     * Probably not actually needed but I got sick of forgetting some
     */
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BasicStoneBlockSets.BLOCKS.getEntries().stream()
                .map(DeferredHolder::get)
                .collect(Collectors.toList());
    }
}