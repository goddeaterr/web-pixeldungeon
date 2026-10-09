// Open each custom info window in a fresh Russian, phone-sized game session.
const {chromium} = require('playwright');
const os = require('os');
const path = require('path');
(async () => {
  const browser = await chromium.launch({headless: true,
    executablePath: process.env.BROWSER_BIN || 'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe',
    args: ['--no-sandbox']});
  const errors = [];
  try {
    for (const name of ['mob:AshGardener', 'mob:GlassMoth', 'mob:HollowScarecrow',
      'item:BlackFruit', 'item:AshglassShard', 'item:GlassSickle', 'item:ConservatorShears']) {
      const page = await browser.newPage({viewport: {width: 390, height: 844}, locale: 'ru-RU'});
      page.on('pageerror', e => errors.push(`${name}: ${e.message}`));
      page.on('console', m => { if (m.type() === 'error') errors.push(`${name}: ${m.text()}`); });
      await page.goto(process.argv[2] || 'http://127.0.0.1:8765/?autotest', {waitUntil: 'domcontentloaded'});
      await page.waitForFunction(() => {
        try { return window.spd && window.spd.debug && window.spd.autotestStatus().includes('scene=GameScene'); }
        catch { return false; }
      }, null, {timeout: 120000});
      await page.evaluate(() => window.spd.autotest(false));
      await page.evaluate(() => window.spd.debug('goto 12 4'));
      await page.waitForFunction(() => window.spd.autotestStatus().includes('depth=12')
        && window.spd.autotestStatus().includes('scene=GameScene')
        && window.spd.autotestStatus().includes('ready=true'), null, {timeout: 120000});
      await page.evaluate(n => window.spd.debug(`info ${n}`), name);
      await page.waitForTimeout(500);
      const out = path.join(process.env.SMOKE_OUTPUT_DIR || os.tmpdir(),
        `orchard-info-${name.replace(':', '-')}-ru.png`);
      await page.screenshot({path: out});
      console.log(name, out);
      // Closing an active TeaVM game tears down its audio context; that is outside the test.
      page.removeAllListeners('pageerror');
      page.removeAllListeners('console');
      await page.close();
    }
    if (errors.length) throw new Error(errors.join('\n'));
    console.log('Russian phone info windows passed.');
  } finally { await browser.close(); }
})().catch(e => { console.error(e); process.exitCode = 1; });
