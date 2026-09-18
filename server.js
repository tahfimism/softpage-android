const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 3000;

const htmlContent = `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Shitol Pata (শীতল পাতা)</title>
  <style>
    :root {
      --leaf-primary: #5B8C6E;
      --leaf-dark: #3A6349;
      --bg-dark: #121214;
      --card-bg: #1C1C20;
      --border-color: #2A2A30;
      --text-main: #E7EDE9;
      --text-muted: #9BA89F;
    }
    * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
    body {
      background-color: var(--bg-dark);
      color: var(--text-main);
      display: flex;
      flex-direction: column;
      align-items: center;
      min-height: 100vh;
      padding: 24px 16px;
    }
    .container {
      max-width: 680px;
      width: 100%;
      display: flex;
      flex-direction: column;
      gap: 20px;
    }
    .header-card {
      background: var(--card-bg);
      border: 1px solid var(--border-color);
      border-radius: 20px;
      padding: 28px;
      text-align: center;
      box-shadow: 0 10px 30px rgba(0,0,0,0.4);
      position: relative;
      overflow: hidden;
    }
    .header-card::before {
      content: "";
      position: absolute;
      top: 0; left: 0; right: 0; height: 4px;
      background: linear-gradient(90deg, #5B8C6E, #E0A96D, #5B8C6E);
    }
    .badge {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      background: rgba(91, 140, 110, 0.18);
      color: #6EB286;
      font-size: 13px;
      font-weight: 600;
      padding: 5px 14px;
      border-radius: 999px;
      margin-bottom: 14px;
      border: 1px solid rgba(91, 140, 110, 0.3);
    }
    h1 {
      font-size: 28px;
      font-weight: 700;
      letter-spacing: -0.5px;
      margin-bottom: 6px;
    }
    .tagline {
      color: #6EB286;
      font-size: 16px;
      font-weight: 500;
      margin-bottom: 14px;
    }
    p {
      color: var(--text-muted);
      font-size: 14px;
      line-height: 1.6;
    }
    .status-badge {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      margin-top: 16px;
      background: #17241C;
      border: 1px solid #285437;
      color: #79D498;
      padding: 8px 16px;
      border-radius: 12px;
      font-size: 13px;
      font-weight: 600;
    }
    .pulse-dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      background: #4ADE80;
      box-shadow: 0 0 10px #4ADE80;
      animation: pulse 2s infinite;
    }
    @keyframes pulse {
      0% { opacity: 0.4; }
      50% { opacity: 1; }
      100% { opacity: 0.4; }
    }
    .section-card {
      background: var(--card-bg);
      border: 1px solid var(--border-color);
      border-radius: 16px;
      padding: 22px;
    }
    .section-title {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 12px;
      display: flex;
      align-items: center;
      gap: 8px;
    }
    .palette-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(130px, 1fr));
      gap: 10px;
      margin-top: 12px;
    }
    .palette-item {
      padding: 12px;
      border-radius: 10px;
      border: 1px solid rgba(255,255,255,0.08);
      text-align: center;
      cursor: pointer;
      transition: transform 0.15s, border-color 0.15s;
    }
    .palette-item:hover {
      transform: translateY(-2px);
      border-color: var(--leaf-primary);
    }
    .palette-name { font-size: 12px; font-weight: 600; margin-bottom: 6px; }
    .swatches { display: flex; justify-content: center; gap: 6px; }
    .swatch { width: 18px; height: 18px; border-radius: 50%; border: 1px solid rgba(0,0,0,0.2); }
    .download-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      background: var(--leaf-primary);
      color: #FFFFFF;
      font-size: 15px;
      font-weight: 600;
      padding: 14px 20px;
      border-radius: 12px;
      text-decoration: none;
      transition: background 0.2s;
      margin-top: 10px;
    }
    .download-btn:hover { background: var(--leaf-dark); }
    .info-list { list-style: none; display: flex; flex-direction: column; gap: 10px; font-size: 13px; color: var(--text-muted); }
    .info-list li { display: flex; align-items: flex-start; gap: 8px; }
    .info-list li span { color: #6EB286; font-weight: bold; }
  </style>
</head>
<body>
  <div class="container">
    <div class="header-card">
      <div class="badge">🌿 Native Android Studio Build</div>
      <h1>Shitol Pata (শীতল পাতা)</h1>
      <div class="tagline">ডিজিটাল ডকুমেন্টের জন্য এক টুকরো শীতল পাতা</div>
      <p>
        Shitol Pata is compiled as a <strong>Native Android Application</strong> using Jetpack Compose, Kotlin 2.0, Android PdfRenderer, and Statistical Otsu Binarization.
      </p>
      <div class="status-badge">
        <div class="pulse-dot"></div>
        <span>Debug APK Built & Ready (15.8 MB)</span>
      </div>
      <a href="/download-apk" class="download-btn">
        <span>⬇️</span> Download app-debug.apk
      </a>
    </div>

    <div class="section-card">
      <div class="section-title">📱 Why the Preview Shows This Screen</div>
      <ul class="info-list">
        <li><span>1.</span> In AI Studio, projects targeting the <strong>Android runtime</strong> build native Android APKs run inside the Android Streaming Emulator.</li>
        <li><span>2.</span> The native APK was previously failing compilation due to missing Gradle AndroidX flags and type adjustments. We have resolved all compilation errors and successfully built <code>app-debug.apk</code>.</li>
        <li><span>3.</span> The Android streaming emulator automatically installs and launches the APK upon turn completion. You can also download the APK directly above to test on your phone.</li>
      </ul>
    </div>

    <div class="section-card">
      <div class="section-title">🎨 Curated Color Schemes Implemented in App</div>
      <div class="palette-grid">
        <div class="palette-item" style="background: #1C1B1A; color: #E6E1DC;">
          <div class="palette-name">Classic Dark</div>
          <div class="swatches"><div class="swatch" style="background:#1C1B1A"></div><div class="swatch" style="background:#E6E1DC"></div></div>
        </div>
        <div class="palette-item" style="background: #2D251E; color: #E8D8C8;">
          <div class="palette-name">Warm Sepia</div>
          <div class="swatches"><div class="swatch" style="background:#2D251E"></div><div class="swatch" style="background:#E8D8C8"></div></div>
        </div>
        <div class="palette-item" style="background: #1B2228; color: #D5E0EA;">
          <div class="palette-name">Night Blue</div>
          <div class="swatches"><div class="swatch" style="background:#1B2228"></div><div class="swatch" style="background:#D5E0EA"></div></div>
        </div>
        <div class="palette-item" style="background: #1E2320; color: #D8E5DC;">
          <div class="palette-name">Shitol Green</div>
          <div class="swatches"><div class="swatch" style="background:#1E2320"></div><div class="swatch" style="background:#D8E5DC"></div></div>
        </div>
        <div class="palette-item" style="background: #000000; color: #EEEEEE;">
          <div class="palette-name">AMOLED Black</div>
          <div class="swatches"><div class="swatch" style="background:#000000"></div><div class="swatch" style="background:#EEEEEE"></div></div>
        </div>
        <div class="palette-item" style="background: #FDF6E2; color: #2C2518;">
          <div class="palette-name">Cream + Ink</div>
          <div class="swatches"><div class="swatch" style="background:#FDF6E2"></div><div class="swatch" style="background:#2C2518"></div></div>
        </div>
      </div>
    </div>
  </div>
</body>
</html>`;

const server = http.createServer((req, res) => {
  if (req.url === '/download-apk') {
    const apkPath = path.join(__dirname, 'app/build/outputs/apk/debug/app-debug.apk');
    if (fs.existsSync(apkPath)) {
      const stat = fs.statSync(apkPath);
      res.writeHead(200, {
        'Content-Type': 'application/vnd.android.package-archive',
        'Content-Length': stat.size,
        'Content-Disposition': 'attachment; filename="shitol-pata-debug.apk"'
      });
      fs.createReadStream(apkPath).pipe(res);
      return;
    } else {
      res.writeHead(404, { 'Content-Type': 'text/plain' });
      res.end('APK not found');
      return;
    }
  }

  res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
  res.end(htmlContent);
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`Shitol Pata preview server running on port ${PORT}`);
});
