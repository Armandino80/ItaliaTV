# ItaliaTV

ItaliaTV is een Android TV / Google TV launcher voor officiële Italiaanse tv- en radiostreams waarvoor geen goede native TV-app beschikbaar is.

De app is bedoeld voor bediening met een afstandsbediening op apparaten zoals de STRONG LEAP-S3+V2. Een tegel opent de officiële broadcasterpagina intern in GeckoView. Tijdens het laden blijft het scherm zwart; zodra de officiële videoplayer wordt gevonden, wordt die schermvullend gemaakt. Als na ongeveer 15 seconden geen geschikte player wordt gevonden, verschijnt de gewone website als fallback.

ItaliaTV haalt geen losse stream-URL's uit websites, restreamt niets en omzeilt geen DRM of authenticatie. De officiële website en player blijven de bron van de video.

## Kanalen

De huidige versie bevat tegels voor:

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
- standaard interne fullscreen-modus via GeckoView
- zwarte laadweergave zodat de website-interface niet eerst in beeld flitst
- automatische detectie van video, playercontainer of video-iframe
- fallback naar de gewone officiële website als geen player wordt gevonden
- Back keert terug naar ItaliaTV
- geen accounts, analytics of advertenties in de app zelf

## Bouwen

De repository bevat een GitHub Actions-workflow die een debug-APK bouwt en als `latest` GitHub Release publiceert.

Lokaal bouwen kan met een geschikte Android SDK/JDK via:

```bash
./gradlew assembleDebug
```

## Installatie

Installeer de gebouwde APK via sideloading op Android TV / Google TV. Een aparte TV-browser is voor de normale kanaaltegels niet meer nodig.
