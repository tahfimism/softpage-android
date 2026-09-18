"""
Batch recolor scanned PDFs in a directory: PDF -> images -> recolor background -> PDF

Best for scanned/image-based PDFs. Output pages are rasterized (not selectable text).

Requirements:
  pip install pdf2image pillow img2pdf

Poppler:
  - Windows: install poppler and set POPPLER_PATH below (or add to PATH)
  - Linux: sudo apt-get install poppler-utils
  - Mac: brew install poppler
"""

import os
import sys
import tempfile

from pdf2image import convert_from_path
from PIL import Image
import img2pdf


# =========================
# PARAMETERS (edit these)
# =========================
INPUT_DIR = r"."
OUTPUT_DIR = r"."


DPI = 200                       # 150-300 typical; higher = clearer, bigger files
THRESH = 235                    # 0-255; lower changes more light areas, higher changes only pure whites
PAPER_HEX = "#d1c6b4"           # off-white/beige paper color (e.g., "#F2E8D5")
SOFTEN = 0.0                    # 0.0-1.0; lift ink slightly toward paper (try 0.05 to 0.12)

# Windows only (leave None on Linux/Mac if poppler is installed normally)
POPPLER_PATH = r"F:\soft new stock\poppler-24.08.0\Library\bin"             # e.g., 

# Output format per page:
PAGE_IMAGE_FORMAT = "JPEG"      # "JPEG" or "PNG"
JPEG_QUALITY = 95               # only used if JPEG
# =========================




try:
    import pdf2image, PIL, img2pdf
except ImportError:
    print("Missing Python packages.")
    print("Run: pip install pdf2image pillow img2pdf")
    input("Press Enter to exit...")
    exit(1)




# -------------

def hex_to_rgb(h: str):
    h = h.strip().lstrip("#")
    if len(h) != 6:
        raise ValueError("PAPER_HEX must be like #RRGGBB")
    return tuple(int(h[i:i+2], 16) for i in (0, 2, 4))


def recolor_background(img: Image.Image, thresh: int, paper_rgb, soften: float = 0.0) -> Image.Image:
    """
    Replace near-white pixels with paper_rgb.
    soften (0..1) slightly pulls ink colors toward the paper color to reduce harsh contrast.
    """
    img = img.convert("RGB")
    px = img.load()
    w, h = img.size

    pr, pg, pb = paper_rgb
    soften = max(0.0, min(1.0, float(soften)))

    for y in range(h):
        for x in range(w):
            r, g, b = px[x, y]

            # Background: near-white pixels
            if r >= thresh and g >= thresh and b >= thresh:
                px[x, y] = (pr, pg, pb)
            else:
                # Optional: reduce harshness by lifting ink slightly
                if soften > 0.0:
                    nr = int(r + soften * (pr - r))
                    ng = int(g + soften * (pg - g))
                    nb = int(b + soften * (pb - b))
                    px[x, y] = (nr, ng, nb)

    return img


def process_pdf(input_pdf_path: str, output_pdf_path: str, paper_rgb):
    print(f"\n==> Processing: {os.path.basename(input_pdf_path)}")

    try:
        pages = convert_from_path(
            input_pdf_path,
            dpi=DPI,
            poppler_path=POPPLER_PATH
        )
    except Exception as e:
        print("   [ERROR] Failed to render pages (usually Poppler issue).")
        print("   ", e)
        return False

    with tempfile.TemporaryDirectory() as tmpdir:
        img_paths = []

        for i, page_img in enumerate(pages, start=1):
            processed = recolor_background(page_img, THRESH, paper_rgb, soften=SOFTEN)

            out_img_path = os.path.join(tmpdir, f"page_{i:04d}")
            if PAGE_IMAGE_FORMAT.upper() == "PNG":
                out_img_path += ".png"
                processed.save(out_img_path, "PNG", optimize=True)
            else:
                out_img_path += ".jpg"
                processed.save(out_img_path, "JPEG", quality=JPEG_QUALITY)

            img_paths.append(out_img_path)
            if i % 5 == 0 or i == len(pages):
                print(f"   Page {i}/{len(pages)}")

        # Build output PDF
        with open(output_pdf_path, "wb") as f:
            f.write(img2pdf.convert(img_paths))

    print(f"   Saved -> {output_pdf_path}")
    return True


def main():
    # Basic validation
    if not os.path.isdir(INPUT_DIR):
        print("INPUT_DIR not found:", INPUT_DIR, file=sys.stderr)
        sys.exit(1)

    os.makedirs(OUTPUT_DIR, exist_ok=True)

    if not (0 <= THRESH <= 255):
        print("THRESH must be 0..255", file=sys.stderr)
        sys.exit(1)

    try:
        paper_rgb = hex_to_rgb(PAPER_HEX)
    except Exception as e:
        print("Bad PAPER_HEX:", e, file=sys.stderr)
        sys.exit(1)

    pdfs = [f for f in os.listdir(INPUT_DIR) if f.lower().endswith(".pdf")]
    if not pdfs:
        print("No PDFs found in:", INPUT_DIR)
        return

    ok = 0
    fail = 0

    for fn in sorted(pdfs):
        in_path = os.path.join(INPUT_DIR, fn)
        base = os.path.splitext(fn)[0]
        out_path = os.path.join(OUTPUT_DIR, f"{base}_softpaper.pdf")

        if process_pdf(in_path, out_path, paper_rgb):
            ok += 1
        else:
            fail += 1

    print("\n==== Summary ====")
    print("Success:", ok)
    print("Failed :", fail)

    if fail > 0:
        print("\nIf failures mention Poppler:")
        print(" - Windows: set POPPLER_PATH to ...\\poppler\\Library\\bin")
        print(" - Or add Poppler's bin folder to PATH.")


if __name__ == "__main__":
    main()
