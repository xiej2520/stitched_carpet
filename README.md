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

- Type: `boolean`
- Default value: `false`
- Required options: `true`, `false`
- Categories: `shulkerclone`, `mobs`, `survival`, `backport`, `bugfix`

Technically MC-183884 was introduced in 1.16 snapshots, hopefully I didn't introduce it while fixing
the other bugs...


### QoL/Fun

#### instantMiningGold

Instant mining gold with iron and diamond pickaxes. Fast beacon mining and replacement for resin scaffholding.

- Type: `boolean`
- Default value: `false`
- Required options: `true`, `false`
- Categories: `survival`

## License

LGPL

Code from this project has been taken from [minitweaks](https://github.com/manyrandomthings/minitweaks),
[farmable-shulkers](https://github.com/Kira-NT/farmable-shulkers) and modified.
Some backport code is from yarn mappings of Mojang code.

