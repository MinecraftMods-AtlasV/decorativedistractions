package info.atlasv.decorative_distractions.tint.block;

import info.atlasv.decorative_distractions.tint.TintDataComponents;
import info.atlasv.decorative_distractions.tint.TintDyeHistory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TintBlockEntity extends BlockEntity {
    // 0xFFFFFF = white -> multiplying the texture by white is a no-op, so the raw texture shows
    public static final int DEFAULT_COLOR = 0xFFFFFF;

    private int colour = DEFAULT_COLOR;


    // Ordered list of dye-application steps transferred from the item on placement.
    // Never null — defaults to TintDyeHistory.EMPTY
    private TintDyeHistory dyeHistory = TintDyeHistory.EMPTY;

    public TintBlockEntity(BlockPos pos, BlockState state) {
        super(TintBlockEntities.TINT_BLOCK.get(), pos, state);
    }

    // Accessors
    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public TintDyeHistory getDyeHistory() {
        return dyeHistory;
    }

    public void setDyeHistory(TintDyeHistory dyeHistory) {
        this.dyeHistory = dyeHistory != null ? dyeHistory : TintDyeHistory.EMPTY;
        setChanged();
        // No separate network update needed here - history is only read server-side
        // by the /GetTint command. It is still included in getUpdateTag() so a
        // freshly connecting client receives it, but no visual re-render is needed.
        // TODO: Figure out how this command would work for LuckPerms
    }


    // NBT persistence

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("colour", colour);

        // Encode TintDyeHistory via its Codec -> store as a list tag under "dye_history"
        if (!dyeHistory.isEmpty()) {
            TintDyeHistory.CODEC.encodeStart(NbtOps.INSTANCE, dyeHistory)
                    .ifSuccess(encoded -> tag.put("dye_history", encoded));
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        colour = tag.contains("colour") ? tag.getInt("colour") : DEFAULT_COLOR;

        if (tag.contains("dye_history")) {
            TintDyeHistory.CODEC.parse(NbtOps.INSTANCE, tag.get("dye_history"))
                    .ifSuccess(h -> dyeHistory = h)
                    .ifError(err -> dyeHistory = TintDyeHistory.EMPTY);
        } else {
            dyeHistory = TintDyeHistory.EMPTY;
        }
    }

    // Multiplayer sync

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}