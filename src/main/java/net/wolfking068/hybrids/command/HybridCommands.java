package net.wolfking068.hybrids.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.wolfking068.hybrids.api.entities.hybrids.HybridForm;
import net.wolfking068.hybrids.common.player.HybridFormMode;
import net.wolfking068.hybrids.common.player.HybridPlayerData;

import java.util.Collection;
import java.util.List;

public final class HybridCommands {
    private HybridCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("hybrids")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("vampire_werewolf")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(context -> setVampireWerewolf(
                                        context.getSource(),
                                        EntityArgument.getPlayers(context, "targets"),
                                        false
                                ))
                                .then(Commands.argument("force", BoolArgumentType.bool())
                                        .executes(context -> setVampireWerewolf(
                                                context.getSource(),
                                                EntityArgument.getPlayers(context, "targets"),
                                                BoolArgumentType.getBool(context, "force")
                                        )))))
                .then(Commands.literal("form")
                        .then(formMode("human", HybridFormMode.HUMAN))
                        .then(formMode("beast", HybridFormMode.BEAST))
                        .then(formMode("full", HybridFormMode.FULL)));

        dispatcher.register(root);
    }

    private static int setVampireWerewolf(CommandSourceStack source, Collection<ServerPlayer> targets, boolean force) {
        int changed = 0;
        int skipped = 0;

        for (ServerPlayer player : targets) {
            if (!force && !HybridPlayerData.canBecomeVampireWerewolf(player)) {
                skipped++;
                player.sendSystemMessage(Component.translatable("command.hybrids.vampire_werewolf.need_factions"));
                continue;
            }

            HybridPlayerData.setForm(player, HybridForm.VAMPIRE_WEREWOLF);
            HybridPlayerData.resetHybridAttempts(player);
            player.sendSystemMessage(Component.translatable("command.hybrids.vampire_werewolf.success"));
            changed++;
        }

        int changedCount = changed;
        int skippedCount = skipped;
        source.sendSuccess(() -> Component.translatable("command.hybrids.vampire_werewolf.summary", changedCount, skippedCount), true);
        return changed;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> formMode(String name, HybridFormMode mode) {
        return Commands.literal(name)
                .executes(context -> setFormMode(
                        context.getSource(),
                        List.of(context.getSource().getPlayerOrException()),
                        mode
                ))
                .then(Commands.argument("targets", EntityArgument.players())
                        .executes(context -> setFormMode(
                                context.getSource(),
                                EntityArgument.getPlayers(context, "targets"),
                                mode
                        )));
    }

    private static int setFormMode(CommandSourceStack source, Collection<ServerPlayer> targets, HybridFormMode mode) {
        int changed = 0;
        int skipped = 0;

        for (ServerPlayer player : targets) {
            if (!HybridPlayerData.isVampireWerewolf(player)) {
                skipped++;
                player.sendSystemMessage(Component.translatable("command.hybrids.form.need_hybrid"));
                continue;
            }

            HybridPlayerData.setFormMode(player, mode);
            player.sendSystemMessage(Component.translatable("command.hybrids.form.success", mode.getSerializedName()));
            changed++;
        }

        int changedCount = changed;
        int skippedCount = skipped;
        source.sendSuccess(() -> Component.translatable("command.hybrids.form.summary", changedCount, skippedCount), true);
        return changed;
    }
}
