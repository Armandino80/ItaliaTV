package nl.armandino.italiatv;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

public class FullscreenBrowserActivity extends Activity {

    public static final String EXTRA_URL = "url";
    private static final String EXTENSION_LOCATION = "resource://android/assets/fullscreen/";
    private static final String EXTENSION_ID = "fullscreen@italiatv";

    private static GeckoRuntime runtime;

    private GeckoSession session;
    private GeckoView geckoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enterImmersiveMode();

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        geckoView = new GeckoView(this);
        root.addView(geckoView, new FrameLayout.LayoutParams(
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
        if (session != null && session.isOpen()) {
            session.close();
        }
        super.onDestroy();
    }
}
