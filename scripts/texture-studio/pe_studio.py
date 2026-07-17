#!/usr/bin/env python3
"""CLI for Equivox texture studio."""

from __future__ import annotations

import argparse
import json
import sys
from io import BytesIO
from pathlib import Path

from PIL import Image

from pe_studio.ai_export import openai_image, write_prompt_pack
from pe_studio.palette import THEMES
from pe_studio.pack_generator import generate_pack, write_resource_pack_meta
from pe_studio.preview import write_preview
from pe_studio.prompt_gen import generate_prompt_files
from pe_studio.remap import klein_tier_from_master, remap_image

REPO_ROOT = Path(__file__).resolve().parents[2]
DEFAULT_TEX = REPO_ROOT / "src" / "main" / "resources" / "assets" / "equivox" / "textures"


def _repo_tex() -> Path:
    return DEFAULT_TEX


def cmd_remap(args: argparse.Namespace) -> int:
    theme = THEMES[args.theme]
    src = Path(args.input)
    dst = Path(args.output)
    img = remap_image(Image.open(src), theme)
    dst.parent.mkdir(parents=True, exist_ok=True)
    img.save(dst)
    print(f"remap {src.name} -> {dst} ({theme.name})")
    return 0


def cmd_batch_theme(args: argparse.Namespace) -> int:
    """Remap a folder of PNGs with the same theme (e.g. prototype DM tool set)."""
    theme = THEMES[args.theme]
    src_dir = Path(args.input)
    out_dir = Path(args.output)
    n = 0
    for src in sorted(src_dir.glob("*.png")):
        dst = out_dir / src.name
        remap_image(Image.open(src), theme).save(dst)
        n += 1
    print(f"batch-theme: {n} files -> {out_dir}")
    return 0


def cmd_klein_tiers(args: argparse.Namespace) -> int:
    master_path = Path(args.master)
    out_dir = Path(args.output)
    out_dir.mkdir(parents=True, exist_ok=True)
    master = Image.open(master_path)
    for tier in range(1, 7):
        out = out_dir / f"klein_star_{tier}.png"
        klein_tier_from_master(master, tier).save(out)
        print(f"klein tier {tier} -> {out}")
    return 0


def cmd_generate_pack(args: argparse.Namespace) -> int:
    import shutil as _shutil

    src = Path(args.input)
    out = Path(args.output)
    install = None if args.no_install else Path(args.install)
    stats = generate_pack(src, out, install)
    rp = out.parent.parent / "pe-art-pack-resourcepack"
    if rp.exists():
        _shutil.rmtree(rp)
    rp_assets = rp / "assets" / "equivox" / "textures"
    rp_assets.parent.mkdir(parents=True, exist_ok=True)
    _shutil.copytree(out, rp_assets)
    write_resource_pack_meta(rp)
    print(
        f"generate-pack: keep={stats['keep']} theme={stats['theme']} klein={stats['klein']}"
    )
    print(f"output: {out}")
    print(f"resource pack: {rp}")
    if install:
        print(f"installed to mod: {install}")
    return 0


def cmd_prompt_pack(args: argparse.Namespace) -> int:
    tex = Path(args.input)
    inventory = sorted(
        p.relative_to(tex).as_posix()[:-4]
        for p in tex.rglob("*.png")
    )
    out = Path(args.output).resolve()
    stats = generate_prompt_files(inventory, out)
    print(f"prompt-pack: {stats['prompts']} prompts, {stats['skipped']} skipped -> {out}")
    return 0


def cmd_preview(args: argparse.Namespace) -> int:
    root = Path(args.input)
    out = Path(args.output)
    n = write_preview(root, out, scale=args.scale)
    print(f"preview: {n} textures -> {out}")
    return 0


def cmd_manifest(args: argparse.Namespace) -> int:
    root = Path(args.input)
    items = []
    for p in sorted(root.rglob("*.png")):
        img = Image.open(p)
        items.append(
            {
                "path": p.relative_to(root).as_posix(),
                "width": img.width,
                "height": img.height,
            }
        )
    out = Path(args.output)
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text(json.dumps(items, indent=2), encoding="utf-8")
    print(f"manifest: {len(items)} entries -> {out}")
    return 0


def cmd_validate(args: argparse.Namespace) -> int:
    root = Path(args.input)
    errors: list[str] = []
    for p in sorted(root.rglob("*.png")):
        img = Image.open(p)
        rel = p.relative_to(root).as_posix()
        if "item" in rel and img.width != 16 and img.height != 16:
            if img.width != 32 or img.height != 32:
                errors.append(f"{rel}: item should be 16x16, got {img.width}x{img.height}")
        if "models/armor" in rel or "entity/equipment" in rel:
            if img.width != 64 or img.height != 32:
                errors.append(f"{rel}: armor layer should be 64x32, got {img.width}x{img.height}")
    if errors:
        for e in errors:
            print("WARN", e)
        return 1
    print(f"validate OK ({len(list(root.rglob('*.png')))} png)")
    return 0


def cmd_ai_prompts(args: argparse.Namespace) -> int:
    n = write_prompt_pack(Path(args.output))
    print(f"ai-prompts: {n} files -> {args.output}")
    return 0


def cmd_ai_image(args: argparse.Namespace) -> int:
    prompt = Path(args.prompt_file).read_text(encoding="utf-8").strip()
    raw = openai_image(prompt)
    img = Image.open(BytesIO(raw)).convert("RGBA")
    size = args.size
    img = img.resize((size, size), Image.Resampling.NEAREST)
    out = Path(args.output)
    out.parent.mkdir(parents=True, exist_ok=True)
    img.save(out)
    print(f"ai-image -> {out} ({size}x{size})")
    return 0


