package com.shirokuma.musicplayer.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shirokuma.musicplayer.R;

public class NavbackFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_navback, container);
        // initial view
        rootView.findViewById(R.id.btn_back).setOnClickListener(onClick);
        return rootView;
    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_back) {
                getActivity().finish();
            }
        }
    };
}
