(() => {
  const MARK = 'data-italiatv-fullscreen';
  const LOADER = 'data-italiatv-loader';
  const IS_TOP = window.top === window;
  const FALLBACK_MS = 15000;
  const STOP_SEARCH_MS = 60000;
  const startedAt = Date.now();

  let bootStyle = null;
  let loader = null;
  let maximized = false;
  let fallbackReleased = false;

  function important(el, prop, value) {
    if (el && el.style) el.style.setProperty(prop, value, 'important');
  }

  function installBootMask() {
    if (!IS_TOP || bootStyle) return;
    bootStyle = document.createElement('style');
    bootStyle.id = 'italiatv-boot-mask';
    bootStyle.textContent = `
      html, body { background:#000 !important; }
      body > *:not([${LOADER}]) { opacity:0 !important; }
      [${LOADER}] { opacity:1 !important; visibility:visible !important; }
    `;
    (document.documentElement || document).appendChild(bootStyle);
  }

  function ensureLoader() {
    if (!IS_TOP || loader || !document.body) return;
    loader = document.createElement('div');
    loader.setAttribute(LOADER, '1');
    loader.textContent = 'ItaliaTV  •  zender laden…';
    Object.assign(loader.style, {
      position: 'fixed',
      inset: '0',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: '#000',
      color: '#fff',
      fontFamily: 'sans-serif',
      fontSize: '26px',
      fontWeight: '600',
      zIndex: '2147483647'
    });
    document.body.appendChild(loader);
  }

  function releaseBootMask() {
    if (!IS_TOP) return;
    fallbackReleased = true;
    if (bootStyle) {
      bootStyle.remove();
      bootStyle = null;
    }
    if (loader) {
      loader.remove();
      loader = null;
    }
  }

  installBootMask();
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', ensureLoader, { once: true });
  } else {
    ensureLoader();
  }

  function descriptor(el) {
    return [
      el.id || '',
      typeof el.className === 'string' ? el.className : '',
      el.getAttribute && (el.getAttribute('aria-label') || ''),
      el.getAttribute && (el.getAttribute('title') || '')
    ].join(' ').toLowerCase();
  }

  function scoreElement(el) {
    if (!el || !el.getBoundingClientRect) return 0;
    const rect = el.getBoundingClientRect();
    if (rect.width < 160 || rect.height < 90) return 0;

    const style = window.getComputedStyle(el);
    if (style.display === 'none' || style.visibility === 'hidden') return 0;

    const desc = descriptor(el);
    if (/(cookie|consent|privacy|banner|advert|doubleclick|googleads|promo)/.test(desc)) return 0;

    let score = rect.width * rect.height;
    if (/(live|diretta|stream|video|player|radiovision|tv)/.test(desc)) score *= 2.4;
    if (el.tagName === 'VIDEO') score *= 1.8;
    return score;
  }

  function findPlayer() {
    const selectors = [
      '[data-player]',
      '[id*="player"]',
      '[class*="live-player"]',
      '[class*="livePlayer"]',
      '[class*="stream-player"]',
      '[class*="video-player"]',
      '[class*="videoPlayer"]',
      '.video-js',
      '.vjs-player',
      '[class*="player"] video',
      'video'
    ];

    const seen = new Set();
    const candidates = [];
    selectors.forEach(selector => {
      document.querySelectorAll(selector).forEach(el => {
        if (!seen.has(el)) {
          seen.add(el);
          candidates.push(el);
        }
      });
    });

    candidates.sort((a, b) => scoreElement(b) - scoreElement(a));
    const hit = candidates.find(el => scoreElement(el) > 25000);
    if (!hit) return null;

    if (hit.tagName === 'VIDEO') {
      return hit.closest(
        '.video-js, .vjs-player, [data-player], [id*="player"], [class*="player"], [class*="Player"]'
      ) || hit.parentElement || hit;
    }
    return hit;
  }

  function findLikelyVideoFrame() {
    const frames = Array.from(document.querySelectorAll('iframe'))
      .filter(frame => {
        const src = (frame.getAttribute('src') || '').toLowerCase();
        const desc = descriptor(frame) + ' ' + src;
        return !/(consent|privacy|cookie|advert|doubleclick|googleads|recaptcha)/.test(desc);
      })
      .sort((a, b) => {
        const boost = el => {
          const src = (el.getAttribute('src') || '').toLowerCase();
          const factor = /(live|diretta|stream|video|player|radiovision|tv)/.test(src) ? 2.2 : 1;
          return scoreElement(el) * factor;
        };
        return boost(b) - boost(a);
      });

    if (!frames.length) return null;
    const first = frames[0];
    return scoreElement(first) > 40000 ? first : null;
  }

  function maximize(target) {
    if (!target || maximized || target.getAttribute(MARK) === '1') return false;
    maximized = true;
    target.setAttribute(MARK, '1');
    releaseBootMask();

    const path = new Set();
    let node = target;
    while (node && node.nodeType === 1) {
      path.add(node);
      node = node.parentElement;
    }

    important(document.documentElement, 'background', '#000');
    important(document.documentElement, 'overflow', 'hidden');
    if (document.body) {
      important(document.body, 'margin', '0');
      important(document.body, 'padding', '0');
      important(document.body, 'background', '#000');
      important(document.body, 'overflow', 'hidden');
    }

    document.querySelectorAll('body *').forEach(el => {
      if (!path.has(el) && !el.contains(target) && !target.contains(el)) {
        important(el, 'visibility', 'hidden');
      }
    });

    path.forEach(el => {
      important(el, 'visibility', 'visible');
      important(el, 'opacity', '1');
      important(el, 'max-width', 'none');
      important(el, 'max-height', 'none');
      important(el, 'overflow', 'visible');
    });

    important(target, 'position', 'fixed');
    important(target, 'inset', '0');
    important(target, 'width', '100vw');
    important(target, 'height', '100vh');
    important(target, 'max-width', 'none');
    important(target, 'max-height', 'none');
    important(target, 'margin', '0');
    important(target, 'padding', '0');
    important(target, 'z-index', '2147483646');
    important(target, 'background', '#000');
    important(target, 'visibility', 'visible');
    important(target, 'opacity', '1');

    const videos = target.tagName === 'VIDEO'
      ? [target]
      : Array.from(target.querySelectorAll('video'));

    videos.forEach(video => {
      important(video, 'position', 'absolute');
      important(video, 'inset', '0');
      important(video, 'width', '100%');
      important(video, 'height', '100%');
      important(video, 'object-fit', 'contain');
      important(video, 'background', '#000');
      important(video, 'visibility', 'visible');
      important(video, 'opacity', '1');
      video.controls = true;
    });

    return true;
  }

  function tryFullscreen() {
    if (maximized) return true;

    const player = findPlayer();
    if (player && maximize(player)) return true;

    const frame = findLikelyVideoFrame();
    if (frame && maximize(frame)) return true;

    return false;
  }

  const timer = setInterval(() => {
    ensureLoader();
    tryFullscreen();

    const elapsed = Date.now() - startedAt;
    if (!maximized && !fallbackReleased && elapsed >= FALLBACK_MS) {
      releaseBootMask();
    }
    if (elapsed >= STOP_SEARCH_MS) {
      clearInterval(timer);
    }
  }, 250);

  new MutationObserver(() => {
    ensureLoader();
    tryFullscreen();
  }).observe(document.documentElement, {
    childList: true,
    subtree: true,
    attributes: true,
    attributeFilter: ['src', 'class', 'style']
  });
})();
