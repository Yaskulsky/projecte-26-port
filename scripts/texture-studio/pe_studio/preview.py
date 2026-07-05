"""HTML contact-sheet preview for texture folders."""

from __future__ import annotations

import base64
import io
from pathlib import Path

from PIL import Image


def _to_data_uri(path: Path, scale: int = 8) -> str:
    img = Image.open(path).convert("RGBA")
    if scale > 1:
        img = img.resize((img.width * scale, img.height * scale), Image.Resampling.NEAREST)
    buf = io.BytesIO()
    img.save(buf, format="PNG")
    b64 = base64.standard_b64encode(buf.getvalue()).decode("ascii")
    return f"data:image/png;base64,{b64}"


def write_preview(root: Path, out_html: Path, scale: int = 8) -> int:
    pngs = sorted(root.rglob("*.png"))
    rows: list[str] = []
    for p in pngs:
        rel = p.relative_to(root).as_posix()
        uri = _to_data_uri(p, scale)
        rows.append(
            f'<div class="cell"><img src="{uri}" alt="{rel}"/><code>{rel}</code></div>'
        )

    html = f"""<!DOCTYPE html>
<html><head><meta charset="utf-8"/><title>PE textures</title>
<style>
body {{ background:#1a1a1a; color:#ddd; font-family:system-ui,sans-serif; padding:16px; }}
.grid {{ display:flex; flex-wrap:wrap; gap:12px; }}
.cell {{ background:#2a2a2a; border:1px solid #444; padding:8px; text-align:center; width:160px; }}
.cell img {{ image-rendering:pixelated; max-width:128px; }}
code {{ font-size:10px; word-break:break-all; display:block; margin-top:6px; }}
</style></head><body>
<h1>ProjectE textures ({len(pngs)} files)</h1>
<div class="grid">{''.join(rows)}</div>
</body></html>"""
    out_html.parent.mkdir(parents=True, exist_ok=True)
    out_html.write_text(html, encoding="utf-8")
    return len(pngs)
