package org.leodreamer.wildcard_pattern.wildcard.feature;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

public interface IWildcardComponentUI {

    void createUILine(WidgetGroup line);

    default void onSave() {}
}
