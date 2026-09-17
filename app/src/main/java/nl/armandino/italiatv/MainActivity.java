package nl.armandino.italiatv;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private final List<Channel> channels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundColor(Color.rgb(16, 17, 20));
        loadChannels();
        setContentView(buildUi());
    }

    private View buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(48), dp(34), dp(48), dp(32));
        root.setBackgroundColor(Color.rgb(16, 17, 20));

        TextView title = new TextView(this);
        title.setText("ItaliaTV");
        title.setTextColor(Color.WHITE);
        title.setTextSize(34);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        root.addView(title, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView subtitle = new TextView(this);
        subtitle.setText("Livekanalen • kies een zender");
        subtitle.setTextColor(Color.rgb(190, 194, 201));
        subtitle.setTextSize(16);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        subtitleParams.topMargin = dp(4);
        subtitleParams.bottomMargin = dp(22);
        root.addView(subtitle, subtitleParams);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setVerticalScrollBarEnabled(false);

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(3);
        grid.setAlignmentMode(GridLayout.ALIGN_MARGINS);
        grid.setUseDefaultMargins(false);
        grid.setPadding(0, 0, 0, dp(20));

        Button firstButton = null;
        for (int i = 0; i < channels.size(); i++) {
            Channel channel = channels.get(i);
            Button tile = createTile(channel, i);
            if (firstButton == null) {
                firstButton = tile;
            }

            GridLayout.LayoutParams p = new GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f));
            p.width = 0;
            p.height = dp(122);
            p.setMargins(dp(10), dp(10), dp(10), dp(10));
            grid.addView(tile, p);
        }

        scroll.addView(grid, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT));

        root.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f));

        if (firstButton != null) {
            firstButton.post(firstButton::requestFocus);
        }

        return root;
    }

    private Button createTile(Channel channel, int index) {
        Button button = new Button(this);
        button.setAllCaps(false);
        button.setText(channel.name + "\n" + channel.subtitle);
        button.setTextSize(19);
        button.setTextColor(Color.WHITE);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setPadding(dp(16), dp(12), dp(16), dp(12));
        button.setFocusable(true);
        button.setFocusableInTouchMode(true);
        button.setMinHeight(0);
        button.setMinWidth(0);
        button.setStateListAnimator(null);

        int normal = tileColor(index);
        int focused = lighten(normal, 1.32f);
        int pressed = lighten(normal, 1.16f);
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_pressed},
                new int[]{android.R.attr.state_focused},
                new int[]{}
        };
        int[] colors = new int[]{pressed, focused, normal};
        button.setBackgroundTintList(new ColorStateList(states, colors));

        button.setOnFocusChangeListener((v, hasFocus) -> {
            float scale = hasFocus ? 1.06f : 1.0f;
            v.animate().scaleX(scale).scaleY(scale).setDuration(110).start();
        });

        button.setOnClickListener(v -> openChannel(channel));
        return button;
    }

    private void openChannel(Channel channel) {
        if ("fullscreen".equalsIgnoreCase(channel.mode)) {
            Intent intent = new Intent(this, FullscreenBrowserActivity.class);
            intent.putExtra(FullscreenBrowserActivity.EXTRA_URL, channel.url);
            startActivity(intent);
            return;
        }
        openInBrowser(channel.url);
    }

    private void openInBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this,
                    "Geen browser gevonden.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void loadChannels() {
        channels.clear();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getAssets().open("channels.json"), StandardCharsets.UTF_8))) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            JSONArray array = new JSONArray(json.toString());
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                channels.add(new Channel(
                        item.getString("name"),
                        item.optString("subtitle", "Live"),
                        item.getString("url"),
                        item.optString("mode", "fullscreen")
                ));
            }
        } catch (Exception e) {
            Toast.makeText(this,
                    "Kan kanalenlijst niet laden.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private int tileColor(int index) {
        int[] palette = new int[]{
                Color.rgb(40, 91, 68),
                Color.rgb(55, 72, 118),
                Color.rgb(110, 46, 52),
                Color.rgb(86, 63, 126),
                Color.rgb(50, 93, 103),
                Color.rgb(125, 64, 44),
                Color.rgb(73, 83, 93),
                Color.rgb(94, 73, 42),
                Color.rgb(53, 102, 62)
        };
        return palette[index % palette.length];
    }

    private int lighten(int color, float factor) {
        int r = Math.min(255, Math.round(Color.red(color) * factor));
        int g = Math.min(255, Math.round(Color.green(color) * factor));
        int b = Math.min(255, Math.round(Color.blue(color) * factor));
        return Color.rgb(r, g, b);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private static final class Channel {
        final String name;
        final String subtitle;
        final String url;
        final String mode;

        Channel(String name, String subtitle, String url, String mode) {
            this.name = name;
            this.subtitle = subtitle;
            this.url = url;
            this.mode = mode;
        }
    }
}
