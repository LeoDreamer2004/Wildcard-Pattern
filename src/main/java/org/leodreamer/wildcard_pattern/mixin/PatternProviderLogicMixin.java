package org.leodreamer.wildcard_pattern.mixin;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.stacks.AEKey;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.util.inv.AppEngInternalInventory;
import org.leodreamer.wildcard_pattern.WildcardItems;
import org.leodreamer.wildcard_pattern.wildcard.WildcardPatternDecoder;
import org.leodreamer.wildcard_pattern.wildcard.WildcardPatternLogic;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;

@Mixin(value = PatternProviderLogic.class, remap = false)
public abstract class PatternProviderLogicMixin {

    @Shadow
    @Final
    private List<IPatternDetails> patterns;

    @Shadow
    @Final
    private Set<AEKey> patternInputs;

    @Shadow
    @Final
    private AppEngInternalInventory patternInventory;

    @Shadow
    @Final
    private PatternProviderLogicHost host;


    @Inject(method = "updatePatterns", at = @At(value = "INVOKE", target = "Ljava/util/Set;clear()V", shift = At.Shift.AFTER))
    void decodeWildcardPattern(CallbackInfo ci){
        var level = host.getBlockEntity().getLevel();
        for (var stack : patternInventory) {
            if (WildcardPatternDecoder.INSTANCE.isEncodedPattern(stack)) {
                WildcardPatternLogic.decodePatterns(stack, level)
                    .forEach(this::wildcard$updatePattern);
            }
        }
    }


    @Unique
    private void wildcard$updatePattern(IPatternDetails details) {
        if (details != null) {
            patterns.add(details);
            for (var input : details.getInputs()) {
                for (var inputCandidate : input.getPossibleInputs()) {
                    patternInputs.add(inputCandidate.what().dropSecondary());
                }
            }
        }
    }
}
