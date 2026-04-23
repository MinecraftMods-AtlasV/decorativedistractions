package info.atlasv.decorative_distractions.tint;

import info.atlasv.decorative_distractions.tint.block.TintBlock;
import info.atlasv.decorative_distractions.tint.block.TintBlockEntity;
import info.atlasv.decorative_distractions.tint.block.TintSlabBlock;
import info.atlasv.decorative_distractions.tint.block.TintStairBlock;
import info.atlasv.decorative_distractions.tint.item.TintBlockItem;
import info.atlasv.decorative_distractions.tint.TintDataComponents;
import info.atlasv.decorative_distractions.tint.TintDyeHistory;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;


public class TintEntry {

    public final String name;

    // Base block
    public final DeferredBlock<TintBlock> block;
    public final DeferredItem<TintBlockItem> item;

    // Slab
    public final DeferredBlock<TintSlabBlock> slab;
    public final DeferredItem<TintBlockItem> slabItem;

    // Stairs
    public final DeferredBlock<TintStairBlock> stairs;
    public final DeferredItem<TintBlockItem> stairsItem;

    // Constructor
    /**
     * @param name   Registry name for the base block. Slab and stair names are
     *               derived automatically: {@code <name>_slab} and {@code <name>_stairs}.
     * @param props  Block behaviour properties, copied for the slab and stair as well.
     * @param blocks The modules {@link DeferredRegister.Blocks} instance.
     * @param items  The modules {@link DeferredRegister.Items} instance.
     */
    public TintEntry(
            String name,
            BlockBehaviour.Properties props,
            DeferredRegister.Blocks blocks,
            DeferredRegister.Items items
    ) {
        this.name = name;

        // base block
        this.block = blocks.register(name, () -> new TintBlock(props));
        this.item = registerItem(items, name, this.block);

        // slab
        String slabName = name + "_slab";
        this.slab = blocks.register(slabName, () -> new TintSlabBlock(BlockBehaviour.Properties.ofFullCopy(this.block.get())));
        this.slabItem = registerItem(items, slabName, this.slab);

        // stairs
        // StairBlock needs a base BlockState supplier, using a lazy reference so that
        // the base block is already registered before the lambda is used.
        String stairsName = name + "_stairs";
        this.stairs = blocks.register(stairsName,
                () -> new TintStairBlock(this.block.get().defaultBlockState(),
                        BlockBehaviour.Properties.ofFullCopy(this.block.get())));
        this.stairsItem = registerItem(items, stairsName, this.stairs);
    }

    /**
     * Wires the slab and stairs supplier back-references onto the base block item.
     * Must be called after the deferred registers have been bound to the event bus
     * (i.e. after {@code BLOCKS.register(eventBus)} and {@code ITEMS.register(eventBus)}
     * have been called), so that {@link net.neoforged.neoforge.registries.DeferredItem#get()}
     * returns a bound value. Called by
     * {@link info.atlasv.decorative_distractions.tint.block.TintBlocks#register}.
     */
    public void wireSuppliers() {
        this.item.get().setSlabAndStairsSuppliers(
                () -> this.slabItem.get(),
                () -> this.stairsItem.get()
        );
    }

    // Helpers
    /**
     * Creates and registers a {@link TintBlockItem} with the standard tint components
     * (white {@code DYED_COLOR} + empty {@code DYE_HISTORY}).
     */
    private static <B extends TintBlock> DeferredItem<TintBlockItem> registerItem(
            DeferredRegister.Items items,
            String name,
            DeferredBlock<? extends net.minecraft.world.level.block.Block> blockHolder
    ) {
        return items.register(name, () -> new TintBlockItem(
                blockHolder.get(),
                new Item.Properties()
                        .component(
                                DataComponents.DYED_COLOR,
                                new DyedItemColor(TintBlockEntity.DEFAULT_COLOR, true))
                        .component(
                                TintDataComponents.DYE_HISTORY.get(),
                                TintDyeHistory.EMPTY)
        ));
    }
}