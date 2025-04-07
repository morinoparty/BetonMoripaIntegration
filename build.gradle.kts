plugins {
    id("java")
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
    alias(libs.plugins.plugin.yml.paper)
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")

    // BetonQuest
    maven("https://nexus.betonquest.org/repository/betonquest/")
    maven("https://repo.dmulloy2.net/repository/public/")
    // QuickShop
    maven("https://repo.codemc.io/repository/maven-public/")
    // HuskHome
    maven("https://repo.william278.net/releases")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.exp4j)

    // Paper
    compileOnly(libs.paper.api)

    // Plugins
    compileOnly(libs.betonquest)
    compileOnly(libs.quickshop.bukkit)
    compileOnly(libs.quickshop.api)
    compileOnly(libs.huskhomes)
    compileOnly(libs.griefprevention)

    // Others
    paperLibrary(libs.guice)
}

version = "1.3.0"

paper {
    authors = listOf("tyonakaisan")
    website = "https://github.com/tyonakaisan"
    apiVersion = "1.21"
    generateLibrariesJson = true
    foliaSupported = false

    val mainPackage = "github.tyonakaisan.betonmoripaintegration"
    main = "$mainPackage.BetonMoripaIntegration"
    bootstrapper = "$mainPackage.BetonMoripaIntegrationBootstrap"
    loader = "$mainPackage.BetonMoripaIntegrationLoader"

    serverDependencies {
        register("BetonQuest") {
            required = true
        }
        register("QuickShop-Hikari") {
            required = false
        }
        register("HuskHomes") {
            required = false
        }
        register("GriefPrevention") {
            required = false
        }
    }
}

tasks {
    val paperPlugins = runPaper.downloadPluginsSpec {
        github("BetonQuest", "BetonQuest", "v${libs.versions.betonquest.get()}", "BetonQuest.jar")
        modrinth("QuickShop-Hikari", "Qrp1IEXz")
        github("WiIIiam278", "HuskHomes", libs.versions.huskhomes.get(), "HuskHomes-Paper-${libs.versions.huskhomes.get()}.jar")
        github("MilkBowl", "Vault", "1.7.3", "Vault.jar")
        hangar("PlaceholderAPI", libs.versions.placeholderapi.get())
        url("https://ci.minebench.de/job/FakeEconomy/lastSuccessfulBuild/artifact/target/FakeEconomy.jar")
        url("https://dev.bukkit.org/projects/grief-prevention/files/5471866/download")
        url("https://ci.dmulloy2.net/job/ProtocolLib/lastSuccessfulBuild/artifact/build/libs/ProtocolLib.jar")
    }

    compileJava {
        this.options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    shadowJar {
        this.archiveClassifier.set(null as String?)
    }

    runServer {
        minecraftVersion("1.21.4")
        downloadPlugins.from(paperPlugins)
    }

    test {
        useJUnitPlatform()
    }
}