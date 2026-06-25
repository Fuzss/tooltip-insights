package fuzs.tooltipinsights.common.mixin.client;

import net.minecraft.client.gui.font.AtlasGlyphProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AtlasGlyphProvider.class)
abstract class AtlasGlyphProviderMixin {

    @ModifyConstant(method = "<clinit>", constant = @Constant(floatValue = 8.0F))
    private static float on(float constant) {
        return 24.0F;
    }
}
