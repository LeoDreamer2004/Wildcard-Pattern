package org.leodreamer.wildcard_pattern.wildcard;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.GenericStack;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.wildcard_pattern.wildcard.feature.IWildcardFilterComponent;
import org.leodreamer.wildcard_pattern.wildcard.feature.IWildcardIOComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class WildcardPatternLogic {

    private final ItemStack stack;

    public enum IO {

        IN("in"),
        OUT("out");

        public final String key;

        IO(String key) {
            this.key = key;
        }
    }

    private WildcardPatternLogic(ItemStack stack) {
        this.stack = stack;
    }

    public static WildcardPatternLogic on(ItemStack stack) {
        return new WildcardPatternLogic(stack);
    }

    public @NotNull List<IWildcardIOComponent> getIOComponents(IO io) {
        return WildcardSerializers.fromNbtIO(io, stack.getOrCreateTag());
    }

    public ItemStack setIOComponents(IO io, @NotNull List<IWildcardIOComponent> components) {
        WildcardSerializers.toNbtIO(components, io, stack.getOrCreateTag());
        return stack;
    }

    public @NotNull List<IWildcardFilterComponent> getFilterComponents() {
        return WildcardSerializers.fromNbtFilter(stack.getOrCreateTag());
    }

    public ItemStack setFilterComponents(@NotNull List<IWildcardFilterComponent> components) {
        WildcardSerializers.toNbtFilter(components, stack.getOrCreateTag());
        return stack;
    }

    public @Nullable GenericStack[] getIOStacks(IO io, Material material) {
        var components = getIOComponents(io);
        if (components.isEmpty()) return null;

        var stacks = new ArrayList<GenericStack>();
        for (var component : components) {
            var stack = component.apply(material);
            if (stack == null) return null;
            stacks.add(stack);
        }
        return stacks.toArray(new GenericStack[0]);
    }

    public boolean test(Material material) {
        for (var component : getFilterComponents()) {
            if (!component.test(material)) {
                return false;
            }
        }
        return true;
    }

    public Stream<IPatternDetails> generateAllPatterns(Level level) {
        return GTCEuAPI.materialManager.getRegisteredMaterials().stream()
            .filter(this::test)
            .map(material -> {
                var input = getIOStacks(IO.IN, material);
                var output = getIOStacks(IO.OUT, material);
                if (input == null || output == null || input.length == 0 || output.length == 0)
                    return null;
                // FIXME: a little silly here
                var item = PatternDetailsHelper.encodeProcessingPattern(input, output, "Someone - Oh Wildcard!");
                return PatternDetailsHelper.decodePattern(item, level);
            })
            .filter(Objects::nonNull);
    }

    public static Stream<IPatternDetails> decodePatterns(ItemStack stack, Level level) {
        return on(stack).generateAllPatterns(level);
    }
}
