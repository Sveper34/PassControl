package com.example.PassControll.ui.Content;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.PassControll.R;

public class ContentFragment extends Fragment {

    private ContentViewModel contentViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        contentViewModel =
                ViewModelProviders.of(this).get(ContentViewModel.class);
        View root = inflater.inflate(R.layout.fragment_content, container, false);

        return root;
    }
}
