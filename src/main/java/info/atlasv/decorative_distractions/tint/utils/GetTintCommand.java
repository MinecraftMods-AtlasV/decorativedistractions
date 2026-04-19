package info.atlasv.decorative_distractions.tint.utils;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import info.atlasv.decorative_distractions.tint.TintDataComponents;
import info.atlasv.decorative_distractions.tint.TintDyeHistory;
import info.atlasv.decorative_distractions.tint.block.TintBlockEntity;
import info.atlasv.decorative_distractions.tint.recipe.TintRecipeSerializers;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Registers the /GetTint command with two sub-commands:
 * <ul>
 *   <li>{@code /GetTint hand} – reads the colour + history from the item in the player's main hand.</li>
 *   <li>{@code /GetTint block <x y z>} – reads the colour + history from the {@link TintBlockEntity} at the given position.</li>
 * </ul>
 *
 * <p>If the item/block carries a {@link TintDyeHistory} component (written by
 * {@link TintRecipeSerializers.TintDyeRecipe}), the exact dye steps
 * are reported directly. For items/blocks that pre-date the history component,
 * a greedy approximation is performed instead (see {@link #findClosestDyes}). Which arguably isn't needed because it was never release with the approximation</p>
 */
public class GetTintCommand {

    // Constants

    /** Maximum number of dyes considered in a single approximation step. */
    private static final int MAX_DYES = 8;

    private static final SimpleCommandExceptionType NOT_A_TINT_BLOCK =
            new SimpleCommandExceptionType(Component.literal("That block is not a tintable block."));

    private static final SimpleCommandExceptionType NOT_A_TINT_ITEM =
            new SimpleCommandExceptionType(Component.literal("You are not holding a tintable item."));

    // Pre-computed dye data

    private record DyeEntry(DyeColor dye, String name, float r, float g, float b) {
        static DyeEntry of(DyeColor dye) {
            int packed = dye.getTextureDiffuseColor(); // 0xAARRGGBB
            float r = (packed >> 16) & 0xFF;
            float g = (packed >>  8) & 0xFF;
            float b =  packed        & 0xFF;
            String name = java.util.Arrays.stream(dye.getName().replace('_', ' ').split(" "))
                    .map(w -> Character.toUpperCase(w.charAt(0)) + w.substring(1))
                    .collect(java.util.stream.Collectors.joining(" "));
            return new DyeEntry(dye, name, r, g, b);
        }
    }

    private static final DyeEntry[] DYE_ENTRIES;
    static {
        DyeColor[] values = DyeColor.values();
        DYE_ENTRIES = new DyeEntry[values.length];
        for (int i = 0; i < values.length; i++) DYE_ENTRIES[i] = DyeEntry.of(values[i]);
    }

    // Registration (I *hate* how the command registration system works this looks horrible!)

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("GetTint")
                        .then(Commands.literal("hand")
                                .executes(ctx -> getTintFromHand(ctx.getSource())))
                        .then(Commands.literal("block")
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(ctx -> getTintFromBlock(
                                                ctx.getSource(),
                                                BlockPosArgument.getLoadedBlockPos(ctx, "pos")))))
        );
    }

    // Command implementation

    private static int getTintFromHand(CommandSourceStack source) throws CommandSyntaxException {
        var player = source.getPlayerOrException();
        ItemStack held = player.getMainHandItem();

        DyedItemColor dyedColor = held.get(DataComponents.DYED_COLOR);
        if (dyedColor == null) throw NOT_A_TINT_ITEM.create();

        int packedRgb = dyedColor.rgb();
        TintDyeHistory history = held.getOrDefault(
                TintDataComponents.DYE_HISTORY.get(), TintDyeHistory.EMPTY);

        return report(source, packedRgb, history, "held item");
    }

    private static int getTintFromBlock(CommandSourceStack source, BlockPos pos)
            throws CommandSyntaxException {

        BlockEntity be = source.getLevel().getBlockEntity(pos);
        if (!(be instanceof TintBlockEntity tbe)) throw NOT_A_TINT_BLOCK.create();

        int packedRgb = tbe.getColour();
        TintDyeHistory history = tbe.getDyeHistory();
        String context = "block at " + pos.getX() + " " + pos.getY() + " " + pos.getZ();

        return report(source, packedRgb, history, context);
    }

    // Reporting

    private static int report(CommandSourceStack source, int packedRgb,
                              TintDyeHistory history, String context) {
        String hexColor = String.format("#%06X", packedRgb);
        StringBuilder msg = new StringBuilder();
        msg.append("Colour of ").append(context).append(": ").append(hexColor).append("\n");

        if (!history.isEmpty()) {
            // Exact history available
            msg.append("Dye history (").append(history.steps().size()).append(" step(s)):\n");
            int stepNum = 1;
            for (List<DyeColor> step : history.steps()) {
                java.util.Map<String, Long> counts = step.stream()
                        .collect(java.util.stream.Collectors.groupingBy(
                                GetTintCommand::formatDyeName,
                                java.util.stream.Collectors.counting()));
                StringBuilder stepStr = new StringBuilder("  Step ").append(stepNum++).append(": ");
                counts.forEach((name, count) -> {
                    if (count == 1) stepStr.append(name).append(", ");
                    else            stepStr.append(name).append(" x").append(count).append(", ");
                });
                if (stepStr.toString().endsWith(", "))
                    stepStr.setLength(stepStr.length() - 2);
                msg.append(stepStr).append("\n");
            }
        } else {
            // No history: approximation fall back
            float targetR = (packedRgb >> 16) & 0xFF;
            float targetG = (packedRgb >>  8) & 0xFF;
            float targetB =  packedRgb        & 0xFF;

            List<DyeEntry> bestCombo = findClosestDyes(targetR, targetG, targetB);

            msg.append("(No dye history recorded: showing closest approximation)\n");

            if (bestCombo.isEmpty()) {
                msg.append("No dyes found (colour may be default white).");
            } else {
                java.util.Map<String, Long> counts = bestCombo.stream()
                        .collect(java.util.stream.Collectors.groupingBy(
                                e -> e.name(), java.util.stream.Collectors.counting()));
                msg.append("Closest dye combination (").append(bestCombo.size()).append(" dye(s)):\n");
                counts.forEach((name, count) -> {
                    if (count == 1) msg.append("  - ").append(name).append("\n");
                    else            msg.append("  - ").append(name).append(" x").append(count).append("\n");
                });
                float[] avg = averageColour(bestCombo);
                int approxPacked = packRgb(Math.round(avg[0]), Math.round(avg[1]), Math.round(avg[2]));
                double distance = euclidean(targetR, targetG, targetB, avg[0], avg[1], avg[2]);
                msg.append(String.format("Approximate result colour: #%06X (Δ=%.1f)", approxPacked, distance));
            }
        }

        source.sendSuccess(() -> Component.literal(msg.toString()), false);
        return 1;
    }

    // Approximation (fallback)

    private static List<DyeEntry> findClosestDyes(float targetR, float targetG, float targetB) {
        List<DyeEntry> chosen = new ArrayList<>();
        float sumR = 0, sumG = 0, sumB = 0;
        double bestDistance = euclidean(targetR, targetG, targetB, 255, 255, 255);

        for (int step = 0; step < MAX_DYES; step++) {
            DyeEntry bestDye = null;
            double bestNewDistance = bestDistance;
            int count = chosen.size() + 1;

            for (DyeEntry candidate : DYE_ENTRIES) {
                float newR = (sumR + candidate.r()) / count;
                float newG = (sumG + candidate.g()) / count;
                float newB = (sumB + candidate.b()) / count;
                double d = euclidean(targetR, targetG, targetB, newR, newG, newB);
                if (d < bestNewDistance) { bestNewDistance = d; bestDye = candidate; }
            }

            if (bestDye == null) break;
            chosen.add(bestDye);
            sumR += bestDye.r(); sumG += bestDye.g(); sumB += bestDye.b();
            bestDistance = bestNewDistance;
        }

        return chosen;
    }

    // Helpers

    private static String formatDyeName(DyeColor dye) {
        return java.util.Arrays.stream(dye.getName().replace('_', ' ').split(" "))
                .map(w -> Character.toUpperCase(w.charAt(0)) + w.substring(1))
                .collect(java.util.stream.Collectors.joining(" "));
    }

    private static float[] averageColour(List<DyeEntry> dyes) {
        float r = 0, g = 0, b = 0;
        for (DyeEntry e : dyes) { r += e.r(); g += e.g(); b += e.b(); }
        int n = dyes.size();
        return new float[]{ r / n, g / n, b / n };
    }

    private static double euclidean(float r1, float g1, float b1, float r2, float g2, float b2) {
        float dr = r1 - r2, dg = g1 - g2, db = b1 - b2;
        return Math.sqrt(dr * dr + dg * dg + db * db);
    }

    private static int packRgb(int r, int g, int b) {
        return (Math.max(0, Math.min(255, r)) << 16)
                | (Math.max(0, Math.min(255, g)) <<  8)
                |  Math.max(0, Math.min(255, b));
    }
}