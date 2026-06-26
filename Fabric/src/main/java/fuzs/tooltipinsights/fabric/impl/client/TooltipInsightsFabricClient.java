package fuzs.tooltipinsights.fabric.impl.client;

import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.common.api.core.v1.ModLoaderEnvironment;
import fuzs.tooltipinsights.common.impl.TooltipInsights;
import fuzs.tooltipinsights.common.impl.client.TooltipInsightsClient;
import fuzs.tooltipinsights.common.impl.client.TooltipInsightsClientDevelopment;
import net.fabricmc.api.ClientModInitializer;

public class TooltipInsightsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(TooltipInsights.MOD_ID, TooltipInsightsClient::new);
        if (ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration(TooltipInsights.MOD_ID)) {
            ClientModConstructor.construct(TooltipInsights.id("client/development"),
                    TooltipInsightsClientDevelopment::new);
        }
    }
}
