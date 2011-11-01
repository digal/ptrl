What's this
===========

This is my first serious Java project, I've started around 2005. It was planned as a postapocalyptical roguelike [game](http://en.wikipedia.org/wiki/Roguelike) and seems like I spent a lot of time on it.
It was not finished, and I don't think it ever will, but I think it's a good idea to put it here for the sake of nostalgia.

I decided to modify this legacy sources a little to enable major parts of gameplay (like enemies and weapons), fix some annoying bugs, and create an ant build.

Manual
======

JRE 1.5+ required

- Download [archive](https://github.com/downloads/digal/ptrl/ptrl.zip) and unzip it
- Run `java -jar ptrl.jar` from the game dir

After starting a new game, naming your character, and generating stats, you will likely appear on the empty or forest map. Press `M` (`shift-m`) to go to the global map. Not all locations have interesting map generators, but you can visit a town (gray '*') to see a town map, or mountains (gray/white '^') to see a cavern map. There should be some weapons and ammo on the center side of each location, and also some zombies or even imps. There could be also few psionic attacks available, if your 'Combat Psionics' skill is high enough.

Controls
========
- Arrows: move
- `M`: go to the global map
- `>`: enter location (on the global map)
- `g`: pickup
- `i`: equipment
- `I`: inventory
- `s`: skills
- `m`: messages
- `\[`/`\]`: switch close combat attack type
- `;`/`'`: switch ranged attack type (weapon fire modes, psionic attacks)
- `R` reload weapon (let you choose ammo type)
- `r` fast reload with last ammo type
- `t` target (Arrows to target, `-`/`+` to switch targets, Enter to shoot)
- `S` - save (not sure if it works)
- `Q` - quit


