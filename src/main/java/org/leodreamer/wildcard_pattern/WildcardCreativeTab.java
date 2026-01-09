package org.leodreamer.wildcard_pattern;

import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import org.leodreamer.wildcard_pattern.lang.DataGenScanned;
import org.leodreamer.wildcard_pattern.lang.RegisterLanguage;

import static org.leodreamer.wildcard_pattern.WildcardPattern.MOD_ID;
import static org.leodreamer.wildcard_pattern.WildcardPattern.REGISTRATE;

@DataGenScanned
public class WildcardCreativeTab {
    @RegisterLanguage("Wildcard Pattern")
    private static final String NAME = "sftcore.creative_tab";

    public static final RegistryEntry<CreativeModeTab> WILDCARD_TAB = REGISTRATE
        .defaultCreativeTab(
            MOD_ID,
            builder -> builder
                .displayItems(
                    new GTCreativeModeTabs.RegistrateDisplayItemsGenerator(MOD_ID, REGISTRATE)
                )
                .title(Component.translatable(NAME))
                .icon(WildcardItems.WILDCARD_PATTERN::asStack)
                .build()
        )
        .register();

    public static void init() {
        REGISTRATE.creativeModeTab(() -> WILDCARD_TAB);
    }
}
