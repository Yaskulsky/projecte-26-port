"""Luminance-preserving palette remap — keeps pixel-art structure, changes mood."""

from __future__ import annotations

from PIL import Image

from .palette import Theme, pick_color


def luminance(r: int, g: int, b: int) -> float:
    return 0.299 * r + 0.587 * g + 0.114 * b


def remap_image(src: Image.Image, theme: Theme, strength: float = 1.0) -> Image.Image:
    """Remap palette; strength 0=unchanged, 1=full theme."""
    strength = max(0.0, min(1.0, strength))
    src = src.convert("RGBA")
    out = Image.new("RGBA", src.size)
    sp = src.load()
    dp = out.load()
    assert sp is not None and dp is not None

    for y in range(src.height):
        for x in range(src.width):
            r, g, b, a = sp[x, y]
            if a < 16:
                dp[x, y] = (0, 0, 0, 0)
                continue
            if strength <= 0:
                dp[x, y] = (r, g, b, a)
                continue
            lum = luminance(r, g, b)
            nr, ng, nb = pick_color(lum, theme)
            if 55 < lum < 200:
                mix = 0.15
                nr = int(nr * (1 - mix) + r * mix)
                ng = int(ng * (1 - mix) + g * mix)
                nb = int(nb * (1 - mix) + b * mix)
            nr = int(r + (nr - r) * strength)
            ng = int(g + (ng - g) * strength)
            nb = int(b + (nb - b) * strength)
            dp[x, y] = (nr, ng, nb, a)
    return out


def klein_tier_from_master(master: Image.Image, tier: int, max_tier: int = 6) -> Image.Image:
    """Derive lower Klein Star tiers from the brightest reference (keeps facets)."""
    if tier < 1 or tier > max_tier:
        raise ValueError(f"tier must be 1..{max_tier}, got {tier}")
    power = tier / max_tier
    src = master.convert("RGBA")
    out = Image.new("RGBA", src.size)
    sp = src.load()
    dp = out.load()
    assert sp is not None and dp is not None

    for y in range(src.height):
        for x in range(src.width):
            r, g, b, a = sp[x, y]
            if a < 16:
                dp[x, y] = (0, 0, 0, 0)
                continue
            lum = luminance(r, g, b)
            if lum > 180:
                scale = 0.35 + 0.65 * power
                dp[x, y] = (
                    min(255, int(r * scale + 40 * power)),
                    min(255, int(g * scale + 60 * power)),
                    min(255, int(b * scale + 80 * power)),
                    a,
                )
            elif lum > 80:
                scale = 0.5 + 0.5 * power
                dp[x, y] = (int(r * scale), int(g * scale), int(b * scale), a)
            else:
                fade = 0.7 + 0.3 * power
                dp[x, y] = (int(r * fade), int(g * fade), int(b * fade), int(a * fade))
    return out
