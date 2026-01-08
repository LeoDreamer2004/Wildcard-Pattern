package org.leodreamer.wildcard_pattern;

import appeng.core.definitions.AEItems;
import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.MV;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plate;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Polyethylene;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;

@GTAddon
public class WildcardGTAddon implements IGTAddon {
    @Override
    public GTRegistrate getRegistrate() {
        return WildcardPattern.REGISTRATE;
    }

    @Override
    public void initializeAddon() {
    }

    @Override
    public String addonModId() {
        return WildcardPattern.MOD_ID;
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        ASSEMBLER_RECIPES.recipeBuilder("wildcard_pattern")
            .inputItems(AEItems.BLANK_PATTERN.m_5456_(), 16)
            .inputItems(ChemicalHelper.get(plate, Polyethylene, 4))
            .outputItems(WildcardItems.WILDCARD_PATTERN)
            .duration(200)
            .EUt(VA[MV])
            .save(provider);
    }
}
