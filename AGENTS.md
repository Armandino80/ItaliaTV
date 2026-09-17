# ItaliaTV agent notes

## Product goal
Keep ItaliaTV a reliable Android TV launcher for official Italian broadcaster web streams that do not have a useful native Google TV app.

## Hard constraints
- Target primary device: STRONG LEAP-S3+V2, Google TV / Android 14.
- Remote / D-pad navigation must remain first-class.
- Do not restream broadcaster video.
- Do not scrape stream URLs, bypass DRM, or circumvent broadcaster authentication.
- Default mode opens the broadcaster's own official page inside the in-app GeckoView fullscreen flow.
- The fullscreen flow may change page layout locally, hide unrelated page chrome and enlarge the broadcaster's own video/player/iframe, but it must not extract, proxy or replace the underlying stream.
- Keep native apps such as RaiPlay and Mediaset Infinity outside this launcher unless there is a clear UX reason to add deep links later.
- Keep channel data in `app/src/main/assets/channels.json` so most changes require no Java edits. Omitted `mode` defaults to `fullscreen`; `mode: "external"` remains available only as an exceptional fallback.
- Keep a black loading mask in front of broadcaster page chrome while looking for the player. If no player is found within about 15 seconds, reveal the ordinary official website as fallback instead of leaving the user on a blank screen.
- Avoid unnecessary dependencies. GeckoView is intentionally bundled so normal channels no longer depend on a separately installed TV browser.

## Acceptance checks for changes
- App appears in Google TV apps through `LEANBACK_LAUNCHER`.
- First tile receives focus on launch.
- D-pad can reach every tile.
- Current channel tiles open internally by default.
- The official broadcaster page stays hidden behind a black loading screen while the player is located.
- When a suitable video/player/iframe is found, it fills the TV screen without extracting the stream URL.
- If no suitable player is found promptly, the ordinary official website is shown as fallback.
- Back returns to ItaliaTV.
- App still builds with JDK 17, Android SDK 35 and Gradle 8.10.2.
