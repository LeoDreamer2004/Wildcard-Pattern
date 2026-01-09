package org.leodreamer.wildcard_pattern.wildcard.gui;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import lombok.Getter;
import org.leodreamer.wildcard_pattern.lang.DataGenScanned;
import org.leodreamer.wildcard_pattern.wildcard.feature.IWildcardComponentUI;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@DataGenScanned
public class WildcardComponentListGroup<L extends IWildcardComponentUI> extends WidgetGroup {

    @Getter
    private final ArrayList<L> components;

    private static final int LINE_HEIGHT = 25;
    private static final int MAX_COMPONENTS = 6;

    private BiConsumer<Integer, WidgetGroup> lineStyle = null;

    public WildcardComponentListGroup(List<L> components, int x, int y, int width) {
        super(x, y, width, 0);
        this.components = components.size() < MAX_COMPONENTS ? new ArrayList<>(components) :
            new ArrayList<>(components.subList(0, MAX_COMPONENTS));
        onComponentsChanged();
    }

    public void setLineStyle(BiConsumer<Integer, WidgetGroup> lineStyle) {
        this.lineStyle = lineStyle;
        onComponentsChanged();
    }

    public void addComponent(L component) {
        if (components.size() >= MAX_COMPONENTS) {
            return;
        }
        components.add(component);
        onComponentsChanged();
    }

    public void removeComponent(int index) {
        if (index < 0 || index >= components.size()) {
            return;
        }
        components.remove(index);
        onComponentsChanged();
    }

    private void onComponentsChanged() {
        int len = components.size();
        setSizeHeight(10 + len * LINE_HEIGHT);
        clearAllWidgets();

        for (int i = 0; i < len; i++) {
            var component = components.get(i);
            var lineGroup = new WidgetGroup(0, 3 + i * LINE_HEIGHT, 156, LINE_HEIGHT);
            if (lineStyle != null) {
                lineStyle.accept(i, lineGroup);
            }
            component.createUILine(lineGroup);
            addWidget(lineGroup);
        }
    }
}
