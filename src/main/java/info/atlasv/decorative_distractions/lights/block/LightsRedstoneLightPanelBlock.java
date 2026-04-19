package info.atlasv.decorative_distractions.lights.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

public class LightsRedstoneLightPanelBlock extends Block {

    public static final MapCodec<LightsRedstoneLightPanelBlock> CODEC =
            simpleCodec(LightsRedstoneLightPanelBlock::new);

    public static final BooleanProperty LIT      = RedstoneTorchBlock.LIT;
    public static final BooleanProperty INVERTED = BooleanProperty.create("inverted");
    public static final DirectionProperty FACING  = BlockStateProperties.FACING;

    // FACING = the direction the glass face points outward.
    // Rule: if the glass points in direction D, the panel sits flush with that face of the block space I think?
    //   NORTH (-Z): panel at z=0..2
    //   SOUTH (+Z): panel at z=14..16
    //   EAST  (+X): panel at x=14..16
    //   WEST  (-X): panel at x=0..2
    //   UP    (+Y): panel at y=14..16
    //   DOWN  (-Y): panel at y=0..2
    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Map.of(
            Direction.NORTH, Block.box(5, 5, 12, 11,11,16),
            Direction.SOUTH, Block.box(5, 5, 0,11,11,4),
            Direction.EAST, Block.box( 0,5, 5, 4,11,11),
            Direction.WEST, Block.box( 12, 5, 5, 16, 11,11),
            Direction.UP, Block.box(   5, 0,5, 11,4,11),
            Direction.DOWN, Block.box( 5, 12, 5, 11,16, 11)
    ));

    @Override
    public MapCodec<LightsRedstoneLightPanelBlock> codec() {
        return CODEC;
    }

    public LightsRedstoneLightPanelBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING,   Direction.NORTH)
                .setValue(LIT,      false)
                .setValue(INVERTED, false));
    }

    // Placement

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Returns the exact face of the target block that was clicked, which is the direction
        // the panel should face (outward, toward the player).
        Direction facing  = context.getClickedFace();
        boolean  powered  = context.getLevel().hasNeighborSignal(context.getClickedPos());
        // Fresh placement is never inverted; lit = powered for a normal lamp.
        boolean  lit      = powered; // inverted defaults false, so inverted != powered = powered
        return this.defaultBlockState()
                .setValue(FACING,   facing)
                .setValue(LIT,      lit)
                .setValue(INVERTED, false);
    }

    // Shape

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level,
                                  BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    // Redstone reaction

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos,
                                   Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            boolean lit         = state.getValue(LIT);
            boolean inverted    = state.getValue(INVERTED);
            boolean powered     = level.hasNeighborSignal(pos);
            boolean shouldBeLit = inverted != powered; // XOR handles both modes

            if (lit != shouldBeLit) {
                if (lit) {
                    // Turning off - small delay, same as vanilla redstone lamp I think?
                    // not sure if I even have to define this?
                    level.scheduleTick(pos, this, 4);
                } else {
                    level.setBlock(pos, state.setValue(LIT, true), 2);
                }
            }
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level,
                        BlockPos pos, RandomSource random) {
        boolean inverted    = state.getValue(INVERTED);
        boolean powered     = level.hasNeighborSignal(pos);
        boolean shouldBeLit = inverted != powered;

        if (state.getValue(LIT) != shouldBeLit) {
            level.setBlock(pos, state.setValue(LIT, shouldBeLit), 2);
        }
    }

    // Right-click toggles INVERTED

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level,
                                               BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (!level.isClientSide) {
            boolean inverted = !state.getValue(INVERTED);
            boolean powered  = level.hasNeighborSignal(pos);
            boolean lit      = inverted != powered;

            level.setBlock(pos, state
                    .setValue(INVERTED, inverted)
                    .setValue(LIT,      lit), 3);

            player.displayClientMessage(
                    Component.translatable(inverted
                            ? "block.decorative_distractions.light_panel.inverted"
                            : "block.decorative_distractions.light_panel.normal"),
                    true // action bar, not chat
            );
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    // Blockstate definition

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT, INVERTED);
    }
}