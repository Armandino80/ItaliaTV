package nl.armandino.italiatv;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

public class FullscreenBrowserActivity extends Activity {

    public static final String EXTRA_URL = "url";
    private static final String EXTENSION_LOCATION = "resource://android/assets/fullscreen/";
    private static final String EXTENSION_ID = "fullscreen@italiatv";
    private static final long LOADER_TIMEOUT_MS = 5000L;

    private static GeckoRuntime runtime;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private GeckoSession session;
    private GeckoView geckoView;
    private FrameLayout root;
    private View loadingOverlay;

    private final Runnable hideLoaderRunnable = this::hideLoadingOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enterImmersiveMode();

        root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        geckoView = new GeckoView(this);
        root.addView(geckoView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        TextView loader = new TextView(this);
        loader.setText("ItaliaTV • zender laden…");
        loader.setTextColor(Color.WHITE);
        loader.setTextSize(26);
        loader.setGravity(Gravity.CENTER);
        loader.setBackgroundColor(Color.BLACK);
        loadingOverlay = loader;
        root.addView(loader, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        setContentView(root);

        String url = getIntent().getStringExtra(EXTRA_URL);
        if (url == null || url.trim().isEmpty()) {
            finish();
            return;
        }

        session = new GeckoSession();
        session.setContentDelegate(new GeckoSession.ContentDelegate() {});

        if (runtime == null) {
            runtime = GeckoRuntime.create(this);
        }

        runtime.getWebExtensionController()
                .ensureBuiltIn(EXTENSION_LOCATION, EXTENSION_ID)
                .accept(
                        extension -> openPage(url),
                        error -> {
                            Toast.makeText(this,
                                    "Fullscreen-optimalisatie kon niet worden geladen; website wordt normaal geopend.",
                                    Toast.LENGTH_LONG).show();
                            openPage(url);
                        });
    }

    private void openPage(String url) {
        if (!session.isOpen()) {
            session.open(runtime);
            geckoView.setSession(session);
        }
        session.loadUri(url);

        // The loading screen is native, so a broadcaster page can never keep it stuck.
        // After a few seconds the official page becomes visible while the fullscreen
        // helper continues looking for the video player in the background.
        handler.removeCallbacks(hideLoaderRunnable);
        handler.postDelayed(hideLoaderRunnable, LOADER_TIMEOUT_MS);
    }

    private void hideLoadingOverlay() {
        if (loadingOverlay != null && loadingOverlay.getParent() == root) {
            root.removeView(loadingOverlay);
        }
        loadingOverlay = null;
    }

    private void enterImmersiveMode() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enterImmersiveMode();
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(hideLoaderRunnable);
        if (session != null && session.isOpen()) {
            session.close();
        }
        super.onDestroy();
    }
}
