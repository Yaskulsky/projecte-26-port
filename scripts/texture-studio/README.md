# ProjectE Texture Studio

Narzedzie do **porzadnej** pracy nad teksturami — nie generuje koleczek z PowerShella.

## Setup (raz)

```powershell
.\scripts\texture-studio\setup.ps1
```

Tworzy venv w `scripts/texture-studio/.venv` i instaluje Pillow.

## Uruchomienie

```powershell
.\scripts\texture-studio\run.ps1 preview
.\scripts\texture-studio\run.ps1 validate
.\scripts\texture-studio\run.ps1 klein-tiers -m src\main\resources\assets\projecte\textures\item\stars\klein_star_6.png -o build\klein-test
.\scripts\texture-studio\run.ps1 remap -i src\...\pickaxe.png -o build\dm_pick.png -t dm
.\scripts\texture-studio\run.ps1 ai-prompts -o build\ai-prompts
```

## Komendy

| Komenda | Co robi |
|---------|---------|
| `preview` | HTML z wszystkimi PNG (pixelated zoom) |
| `validate` | Sprawdza rozmiary 16x16 / 64x32 |
| `manifest` | JSON lista plikow |
| `remap` | Jedna tekstura, nowa paleta (zachowuje ksztalt) |
| `batch-theme` | Caly folder z ta sama paleta |
| `klein-tiers` | Tier 1-6 z mastera (facety zostaja) |
| `generate-pack` | **Pelny art pack** → `build/pe-art-pack/` + instalacja do moda |
| `ai-prompts` | Pliki .txt pod PixelLab / MJ / DALL-E |
| `ai-image` | Opcjonalnie API OpenAI (`OPENAI_API_KEY`) |

## Zalecany workflow art packa

1. **Nie generuj od zera** — wez oryginal PE PNG jako master.
2. `preview` — zobacz calosc w przegladarce.
3. `klein-tiers` / `remap` — warianty w `build/`, nie w repo.
4. Reczna poprawka w Aseprite / LibreSprite.
5. Dopiero potem podmiana w `src/main/resources/...`.
6. `ai-prompts` + zewnetrzne AI na itemy bez dobrego mastera.

## Prawa

- Remap **oryginalnych** tekstur ProjectE (MIT) = OK do moda.
- Nie trenuj AI na assetach Bbublick (ARR).
- Finalny pixel-art w edytorze = twoje dzielo.
