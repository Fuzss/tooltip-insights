plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-common")
}

dependencies {
    modCompileOnlyApi(sharedLibs.puzzleslib.common)
}

spotless {
    format("classTweaker") {
        target("**/*.classtweaker", "**/*.accesswidener")

        replaceRegex(
            "Normalize whitespace",
            "[ \\t]+",
            "\t"
        )

        endWithNewline()
    }
}

multiloader {
    mixins {
        clientMixin("FontManager\$CachedFontProviderMixin")
    }
}
