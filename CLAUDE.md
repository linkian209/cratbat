# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CratBat is a Minecraft Forge mod that adds the "CratBat" item into Minecraft 1.20.1. The project uses MinecraftForge 47.4.9 and targets Java 17. The mod includes optional integration with Vampirism mod and JEI (Just Enough Items).

## Build System & Common Commands

This project uses Gradle with ForgeGradle plugin:

```bash
# Build the mod
./gradlew build

# Run tests
./gradlew test

# Run in development environment
./gradlew runClient           # Run Minecraft client with mod loaded
./gradlew runServer           # Run dedicated server with mod loaded
./gradlew runGameTestServer   # Run game test server

# IDE setup
./gradlew genEclipseRuns     # Generate Eclipse run configurations
./gradlew genIntellijRuns    # Generate IntelliJ run configurations

# Development utilities
./gradlew --refresh-dependencies  # Refresh dependency cache
./gradlew clean                   # Clean build artifacts
./gradlew runData                 # Generate mod data (recipes, models, etc.)
```

The final mod JAR is built to `build/libs/cratbat-1.0.0.jar`.

## Code Architecture

### Package Structure
```
src/main/java/xyz/nineworlds/cratbat/
├── CratBatMod.java              # Main mod class, item/tab registration
├── CratBatConfig.java           # Configuration with server sync support
├── command/
│   └── CratBatCommand.java      # /cratbat commands
├── event/
│   ├── CratBatEventHandler.java # Combat + login/logout events
│   └── BatDropHandler.java      # Bat wing drops
├── item/
│   ├── CratBatItem.java         # Main weapon
│   ├── CrankCrankDollItem.java  # Creates Crank Skulls
│   ├── CrankSkullItem.java      # Player head with texture
│   ├── BatWingItem.java         # Crafting ingredient
│   └── CratBatShieldItem.java   # Protection charm (Curios)
├── network/
│   ├── CratBatNetwork.java      # Network channel management
│   └── ConfigSyncPacket.java    # Config sync packet
├── recipe/
│   ├── CratBatRecipe.java       # Custom crafting recipe
│   └── CratBatRecipeSerializer.java
├── util/
│   ├── NBTUtil.java             # Skull NBT creation
│   └── PlayerTextureUtil.java   # Texture URL extraction
└── integration/
    ├── VampirismIntegration.java
    ├── VampirismTaskIntegration.java
    └── jei/                      # JEI recipe display
```

### Mod Configuration
- **Mod ID**: `cratbat`
- **Display Name**: CratBat
- **Main Class**: `xyz.nineworlds.cratbat.CratBatMod`
- **Forge Version**: 47.4.9 (Minecraft 1.20.1)

### Key Components
- **DeferredRegister Pattern**: Uses Forge's deferred registration for items and creative tabs
- **Event-Driven Architecture**: Mod setup and game events handled through Forge event buses
- **Config System**: ForgeConfigSpec-based with client-server synchronization
- **Network System**: SimpleChannel for config sync between server and clients
- **Creative Mode Integration**: Custom creative tab registration

### Dependencies
- **Core**: MinecraftForge 47.4.9, Minecraft 1.20.1
- **Testing**: JUnit 5, Mockito
- **Optional**: JEI 15.20.0.106 (compile-time API, runtime dependency)
- **Optional**: Vampirism 1.10.13 (API integration)
- **Optional**: Curios API (for CratBat Shield charm slot)

## Key Features

### Client-Server Config Sync
- Server config automatically syncs to clients on login
- `CratBatConfig.getTargetPlayerName/UUID/Texture()` return server values when connected
- Config clears on disconnect, reverting to local values

### Server Commands
- `/cratbat setCrat <player>` - Set target player (OP level 2)
- `/cratbat info` - Show current configuration

### Crank Skull System
- CrankCrank Doll creates Crank Skull when used on target player
- Skull NBT includes embedded texture URL for reliable skin display
- Uses `NBTUtil.createSkullOwnerTag()` for proper NBT structure

## Testing

95 unit tests covering:
- Config getter/setter methods and server override system
- Network packet encoding/decoding
- UUID conversion and texture URL handling
- Skull NBT structure creation

Run with `./gradlew test`

## Development Notes

### Important Patterns

1. **Config Access**: Always use getter methods (`CratBatConfig.getTargetPlayerName()`) not direct field access, to respect server overrides.

2. **Exception**: `ConfigSyncPacket.fromServerConfig()` intentionally uses direct field access to read server's actual config values.

3. **Skull Creation**: Use `NBTUtil.createSkullOwnerTag()` to create proper skull NBT with texture data.

### Network Protocol
- Protocol version: "1"
- Channel: `cratbat:main`
- Packets: `ConfigSyncPacket` (server → client)
