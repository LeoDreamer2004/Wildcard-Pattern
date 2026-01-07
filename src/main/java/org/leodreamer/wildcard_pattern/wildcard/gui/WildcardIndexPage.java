package org.leodreamer.wildcard_pattern.wildcard.gui;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.gui.widget.TankWidget;
import com.gregtechceu.gtceu.api.transfer.fluid.CustomFluidTank;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.utils.GTMath;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.wildcard_pattern.lang.DataGenScanned;
import org.leodreamer.wildcard_pattern.lang.RegisterLanguage;
import org.leodreamer.wildcard_pattern.wildcard.WildcardPatternLogic;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@DataGenScanned
public class WildcardIndexPage extends WidgetGroup {

    private WidgetGroup inputGroup, outputGroup;
    private final List<IPatternDetails> patterns;
    int tick = 0;
    private static final int PATTERN_CYCLE = 20;

    @RegisterLanguage("%d Patterns Available")
    static final String PATTERNS_AVAILABLE = "sftcore.item.wildcard_pattern.patterns_available";

    public WildcardIndexPage(WildcardPatternLogic logic, Level level, int x, int y, int width, int height) {
        super(x, y, width, height);

        patterns = logic.generateAllPatterns(level).toList();

        var component = Component.translatable(PATTERNS_AVAILABLE, patterns.size());
        int fontWidth = Minecraft.getInstance().font.width(component);
        addWidget(new LabelWidget((width - fontWidth) / 2, 5, component.getString()));

        initPatternDisplay();
        displayPattern(patterns.isEmpty() ? null : patterns.getFirst());
    }

    private void initPatternDisplay() {
        int w = getSizeWidth(), h = getSizeHeight();

        inputGroup = new WidgetGroup(10, 18, (w - 10) / 2, h - 20);
        outputGroup = new WidgetGroup((w + 30) / 2, 18, (w - 10) / 2, h - 20);

        var bar = new Widget((w - 10) / 2, 32, 15, 10).setBackground(
            GuiTextures.PROGRESS_BAR_ARROW.getSubTexture(0, 0, 1, 0.5)
        );

        addWidget(inputGroup);
        addWidget(bar);
        addWidget(outputGroup);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (++tick % PATTERN_CYCLE == 0) {
            if (patterns.isEmpty()) {
                displayPattern(null);
            } else {
                displayPattern(patterns.get((tick / PATTERN_CYCLE) % patterns.size()));
            }
        }
    }

    private void displayPattern(@Nullable IPatternDetails pattern) {
        if (pattern == null) {
            displayPatternSlots(inputGroup, Stream.of());
            displayPatternSlots(outputGroup, Stream.of());
        } else {
            displayPatternSlots(
                inputGroup, Arrays.stream(pattern.getInputs())
                    .map(i -> new GenericStack(i.getPossibleInputs()[0].what(), i.getMultiplier()))
            );
            displayPatternSlots(outputGroup, Arrays.stream(pattern.getOutputs()));
        }
    }

    private void displayPatternSlots(WidgetGroup group, Stream<GenericStack> stacks) {
        group.clearAllWidgets();
        var iterator = stacks.iterator();
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 3; c++) {
                if (!iterator.hasNext()) {
                    var slot = new SlotWidget(new CustomItemStackHandler(), 0, 18 * c, 18 * r, false, false);
                    group.addWidget(slot);
                    continue;
                }

                var next = iterator.next();
                if (next.what() instanceof AEItemKey item) {
                    var slot = new SlotWidget(new CustomItemStackHandler(), 0, 18 * c, 18 * r, false, false);
                    slot.setItem(item.toStack(GTMath.saturatedCast(next.amount())));
                    group.addWidget(slot);
                } else if (next.what() instanceof AEFluidKey fluid) {
                    var handler = new CustomFluidTank(Integer.MAX_VALUE);
                    handler.setFluid(fluid.toStack(GTMath.saturatedCast(next.amount())));
                    var slot = new TankWidget(handler, 18 * c, 18 * r, false, false)
                        .setBackground(GuiTextures.SLOT).setClientSideWidget();
                    group.addWidget(slot);
                }
            }
        }
    }
}
