package com.example.android.rsrrevalidatieservicecopy.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.rsrrevalidatieservicecopy.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View mWindow;

    public CustomInfoWindowAdapter(Context context) {
        mWindow = LayoutInflater.from(context).inflate(R.layout.info_window, null);
        mWindow.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                .WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
    }

    private void renderWindowText(Marker marker, View view) {
        String title = marker.getTitle();
        TextView tvTitle = view.findViewById(R.id.text_location);

        if (!title.equals("")) {
            tvTitle.setText(title);
        }

        String snippet = marker.getSnippet();
        TextView tvSnippet = view.findViewById(R.id.address_info);

        if (!snippet.equals("")) {
            tvSnippet.setText(snippet);
        }

        String snippet2 = marker.getSnippet();
        TextView tvSnippet2 = view.findViewById(R.id.remember_location);

        if (!snippet2.equals("")) {
            tvSnippet.setText(snippet);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }
}