def cmd_apply_variant(args: argparse.Namespace) -> int:
    """
    Safe workflow: copy originals to output tree, apply theme remaps only where requested.
    Does NOT touch repo textures unless --in-place.
    """
    theme = THEMES[args.theme]
    mappings: list[tuple[str, str]] = [
        ("item/dm_tools/pickaxe.png", "item/dm_tools/pickaxe.png"),
        ("item/rm_tools/pickaxe.png", "item/rm_tools/pickaxe.png"),
    ]
    if args.mapping_file:
        mappings = []
        for line in Path(args.mapping_file).read_text(encoding="utf-8").splitlines():
            line = line.strip()
            if not line or line.startswith("#"):
                continue
            src, dst = line.split("=", 1)
            mappings.append((src.strip(), dst.strip()))

    base_in = Path(args.input)
    base_out = base_in if args.in_place else Path(args.output)
    if not args.in_place:
        import shutil

        if base_out.exists() and args.clean:
            shutil.rmtree(base_out)
        shutil.copytree(base_in, base_out, dirs_exist_ok=True)

    for rel_src, rel_dst in mappings:
        src = base_in / rel_src
        if not src.exists():
            print(f"skip missing {rel_src}")
            continue
        dst = base_out / rel_dst
        remap_image(Image.open(src), theme).save(dst)
        print(f"variant {rel_src} -> {rel_dst}")
    return 0


def build_parser() -> argparse.ArgumentParser:
    p = argparse.ArgumentParser(description="Equivox texture studio")
    sub = p.add_subparsers(dest="cmd", required=True)

    r = sub.add_parser("remap", help="Remap one PNG with a PE theme")
    r.add_argument("-i", "--input", required=True)
    r.add_argument("-o", "--output", required=True)
    r.add_argument("-t", "--theme", choices=THEMES.keys(), default="dm")
    r.set_defaults(func=cmd_remap)

    b = sub.add_parser("batch-theme", help="Remap all PNGs in a folder")
    b.add_argument("-i", "--input", required=True)
    b.add_argument("-o", "--output", required=True)
    b.add_argument("-t", "--theme", choices=THEMES.keys(), required=True)
    b.set_defaults(func=cmd_batch_theme)

    k = sub.add_parser("klein-tiers", help="Build klein_star_1..6 from master PNG")
    k.add_argument("-m", "--master", required=True, help="Brightest star (e.g. klein_star_6.png)")
    k.add_argument("-o", "--output", required=True)
    k.set_defaults(func=cmd_klein_tiers)

    gp = sub.add_parser("generate-pack", help="Full PE art pack from masters")
    gp.add_argument("-i", "--input", default=str(_repo_tex()))
    gp.add_argument("-o", "--output", default="build/pe-art-pack/textures")
    gp.add_argument(
        "--install",
        help="Copy into mod textures dir (default: repo textures)",
        default=str(_repo_tex()),
    )
    gp.add_argument("--no-install", action="store_true", help="Skip mod install")
    gp.set_defaults(func=cmd_generate_pack)

    pp = sub.add_parser("prompt-pack", help="Per-texture AI prompts (ComfyUI/PixelLab)")
    pp.add_argument("-i", "--input", default=str(_repo_tex()))
    pp.add_argument("-o", "--output", default="build/ai-prompts")
    pp.set_defaults(func=cmd_prompt_pack)

    pr = sub.add_parser("preview", help="HTML contact sheet")
    pr.add_argument("-i", "--input", default=str(_repo_tex()))
    pr.add_argument("-o", "--output", default="build/texture-preview.html")
    pr.add_argument("--scale", type=int, default=8)
    pr.set_defaults(func=cmd_preview)

    m = sub.add_parser("manifest", help="JSON list of textures + sizes")
    m.add_argument("-i", "--input", default=str(_repo_tex()))
    m.add_argument("-o", "--output", default="build/texture-manifest.json")
    m.set_defaults(func=cmd_manifest)

    v = sub.add_parser("validate", help="Check expected PNG dimensions")
    v.add_argument("-i", "--input", default=str(_repo_tex()))
    v.set_defaults(func=cmd_validate)

    ap = sub.add_parser("ai-prompts", help="Write prompt .txt files for external AI")
    ap.add_argument("-o", "--output", default="build/ai-prompts")
    ap.set_defaults(func=cmd_ai_prompts)

    ai = sub.add_parser("ai-image", help="OpenAI DALL-E (needs OPENAI_API_KEY)")
    ai.add_argument("--prompt-file", required=True)
    ai.add_argument("-o", "--output", required=True)
    ai.add_argument("--size", type=int, default=16)
    ai.set_defaults(func=cmd_ai_image)

    av = sub.add_parser("apply-variant", help="Copy tree + remap selected paths")
    av.add_argument("-i", "--input", default=str(_repo_tex()))
    av.add_argument("-o", "--output", default="build/texture-variant")
    av.add_argument("-t", "--theme", choices=THEMES.keys(), required=True)
    av.add_argument("--mapping-file", help="lines: src.png=dst.png")
    av.add_argument("--in-place", action="store_true")
    av.add_argument("--clean", action="store_true")
    av.set_defaults(func=cmd_apply_variant)

    return p


def main() -> int:
    parser = build_parser()
    args = parser.parse_args()
    return args.func(args)


if __name__ == "__main__":
    sys.exit(main())
