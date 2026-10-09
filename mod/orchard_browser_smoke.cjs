// Browser runtime check for the village start and +11 to +16.
const {chromium} = require('playwright');
const os = require('os');
const path = require('path');
const capture = name => path.join(process.env.SMOKE_OUTPUT_DIR || os.tmpdir(), name);

(async () => {
  const browser = await chromium.launch({headless: true,
    executablePath: process.env.BROWSER_BIN || 'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe',
    args: ['--no-sandbox']});
  const page = await browser.newPage({viewport: {width: 390, height: 844}, deviceScaleFactor: 1,
    locale: process.env.SMOKE_LOCALE || 'en-US'});
  const errors = [];
  page.on('pageerror', error => errors.push(error.message));
  page.on('console', message => { if (message.type() === 'error') errors.push(message.text()); });
  try {
    await page.goto(process.argv[2] || 'http://127.0.0.1:8765/?autotest', {waitUntil: 'domcontentloaded'});
    await page.waitForFunction(() => window.spd && window.spd.autotestStatus && window.spd.debug,
      null, {timeout: 120000});
    await page.waitForFunction(() => {
      try { return window.spd.autotestStatus().includes('scene=GameScene'); }
      catch { return false; }
    },
      null, {timeout: 120000});
    console.log('start:', await page.evaluate(() => window.spd.autotestStatus()));
    await page.evaluate(() => window.spd.autotest(false));
    for (const floor of [11, 12, 13, 14, 15, 16]) {
      await page.evaluate(n => window.spd.debug(`goto ${n} 4`), floor);
      await page.waitForFunction(n => window.spd.autotestStatus().includes(`depth=${n}`)
        && window.spd.autotestStatus().includes('scene=GameScene')
        && window.spd.autotestStatus().includes('ready=true'), floor, {timeout: 120000});
      await page.evaluate(() => window.spd.debug('reveal'));
      await page.waitForTimeout(750);
      await page.screenshot({path: capture(`orchard-${floor}.png`)});
      console.log(`+${floor}:`, await page.evaluate(() => window.spd.autotestStatus()));
    }
    await page.evaluate(() => window.spd.debug('goto 11 4'));
    await page.waitForFunction(() => window.spd.autotestStatus().includes('depth=11')
      && window.spd.autotestStatus().includes('scene=GameScene')
      && window.spd.autotestStatus().includes('ready=true'), null, {timeout: 120000});
    await page.evaluate(() => window.spd.debug('tp OrchardMerchant'));
    await page.waitForTimeout(500);
    await page.screenshot({path: capture('orchard-shop.png')});
    await page.evaluate(() => window.spd.debug('talk OrchardMerchant'));
    await page.waitForTimeout(500);
    await page.screenshot({path: capture('orchard-shop-talk.png')});
    await page.mouse.click(190, 498);
    await page.waitForTimeout(350);
    await page.screenshot({path: capture('orchard-shop-dialogue.png')});
    if (errors.length) throw new Error(errors.join('\n'));
    console.log('Browser smoke passed: +11 to +16 and the waystation dialogue loaded without errors.');
  } finally {
    await browser.close();
  }
})().catch(error => { console.error(error); process.exitCode = 1; });
