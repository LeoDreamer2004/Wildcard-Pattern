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
import org.leodreamer.wildcard_pattern.wildcard.WildcardPatternLogic;
import org.spongepowered.asm.mixin.*;

import java.util.List;
import java.util.Set;

@Mixin(PatternProviderLogic.class)
public abstract class PatternProviderLogicMixin {

    @Shadow(remap = false)
    @Final
    private List<IPatternDetails> patterns;

    @Shadow(remap = false)
    @Final
    private Set<AEKey> patternInputs;

    @Shadow(remap = false)
    @Final
    private AppEngInternalInventory patternInventory;

    @Shadow(remap = false)
    @Final
    private PatternProviderLogicHost host;

    @Shadow(remap = false)
    @Final
    private IManagedGridNode mainNode;

    /**
     * @author LeoDreamer
     * @reason Let the pattern support wildcard patterns
     */
    @Overwrite(remap = false)
    public void updatePatterns() {
        patterns.clear();
        patternInputs.clear();
        var level = host.getBlockEntity().getLevel();

        for (var stack : patternInventory) {
            // injected by Wildcard
            if (stack.is(WildcardItems.WILDCARD_PATTERN.asItem())) {
                WildcardPatternLogic.decodePatterns(stack, level)
                    .forEach(this::wildcard$updatePattern);
                continue;
            }
            var details = PatternDetailsHelper.decodePattern(stack, level);
            wildcard$updatePattern(details);
        }

        ICraftingProvider.requestUpdate(mainNode);
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
