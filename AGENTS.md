# ItaliaTV agent notes

## Product goal
Keep ItaliaTV a very small, reliable Android TV launcher for official Italian broadcaster web streams that do not have a useful native Google TV app.

## Hard constraints
- Target primary device: STRONG LEAP-S3+V2, Google TV / Android 14.
- Remote / D-pad navigation must remain first-class.
- Do not embed or restream broadcaster video.
- Do not scrape stream URLs, bypass DRM, or circumvent broadcaster authentication.
- Open official broadcaster pages through Android ACTION_VIEW.
- Keep native apps such as RaiPlay and Mediaset Infinity outside this launcher unless there is a clear UX reason to add deep links later.
- Keep channel data in `app/src/main/assets/channels.json` so most changes require no Java edits.
- Avoid unnecessary dependencies. The current app intentionally uses the Android framework only.

## Acceptance checks for changes
- App appears in Google TV apps through `LEANBACK_LAUNCHER`.
- First tile receives focus on launch.
- D-pad can reach every tile.
- Selecting every tile launches an external browser.
- Back from the browser can return to ItaliaTV.
- App still builds with JDK 17, Android SDK 35 and Gradle 8.10.2.
