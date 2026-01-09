package org.leodreamer.wildcard_pattern.gui;

import appeng.api.stacks.GenericStack;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKey;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.api.item.GTBucketItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.wildcard_pattern.util.ReflectUtils;

public class GenericGTTag {

    private final TagPrefix itemTag;
    @Nullable
    private final FluidStorageKey fluidTag;
    private final GenericType type;

    public enum GenericType {

        ITEM("item"),
        FLUID("fluid");

        public final String key;

        GenericType(String key) {
            this.key = key;
        }
    }

    // I Hate U GTM! Why you do not introduce an empty object until GTM1.6+?
    public static final TagPrefix ITEM_NULL = new TagPrefix("null");
    // What is even worse that I cannot create an empty fluid storage key,
    // as the constructor is private. So use null anyway...
    public static final GenericGTTag EMPTY = item(ITEM_NULL);

    private GenericGTTag(GenericType type, TagPrefix itemTag, @Nullable FluidStorageKey fluidTag) {
        this.type = type;
        this.itemTag = itemTag;
        this.fluidTag = fluidTag;
    }

    public static GenericGTTag item(@NotNull TagPrefix itemTag) {
        return new GenericGTTag(GenericType.ITEM, itemTag, null);
    }

    public static GenericGTTag fluid(@Nullable FluidStorageKey fluidTag) {
        return new GenericGTTag(GenericType.FLUID, null, fluidTag);
    }

    public String name() {
        if (type == GenericType.ITEM) {
            return itemTag.name;
        } else if (fluidTag != null) {
            return fluidTag.getResourceLocation().getPath();
        } else {
            return "null";
        }
    }

    public GenericStack toGenericStack(Material material, int amount) {
        if (type == GenericType.ITEM) {
            var item = ChemicalHelper.get(itemTag, material, amount);
            return GenericStack.fromItemStack(item);
        } else {
            var fluid = getFluidByKey(material, fluidTag);
            var stack = fluid == null ? FluidStack.EMPTY : new FluidStack(fluid, amount);
            return GenericStack.fromFluidStack(stack);
        }
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        if (type == GenericType.ITEM) {
            tag.putString(GenericType.ITEM.key, itemTag.name);
        } else if (fluidTag != null) {
            tag.putString(GenericType.FLUID.key, fluidTag.getResourceLocation().toString());
        }
        return tag;
    }

    public static GenericGTTag fromNBT(CompoundTag tag) {
        if (tag.contains(GenericType.ITEM.key)) {
            var item = TagPrefix.get(tag.getString(GenericType.ITEM.key));
            return item(item);
        } else if (tag.contains(GenericType.FLUID.key)) {
            var rl = new ResourceLocation(tag.getString(GenericType.FLUID.key));
            var fluid = FluidStorageKey.getByName(rl);
            return fluid(fluid);
        }
        return EMPTY;
    }

    @NotNull
    public ItemStack createItemOrBucket(Material material) {
        if (type == GenericType.ITEM) {
            return ChemicalHelper.get(itemTag, material);
        } else {
            var fluid = getFluidByKey(material, fluidTag);
            if (fluid == null) return ItemStack.EMPTY;
            return new ItemStack(fluid.getBucket());
        }
    }

    public static GenericGTTag fromItemOrBucket(Item item) {
        if (item instanceof BucketItem bucket) {
            var fluid = bucket.getFluid();
            if (bucket instanceof GTBucketItem gtBucket) {
                var material = ReflectUtils.getFieldValue(gtBucket, "material", Material.class);
                for (var key : FluidStorageKey.allKeys()) {
                    // test all fluid storage keys for a match
                    if (fluid == getFluidByKey(material, key)) {
                        return fluid(key);
                    }
                }
            }
            return fluid(FluidStorageKeys.LIQUID);
        }
        var tag = ChemicalHelper.getPrefix(item);
        return item(tag == null ? ITEM_NULL : tag);
    }

    @Nullable
    private static Fluid getFluidByKey(Material material, FluidStorageKey tag) {
        try {
            return material.getProperty(PropertyKey.FLUID).getStorage().get(tag);
        } catch (Exception ignored) {
            return null;
        }
    }
}
