# Stitched Carpet

Forwards and backwards porting interesting technical features to Minecraft 1.15.2, and some other fun flags.

Requires [fabric-carpet](https://github.com/gnembon/fabric-carpet)

Code has been merged from many mods, attribution has been given where I can.

## Settings

### Backport

#### shulkerCloning

1.17 Shulker cloning from 20w45a.
"A shulker hitting a shulker with a shulker bullet can make a new shulker".
Also fixes a bug where shulker bullets can't hit certain targets (the shulker that
fired them).

- Type: `boolean`
- Default value: `false`
- Required options: `true`, `false`
- Categories: `shulkerclone`, `mobs`, `survival`, `backport`

#### shulkerBehaviorFix

Fixes [MC-139265](https://bugs.mojang.com/browse/MC-139265) / [MC-168900](https://bugs.mojang.com/browse/MC-168900),
which makes shulkers use portals correctly, [MC-183884](https://bugs.mojang.com/browse/MC-183884) which allows shulkers
to be next to each other without teleporting, [MC-159773](https://bugs.mojang.com/browse/MC/issues/MC-159773),
shulkers will check face of the block they are attached to, instead of the top face.
Some code from Farmable-Shulkers.

- Type: `boolean`
- Default value: `false`
- Required options: `true`, `false`
- Categories: `shulkerclone`, `mobs`, `survival`, `backport`, `bugfix`

Technically MC-183884 was introduced in 1.16 snapshots, hopefully I didn't introduce it while fixing
the other bugs...

Note: there are many other mechanics that 1.17+ shulker farms depend on that break in 1.15:
- Shulkers do not get ejected into the same spot from boats in 1.15-1.16 as they do in 1.17
- Snowballs seem to have different hitboxes, they also cannot be shot from underneath scaffolding
- Piston not pushing shulker properly in this implementation.

#### mendingOnlyDamaged

Mending only repairs equipped damaged items, from 1.16+

- Type: `boolean`
- Default value: `false`
- Required options: `true`, `false`
- Categories: `survival`, `backport`

#### endPlatform

When to generate obsidian platform in the end. Is `player` in 1.15.2, `all` in 1.16.5 in vanilla minecraft.
Original implementation from Carpet-TCTC-Addition and Vanilla 1.16.5.

- `all` - Generate end platform when all entities are transferred to the_end dimension.
- `none` - End platform will not be generated anyway.
- `player` - End platform is generated only when the player entity teleports to the_end dimension.

- Type: `enum`
- Default value: `player`
- Required options: `all`, `none`, `player`
- Categories: `backport`, `bugfix`, `feature`

#### hoeMiningTool

Makes hoes a mining tool like 1.16+.
Hoe harvests nether wart block, hay, sponge, dried kelp, and leaves, can be enchanted with Efficiency, Fortune and
Silk Touch, and takes damage from breaking blocks like in 1.16.

- Type: `boolean`
- Default value: `false`
- Required options: `true`, `false
- Categories: `survival`, `backport

### Reintroduce

#### infinityMendingStacking

Allows infinity and mending enchantments to stack on bows like from 1.9 to 1.11.

- Type: `boolean`
- Default value: `false`
- Required options: `true`, `false`
- Categories: `survival`, `reintroduce`

#### protectionStacking

Allows different types of Protection enchantments to stack on bows like from 1.14 - 1.14.2.
Works in enchanting table.

- Type: `boolean`
- Default value: `false`
- Required options: `true`, `false`
- Categories: `survival`, `reintroduce`


### QoL/Fun

#### instantMiningGold

Instant mine gold blocks with iron and diamond pickaxes. Fast beacon mining and replacement for resin scaffolding.

- Type: `boolean`
- Default value: `false`
- Required options: `true`, `false`
- Categories: `survival`

#### instantMiningWood

A Diamond Axe with Efficiency V and Haste II will instant mine wood.
Based off of implementation from Carpet-Addons-Not-Found.

- Type: `boolean`
- Default value: `false`
- Required options: `true`, `false`
- Categories: `survival`

#### instantMiningConcrete

A Diamond Pickaxe with Efficiency V and Haste II will instant mine concrete.
Based off of implementation from Carpet-Addons-Not-Found.

- Type: `boolean`
- Default value: `false`
- Required options: `true`, `false`
- Categories: `survival`

#### endPlatformSpawnPoint

Change the end platform spawn location. Use separated 'x,y,z', anything else uses default position.
Note: this only works for going through the end portal to the end. Outer end gateways are configured at game launch time
and it would take a lot more work to intercept that as a runtime configuration option. It would be relatively simple to
make a standalone mod to do both.

- Type: `string`
- Default value: ``
- Categories: `creative`, `feature`

## License

CC0 1.0

Code from this project has been taken from [minitweaks](https://github.com/manyrandomthings/minitweaks),
[farmable-shulkers](https://github.com/Kira-NT/farmable-shulkers),
[Carpet-Addons-Not-Found](https://github.com/Gilly7CE/Carpet-Addons-Not-Found),
[Carpet-TCTC-Addition](https://github.com/The-Cat-Town-Craft/Carpet-TCTC-Addition) and modified.
Some backport code is from yarn mappings of Mojang code.

