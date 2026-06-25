package fuzs.tooltipinsights.common.impl.network.chat.contents.objects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.contents.objects.ObjectInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

/**
 * @see net.minecraft.network.chat.contents.objects.AtlasSprite
 */
public record SizedAtlasSprite(Identifier atlas,
                               Identifier sprite,
                               int width,
                               int height) implements ObjectInfo, FontDescription {
    public static final MapCodec<SizedAtlasSprite> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    net.minecraft.network.chat.contents.objects.AtlasSprite.MAP_CODEC.forGetter(SizedAtlasSprite::packSprite),
                    ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(SizedAtlasSprite::width),
                    ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(SizedAtlasSprite::height))
            .apply(instance, SizedAtlasSprite::new));

    public SizedAtlasSprite(net.minecraft.network.chat.contents.objects.AtlasSprite sprite, int spriteWidth, int spriteHeight) {
        this(sprite.atlas(), sprite.sprite(), spriteWidth, spriteHeight);
    }

    private net.minecraft.network.chat.contents.objects.AtlasSprite packSprite() {
        return new net.minecraft.network.chat.contents.objects.AtlasSprite(this.atlas, this.sprite);
    }

    public float ratio() {
        return this.width / (float) this.height;
    }

    @Override
    public MapCodec<SizedAtlasSprite> codec() {
        return MAP_CODEC;
    }

    @Override
    public FontDescription fontDescription() {
        return this;
    }

    @Override
    public String defaultFallback() {
        return "[" + this.sprite.toShortString() + (
                this.atlas.equals(net.minecraft.network.chat.contents.objects.AtlasSprite.DEFAULT_ATLAS) ? "" :
                        "@" + this.atlas.toShortString()) + " as " + this.width + "x" + this.height + "]";
    }
}
