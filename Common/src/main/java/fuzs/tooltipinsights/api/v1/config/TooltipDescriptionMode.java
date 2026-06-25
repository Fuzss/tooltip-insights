package fuzs.tooltipinsights.api.v1.config;

import fuzs.puzzleslib.api.util.v1.CommonHelper;
import fuzs.tooltipinsights.impl.TooltipInsights;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Optional;

public enum TooltipDescriptionMode {
    DISABLED {
        @Override
        public boolean isActive() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void processTooltipLines(ItemStack itemStack, List<Component> tooltipLines, TooltipFlag tooltipFlag) {
            throw new UnsupportedOperationException();
        }
    },
    NEVER {
        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public void processTooltipLines(ItemStack itemStack, List<Component> tooltipLines, TooltipFlag tooltipFlag) {
            // NO-OP
        }
    },
    ALWAYS {
        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public void processTooltipLines(ItemStack itemStack, List<Component> tooltipLines, TooltipFlag tooltipFlag) {
            // NO-OP
        }
    },
    SHIFT {
        @Override
        public boolean isActive() {
            return CommonHelper.hasShiftDown();
        }

        @Override
        Optional<Component> component() {
            return Optional.of(SHIFT_COMPONENT);
        }
    },
    CONTROL {
        @Override
        public boolean isActive() {
            return CommonHelper.hasControlDown();
        }

        @Override
        Optional<Component> component() {
            return Optional.of(CONTROL_COMPONENT);
        }
    },
    ALT {
        @Override
        public boolean isActive() {
            return CommonHelper.hasAltDown();
        }

        @Override
        Optional<Component> component() {
            return Optional.of(ALT_COMPONENT);
        }
    };

    public static final Component SHIFT_COMPONENT = component("tooltip.shift");
    public static final Component CONTROL_COMPONENT = component("tooltip.control");
    public static final Component ALT_COMPONENT = component("tooltip.alt");
    public static final String VIEW_DESCRIPTIONS_KEY = Util.makeDescriptionId("gui",
            TooltipInsights.id("tooltip.view_descriptions"));

    private static Component component(String name) {
        return Component.translatable(Util.makeDescriptionId("gui", TooltipInsights.id(name)))
                .withStyle(ChatFormatting.LIGHT_PURPLE);
    }

    public abstract boolean isActive();

    Optional<Component> component() {
        return Optional.empty();
    }

    public void processTooltipLines(ItemStack itemStack, List<Component> tooltipLines, TooltipFlag tooltipFlag) {
        Component component = Component.translatable(VIEW_DESCRIPTIONS_KEY, this.component().orElseThrow())
                .withStyle(ChatFormatting.GRAY);
        tooltipLines.add(this.getLineIndex(itemStack, tooltipLines, tooltipFlag), component);
    }

    private int getLineIndex(ItemStack itemStack, List<Component> tooltipLines, TooltipFlag tooltipFlag) {
        int lineIndex = -1;

        if (!itemStack.isEmpty() && tooltipFlag.isAdvanced()) {
            // add this just before the 'dev-only' tooltip lines
            Component component = Component.literal(BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString())
                    .withStyle(ChatFormatting.DARK_GRAY);
            // also this is probably the most reliable way instead of using fixes indices regarding mod interference
            lineIndex = tooltipLines.lastIndexOf(component);
        }

        if (lineIndex == -1) {
            return tooltipLines.size();
        } else {
            return lineIndex;
        }
    }
}
