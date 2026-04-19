package info.atlasv.decorative_distractions.tint.block;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

public class TintBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, DecorativeDistractions.MODID);

    // Single block entity type shared by base blocks, slabs, and stairs.
    // All three block types produce a TintBlockEntity, so they can all be
    // listed here under one type - no separate TINT_SLAB / TINT_STAIR needed.
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TintBlockEntity>> TINT_BLOCK =
            BLOCK_ENTITY_TYPES.register("tint_block", () ->
                    BlockEntityType.Builder.of(TintBlockEntity::new,
                            TintBlocks.TINTED_COBBLESTONE.block.get(),
                            TintBlocks.TINTED_COBBLESTONE.slab.get(),
                            TintBlocks.TINTED_COBBLESTONE.stairs.get(),
                            TintBlocks.TINTED_GRASS_BLOCK.block.get(),
                            TintBlocks.TINTED_GRASS_BLOCK.slab.get(),
                            TintBlocks.TINTED_GRASS_BLOCK.stairs.get(),
                            TintBlocks.TINTED_STONE.block.get(),
                            TintBlocks.TINTED_STONE.slab.get(),
                            TintBlocks.TINTED_STONE.stairs.get()
                    ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}