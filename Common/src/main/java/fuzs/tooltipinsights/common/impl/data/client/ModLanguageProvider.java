package fuzs.tooltipinsights.common.impl.data.client;

import fuzs.puzzleslib.common.api.client.data.v3.language.AbstractLanguageProvider;
import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import fuzs.tooltipinsights.common.api.v1.config.TooltipDescriptionMode;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations() {
        this.add(TooltipDescriptionMode.VIEW_DESCRIPTIONS_KEY, "Hold %s to view descriptions.");
    }
}
