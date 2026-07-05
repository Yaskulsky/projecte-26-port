"""Export AI prompt packs + optional OpenAI image API bridge."""

from __future__ import annotations

import json
import os
import urllib.request
from pathlib import Path

PROMPT_TEMPLATES: dict[str, str] = {
    "klein_star": (
        "Minecraft mod item sprite, 16x16 pixel art, Klein Star energy orb, "
        "faceted gem with {tier} glow intensity, cyan-white core, dark outline, "
        "transparent background, no anti-aliasing, game asset style"
    ),
    "dm_tool": (
        "Minecraft 16x16 pixel art tool icon, dark matter {tool}, charcoal metal "
        "with cyan energy veins, ProjectE mod style, transparent background"
    ),
    "ring": (
        "Minecraft 16x16 pixel art ring icon, {name}, gold band with glowing "
        "{gem_color} gem center, ProjectE magical ring, transparent background"
    ),
    "block_collector": (
        "Minecraft block texture 16x16 tile, energy collector mk{mk}, dark casing "
        "with glowing sun lens, pixel art, seamless edges"
    ),
}


def write_prompt_pack(out_dir: Path) -> int:
    out_dir.mkdir(parents=True, exist_ok=True)
    count = 0
    for tier in range(1, 7):
        text = PROMPT_TEMPLATES["klein_star"].format(tier=tier)
        (out_dir / f"klein_star_{tier}.txt").write_text(text, encoding="utf-8")
        count += 1
    for tool in ("pickaxe", "axe", "shovel", "sword", "hoe", "hammer", "shears"):
        text = PROMPT_TEMPLATES["dm_tool"].format(tool=tool)
        (out_dir / f"dm_{tool}.txt").write_text(text, encoding="utf-8")
        count += 1
    (out_dir / "README.txt").write_text(
        "Paste prompts into PixelLab, Scenario, or DALL-E.\n"
        "Downscale result to 16x16 with NEAREST neighbour.\n"
        "Or run: pe_studio.py ai-image --prompt-file klein_star_1.txt --out test.png\n",
        encoding="utf-8",
    )
    return count


def openai_image(prompt: str, size: str = "1024x1024") -> bytes:
    key = os.environ.get("OPENAI_API_KEY")
    if not key:
        raise RuntimeError("Set OPENAI_API_KEY environment variable")
    body = json.dumps(
        {"model": "dall-e-3", "prompt": prompt, "size": size, "n": 1}
    ).encode("utf-8")
    req = urllib.request.Request(
        "https://api.openai.com/v1/images/generations",
        data=body,
        headers={"Authorization": f"Bearer {key}", "Content-Type": "application/json"},
        method="POST",
    )
    with urllib.request.urlopen(req, timeout=120) as resp:
        data = json.loads(resp.read().decode("utf-8"))
    url = data["data"][0]["url"]
    with urllib.request.urlopen(url, timeout=60) as img_resp:
        return img_resp.read()
