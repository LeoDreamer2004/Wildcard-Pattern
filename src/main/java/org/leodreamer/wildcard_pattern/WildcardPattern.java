package org.leodreamer.wildcard_pattern;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.leodreamer.wildcard_pattern.lang.WildcardLangHandler;

@Mod(WildcardPattern.MOD_ID)
public class WildcardPattern {

    public static final String MOD_ID = "wildcard_pattern";
    public static final GTRegistrate REGISTRATE = GTRegistrate.create(MOD_ID);

    public WildcardPattern(FMLJavaModLoadingContext context) {
        REGISTRATE.registerRegistrate();
        var bus = context.getModEventBus();
        bus.addGenericListener(CoverDefinition.class, this::register);
        bus.addListener(EventPriority.LOWEST, (GatherDataEvent event) -> {
            REGISTRATE.addDataGenerator(ProviderType.LANG, WildcardLangHandler::init);
        });
        bus.register(this);
    }

    @SubscribeEvent
    public void register(GTCEuAPI.RegisterEvent<ResourceLocation, CoverDefinition> event) {
        WildcardItems.init();
    }
}
