package fuzs.tooltipinsights.api.v1.config;

public enum TriState {
    TRUE,
    FALSE,
    DEFAULT;

    public boolean toBoolean(boolean defaultValue) {
        return switch (this) {
            case TRUE -> true;
            case FALSE -> false;
            default -> defaultValue;
        };
    }
}
