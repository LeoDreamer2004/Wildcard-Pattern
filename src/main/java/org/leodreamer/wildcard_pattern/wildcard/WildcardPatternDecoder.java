package org.leodreamer.wildcard_pattern.wildcard;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.IPatternDetailsDecoder;
import appeng.api.stacks.AEItemKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.wildcard_pattern.WildcardItems;

public class WildcardPatternDecoder implements IPatternDetailsDecoder {
    public static final WildcardPatternDecoder INSTANCE = new WildcardPatternDecoder();

    @Override
    public boolean isEncodedPattern(ItemStack stack) {
        return stack.is(WildcardItems.WILDCARD_PATTERN.get());
    }

    @Override
    public @Nullable IPatternDetails decodePattern(AEItemKey what, Level level) {
        //we use our decode logic because AE only allow one pattern here
        return null;
    }

    @Override
    public @Nullable IPatternDetails decodePattern(ItemStack what, Level level, boolean tryRecovery) {
        return null;
    }
}
