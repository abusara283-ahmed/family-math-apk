package om.familymath.math12;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.getcapacitor.BridgeActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/* أسرة الرياضيات – يضيف للتطبيق: حفظ الملفات (SCORM وغيرها) في مجلد التنزيلات، والطباعة أو الحفظ PDF */
public class MainActivity extends BridgeActivity {
    private WebView printView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView wv = getBridge().getWebView();
        wv.getSettings().setTextZoom(100);
        wv.addJavascriptInterface(new AndroidSave(this), "AndroidSave");
    }

    public class AndroidSave {
        private final Context ctx;
        AndroidSave(Context c) { ctx = c; }

        @JavascriptInterface
        public String save(String name, String b64, String mime) {
            try {
                byte[] data = Base64.decode(b64, Base64.DEFAULT);
                if (mime == null || mime.isEmpty()) mime = "application/octet-stream";
                String where;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentValues v = new ContentValues();
                    v.put(MediaStore.Downloads.DISPLAY_NAME, name);
                    v.put(MediaStore.Downloads.MIME_TYPE, mime);
                    v.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/أسرة الرياضيات");
                    Uri uri = ctx.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, v);
                    if (uri == null) throw new Exception("no uri");
                    try (OutputStream os = ctx.getContentResolver().openOutputStream(uri)) { os.write(data); }
                    where = "التنزيلات ← أسرة الرياضيات";
                } else {
                    File dir = new File(ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "");
                    dir.mkdirs();
                    File f = new File(dir, name);
                    try (FileOutputStream os = new FileOutputStream(f)) { os.write(data); }
                    where = f.getAbsolutePath();
                }
                final String msg = "حُفظ الملف في: " + where;
                runOnUiThread(() -> Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show());
                return "ok";
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(ctx, "تعذّر حفظ الملف", Toast.LENGTH_LONG).show());
                return "error";
            }
        }

        @JavascriptInterface
        public void print(String html, String title) {
            runOnUiThread(() -> {
                printView = new WebView(MainActivity.this);
                printView.getSettings().setJavaScriptEnabled(true);
                printView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        view.postDelayed(() -> {
                            PrintManager pm = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                            String job = (title == null || title.isEmpty()) ? "أسرة الرياضيات" : title;
                            PrintDocumentAdapter ad = view.createPrintDocumentAdapter(job);
                            pm.print(job, ad, new PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4).build());
                        }, 600);
                    }
                });
                printView.loadDataWithBaseURL("https://localhost/", html, "text/html", "utf-8", null);
            });
        }
    }
}
