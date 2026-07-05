"""ProjectE colour themes for luminance-preserving remaps."""

from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class Theme:
    name: str
    dark: tuple[int, int, int]
    mid: tuple[int, int, int]
    bright: tuple[int, int, int]
    glow: tuple[int, int, int]


THEMES: dict[str, Theme] = {
    "dm": Theme(
        "dm",
        dark=(18, 20, 26),
        mid=(28, 32, 40),
        bright=(50, 58, 68),
        glow=(80, 230, 255),
    ),
    "rm": Theme(
        "rm",
        dark=(30, 8, 12),
        mid=(90, 20, 28),
        bright=(150, 35, 45),
        glow=(255, 100, 70),
    ),
    "gem": Theme(
        "gem",
        dark=(160, 165, 175),
        mid=(200, 205, 215),
        bright=(230, 235, 245),
        glow=(255, 200, 255),
    ),
    "alchemical": Theme(
        "alchemical",
        dark=(22, 14, 32),
        mid=(42, 28, 58),
        bright=(58, 40, 78),
        glow=(255, 220, 80),
    ),
}


def pick_color(lum: float, theme: Theme) -> tuple[int, int, int]:
    if lum > 200:
        return theme.glow
    if lum > 120:
        return theme.bright
    if lum > 55:
        return theme.mid
    return theme.dark
