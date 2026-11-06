# Minecraft Plugin: Zeus's Wrath

Zeus's Wrath is a Paper Minecraft plugin that adds a custom ability item with lightning abilities, potion effects, particles, true damage, a hit-based passive, cooldowns, and protected item handling. Players can receive the Zeus's Wrath honey bottle through the `/polarsmp give Zeus_Wrath` command and activate its abilities with `/a1` and `/a2`.

## Install

1. Use Java 21 or newer.
2. Run a Paper 1.21.11 server.
3. Place the compiled plugin JAR in the server's `plugins` folder.
4. Start or restart the server.
5. Make sure the plugin loads successfully.

## Use

Give Zeus's Wrath to yourself:

`/polarsmp give Zeus_Wrath`

This command requires the `polarsmp.admin` permission, which is granted to server operators by default.

Hold Zeus's Wrath in your main hand to use its abilities.

Use the first ability:

`/a1`

Thunderstorm strikes the holder four times. The first strike happens immediately, followed by additional strikes every 2.3 seconds. Each strike deals 2 hearts of direct true damage and creates electric particles and thunder sounds.

Use the second ability:

`/a2`

Olympian Might grants Speed II and Strength II for 15 seconds. Electric spark and end rod particles appear around the player when the ability activates.

Passive ability:

When the holder attacks with Zeus's Wrath, every fifth hit strikes the target with lightning effects and deals 1.5 hearts of direct true damage. Hit progress is tracked separately for each player.

Both active abilities share a 30-second cooldown.

Use `/withdraw` while holding Zeus's Wrath to remove it from your inventory. The item cannot be manually dropped and is removed from player death drops. The item is represented by a honey bottle and can be identified by its custom item data.

---

More Minecraft plugin projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/bf78b644-1b58-4a17-8212-f1b751b244db?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=bf78b644-1b58-4a17-8212-f1b751b244db
