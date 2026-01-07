package org.leodreamer.wildcard_pattern.wildcard.gui;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.leodreamer.wildcard_pattern.lang.DataGenScanned;
import org.leodreamer.wildcard_pattern.lang.RegisterLanguage;
import org.leodreamer.wildcard_pattern.wildcard.WildcardPatternLogic;
import org.leodreamer.wildcard_pattern.wildcard.feature.IWildcardIOComponent;
import org.leodreamer.wildcard_pattern.wildcard.impl.SimpleIOComponent;
import org.leodreamer.wildcard_pattern.wildcard.impl.TagIOComponent;

import java.util.function.Consumer;

@DataGenScanned
public class WildcardIOFancyConfigurator implements IFancyUIProvider {

    private final WildcardPatternLogic logic;
    private final WildcardPatternLogic.IO io;
    private final Consumer<ItemStack> onSave;

    private WildcardComponentListGroup<IWildcardIOComponent> componentList;

    public WildcardIOFancyConfigurator(
        WildcardPatternLogic logic,
        WildcardPatternLogic.IO io,
        Consumer<ItemStack> onSave
    ) {
        this.logic = logic;
        this.io = io;
        this.onSave = onSave;
    }

    @RegisterLanguage("Input Configuration")
    private static final String TITLE_IN = "sftcore.item.wildcard_pattern.io.title_in";

    @RegisterLanguage("Output Configuration")
    private static final String TITLE_OUT = "sftcore.item.wildcard_pattern.io.title_out";

    @RegisterLanguage("Save")
    private static final String SAVE = "sftcore.item.wildcard_pattern.io.save";

    @RegisterLanguage("Single")
    private static final String CREATE_SINGLE = "sftcore.item.wildcard_pattern.io.single";

    @RegisterLanguage("Tag")
    private static final String CREATE_TAG = "sftcore.item.wildcard_pattern.io.tag";

    @RegisterLanguage("Create an ingredient with a fixed item")
    private static final String CREATE_SINGLE_TOOLTIP = "sftcore.item.wildcard_pattern.io.single.tooltip";

    @RegisterLanguage("Create an ingredient with the GT tag")
    private static final String CREATE_TAG_TOOLTIP = "sftcore.item.wildcard_pattern.io.tag.tooltip";

    @RegisterLanguage("Delete this ingredient")
    private static final String DELETE_TOOLTIP = "sftcore.item.wildcard_pattern.io.delete.tooltip";

    @Override
    public Widget createMainPage(FancyMachineUIWidget ui) {
        var global = new WidgetGroup(0, 0, 158, 180);

        componentList = new WildcardComponentListGroup<>(logic.getIOComponents(io), 0, 0, 158);
        componentList.setLineStyle(
            (i, group) -> {
                group.addWidget(
                    new ButtonWidget(138, 5, 14, 14, cd -> componentList.removeComponent(i))
                        .setBackground(GuiTextures.BUTTON, GuiTextures.CLOSE_ICON)
                        .setHoverTooltips(Component.translatable(DELETE_TOOLTIP))
                );
            }
        );

        var saveBtn = createBottomBtn(Component.translatable(SAVE), 126, cd -> save());
        var createSimple = createBottomBtn(Component.translatable(CREATE_SINGLE), 2, (cd) -> {
            componentList.addComponent(SimpleIOComponent.empty());
        }).setHoverTooltips(Component.translatable(CREATE_SINGLE_TOOLTIP));
        var createTagPrefix = createBottomBtn(Component.translatable(CREATE_TAG), 37, (cd) -> {
            componentList.addComponent(TagIOComponent.empty());
        }).setHoverTooltips(Component.translatable(CREATE_TAG_TOOLTIP));

        global.addWidget(componentList);
        global.addWidget(saveBtn);
        global.addWidget(createSimple);
        global.addWidget(createTagPrefix);
        return global;
    }

    @Override
    public IGuiTexture getTabIcon() {
        return new TextTexture(io.name());
    }

    @Override
    public Component getTitle() {
        return Component.translatable(io == WildcardPatternLogic.IO.IN ? TITLE_IN : TITLE_OUT);
    }

    private ButtonWidget createBottomBtn(Component label, int x, Consumer<ClickData> onPressed) {
        return new ButtonWidget(
            x, 155, 30, 20, new GuiTextureGroup(
                ResourceBorderTexture.BUTTON_COMMON.copy(), new TextTexture(label.getString())
            ), onPressed
        );
    }

    private void save() {
        var components = componentList.getComponents();
        var stack = logic.setIOComponents(io, components);
        components.forEach(IWildcardIOComponent::onSave);
        onSave.accept(stack);
    }
}
