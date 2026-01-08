package org.leodreamer.wildcard_pattern.wildcard.impl;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlag;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.SelectorWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.wildcard_pattern.api.IMaterialFlags;
import org.leodreamer.wildcard_pattern.gui.PhantomGTMaterialSlot;
import org.leodreamer.wildcard_pattern.lang.DataGenScanned;
import org.leodreamer.wildcard_pattern.lang.RegisterLanguage;
import org.leodreamer.wildcard_pattern.wildcard.WildcardSerializers;
import org.leodreamer.wildcard_pattern.wildcard.feature.IWildcardFilterComponent;

import java.util.List;

@DataGenScanned
public class FlagFilterComponent implements IWildcardFilterComponent {

    private Material example;
    @Nullable
    private MaterialFlag flag;
    @Getter
    private boolean whitelist;

    private PhantomGTMaterialSlot exampleSlot;
    private SelectorWidget flagSelector;
    private WidgetGroup parent = null;

    private static final IGuiTexture GROUP_BG_WHITE = ResourceBorderTexture.BUTTON_COMMON.copy()
        .setColor(ColorPattern.PINK.color);
    private static final IGuiTexture GROUP_BG_BLACK = ResourceBorderTexture.BUTTON_COMMON.copy()
        .setColor(ColorPattern.PURPLE.color);

    public static FlagFilterComponent empty() {
        return new FlagFilterComponent(null, GTMaterials.NULL, false);
    }

    public FlagFilterComponent(@Nullable MaterialFlag flag, Material example, boolean whitelist) {
        this.example = example;
        this.flag = flag;
        this.whitelist = whitelist;
    }

    @Override
    public void setWhitelist(boolean whiteList) {
        this.whitelist = whiteList;
        if (parent != null) {
            parent.setBackground(whiteList ? GROUP_BG_WHITE : GROUP_BG_BLACK);
        }
    }

    @Override
    public boolean test(Material material) {
        if (flag == null) return true;
        return whitelist == material.hasFlag(flag);
    }

    @Override
    public void createUILine(WidgetGroup line) {
        line.setBackground(whitelist ? GROUP_BG_WHITE : GROUP_BG_BLACK);
        parent = line;

        exampleSlot = new PhantomGTMaterialSlot(new CustomItemStackHandler(), 0, 3, 3, this::changeExample);
        flagSelector = new MySelectorWidget(25, 5, 80, 15, getMaterialFlagNames(example));
        flagSelector.setOnChanged(this::updateFlag);

        if (example != GTMaterials.NULL) {
            exampleSlot.setMaterial(example);
        }
        if (flag != null) {
            flagSelector.setValue(flag.toString());
        }

        line.addWidget(exampleSlot);
        line.addWidget(flagSelector);
    }

    @Override
    public IWildcardSerializer<IWildcardFilterComponent> getSerializer() {
        return WildcardSerializers.FILTER_FLAG;
    }

    private boolean changeExample(Material material) {
        var ok = material != GTMaterials.NULL;
        if (ok) {
            this.example = material;
            var flags = getMaterialFlagNames(material);
            flagSelector.setCandidates(flags);
            flagSelector.setValue(flags.get(0));
            updateFlag(flags.get(0));
        }
        return ok;
    }

    private void updateFlag(String flagName) {
        if (flagName == null || flagName.isEmpty()) {
            flag = null;
        } else {
            flag = MaterialFlag.getByName(flagName);
        }
    }

    @Override
    public void onSave() {
        example = exampleSlot.getMaterial();
        updateFlag(flagSelector.getValue());
    }

    @RegisterLanguage("no flag")
    private static final String NO_FLAG = "sftcore.item.wildcard_pattern.filter.flag.no_flag";

    private static List<String> getMaterialFlagNames(Material material) {
        var flags = ((IMaterialFlags) material.getFlags()).sftcore$getFlags();

        if (flags.isEmpty()) {
            return List.of(Component.translatable(NO_FLAG).getString());
        }
        return flags.stream().map(MaterialFlag::toString).toList();
    }

    public static class Serializer implements IWildcardSerializer<IWildcardFilterComponent> {

        @Override
        public String key() {
            return "flag";
        }

        @Override
        public @NotNull CompoundTag serialize(IWildcardFilterComponent component) {
            var tag = new CompoundTag();
            var simple = (FlagFilterComponent) component;
            tag.putBoolean("whitelist", simple.whitelist);
            tag.putString("example", simple.example.getResourceLocation().toString());
            if (simple.flag != null) {
                tag.putString("flag", simple.flag.toString());
            }
            return tag;
        }

        @Override
        public @NotNull IWildcardFilterComponent deserialize(CompoundTag nbt) {
            var whitelist = nbt.getBoolean("whitelist");
            var materialId = nbt.getString("example");
            var flagName = nbt.getString("flag");
            MaterialFlag flag = null;
            if (!flagName.isEmpty()) {
                flag = MaterialFlag.getByName(flagName);
            }
            var material = GTCEuAPI.materialManager.getMaterial(materialId);
            if (material == null) material = GTMaterials.NULL;
            return new FlagFilterComponent(flag, material, whitelist);
        }
    }

    private static class MySelectorWidget extends SelectorWidget {

        public MySelectorWidget(int x, int y, int width, int height, List<String> candidates) {
            super(x, y, width, height, candidates, ColorPattern.WHITE.color);
            button.setBackground(GuiTextures.BUTTON);
            textTexture.setColor(ColorPattern.DARK_GRAY.color);
        }
    }
}
