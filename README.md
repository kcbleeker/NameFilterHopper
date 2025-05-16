# NameFilterHopper

A Minecraft Paper plugin that allows you to control what items a hopper can pick up or move, based on the hopper's custom name.

## Features
- Filter items that hoppers can pick up or transfer using custom names.
- Use simple text or special prefixes in the hopper's name to allow or block specific items.

## How to Use

### 1. Install the Plugin
- Place the `namefilterhopper-<version>.jar` file into your server's `plugins` folder.
- Start or reload your Paper server.

### 2. Naming a Hopper
- Use an anvil to give a hopper item a custom name before placing it in the world.
- Place the named hopper in the world. The name you gave it will be used for filtering.

### 3. Setting Up Filters
The hopper's name determines what items it will allow or block. You can use the following formats:

#### Allow Only Specific Items
- Name the hopper with the item name(s) you want to allow, separated by `|` (pipe character).
- Example: `diamond|iron_ingot` — Only allows diamonds and iron ingots.

#### Exclude Specific Items
- Use `x:` before an item name to exclude it.
- Example: `x:stone|x:dirt` — Excludes stone and dirt, allows everything else.

#### Allow Items by Prefix or Part of the Name
- Use `s:` to allow items that start with a string.
- Use `c:` to allow items that have a string as part of their name.
- Example: `s:iron|c:ingot` — Allows items starting with "iron" or with "ingot" as part of their name.

#### Exclude Items by Part of the Name
- Use `n:` to exclude items that have a string as part of their name.
- Example: `n:log` — Excludes any item with "log" as part of its name.

### 4. Example Names
- `diamond` — Only allows diamonds.
- `x:stone` — Excludes stone.
- `diamond|x:stone` — Allows diamonds, excludes stone.
- `s:iron` — Allows items starting with "iron" (e.g., iron_ingot, iron_block).
- `c:ingot` — Allows any item with "ingot" as part of its name.

### 5. In-Game Usage
- Name a hopper item using an anvil, then place it in the world.
- Once placed, the hopper will automatically filter items according to its name.
- No commands are required. Just name the hopper before placing it and it will work!

### 6. How the Hopper Handles Blocked Items
If your hopper tries to move an item that isn't allowed by its name filter, it will look through the rest of its inventory and try to move a different item that is allowed. This way, your hopper doesn't get stuck and can still move other items that match its filter.

## Notes
- Item names must match the Minecraft item type names (e.g., `diamond`, `iron_ingot`, `stone`).
- You can combine multiple filters using the `|` character.
- The plugin works with all hoppers in the world as soon as they are named.

## Support
For questions or issues, open an issue on the plugin's repository or contact the author.
