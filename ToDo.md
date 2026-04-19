# Minecraft Mod TODO

## 1. Items
- [ ] Convert crystals -> dust -> concentrated pigment

## 2. Blocks
- [x] Crystals and variants
  - [ ] Loot tables
  - [ ] Recipes
- [ ] Tinted Blocks
  - [ ] Blocks
    - [ ] Recipes (need to add clean recipe for stone and grass)
    - [x] Loot Tables (have to be handled specially)
  - [ ] Slabs
    - [ ] Recipe (half done, want to impliment all same tinted blocks to same colour variant)
    - [ ] Loot Tables (have to be handled specially)
  - [ ] Stairs
    - [ ] Recipe (half done, want to impliment all same tinted blocks to same colour variant)
    - [ ] Loot Tables (have to be handled specially)
- [ ] Metals
  - [ ] Rusty iron
  - [ ] Wrought iron (very dark)
  - [ ] Cast iron (dark, lighter than wrought)

## 3. Obtaining / Progression
- [ ] Budding crystals
- [ ] Geodes
  - [ ] Consider adding new stone types for geodes
- [ ] Crushing system
  - [ ] Amethyst variants → crushed gems
  - [ ] 1:8 shapeless recipe (crushed gems → pigment)

## 4. Custom Models
- [ ] Custom model support for blocksets
- [ ] Partial blocksets for custom-only blocks
- [ ] Connected models (like chests / connected blocks)

## 5. Systems / Features

### 5.1 Dynamic Tinting System
- [ ] Expand tintable blocks
  - [ ] Leaves (unsure)
  - [ ] Wood (optional / user-request driven)
- [ ] Improve initial recipe

### 5.2 Rendering / Models
- [ ] Fix stair model issues
  - [ ] Reversed inventory model
  - [x] Broken in-world model
  - [x] Investigate StairBlock implementation (nonviable for fixing reversed inventory model)

### 5.3 Shaders
- [ ] Revisit shader implementation

### 5.4 General Setup
- [ ] Define all recipes
- [ ] Define all `properties.of()` for blocks/items

## 6. Compatibility / External Mods
- [ ] Review overlap with Quark (1.21.1)
  - [ ] Identify duplicate features
  - [ ] Decide what to keep / differentiate

## 7. Accessibility

### 7.1 Colour Test Results
- Achromatomaly
  - [ ] Lime vs green too similar
  - [ ] Cyan vs light blue too similar

- Achromatopsia
  - [ ] All colours indistinguishable

- Deuteranomaly
  - [ ] No major issues

- Deuteranopia
  - [ ] Magenta merges with grays
  - [ ] Greens and blues merge
  - [ ] Warm colours merge

- Protanopia
  - [ ] Similar issues to Deuteranopia

- Tritanopia
  - [ ] Greens indistinguishable
  - [ ] Warm + pink merge
  - [ ] Blues merge

### 7.2 Actions
- [ ] Adjust palette for better differentiation
- [ ] Consider alternative cues (patterns, brightness shifts, etc.)

## 8. In-Game Documentation
- [ ] In-game book/manual (probably patchouli)
- [ ] JEI integration (info pages and dyed blocks like firework rockets)
- [ ] REI integration (info pages and dyed blocks like firework rockets)
- [ ] EMI integration (info pages and dyed blocks like firework rockets)
- [ ] Guide integration (AE2 info book system)

## Clean up
- [ ] Clean up comments and add javadoc comments so I don't lose what I'm doing constantly
- [ ] the useless stuff that no longer has a reference or use