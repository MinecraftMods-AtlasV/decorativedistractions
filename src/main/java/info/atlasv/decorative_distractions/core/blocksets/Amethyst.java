package info.atlasv.decorative_distractions.core.blocksets;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Amethyst {

    //  Base registry name (e.g. "rose_quartz")
    public final String name;

    // Texture paths - all derived automatically from the textureDirPath constructor argument
    // Exposed as public fields so datagen providers can reference them when iterating crystal sets
    public final String blockTexture;
    public final String buddingTexture;
    public final String clusterTexture;
    public final String largeBudTexture;
    public final String mediumBudTexture;
    public final String smallBudTexture;
    // Item texture path - the `block/` prefix is swapped to `item/` automatically.
    //e.g. `item/crystals/purple/crystal`
    public final String shardTexture;

    // Holders for the block variations of the crystals
    public final DeferredBlock<Block> block;
    public final DeferredBlock<BuddingAmethystBlock> buddingBlock;
    public final DeferredBlock<AmethystClusterBlock> cluster;
    public final DeferredBlock<AmethystClusterBlock> largeBud;
    public final DeferredBlock<AmethystClusterBlock> mediumBud;
    public final DeferredBlock<AmethystClusterBlock> smallBud;
    public final DeferredBlock<SlabBlock> slab;
    public final DeferredBlock<StairBlock> stairs;
    public final DeferredHolder<Item, Item> shard;

    /**
     * Registers a full amethyst-type block set and their BlockItems.
     *
     * <p>Texture paths are derived automatically from {@code textureDirPath}:
     * <ul>
     *   <li>Block textures: {@code textureDirPath + "/<stem>"} (e.g. {@code "block/crystals/purple/block"})</li>
     *   <li>Shard item texture: {@code textureDirPath} with {@code "block/"} replaced by {@code "item/"}, then {@code + "/crystal"}
     *       (e.g. {@code "item/crystals/purple/crystal"})</li>
     * </ul>
     *
     * <p>The budding block automatically grows its own crystal stages using
     * {@link GrowingBuddingBlock}
     *
     * <p>Drop behaviour mirrors vanilla:
     * <ul>
     *   <li>TODO: {@code cluster} drops {@code shard} ×4 (Fortune-boosted)</li>
     *   <li>TODO: Bud variants drop nothing without Silk Touch</li>
     *   <li>TODO: {@code buddingBlock} drops nothing even with Silk Touch</li>
     *   <li>TODO: {@code slab} and {@code stairs} use standard slab/stair loot</li>
     * </ul>
     *
     * @param name           Base registry name stem (e.g. {@code "rose_quartz"}).
     *                       Block IDs are derived following vanilla conventions:
     *                       {@code rose_quartz_block}, {@code budding_rose_quartz},
     *                       {@code rose_quartz_cluster}, {@code large_rose_quartz_bud}, {@code rose_quartz_shard},
     *                       {@code rose_quartz_block_slab}, {@code rose_quartz_block_stairs}.
     * @param textureDirPath Path to the colour folder inside {@code block/crystals/}
     *                       (e.g. {@code "block/crystals/purple"}). The seven texture paths
     *                       are derived from this automatically — see field docs above.
     * @param blocks         The modules {@link DeferredRegister.Blocks} instance.
     * @param items          The modules {@link DeferredRegister} for items.
     */
    public Amethyst(
            String name,
            String textureDirPath,
            DeferredRegister.Blocks blocks,
            DeferredRegister<Item> items
    ) {
        this.name = name;

        // Derive all texture paths from textureDirPath.
        // Block/bud textures sit under block/crystals/<colour>/
        // The shard item texture sits under item/crystals/<colour>/ - auto swaps the prefix.
        this.blockTexture     = textureDirPath + "/block";
        this.buddingTexture   = textureDirPath + "/budding";
        this.clusterTexture   = textureDirPath + "/cluster";
        this.largeBudTexture  = textureDirPath + "/large_bud";
        this.mediumBudTexture = textureDirPath + "/medium_bud";
        this.smallBudTexture  = textureDirPath + "/small_bud";
        this.shardTexture     = textureDirPath.replaceFirst("^block/", "item/") + "/crystal";

        // Block properties so they act as close as possible to the vanilla stuff
        BlockBehaviour.Properties blockProps = BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK)
                .mapColor(MapColor.COLOR_PURPLE); // TODO: Change for each crystal colour with each dye colour
        BlockBehaviour.Properties buddingProps = BlockBehaviour.Properties.ofFullCopy(Blocks.BUDDING_AMETHYST)
                .mapColor(MapColor.COLOR_PURPLE); // TODO: Change for each crystal colour with each dye colour
        BlockBehaviour.Properties clusterProps = BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER)
                .mapColor(MapColor.COLOR_PURPLE); // TODO: Change for each crystal colour with each dye colour
        BlockBehaviour.Properties largeBudProps = BlockBehaviour.Properties.ofFullCopy(Blocks.LARGE_AMETHYST_BUD)
                .mapColor(MapColor.COLOR_PURPLE); // TODO: Change for each crystal colour with each dye colour
        BlockBehaviour.Properties mediumBudProps = BlockBehaviour.Properties.ofFullCopy(Blocks.MEDIUM_AMETHYST_BUD)
                .mapColor(MapColor.COLOR_PURPLE); // TODO: Change for each crystal colour with each dye colour
        BlockBehaviour.Properties smallBudProps = BlockBehaviour.Properties.ofFullCopy(Blocks.SMALL_AMETHYST_BUD)
                .mapColor(MapColor.COLOR_PURPLE); // TODO: Change for each crystal colour with each dye colour

        // Block Registration
        // Naming follows vanilla conventions: <name>_block, budding_<name>, <name>_cluster, etc.
        this.block = blocks.register(name + "_block",
                () -> new Block(blockProps));

        // GrowingBuddingBlock captures `this` (the Amethyst instance). The lambda is
        // evaluated lazily during registry resolution, after this constructor has fully
        // completed, so all DeferredBlock fields below are guaranteed to be assigned
        // by the time randomTick() is ever called.
        this.buddingBlock = blocks.<BuddingAmethystBlock>register("budding_" + name,
                () -> new GrowingBuddingBlock(this, buddingProps));
        this.cluster = blocks.register(name + "_cluster",
                () -> new AmethystClusterBlock(7, 3, clusterProps));
        this.largeBud = blocks.register("large_" + name + "_bud",
                () -> new AmethystClusterBlock(5, 3, largeBudProps));
        this.mediumBud = blocks.register("medium_" + name + "_bud",
                () -> new AmethystClusterBlock(4, 3, mediumBudProps));
        this.smallBud = blocks.register("small_" + name + "_bud",
                () -> new AmethystClusterBlock(3, 4, smallBudProps));

        // Slab & Stairs Registration
        // Both use blockProps - same hardness, sound, and tool requirement
        // as the solid crystal block. The StairBlock base state is resolved lazily via
        // this.block.get() inside the lambda; by the time the lambda runs the block
        // holder above is guaranteed to be resolved.
        this.slab = blocks.register(name + "_block_slab",
                () -> new SlabBlock(blockProps));
        this.stairs = blocks.register(name + "_block_stairs",
                () -> new StairBlock(this.block.get().defaultBlockState(), blockProps));

        // BlockItem Registration
        registerBlockItem(items, name + "_block",           this.block);
        registerBlockItem(items, "budding_" + name,         this.buddingBlock);
        registerBlockItem(items, name + "_cluster",         this.cluster);
        registerBlockItem(items, "large_" + name + "_bud",  this.largeBud);
        registerBlockItem(items, "medium_" + name + "_bud", this.mediumBud);
        registerBlockItem(items, "small_" + name + "_bud",  this.smallBud);
        registerBlockItem(items, name + "_block_slab",      this.slab);
        registerBlockItem(items, name + "_block_stairs",    this.stairs);

        // Shard Item Registration
        this.shard = items.register(name + "_shard",
                () -> new Item(new Item.Properties()));
    }

    private <T extends Block> void registerBlockItem(
            DeferredRegister<Item> items,
            String id,
            DeferredBlock<T> deferredBlock
    ) {
        items.register(id, () -> new BlockItem(deferredBlock.get(), new Item.Properties()));
    }

    // ----------------------------------------------------------------------

    /**
     * A {@link BuddingAmethystBlock} that grows the crystal stages of a specific
     * {@link Amethyst} set, had to be remade as the vanilla implementation is
     * hardcoded for amethyst.
     *
     * <p>Growth logic mirrors vanilla, 1-in-5 random tick chance, all six
     * directions, waterlogging preserved, only the target blocks are
     * swapped out for the crystal set's stages.
     *
     * <p>Instantiated automatically by {@link Amethyst}'s constructor, there's no
     * need to reference this class directly at call.
     */
    public static class GrowingBuddingBlock extends BuddingAmethystBlock {

        // Mirrors vanilla's growth rate: one attempt per five random ticks on average
        private static final int GROWTH_CHANCE = 5;
        private static final Direction[] DIRECTIONS = Direction.values();

        private final Amethyst set;

        public GrowingBuddingBlock(Amethyst set, BlockBehaviour.Properties properties) {
            super(properties);
            this.set = set;
        }

        @Override
        public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
            if (random.nextInt(GROWTH_CHANCE) != 0) return;

            Direction direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
            BlockPos targetPos = pos.relative(direction);
            BlockState targetState = level.getBlockState(targetPos);

            // Walk up the growth chain: air/water → small → medium → large → cluster.
            // A bud only advances if it is already facing this budding block (direction).
            Block nextStage = null;

            if (canGrowAt(targetState)) {
                nextStage = set.smallBud.get();
            } else if (targetState.is(set.smallBud.get())
                    && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
                nextStage = set.mediumBud.get();
            } else if (targetState.is(set.mediumBud.get())
                    && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
                nextStage = set.largeBud.get();
            } else if (targetState.is(set.largeBud.get())
                    && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
                nextStage = set.cluster.get();
            }

            if (nextStage != null) {
                BlockState newState = nextStage.defaultBlockState()
                        .setValue(AmethystClusterBlock.FACING, direction)
                        .setValue(AmethystClusterBlock.WATERLOGGED,
                                targetState.getFluidState().getType() == Fluids.WATER);
                level.setBlockAndUpdate(targetPos, newState);
            }
        }

        // A bud can start growing in an empty space (air or a water source block).
        // Mirrors vanillas canClusterGrowAtState.
        private static boolean canGrowAt(BlockState state) {
            return state.isAir()
                    || (state.is(Blocks.WATER) && state.getFluidState().isSource());
        }
    }
}