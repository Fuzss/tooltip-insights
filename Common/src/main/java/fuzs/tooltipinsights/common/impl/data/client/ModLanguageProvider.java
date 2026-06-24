package fuzs.tooltipinsights.common.impl.data.client;

import fuzs.puzzleslib.common.api.client.data.v2.AbstractLanguageProvider;
import fuzs.puzzleslib.common.api.data.v2.core.DataProviderContext;
import fuzs.tooltipinsights.common.api.v1.config.TooltipDescriptionMode;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(TooltipDescriptionMode.SHIFT_COMPONENT, "Shift");
        translationBuilder.add(TooltipDescriptionMode.CONTROL_COMPONENT, "Control");
        translationBuilder.add(TooltipDescriptionMode.ALT_COMPONENT, "Alt");
        translationBuilder.add(TooltipDescriptionMode.VIEW_DESCRIPTIONS_KEY, "Hold %s to view descriptions.");
    }
}
