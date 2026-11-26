package net.wolfking068.hybrids.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Shown while a player is turning into a vampire-werewolf hybrid.
 * The effect's timer is the time left until they become a hybrid.
 * It does nothing by itself: HybridTransformationHandler does the work.
 */
public class HybridTransformationEffect extends MobEffect {
    public HybridTransformationEffect() {
        super(MobEffectCategory.NEUTRAL, 0x7A1020);
    }

    /** Milk can't cure it. */
    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }
}
