# Plutus Mace Plugin (1.21.1)

A custom Paper plugin that adds the **Plutus Mace**, a cursed weapon with crimson effects and blood-based abilities.

## Features
- **Integrated Resource Pack**: The plugin automatically generates and serves the custom red mace texture to players when they join. No manual upload required.
- **Red Themed Visuals**: Custom particles (crimson spores, smoke, red dust).
- **Blood Toll (Passive)**: Taking damage while attacking (configurable).
- **Blood Dash (Ability 1)**: Dash forward with a burst of red particles, damaging nearby enemies. (Press **F** or Swap Hand).
- **Blood Pact (Ability 2)**: Link up to 3 players. Linked players share damage taken. (Press **Shift + F**).

## Setup (IMPORTANT)

1. **Host Configuration**: Open `config.yml` and set `resource-pack.host` to your server's public IP or domain.
2. **Port Forwarding**: Ensure port `19133` (default) is open in your firewall/router so players can download the pack from the plugin's internal server.

## How to Build (Get the .jar)

1. **Install Java 21**: Ensure you have JDK 21 installed.
2. **Run Gradle**: 
   ```bash
   ./gradlew build
   ```
3. **Find the JAR**: The finished plugin will be at `build/libs/plutus-mace-1.0.0.jar`.

## Commands
- `/giveplutus`: Gives the Plutus Mace to the player (Permission: `plutus.admin`).
