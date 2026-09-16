# ItaliaTV

ItaliaTV is een lichte Android TV / Google TV launcher voor Italiaanse tv- en radiostreams waarvoor geen goede native TV-app beschikbaar is.

De app is bedoeld voor bediening met een afstandsbediening op apparaten zoals de STRONG LEAP-S3+V2. Een tegel opent de officiële website in een geïnstalleerde browser (bij voorkeur TV Bro). Er wordt bewust geen WebView gebruikt, zodat DRM, cookies, fullscreen-video en websitewijzigingen door de externe browser worden afgehandeld.

## Kanalen

De eerste versie bevat tegels voor:

- TV8
- Cielo
- RTL 102.5
- Radiofreccia
- Radio Zeta
- Radio Italia
- Play2000 / TV2000
- Sportitalia
- Canale 21 Campania

De lijst staat in `app/src/main/assets/channels.json` en kan later eenvoudig worden uitgebreid.

## Gedrag

- Android TV `LEANBACK_LAUNCHER`
- landscape-interface
- D-pad/focusbediening
- geselecteerde tegel wordt visueel vergroot
- opent URL's via een externe browser
- geen accounts, analytics of advertenties in de app zelf

## Bouwen

De repository bevat een GitHub Actions-workflow die een debug-APK bouwt. Na een succesvolle workflow-run is de APK beschikbaar als artifact.

Lokaal bouwen kan met een geschikte Android SDK/JDK via:

```bash
./gradlew assembleDebug
```

## Installatie

Installeer de gebouwde APK via sideloading op Android TV / Google TV. Installeer daarnaast een TV-browser, bij voorkeur TV Bro, zodat de webtegels prettig met de afstandsbediening werken.
