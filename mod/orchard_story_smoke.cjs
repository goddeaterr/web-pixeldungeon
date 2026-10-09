// Verify the non-debug Russian opening illustration and text on a phone viewport.
const {chromium} = require('playwright');
const os = require('os');
const path = require('path');
(async () => {
  const browser = await chromium.launch({headless: true,
    executablePath: process.env.BROWSER_BIN || 'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe',
    args: ['--no-sandbox']});
  const page = await browser.newPage({viewport: {width: 390, height: 844}, locale: 'ru-RU'});
  const errors = [];
  page.on('pageerror', e => errors.push(e.message));
  page.on('console', m => { if (m.type() === 'error') errors.push(m.text()); });
  try {
    await page.goto(process.argv[2] || 'http://127.0.0.1:8766/?autotest', {waitUntil: 'domcontentloaded'});
    await page.waitForFunction(() => {
      try { return window.spd && window.spd.autotestStatus
        && window.spd.autotestStatus().includes('scene=InterlevelScene'); }
      catch { return false; }
    }, null, {timeout: 120000});
    await page.evaluate(() => window.spd.autotest(false));
    await page.waitForTimeout(1400);
    const out = path.join(process.env.SMOKE_OUTPUT_DIR || os.tmpdir(), 'orchard-opening-story-ru.png');
    await page.screenshot({path: out});
    console.log('story:', await page.evaluate(() => window.spd.autotestStatus()), out);
    if (errors.length) throw new Error(errors.join('\n'));
  } finally { await browser.close(); }
})().catch(e => { console.error(e); process.exitCode = 1; });
