package com.salimmulani.masjidhisab;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.ValueCallback;
import java.io.OutputStream;

public class MainActivity extends Activity {
    private WebView webView;
    private static final int CREATE_BACKUP = 1001;
    private String pendingBackup = "";
    private ValueCallback<Uri[]> filePathCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        setContentView(webView);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                Intent intent = params.createIntent();
                try {
                    startActivityForResult(intent, 1002);
                } catch (Exception e) {
                    return false;
                }
                filePathCallback = callback;
                return true;
            }
        });
        webView.addJavascriptInterface(new BackupBridge(), "AndroidBackup");
        webView.loadUrl("file:///android_asset/index.html");
    }

    public class BackupBridge {
        @JavascriptInterface
        public void saveBackup(String data) {
            pendingBackup = data;
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            intent.putExtra(Intent.EXTRA_TITLE,
                    "masjid-hisab-backup-" +
                    new java.text.SimpleDateFormat("yyyy-MM-dd",
                    java.util.Locale.US).format(new java.util.Date()) + ".json");
            startActivityForResult(intent, CREATE_BACKUP);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1002) {
            if (filePathCallback != null) {
                Uri[] results = null;
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    results = new Uri[]{data.getData()};
                }
                filePathCallback.onReceiveValue(results);
                filePathCallback = null;
            }
            return;
        }

        if (requestCode == CREATE_BACKUP) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri uri = data.getData();
                try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                    if (out == null) throw new Exception("OutputStream null");

                    byte[] bytes = pendingBackup.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    out.write(bytes);
                    out.flush();

                webView.evaluateJavascript("alert('Backup सफलतापूर्वक सुरक्षित हो गया है।');", null);

                } catch (Exception e) {
                    webView.evaluateJavascript(
                        "alert('Backup सेव नहीं हो पाया: " +
                        e.getClass().getSimpleName() + "');", null);
                }
            } else {
                webView.evaluateJavascript(
                    "alert('Backup रद्द किया गया।');", null);
            }
            pendingBackup = "";
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) webView.destroy();
        super.onDestroy();
    }
}
