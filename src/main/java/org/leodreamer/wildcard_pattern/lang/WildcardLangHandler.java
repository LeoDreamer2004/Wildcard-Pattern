package org.leodreamer.wildcard_pattern.lang;

import com.gregtechceu.gtceu.data.lang.LangHandler;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import org.leodreamer.wildcard_pattern.util.ReflectUtils;

public class WildcardLangHandler extends LangHandler {

    public static void init(RegistrateLangProvider provider) {
        for (var clazz : ReflectUtils.getClassesWithAnnotation(DataGenScanned.class)) {
            var langMap = ReflectUtils.getStaticFieldsWithAnnotation(clazz, RegisterLanguage.class, String.class);
            for (var entry : langMap.entrySet()) {
                String key = entry.getValue();
                String translation = entry.getKey().value();
                provider.add(key, translation);
            }
        }
    }
}
