package org.leodreamer.wildcard_pattern.wildcard;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.leodreamer.wildcard_pattern.wildcard.feature.IWildcardFilterComponent;
import org.leodreamer.wildcard_pattern.wildcard.feature.IWildcardIOComponent;
import org.leodreamer.wildcard_pattern.wildcard.feature.IWildcardSerializable;
import org.leodreamer.wildcard_pattern.wildcard.impl.*;

import static org.leodreamer.wildcard_pattern.wildcard.feature.IWildcardSerializable.IWildcardSerializer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WildcardSerializers {

    public static final Map<String, IWildcardSerializer<IWildcardIOComponent>> IO_SERIALIZERS = new Object2ObjectOpenHashMap<>();
    public static final Map<String, IWildcardSerializer<IWildcardFilterComponent>> FILTER_SERIALIZERS = new Object2ObjectOpenHashMap<>();

    // spotless:off
    public static final IWildcardSerializer<IWildcardIOComponent>
        IO_SIMPLE = register(IO_SERIALIZERS, SimpleIOComponent.Serializer::new),
        IO_TAG = register(IO_SERIALIZERS, TagIOComponent.Serializer::new);

    public static final IWildcardSerializer<IWildcardFilterComponent>
        FILTER_SIMPLE = register(FILTER_SERIALIZERS, SimpleFilterComponent.Serializer::new),
        FILTER_FLAG = register(FILTER_SERIALIZERS, FlagFilterComponent.Serializer::new),
        FILTER_PROPERTY = register(FILTER_SERIALIZERS, PropertyFilterComponent.Serializer::new);
    // spotless:on

    public static <T> IWildcardSerializer<T> register(
        Map<String, IWildcardSerializer<T>> map, Supplier<IWildcardSerializer<T>> factory
    ) {
        var serializer = factory.get();
        map.put(serializer.key(), serializer);
        return serializer;
    }

    public static <T> List<T> fromNbt(Map<String, IWildcardSerializer<T>> serializerMap, CompoundTag tag, String key) {
        if (tag.get(key) instanceof ListTag listTag) {
            return listTag.stream().map(nbt -> {
                if (nbt instanceof CompoundTag compound) {
                    var type = compound.getString("type");
                    var serializer = serializerMap.get(type);
                    if (serializer == null) {
                        throw new IllegalStateException("Unknown wildcard component type: " + type);
                    }
                    return serializer.deserialize(compound.getCompound("data"));
                }
                return null;
            }).filter(Objects::nonNull).toList();
        }
        return List.of();
    }

    public static <T extends IWildcardSerializable<T>> void toNbt(List<T> components, CompoundTag tag, String key) {
        var listTag = new ListTag();
        for (var component : components) {
            var serializer = component.getSerializer();
            var compound = new CompoundTag();
            compound.putString("type", serializer.key());
            compound.put("data", serializer.serialize(component));
            listTag.add(compound);
        }
        tag.put(key, listTag);
    }

    public static List<IWildcardIOComponent> fromNbtIO(WildcardPatternLogic.IO io, CompoundTag tag) {
        return fromNbt(IO_SERIALIZERS, tag, io.key);
    }

    public static void toNbtIO(List<IWildcardIOComponent> components, WildcardPatternLogic.IO io, CompoundTag tag) {
        toNbt(components, tag, io.key);
    }

    public static List<IWildcardFilterComponent> fromNbtFilter(CompoundTag tag) {
        return fromNbt(FILTER_SERIALIZERS, tag, "filter");
    }

    public static void toNbtFilter(List<IWildcardFilterComponent> components, CompoundTag tag) {
        toNbt(components, tag, "filter");
    }
}
