package org.leodreamer.wildcard_pattern;

import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.api.gui.fancy.TabsWidget;
import com.gregtechceu.gtceu.api.item.component.IItemUIFactory;
import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.leodreamer.wildcard_pattern.lang.DataGenScanned;
import org.leodreamer.wildcard_pattern.lang.RegisterLanguage;
import org.leodreamer.wildcard_pattern.wildcard.WildcardPatternLogic;
import org.leodreamer.wildcard_pattern.wildcard.gui.*;

import java.util.function.Consumer;

@DataGenScanned
public class WildcardPatternBehavior implements IItemUIFactory{

    @Override
    public ModularUI createUI(HeldItemUIFactory.HeldItemHolder heldItemHolder, Player player) {
        InteractionHand hand = heldItemHolder.getHand();
        ItemStack stack = player.getItemInHand(hand);

        WildcardPatternLogic logic = WildcardPatternLogic.on(stack);

        Consumer<ItemStack> save = s -> player.setItemInHand(hand, s);
        int heldSlotIndex = hand == InteractionHand.MAIN_HAND
                        ? player.getInventory().selected
                        : 40; // offhand slot index

        WildcardFancyUIProvider provider = new WildcardFancyUIProvider(logic, player.level(), save, heldSlotIndex);

        return new ModularUI(176, 166, heldItemHolder, player)
            .widget(new WildcardHeldItemUI(provider, 176, 166));
    }
}
