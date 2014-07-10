package com.kodfarki.zoomablelayout;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewFragment extends Fragment {
	private FragmentActivity parentActivity;
	private String id;
	private String title;
	private String url;

    public WebViewFragment(String url) {
        this.url = url;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View webViewFragment = inflater.inflate(R.layout.fragment_webview, container, false);
        WebView webView = (WebView) webViewFragment.findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.loadUrl(url);

        final ProgressDialog progressDialog = ProgressDialog.show(parentActivity, title, "Yükleniyor...", true);
        progressDialog.show();

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressDialog.dismiss();
            }
        });

        return webViewFragment;
	}

	@Override
	public void onStart() {
		super.onStart();

	}
}
