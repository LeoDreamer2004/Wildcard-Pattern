package org.leodreamer.wildcard_pattern.mixin;

import appeng.api.crafting.PatternDetailsHelper;
import net.minecraft.world.item.ItemStack;
import org.leodreamer.wildcard_pattern.WildcardItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PatternDetailsHelper.class)
public class PatternDetailsHelperMixin {

    @Inject(method = "isEncodedPattern", at = @At("HEAD"), cancellable = true, remap = false)
    private static void allowWildcardPatterns(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(WildcardItems.WILDCARD_PATTERN.asItem())) {
            cir.setReturnValue(true);
        }
    }
}
