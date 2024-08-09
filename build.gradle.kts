plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

repositories {
    mavenLocal()
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
    testImplementation("net.objecthunter", "exp4j", "0.4.8")

    // Paper
    compileOnly("io.papermc.paper", "paper-api", "1.21-R0.1-SNAPSHOT")

    // Plugins
    compileOnly("org.betonquest", "betonquest", "2.1.3")
    compileOnly("com.ghostchu", "quickshop-bukkit", "6.2.0.6")
    compileOnly("com.ghostchu", "quickshop-api", "6.2.0.6")
    compileOnly("net.william278.huskhomes", "huskhomes-bukkit", "4.7")
    compileOnly("com.github.GriefPrevention", "GriefPrevention", "16.18.2")

    // Others
    paperLibrary("com.google.inject", "guice", "7.0.0")
}

version = "1.0.0"

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
        github("BetonQuest", "BetonQuest", "v2.1.3", "BetonQuest.jar")
        github("QuickShop-Community", "QuickShop-Hikari", "6.2.0.6", "QuickShop-Hikari-6.2.0.6.jar")
        github("WiIIiam278", "HuskHomes", "4.7", "HuskHomes-Paper-4.7.jar")
        github("MilkBowl", "Vault", "1.7.3", "Vault.jar")
        url("https://ci.minebench.de/job/FakeEconomy/lastSuccessfulBuild/artifact/target/FakeEconomy.jar")
        url("https://dev.bukkit.org/projects/grief-prevention/files/5471866/download")
    }

    compileJava {
        this.options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    shadowJar {
        this.archiveClassifier.set(null as String?)
    }

    runServer {
        minecraftVersion("1.21")
        downloadPlugins.from(paperPlugins)
    }

    test {
        useJUnitPlatform()
    }
}