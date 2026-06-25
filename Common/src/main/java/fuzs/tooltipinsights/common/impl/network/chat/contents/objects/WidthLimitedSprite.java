package fuzs.tooltipinsights.common.impl.network.chat.contents.objects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.contents.objects.ObjectInfo;
import net.minecraft.util.ExtraCodecs;

public record WidthLimitedSprite(int width) implements ObjectInfo, FontDescription {
    public static final MapCodec<WidthLimitedSprite> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("width").forGetter(WidthLimitedSprite::width))
            .apply(instance, WidthLimitedSprite::new));

    @Override
    public FontDescription fontDescription() {
        return this;
    }

    @Override
    public String defaultFallback() {
        return "[" + this.width + "px]";
    }

    @Override
    public MapCodec<? extends ObjectInfo> codec() {
        return MAP_CODEC;
    }
}
