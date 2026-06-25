package fuzs.tooltipinsights.common.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.gui.font.PlainTextRenderable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlainTextRenderable.class)
public interface PlainTextRenderableMixin {

    @ModifyReturnValue(method = "width", at = @At("TAIL"))
    default float width(float original) {
        return 24.0F;
    }
}
