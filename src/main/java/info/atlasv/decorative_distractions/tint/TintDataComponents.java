package info.atlasv.decorative_distractions.tint;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TintDataComponents {

    private static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, DecorativeDistractions.MODID);

    /**
     * Records the ordered list of dye application steps for a tintable block item
     * or block entity. Each element is one crafting step (a list of
     * {@link net.minecraft.world.item.DyeColor}s used in that craft).
     *
     * <p>Capped at {@link TintDyeHistory#MAX_STEPS} steps to prevent chunk NBT corruption.
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TintDyeHistory>>
            DYE_HISTORY = COMPONENTS.register("dye_history", () ->
            DataComponentType.<TintDyeHistory>builder()
                    .persistent(TintDyeHistory.CODEC)
                    .networkSynchronized(TintDyeHistory.STREAM_CODEC)
                    .build());

    public static void register(IEventBus modEventBus) {
        COMPONENTS.register(modEventBus);
    }
}