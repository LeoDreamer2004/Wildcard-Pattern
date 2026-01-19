package org.leodreamer.wildcard_pattern.wildcard.gui;

import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;

public class WildcardHeldItemUI extends FancyMachineUIWidget {

    final WildcardFancyUIProvider provider;

    public WildcardHeldItemUI(WildcardFancyUIProvider mainPage, int width, int height) {
        super(mainPage, width, height);
        provider = mainPage;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        int lockedIndex = provider.getHeldSlotIndex();
        if(playerInventory != null){
            playerInventory.widgets.stream()
                    .filter(SlotWidget.class::isInstance)
                    .map(SlotWidget.class::cast)
                    .filter(s -> parsePlayerInvSlotIndex(s) == lockedIndex)
                    .findFirst()
                    .ifPresent(s -> s.setActive(false));
        }
    }

    private static int parsePlayerInvSlotIndex(SlotWidget slot) {
        String id = slot.getId();
        if (id == null || !id.startsWith("player_inv_")) {
            return -1;
        }
        return Integer.parseInt(id.substring("player_inv_".length()));
    }

}
