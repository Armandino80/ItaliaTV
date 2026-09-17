(() => {
  const MARK = 'data-italiatv-fullscreen';

  function important(el, prop, value) {
    if (el && el.style) el.style.setProperty(prop, value, 'important');
  }

  function scoreElement(el) {
    const rect = el.getBoundingClientRect();
    if (rect.width < 120 || rect.height < 70) return 0;
    return rect.width * rect.height;
  }

  function findPlayer() {
    const preferred = [
      '.video-js',
      '.vjs-player',
      '[class*="video-player"]',
      '[class*="videoPlayer"]',
      '[class*="player"] video',
      'video'
    ];

    for (const selector of preferred) {
      const candidates = Array.from(document.querySelectorAll(selector));
      candidates.sort((a, b) => scoreElement(b) - scoreElement(a));
      if (candidates.length && scoreElement(candidates[0]) > 0) {
        const hit = candidates[0];
        if (hit.tagName === 'VIDEO') {
          return hit.closest('.video-js, [class*="player"], [class*="Player"]') || hit.parentElement || hit;
        }
        return hit;
      }
    }
    return null;
  }

  function findLikelyVideoFrame() {
    const frames = Array.from(document.querySelectorAll('iframe'))
      .filter(f => {
        const src = (f.getAttribute('src') || '').toLowerCase();
        return !/(consent|privacy|cookie|advert|doubleclick|googleads)/.test(src);
      })
      .sort((a, b) => scoreElement(b) - scoreElement(a));
    return frames.length && scoreElement(frames[0]) > 20000 ? frames[0] : null;
  }

  function maximize(target) {
    if (!target || target.getAttribute(MARK) === '1') return;
    target.setAttribute(MARK, '1');

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

    // Hide unrelated page chrome while leaving the player and its descendants visible.
    document.querySelectorAll('body *').forEach(el => {
      if (!path.has(el) && !el.contains(target) && !target.contains(el)) {
        important(el, 'visibility', 'hidden');
      }
    });

    path.forEach(el => {
      important(el, 'visibility', 'visible');
      important(el, 'max-width', 'none');
      important(el, 'max-height', 'none');
    });

    important(target, 'position', 'fixed');
    important(target, 'inset', '0');
    important(target, 'width', '100vw');
    important(target, 'height', '100vh');
    important(target, 'max-width', 'none');
    important(target, 'max-height', 'none');
    important(target, 'margin', '0');
    important(target, 'padding', '0');
    important(target, 'z-index', '2147483647');
    important(target, 'background', '#000');
    important(target, 'visibility', 'visible');

    target.querySelectorAll('video').forEach(video => {
      important(video, 'position', 'absolute');
      important(video, 'inset', '0');
      important(video, 'width', '100%');
      important(video, 'height', '100%');
      important(video, 'object-fit', 'contain');
      important(video, 'background', '#000');
      important(video, 'visibility', 'visible');
      video.controls = true;
    });
  }

  function tryFullscreen() {
    const player = findPlayer();
    if (player) {
      maximize(player);
      return true;
    }

    // If the broadcaster embeds the player cross-origin, enlarge the most likely frame.
    const frame = findLikelyVideoFrame();
    if (frame) {
      maximize(frame);
      return true;
    }
    return false;
  }

  let attempts = 0;
  const timer = setInterval(() => {
    attempts += 1;
    if (tryFullscreen() || attempts > 60) clearInterval(timer);
  }, 500);

  new MutationObserver(() => tryFullscreen()).observe(document.documentElement, {
    childList: true,
    subtree: true
  });
})();
