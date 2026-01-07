package org.leodreamer.wildcard_pattern;

import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

import static com.gregtechceu.gtceu.common.data.GTItems.attach;
import static org.leodreamer.wildcard_pattern.WildcardPattern.REGISTRATE;

public class WildcardItems {

    public static final ItemEntry<ComponentItem> WILDCARD_PATTERN = REGISTRATE
        .item("wildcard_pattern", ComponentItem::create)
        .lang("Wildcard Pattern")
        .model(NonNullBiConsumer.noop())
        .properties(p -> p.stacksTo(1))
        .onRegister(attach(new WildcardPatternBehavior()))
        .register();

    public static void init() {
    }
}
