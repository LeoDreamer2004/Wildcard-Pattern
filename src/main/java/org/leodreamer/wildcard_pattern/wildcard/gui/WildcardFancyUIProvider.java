package org.leodreamer.wildcard_pattern.wildcard.gui;

import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.api.gui.fancy.TabsWidget;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.leodreamer.wildcard_pattern.WildcardItems;
import org.leodreamer.wildcard_pattern.lang.DataGenScanned;
import org.leodreamer.wildcard_pattern.lang.RegisterLanguage;
import org.leodreamer.wildcard_pattern.wildcard.WildcardPatternLogic;

import java.util.function.Consumer;

@DataGenScanned
public final class WildcardFancyUIProvider implements IFancyUIProvider {

    private final WildcardPatternLogic logic;
    private final Level level;
    private final Consumer<ItemStack> saveCallback;
    @Getter
    private final int heldSlotIndex;

    public WildcardFancyUIProvider(
            WildcardPatternLogic logic,
            Level level,
            Consumer<ItemStack> saveCallback,
            int heldSlotIndex
    ) {
        this.logic = logic;
        this.level = level;
        this.saveCallback = saveCallback;
        this.heldSlotIndex = heldSlotIndex;
    }

    @RegisterLanguage("Wildcard Pattern Config")
    private static final String TITLE_CONFIG = "sftcore.item.wildcard_pattern.config";

    @Override
    public Component getTitle() {
        return Component.translatable(TITLE_CONFIG);
    }

    @Override
    public Widget createMainPage(FancyMachineUIWidget ui) {
        return new WildcardIndexPage(logic, level, 0, 0, 158, 80);
    }

    @Override
    public IGuiTexture getTabIcon() {
        return new ItemStackTexture(WildcardItems.WILDCARD_PATTERN.asItem());
    }

    @Override
    public void attachSideTabs(TabsWidget sideTabs) {
        sideTabs.setMainTab(this);

        sideTabs.attachSubTab(
                new WildcardIOFancyConfigurator(logic, WildcardPatternLogic.IO.IN, saveCallback)
        );
        sideTabs.attachSubTab(
                new WildcardIOFancyConfigurator(logic, WildcardPatternLogic.IO.OUT, saveCallback)
        );
        sideTabs.attachSubTab(
                new WildcardFilterFancyConfigurator(logic, saveCallback)
        );
    }
}
