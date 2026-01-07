package org.leodreamer.wildcard_pattern.wildcard.gui;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.*;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.leodreamer.wildcard_pattern.lang.DataGenScanned;
import org.leodreamer.wildcard_pattern.lang.RegisterLanguage;
import org.leodreamer.wildcard_pattern.wildcard.WildcardPatternLogic;
import org.leodreamer.wildcard_pattern.wildcard.feature.IWildcardFilterComponent;
import org.leodreamer.wildcard_pattern.wildcard.impl.FlagFilterComponent;
import org.leodreamer.wildcard_pattern.wildcard.impl.PropertyFilterComponent;
import org.leodreamer.wildcard_pattern.wildcard.impl.SimpleFilterComponent;

import java.util.function.Consumer;

@DataGenScanned
public class WildcardFilterFancyConfigurator implements IFancyUIProvider {

    private final WildcardPatternLogic logic;
    private final Consumer<ItemStack> onSave;

    private WildcardComponentListGroup<IWildcardFilterComponent> componentList;

    public WildcardFilterFancyConfigurator(
        WildcardPatternLogic logic,
        Consumer<ItemStack> onSave
    ) {
        this.logic = logic;
        this.onSave = onSave;
    }

    @RegisterLanguage("Material Filter Configuration")
    private static final String TITLE = "sftcore.item.wildcard_pattern.filter.title";

    @RegisterLanguage("Save")
    private static final String SAVE = "sftcore.item.wildcard_pattern.filter.save";

    @RegisterLanguage("Single")
    private static final String CREATE_SINGLE = "sftcore.item.wildcard_pattern.filter.single";

    @RegisterLanguage("Create a filter of a fixed material")
    private static final String CREATE_SINGLE_TOOLTIP = "sftcore.item.wildcard_pattern.filter.single.tooltip";

    @RegisterLanguage("Prop")
    private static final String CREATE_PROPERTY = "sftcore.item.wildcard_pattern.filter.property";

    @RegisterLanguage("Create a filter of materials with the given property")
    private static final String CREATE_PROPERTY_TOOLTIP = "sftcore.item.wildcard_pattern.filter.property.tooltip";

    @RegisterLanguage("Flag")
    private static final String CREATE_FLAG = "sftcore.item.wildcard_pattern.filter.flag";

    @RegisterLanguage("(ADVANCED) Create a filter of materials with the given flag")
    private static final String CREATE_FLAGS_TOOLTIP = "sftcore.item.wildcard_pattern.filter.flag.tooltip";

    @RegisterLanguage("Delete this filter")
    private static final String DELETE_TOOLTIP = "sftcore.item.wildcard_pattern.filter.delete.tooltip";

    @RegisterLanguage("Toggle the filter to a whitelist")
    private static final String TO_WHITELIST = "sftcore.item.wildcard_pattern.filter.to_whitelist";

    @RegisterLanguage("Toggle the filter to a blacklist")
    private static final String TO_BLACKLIST = "sftcore.item.wildcard_pattern.filter.to_blacklist";

    @Override
    public Widget createMainPage(FancyMachineUIWidget ui) {
        var global = new WidgetGroup(0, 0, 158, 180);

        componentList = new WildcardComponentListGroup<>(
            logic.getFilterComponents(), 0, 0, 158
        );

        componentList.setLineStyle(
            (i, group) -> {
                group.addWidget(
                    new ButtonWidget(138, 5, 14, 14, cd -> componentList.removeComponent(i))
                        .setBackground(GuiTextures.BUTTON, GuiTextures.CLOSE_ICON)
                        .setHoverTooltips(Component.translatable(DELETE_TOOLTIP))
                );
                var component = componentList.getComponents().get(i);
                group.addWidget(createWhitelistButton(component));
            }
        );

        var saveBtn = createBottomBtn(Component.translatable(SAVE), 126, cd -> save());
        var createSimple = createBottomBtn(Component.translatable(CREATE_SINGLE), 2, (cd) -> {
            componentList.addComponent(SimpleFilterComponent.empty());
        }).setHoverTooltips(Component.translatable(CREATE_SINGLE_TOOLTIP));
        var createProperty = createBottomBtn(Component.translatable(CREATE_PROPERTY), 37, (cd) -> {
            componentList.addComponent(PropertyFilterComponent.empty());
        }).setHoverTooltips(Component.translatable(CREATE_PROPERTY_TOOLTIP));
        var createFlag = createBottomBtn(Component.translatable(CREATE_FLAG), 72, (cd) -> {
            componentList.addComponent(FlagFilterComponent.empty());
        }).setHoverTooltips(Component.translatable(CREATE_FLAGS_TOOLTIP));

        global.addWidget(componentList);
        global.addWidget(saveBtn);
        global.addWidget(createSimple);
        global.addWidget(createProperty);
        global.addWidget(createFlag);
        return global;
    }

    @Override
    public IGuiTexture getTabIcon() {
        return new ItemStackTexture(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Aluminium));
    }

    @Override
    public Component getTitle() {
        return Component.translatable(TITLE);
    }

    private ButtonWidget createBottomBtn(Component label, int x, Consumer<ClickData> onPressed) {
        return new ButtonWidget(
            x, 155, 30, 20, new GuiTextureGroup(
                ResourceBorderTexture.BUTTON_COMMON.copy(), new TextTexture(label.getString())
            ), onPressed
        );
    }

    private ButtonWidget createWhitelistButton(IWildcardFilterComponent component) {
        var toggle = new ButtonWidget(120, 5, 14, 14, cd -> {});
        toggle.setOnPressCallback(cd -> {
            boolean cur = component.isWhitelist();
            component.setWhitelist(!cur);
            setButtonWhitelist(toggle, !cur);
        });
        setButtonWhitelist(toggle, component.isWhitelist());
        return toggle;
    }

    private void setButtonWhitelist(ButtonWidget button, boolean whitelist) {
        button.setBackground(
            GuiTextures.BUTTON, new TextTexture(whitelist ? "W" : "B")
                .setColor(ColorPattern.DARK_GRAY.color).setDropShadow(false)
        );
        button
            .setHoverTooltips(whitelist ? Component.translatable(TO_BLACKLIST) : Component.translatable(TO_WHITELIST));
    }

    private void save() {
        var components = componentList.getComponents();
        var stack = logic.setFilterComponents(components);
        components.forEach(IWildcardFilterComponent::onSave);
        onSave.accept(stack);
    }
}
