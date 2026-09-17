# ItaliaTV agent notes

## Product goal
Keep ItaliaTV a reliable Android TV launcher for official Italian broadcaster web streams that do not have a useful native Google TV app.

## Hard constraints
- Target primary device: STRONG LEAP-S3+V2, Google TV / Android 14.
- Remote / D-pad navigation must remain first-class.
- Do not restream broadcaster video.
- Do not scrape stream URLs, bypass DRM, or circumvent broadcaster authentication.
- Default mode opens official broadcaster pages through Android `ACTION_VIEW`.
- A channel may use the in-app GeckoView fullscreen mode only when this displays the broadcaster's own official webpage/player and merely changes page layout locally; it must not extract, proxy or replace the underlying stream.
- Keep native apps such as RaiPlay and Mediaset Infinity outside this launcher unless there is a clear UX reason to add deep links later.
- Keep channel data in `app/src/main/assets/channels.json` so most changes require no Java edits. Optional `mode: "fullscreen"` selects the in-app fullscreen browser; omitted mode means external browser.
- Avoid unnecessary dependencies. GeckoView is intentionally included only to support broadcaster pages that are unusable as TV video in an external browser.

## Acceptance checks for changes
- App appears in Google TV apps through `LEANBACK_LAUNCHER`.
- First tile receives focus on launch.
- D-pad can reach every tile.
- Normal tiles launch an external browser.
- Fullscreen tiles open the official broadcaster page inside the in-app GeckoView and make the web video/player fill the TV screen without extracting the stream URL.
- Back returns to ItaliaTV.
- App still builds with JDK 17, Android SDK 35 and Gradle 8.10.2.
