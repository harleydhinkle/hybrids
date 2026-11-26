package net.wolfking068.hybrids.common.player;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.wolfking068.hybrids.Hybrids;
import net.wolfking068.hybrids.api.entities.hybrids.HybridTransformationHandler;

/**
 * Lets a vampire use the Werewolves mod's werewolf tooth (werewolves:werewolf_tooth),
 * or a werewolf use Vampirism's vampire fang (vampirism:vampire_fang),
 * to try to become a vampire-werewolf hybrid.
 * Everyone else keeps the items' normal behaviour.
 */
@Mod.EventBusSubscriber(modid = Hybrids.MOD_ID)
public final class HybridToothEvents {
    private HybridToothEvents() {
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();
        boolean vampireWithTooth = stack.is(de.teamlapen.werewolves.core.ModItems.WEREWOLF_TOOTH.get())
                && HybridTransformationHandler.isVampire(player);
        boolean werewolfWithFang = stack.is(de.teamlapen.vampirism.core.ModItems.VAMPIRE_FANG.get())
                && HybridTransformationHandler.isWerewolf(player);
        if (!vampireWithTooth && !werewolfWithFang) {
            return;
        }

        // Stop the item's normal code (from Werewolves or Vampirism) from running
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (HybridPlayerData.isVampireWerewolf(serverPlayer)) {
            serverPlayer.displayClientMessage(Component.translatable("message.hybrids.tooth.already_hybrid"), true);
            return;
        }
        if (HybridPlayerData.isTransforming(serverPlayer)) {
            int minutes = HybridTransformationHandler.minutesLeft(HybridPlayerData.getTransformTicks(serverPlayer));
            serverPlayer.displayClientMessage(Component.translatable("message.hybrids.tooth.already_transforming", minutes), true);
            return;
        }

        if (!serverPlayer.getAbilities().instabuild) {
            stack.shrink(1);
        }
        HybridTransformationHandler.tryToBecomeHybrid(serverPlayer);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer serverPlayer) {
            HybridTransformationHandler.tickTransformation(serverPlayer);
        }
    }
}
