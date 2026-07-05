"""Generate full ProjectE art pack from repo masters."""

from __future__ import annotations

import os
import re
import shutil
from pathlib import Path

from PIL import Image

from .palette import THEMES
from .remap import klein_tier_from_master, remap_image

# (regex on posix path, action, theme or None, strength)
# action: keep | theme | klein
RULES: list[tuple[str, str, str | None, float]] = [
    (r"item/stars/klein_star_\d+\.png", "keep", None, 0),
    (r"item/dm_tools/.*", "theme", "dm", 0.28),
    (r"item/rm_tools/.*", "theme", "rm", 0.28),
    (r"item/dm_armor/.*", "theme", "dm", 0.38),
    (r"item/rm_armor/.*", "theme", "rm", 0.38),
    (r"item/gem_armor/.*", "theme", "gem", 0.32),
    (r"models/armor/dark_matter_.*", "theme", "dm", 0.42),
    (r"models/armor/red_matter_.*", "theme", "rm", 0.42),
    (r"models/armor/gem_armor_.*", "theme", "gem", 0.34),
    (r"block/alchemical_chest\.png", "theme", "alchemical", 0.22),
    (r"block/condenser_mk1\.png", "theme", "dm", 0.2),
    (r"block/condenser_mk2\.png", "theme", "rm", 0.2),
    (r"block/dark_matter_block\.png", "theme", "dm", 0.3),
    (r"block/red_matter_block\.png", "theme", "rm", 0.3),
    (r"block/alchemical_coal_block\.png", "theme", "alchemical", 0.18),
    (r"block/mobius_fuel_block\.png", "theme", "alchemical", 0.15),
    (r"block/aeternalis_fuel_block\.png", "theme", "alchemical", 0.15),
    (r"block/collectors/.*", "theme", "dm", 0.12),
    (r"block/relays/.*", "theme", "alchemical", 0.12),
    (r"block/matter_furnace/dm_.*", "theme", "dm", 0.22),
    (r"block/matter_furnace/rm_.*", "theme", "rm", 0.22),
    (r"block/explosives/.*", "theme", "rm", 0.15),
    (r"item/matter/dark\.png", "theme", "dm", 0.35),
    (r"item/matter/red\.png", "theme", "rm", 0.35),
    (r"item/philosophers_stone\.png", "theme", "alchemical", 0.18),
    (r"item/repair_talisman\.png", "theme", "alchemical", 0.15),
    (r"item/hyperkinetic_lens\.png", "theme", "dm", 0.2),
    (r"item/catalytic_lens\.png", "theme", "alchemical", 0.15),
    (r"item/destruction_catalyst\.png", "theme", "rm", 0.18),
    (r"item/divining_rod_.*", "theme", "alchemical", 0.1),
    (r"item/dense_gem_.*", "theme", "gem", 0.2),
    (r"item/mercurial_eye\.png", "theme", "dm", 0.15),
    (r"gui/.*", "keep", None, 0),
    (r"item/alchemy_bags/.*", "keep", None, 0),
    (r"item/fuels/.*", "keep", None, 0),
    (r"item/covalence_dust/.*", "keep", None, 0),
    (r"item/rings/.*", "keep", None, 0),
    (r"entity/(?!equipment).*", "keep", None, 0),
    (r"item/tome\.png", "keep", None, 0),
    (r"item/book\.png", "keep", None, 0),
    (r"item/transmutation_tablet\.png", "keep", None, 0),
]


def _match_rule(rel: str) -> tuple[str, str | None, float]:
    for pattern, action, theme, strength in RULES:
        if re.fullmatch(pattern, rel):
            return action, theme, strength
    return "keep", None, 0.0


def _same_path(left: Path, right: Path) -> bool:
    return os.path.normcase(os.path.abspath(str(left))) == os.path.normcase(os.path.abspath(str(right)))


def _sync_equipment_layers(tex_out: Path) -> None:
    armor = tex_out / "models" / "armor"
    eq = tex_out / "entity" / "equipment"
    pairs = [
        ("dark_matter_layer_1.png", "humanoid/dark_matter.png"),
        ("dark_matter_layer_2.png", "humanoid_leggings/dark_matter.png"),
        ("red_matter_layer_1.png", "humanoid/red_matter.png"),
        ("red_matter_layer_2.png", "humanoid_leggings/red_matter.png"),
        ("gem_armor_layer_1.png", "humanoid/gem_armor.png"),
        ("gem_armor_layer_2.png", "humanoid_leggings/gem_armor.png"),
    ]
    for src_name, dst_rel in pairs:
        src = armor / src_name
        dst = eq / dst_rel
        if src.exists():
            dst.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(src, dst)


def generate_pack(src_root: Path, out_root: Path, install_mod: Path | None = None) -> dict[str, int]:
    stats = {"keep": 0, "theme": 0, "klein": 0}
    if install_mod is not None and _same_path(install_mod, src_root):
        # The default CLI points install back at the repo textures dir, so skip
        # the install copy when the source and destination are the same tree.
        install_mod = None
    klein_master = src_root / "item" / "stars" / "klein_star_6.png"
    if not klein_master.exists():
        klein_master = src_root / "item" / "stars" / "klein_star_1.png"
    master_img = Image.open(klein_master)

    if out_root.exists():
        shutil.rmtree(out_root)
    shutil.copytree(src_root, out_root)

    for src in sorted(src_root.rglob("*.png")):
        rel = src.relative_to(src_root).as_posix()
        dst = out_root / rel
        action, theme_name, strength = _match_rule(rel)

        if action == "klein":
            m = re.search(r"klein_star_(\d+)\.png", rel)
            tier = int(m.group(1)) if m else 1
            klein_tier_from_master(master_img, tier).save(dst)
            stats["klein"] += 1
        elif action == "theme" and theme_name:
            theme = THEMES[theme_name]
            remap_image(Image.open(src), theme, strength).save(dst)
            stats["theme"] += 1
        else:
            stats["keep"] += 1

    # Curios slot icon from klein tier 1.
    # Curios slot JSON expects `projecte:curios/empty_klein_star`, so keep the
    # generated art pack aligned with the runtime resource location.
    slot = out_root / "curios" / "empty_klein_star.png"
    k1 = out_root / "item" / "stars" / "klein_star_1.png"
    if k1.exists():
        slot.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(k1, slot)

    _sync_equipment_layers(out_root)

    if install_mod:
        if install_mod.exists():
            shutil.rmtree(install_mod)
        shutil.copytree(out_root, install_mod)

    return stats


def write_resource_pack_meta(pack_dir: Path, name: str = "ProjectE Original Art") -> None:
    pack_dir.mkdir(parents=True, exist_ok=True)
    meta = {
        "pack": {
            "description": name,
            "pack_format": 64,
            "supported_formats": {"min_inclusive": 64, "max_inclusive": 64},
        }
    }
    import json

    (pack_dir / "pack.mcmeta").write_text(json.dumps(meta, indent=2), encoding="utf-8")
