
# CratBat

A Minecraft Forge mod that adds the "CratBat" item to Minecraft 1.20.1.

## About

CratBat is a Minecraft mod that introduces a new weapon item called the CratBat. The mod is built for Minecraft 1.20.1 using MinecraftForge 47.4.9 and includes optional integration with JEI (Just Enough Items) and Vampirism mods.

## Features

- **CratBat Item**: A new weapon with configurable attack damage and speed
- **JEI Integration**: Optional integration for recipe viewing
- **Vampirism Integration**: Optional mod compatibility
- **Configurable**: Attack damage and speed can be adjusted via configuration

## Building

Build the mod using Gradle:

```bash
# Build the mod
./gradlew build

# Run in development environment
./gradlew runClient           # Run Minecraft client with mod loaded
./gradlew runServer           # Run dedicated server with mod loaded

# IDE setup
./gradlew genEclipseRuns     # Generate Eclipse run configurations
./gradlew genIntellijRuns    # Generate IntelliJ run configurations

# Development utilities
./gradlew --refresh-dependencies  # Refresh dependency cache
./gradlew clean                   # Clean build artifacts
```

The final mod JAR is built to `build/libs/cratbat-1.0.0.jar`.

## Dependencies

- **Core**: MinecraftForge 47.4.9, Minecraft 1.20.1
- **Optional**: JEI 15.19.0.88 (Just Enough Items)
- **Optional**: Vampirism 1.10.13

## Development Setup
==============================

Step 1: Open your command-line and browse to the folder where you extracted the zip file.

Step 2: You're left with a choice.
If you prefer to use Eclipse:
1. Run the following command: `./gradlew genEclipseRuns`
2. Open Eclipse, Import > Existing Gradle Project > Select Folder 
   or run `gradlew eclipse` to generate the project.

If you prefer to use IntelliJ:
1. Open IDEA, and import project.
2. Select your build.gradle file and have it import.
3. Run the following command: `./gradlew genIntellijRuns`
4. Refresh the Gradle Project in IDEA if required.

If at any point you are missing libraries in your IDE, or you've run into problems you can 
run `gradlew --refresh-dependencies` to refresh the local cache. `gradlew clean` to reset everything 
(this does not affect your code) and then start the process again.

Mapping Names:
=============================
By default, the MDK is configured to use the official mapping names from Mojang for methods and fields 
in the Minecraft codebase. These names are covered by a specific license. All modders should be aware of this
license, if you do not agree with it you can change your mapping names to other crowdsourced names in your 
build.gradle. For the latest license text, refer to the mapping file itself, or the reference copy here:
https://github.com/MinecraftForge/MCPConfig/blob/master/Mojang.md

Additional Resources: 
=========================
Community Documentation: https://docs.minecraftforge.net/en/1.20.1/gettingstarted/
LexManos' Install Video: https://youtu.be/8VEdtQLuLO0
Forge Forums: https://forums.minecraftforge.net/
Forge Discord: https://discord.minecraftforge.net/
