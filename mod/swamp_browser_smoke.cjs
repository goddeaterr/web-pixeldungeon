// Run against a local html:distDebug -PwebDebug build with ?autotest.
// Set NODE_PATH to a Node installation containing Playwright; pass a URL if needed.
const {chromium} = require('playwright');
const os = require('os');
const path = require('path');
const capture = name => path.join(process.env.SMOKE_OUTPUT_DIR || os.tmpdir(), name);

(async () => {
  const browser = await chromium.launch({
    headless: true,
    executablePath: process.env.BROWSER_BIN || 'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe',
    args: ['--no-sandbox']
  });
  const page = await browser.newPage({viewport: {width: 1280, height: 900}});
  const errors = [];
  page.on('pageerror', error => errors.push(error.message));
  page.on('console', message => {
    if (message.type() === 'error') errors.push(message.text());
  });
  try {
    await page.goto(process.argv[2] || 'http://127.0.0.1:8765/?autotest', {waitUntil: 'domcontentloaded'});
    await page.waitForFunction(() => window.spd && window.spd.autotestStatus && window.spd.debug,
      null, {timeout: 120000});
    await page.waitForFunction(() => {
      try { return window.spd.autotestStatus().includes('scene=GameScene'); }
      catch { return false; }
    },
      null, {timeout: 120000});
    await page.evaluate(() => window.spd.debug('goto 6 4'));
    await page.waitForFunction(() => window.spd.autotestStatus().includes('depth=6')
      && window.spd.autotestStatus().includes('scene=GameScene'), null, {timeout: 120000});
    await page.evaluate(() => window.spd.autotest(false));
    await page.evaluate(() => window.spd.debug('reveal'));
    await page.waitForTimeout(700);
    const villagePos = await page.evaluate(() => Number(/pos=(\d+)/.exec(window.spd.autotestStatus())[1]));
    await page.evaluate(() => window.spd.debug('tp LastHearthMerchant'));
    await page.waitForFunction(previous => !window.spd.autotestStatus().includes(`pos=${previous} `),
      villagePos, {timeout: 15000});
    await page.waitForTimeout(1200);
    await page.screenshot({path: capture('swamp-village-browser.png')});
    console.log('village:', await page.evaluate(() => window.spd.autotestStatus()));
    await page.evaluate(() => window.spd.debug('info LastHearthMerchant'));
    await page.waitForTimeout(1200);
    await page.screenshot({path: capture('swamp-shopkeeper-browser.png')});
    await page.keyboard.press('Escape');
    await page.evaluate(() => window.spd.debug('goto 7 4'));
    await page.waitForFunction(() => window.spd.autotestStatus().includes('depth=7')
      && window.spd.autotestStatus().includes('scene=GameScene'), null, {timeout: 120000});
    await page.evaluate(() => window.spd.debug('reveal'));
    await page.waitForTimeout(1200);
    await page.screenshot({path: capture('swamp-floor-browser.png')});
    console.log('swamp:', await page.evaluate(() => window.spd.autotestStatus()));
    await page.evaluate(() => window.spd.debug('goto 10 4'));
    await page.waitForFunction(() => window.spd.autotestStatus().includes('depth=10')
      && window.spd.autotestStatus().includes('scene=GameScene'), null, {timeout: 120000});
    await page.evaluate(() => window.spd.debug('reveal'));
    await page.evaluate(() => window.spd.debug('tough'));
    await page.evaluate(() => window.spd.debug('tp GraveWarden'));
    await page.waitForTimeout(1600);
    await page.screenshot({path: capture('swamp-boss-browser.png')});
    console.log('boss:', await page.evaluate(() => window.spd.autotestStatus()));
    if (process.env.TEST_BOSS_COMBAT) {
      await page.evaluate(() => window.spd.autotest(true));
      await page.waitForTimeout(15000);
      await page.evaluate(() => window.spd.autotest(false));
      await page.screenshot({path: capture('swamp-boss-combat-browser.png')});
      console.log('boss combat:', await page.evaluate(() => window.spd.autotestStatus()));
    }
    if (errors.length) throw new Error(errors.join('\n'));
    console.log('Browser smoke passed: village, swamp, boss loaded without errors.');
  } finally {
    await browser.close();
  }
})().catch(error => { console.error(error); process.exitCode = 1; });
