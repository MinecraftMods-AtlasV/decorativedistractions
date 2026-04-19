package info.atlasv.decorative_distractions.tint;

import com.mojang.serialization.Codec;
import info.atlasv.decorative_distractions.tint.recipe.TintRecipeSerializers;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record TintDyeHistory(List<List<DyeColor>> steps) {

    /** Maximum number of dye application steps retained. Needed to prevent NBT based chunk corruption*/
    public static final int MAX_STEPS = 64;

    /** Empty history, default state for a freshly crafted item. */
    public static final TintDyeHistory EMPTY = new TintDyeHistory(List.of());

    // Codecs
    /**
     * Codec for disk persistence (item NBT / block entity NBT).
     * DyeColor is serialised by its lowercase name string, e.g. {@code "red"}.
     */
    public static final Codec<TintDyeHistory> CODEC =
            Codec.list(Codec.list(DyeColor.CODEC))
                    .xmap(TintDyeHistory::new, TintDyeHistory::steps);

    /**
     * Stream codec for network sync (client <-> server packets).
     * DyeColor is sent as a single byte (its ordinal).
     */
    private static final StreamCodec<ByteBuf, DyeColor> DYE_STREAM =
            ByteBufCodecs.BYTE.map(
                    b -> DyeColor.values()[b & 0xFF],
                    d -> (byte) d.ordinal()
            );

    private static final StreamCodec<ByteBuf, List<DyeColor>> DYE_LIST_STREAM =
            DYE_STREAM.apply(ByteBufCodecs.list());

    private static final StreamCodec<ByteBuf, List<List<DyeColor>>> DYE_LIST_LIST_STREAM =
            DYE_LIST_STREAM.apply(ByteBufCodecs.list());

    public static final StreamCodec<RegistryFriendlyByteBuf, TintDyeHistory> STREAM_CODEC =
            DYE_LIST_LIST_STREAM
                    .map(TintDyeHistory::new, TintDyeHistory::steps)
                    .cast();

    // Canonical constructor, can't be changed

    public TintDyeHistory {
        List<List<DyeColor>> copy = new ArrayList<>(steps.size());
        for (List<DyeColor> step : steps) {
            copy.add(Collections.unmodifiableList(new ArrayList<>(step)));
        }
        steps = Collections.unmodifiableList(copy);
    }

    // Mutation helpers (return new instances - records are immutable)
    /**
     * Returns a new {@code TintDyeHistory} with {@code newStep} appended.
     * If the result would exceed {@value #MAX_STEPS} steps, the oldest step is
     * dropped first (FIFO system).
     *
     * @param newStep the dyes used in the new crafting operation (must be non-empty).
     * @return a new history with the step appended.
     */
    public TintDyeHistory withStep(List<DyeColor> newStep) {
        List<List<DyeColor>> updated = new ArrayList<>(steps);
        updated.add(new ArrayList<>(newStep));
        // Enforce FIFO: drop oldest if over limit
        while (updated.size() > MAX_STEPS) {
            updated.remove(0);
        }
        return new TintDyeHistory(updated);
    }

    /**
     * Returns {@code true} if no dye steps have been recorded
     * (i.e. this is the default state for a freshly crafted item).
     */
    public boolean isEmpty() {
        return steps.isEmpty();
    }
}