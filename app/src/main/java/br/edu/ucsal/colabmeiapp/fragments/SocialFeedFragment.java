package br.edu.ucsal.colabmeiapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.edu.ucsal.colabmeiapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SocialFeedFragment extends Fragment {


    public SocialFeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social_feed, container, false);
    }

}
