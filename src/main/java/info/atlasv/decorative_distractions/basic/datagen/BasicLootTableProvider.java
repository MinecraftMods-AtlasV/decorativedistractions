package info.atlasv.decorative_distractions.basic.datagen;

import info.atlasv.decorative_distractions.basic.block.BasicAmethystBlockSets;
import info.atlasv.decorative_distractions.basic.block.BasicStoneBlockSets;
import info.atlasv.decorative_distractions.core.blocksets.Amethyst;
import info.atlasv.decorative_distractions.core.blocksets.Stone;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BasicLootTableProvider extends BlockLootSubProvider {

    protected BasicLootTableProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        // Stone sets
        for (BasicStoneBlockSets.StoneVariant variant : BasicStoneBlockSets.StoneVariant.values()) {
            registerStoneSet(BasicStoneBlockSets.SMOOTH_VARIANTS.get(variant));
            registerStoneSet(BasicStoneBlockSets.SMOOTH_BRICKS_VARIANTS.get(variant));
        }

        // Vanilla amethyst block slab and stairs
        dropSelf(BasicAmethystBlockSets.VANILLA_AMETHYST_STAIRS.get());
        add(BasicAmethystBlockSets.VANILLA_AMETHYST_SLAB.get(), this::createSlabItemTable);

        // Custom crystal variants
        for (BasicAmethystBlockSets.AmethystVariant variant : BasicAmethystBlockSets.AmethystVariant.values()) {
            registerAmethystSet(BasicAmethystBlockSets.CRYSTALS.get(variant));
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Stone
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Registers drop-self loot tables for all six variants in a Stone set.
     * Slabs use createSlabItemTable so that a double slab correctly drops 2 items.
     */
    private void registerStoneSet(Stone stone) {
        dropSelf(stone.block.get());
        dropSelf(stone.stairs.get());
        add(stone.slab.get(), this::createSlabItemTable);
        dropSelf(stone.wall.get());
        dropSelf(stone.pressurePlate.get());
        dropSelf(stone.button.get());
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Amethyst
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Registers loot tables for every block in an {@link Amethyst} set:
     * <ul>
     *   <li>{@code block} / {@code stairs} — always drop themselves</li>
     *   <li>{@code slab} — double-slab aware via {@link #createSlabItemTable}</li>
     *   <li>{@code buddingBlock} — drops nothing, even with Silk Touch</li>
     *   <li>{@code smallBud} / {@code mediumBud} / {@code largeBud} — Silk Touch only, otherwise nothing</li>
     *   <li>{@code cluster} — Silk Touch → itself; pickaxe without Silk Touch → 4 shards (Fortune-boosted);
     *       any other tool without Silk Touch → 2 shards flat</li>
     * </ul>
     */
    private void registerAmethystSet(Amethyst crystal) {
        dropSelf(crystal.block.get());
        dropSelf(crystal.stairs.get());
        add(crystal.slab.get(), this::createSlabItemTable);

        // Budding block never drops anything (mirrors vanilla BuddingAmethystBlock)
        add(crystal.buddingBlock.get(), noDrop());

        // Buds: Silk Touch only, otherwise nothing
        add(crystal.smallBud.get(),  createSilkTouchOnlyDrop(crystal.smallBud.get()));
        add(crystal.mediumBud.get(), createSilkTouchOnlyDrop(crystal.mediumBud.get()));
        add(crystal.largeBud.get(),  createSilkTouchOnlyDrop(crystal.largeBud.get()));

        // Cluster: Silk Touch → block; otherwise shard drops with Fortune scaling
        add(crystal.cluster.get(), createClusterDrops(crystal));
    }

    /**
     * Drops {@code block} only when mined with Silk Touch, nothing otherwise.
     * Delegates to the inherited {@link #createSilkTouchOnlyTable} which uses
     * {@code this.hasSilkTouch()} internally.
     */
    private LootTable.Builder createSilkTouchOnlyDrop(Block block) {
        return createSilkTouchOnlyTable(block);
    }

    /**
     * Replicates vanilla amethyst cluster drop behaviour for a custom shard item:
     * <ul>
     *   <li>Silk Touch → cluster block itself</li>
     *   <li>Pickaxe, no Silk Touch → 4 shards + {@link ApplyBonusCount#addOreBonusCount Fortune} scaling
     *       (Fortune I avg 5.33, II avg 7, III avg 8.8 — matching the wiki)</li>
     *   <li>Any other tool, no Silk Touch → 2 shards flat</li>
     * </ul>
     * Pickaxes are matched via the {@code minecraft:pickaxes} item tag rather than
     * hardcoded item IDs, so modded pickaxes are handled automatically.
     */
    private LootTable.Builder createClusterDrops(Amethyst crystal) {
        Item shard = crystal.shard.get();

        // Match any item in the minecraft:pickaxes tag — the TagKey overload on
        // ItemPredicate.Builder resolves via BuiltInRegistries internally, so no
        // manual registry lookup is needed. Modded pickaxes are covered automatically.
        LootItemCondition.Builder isPickaxe = MatchTool.toolMatches(
                ItemPredicate.Builder.item().of(ItemTags.PICKAXES)
        );

        return LootTable.lootTable()
                // Silk Touch → drop the cluster block itself
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(hasSilkTouch())
                        .add(LootItem.lootTableItem(crystal.cluster.get())))

                // Pickaxe, no Silk Touch → 4 shards, Fortune-boosted
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(doesNotHaveSilkTouch())
                        .when(isPickaxe)
                        .add(LootItem.lootTableItem(shard)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(4)))
                                .apply(ApplyBonusCount.addOreBonusCount(
                                        registries.lookupOrThrow(Registries.ENCHANTMENT)
                                                .getOrThrow(Enchantments.FORTUNE)))))

                // Any other tool, no Silk Touch → 2 shards flat
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(doesNotHaveSilkTouch())
                        .when(isPickaxe.invert())
                        .add(LootItem.lootTableItem(shard)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2)))));
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Known blocks
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Tells the framework which blocks this provider is responsible for, covering all
     * blocks in {@link BasicStoneBlockSets#BLOCKS}, all blocks in
     * {@link BasicAmethystBlockSets#BLOCKS}, and the two vanilla amethyst slab/stairs
     * registered separately. Any block missing a loot table entry causes datagen to throw.
     */
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Stream.of(
                BasicStoneBlockSets.BLOCKS.getEntries().stream().map(DeferredHolder::get),
                BasicAmethystBlockSets.BLOCKS.getEntries().stream().map(DeferredHolder::get),
                Stream.of(
                        BasicAmethystBlockSets.VANILLA_AMETHYST_SLAB.get(),
                        BasicAmethystBlockSets.VANILLA_AMETHYST_STAIRS.get()
                )
        ).flatMap(s -> s).collect(Collectors.toList());
    }
}