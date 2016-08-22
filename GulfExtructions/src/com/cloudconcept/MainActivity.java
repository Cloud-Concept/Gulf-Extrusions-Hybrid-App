package com.cloudconcept;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.ui.SalesforceActivity;

public class MainActivity extends SalesforceActivity {

    private WebView webview;
    private static final String TAG = "Main";
    private ProgressDialog progressBar;
    Button btnh;

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    public void onResume(RestClient client) {

        webview = (WebView) findViewById(R.id.webView);
        WebSettings settings = webview.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);

        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        if (progressBar == null) {
            progressBar = ProgressDialog.show(MainActivity.this, "Loading Data", "Loading...");
        }

        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "Processing webview url click...");
                view.loadUrl(url);
                if (progressBar.isShowing()) {
                    progressBar.cancel();
                }
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "Finished loading URL: " + url);
                if (progressBar.isShowing() || progressBar.isIndeterminate()) {
                    progressBar.cancel();
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e(TAG, "Error: " + description);
                Toast.makeText(MainActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
                if (progressBar.isShowing()) {
                    progressBar.cancel();
                }
                alertDialog.setTitle("Error");
                alertDialog.setMessage(description);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alertDialog.show();
            }
        });

//        https://mysite.secure.force.com/services/oauth2/authorize?
//        response_type=code&client_id=<your_client_id>&
//                redirect_uri=<your_redirect_uri>

//        webview.loadUrl("https://dev-gulfextrusions.cs81.force.com/" + client.getClientInfo().userId + "/one/one.app");
        String url = client.getClientInfo().getInstanceUrl().toString() + "/one/one.app";
        webview.loadUrl(url);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack() == true) {
            webview.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
