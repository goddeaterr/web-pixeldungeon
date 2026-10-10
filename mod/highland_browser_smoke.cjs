// Runtime check for +16–20 and Russian, phone-sized custom info panels.
const {chromium}=require('playwright');
const os=require('os');
const path=require('path');
const out=name=>path.join(process.env.SMOKE_OUTPUT_DIR||os.tmpdir(),name);

(async()=>{
  const browser=await chromium.launch({headless:true,
    executablePath:process.env.BROWSER_BIN||'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe',
    args:['--no-sandbox']});
  const page=await browser.newPage({viewport:{width:390,height:844},locale:'ru-RU'});
  const errors=[];
  page.on('pageerror',e=>errors.push(e.message));
  page.on('console',m=>{if(m.type()==='error')errors.push(m.text());});
  try{
    await page.goto(process.argv[2]||'http://127.0.0.1:8765/?autotest',{waitUntil:'domcontentloaded'});
    await page.waitForFunction(()=>{try{return window.spd&&window.spd.debug&&window.spd.autotestStatus().includes('scene=GameScene');}catch{return false;}},null,{timeout:120000});
    await page.evaluate(()=>window.spd.autotest(false));
    for(const floor of [16,17,18,19,20]){
      await page.evaluate(n=>window.spd.debug(`goto ${n} 4`),floor);
      await page.waitForFunction(n=>window.spd.autotestStatus().includes(`depth=${n}`)
        && window.spd.autotestStatus().includes('scene=GameScene')
        && window.spd.autotestStatus().includes('ready=true'),floor,{timeout:120000});
      await page.evaluate(()=>window.spd.debug('reveal'));
      await page.waitForTimeout(650);
      await page.screenshot({path:out(`highland-${floor}-ru.png`)});
      console.log(`+${floor}`,await page.evaluate(()=>window.spd.autotestStatus()));
    }
    for(const name of ['mob:SlateCrawler','mob:FrostSurveyor','mob:WindEffigy','mob:WindAnchor','mob:LastSurveyor',
      'item:WindglassShard','item:SurveyRation','item:AnchorPike']){
      await page.evaluate(n=>window.spd.debug(`info ${n}`),name);
      await page.waitForTimeout(300);
      await page.screenshot({path:out(`highland-info-${name.replace(':','-')}-ru.png`)});
      await page.keyboard.press('Escape');
      await page.waitForTimeout(150);
    }
    await page.evaluate(()=>window.spd.debug('goto 16 4'));
    await page.waitForFunction(()=>window.spd.autotestStatus().includes('depth=16')
      && window.spd.autotestStatus().includes('scene=GameScene')
      && window.spd.autotestStatus().includes('ready=true'),null,{timeout:120000});
    await page.evaluate(()=>window.spd.debug('tp SurveyMerchant'));
    await page.waitForTimeout(350);
    await page.screenshot({path:out('highland-shop-ru.png')});
    await page.evaluate(()=>window.spd.debug('talk SurveyMerchant'));
    await page.waitForTimeout(350);
    await page.screenshot({path:out('highland-shop-talk-ru.png')});
    await page.mouse.click(190,498);
    await page.waitForTimeout(300);
    await page.screenshot({path:out('highland-shop-dialogue-ru.png')});
    if(errors.length)throw new Error(errors.join('\n'));
    console.log('Highland browser smoke passed.');
  }finally{await browser.close();}
})().catch(e=>{console.error(e);process.exitCode=1;});
