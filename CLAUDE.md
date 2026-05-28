# DamageTilt Mod — Claude Code Kontext

**GitHub:** https://github.com/ErikEdits/damagetilt-mod
**Modrinth:** https://modrinth.com/mod/damage-tilt

---

## Build

```bash
gradle remapJar

# Spezifische MC-Version:
MC_VERSION=1.18 YARN_VERSION=1.18+build.1 FABRIC_API=0.44.0+1.18 MOD_VERSION=1.0.0-1.18 gradle remapJar
```

CI: GitHub Actions → `.github/workflows/build.yml` → baut 1.18 / 1.18.1 / 1.18.2

---

## Source-Varianten

| Ordner | MC-Versionen | Besonderheiten |
|--------|-------------|----------------|
| `src/v1_18` | 1.18 / 1.18.1 / 1.18.2 | `LiteralText`, MC `Quaternion`/`Vec3f`, kein `Screen.close()` in 1.18/1.18.1 |
| `src/v1_19_old` | 1.19 – 1.19.2 | `Text.literal()`, MC Quaternion, `drawCenteredTextWithShadow` braucht `.asOrderedText()` |
| `src/v1_19_mid` | 1.19.3 | JOML `Quaternionf`, `ButtonWidget.builder()` |
| `src/v1_19_new` | 1.19.4 | Natives direktionales Tilt (nur Toggle nötig) |
| `src/v1_20_old` | 1.20 – 1.20.1 | DrawContext API |
| `src/v1_20_mid` | 1.20.2 – 1.20.4 | DrawContext API |
| `src/v1_20_new` | 1.20.5+ / 1.21.x | `damageTiltStrength`-Slider, Java 21 |

`src/main/java` + `src/main/resources` → immer eingebunden (alle Varianten)

---

## Wichtige Erkenntnisse

- **Fabric API Mod-ID**: Fabric API 0.44.x+1.18 heißt intern `fabric` (nicht `fabric-api`)
  → `"fabric-api"` NICHT in `depends` von v1_18-fabric.mod.json verwenden
- **Mod-Icon**: Muss quadratisches **RGBA PNG ≤ 128×128** sein (Fabric Loader 0.19.x)
  → Liegt in `src/main/resources/assets/damagetilt/icon.png`
- **`Screen.close()`**: Erst ab 1.18.2 verfügbar → in 1.18/1.18.1 `this.client.setScreen(null)` nutzen
- **Direktionales Tilt**: 3 Mixins in v1_18/v1_19_old: `DamageAngleTrackerMixin` + `CameraDirectionalTiltMixin` + `DamageTiltHandlerMixin`

---

## Mixins (v1_18)

| Mixin | Ziel | Funktion |
|-------|------|----------|
| `DamageAngleTrackerMixin` | `LivingEntity.damage()` | Berechnet Winkel Spieler→Angreifer |
| `CameraDirectionalTiltMixin` | `Camera.update()` RETURN | Wendet Z-Roll basierend auf Winkel an |
| `DamageTiltHandlerMixin` | `GameRenderer.renderWorld` | Unterdrückt vanilla-broken-Tilt (gibt hurtTime=0 zurück) |

---

## Offene Aufgaben

- [ ] MC 1.18.2 testen (kein Log vorhanden)
- [ ] Neue JARs auf Modrinth hochladen (icon-fix + fabric-api-fix sind drin)
