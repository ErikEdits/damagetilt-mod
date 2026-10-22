# DamageTilt

A Fabric client mod that lets you toggle Minecraft's damage tilt effect with a hotkey.

On Minecraft 1.18 – 1.19.3 the vanilla damage tilt always leans the camera the same way,
regardless of where the hit came from. DamageTilt backports the directional tilt from 1.19.4,
so the camera leans toward the side you were hit from.

## Features

- Hotkey (default `K`, set on first launch) toggles the damage tilt on and off.
- Directional damage tilt on 1.18.x.
- Mod icon and description shown in Mod Menu.
- Settings are stored in `config/damagetilt.json`.

## Supported versions

| Minecraft | Notes |
|---|---|
| 1.18, 1.18.1, 1.18.2 | Directional tilt backport |
| 1.21.9, 1.21.10, 1.21.11 | Toggles the game's damage tilt option |
| 26.1, 26.1.1, 26.1.2, 26.2, 26.3 | Toggles the game's damage tilt option |

Requires Fabric Loader and the matching Fabric API. Mod Menu is optional.

## Building

Each Minecraft version is a separate source variant under `src/`. The build picks the right
one from `MC_VERSION`:

```bash
MC_VERSION=1.21.11 YARN_VERSION=1.21.11+build.6 FABRIC_API=0.141.6+1.21.11 MOD_VERSION=1.1.0-1.21.11 gradle build
```

For 26.x no Yarn mappings are needed (`YARN_VERSION` can be left out). The jar is written to `build/libs/`.
The full build matrix is in `.github/workflows/build.yml`.

## License

MIT, see the author in `fabric.mod.json`.
