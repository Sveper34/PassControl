package com.example.PassControll.ui.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import com.example.PassControll.MainActivity;
import com.example.PassControll.R;
import com.example.PassControll.ui.home.HomeViewModel;


public class SettingsFragment extends Fragment {
    FragmentManager myFragmentManager;
    private HomeViewModel homeViewModel;
    private Spinner spWatchlist;
    private int ActualPreferenceSync;
    SharedPreferences settings;
    String[] listWatch;
    String[] listWatchid;
    int curpositionWatch;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        settings = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        getName();//получить из настроек значение актуальной вахты
        spWatchlist = root.findViewById(R.id.WatchList);//Приязка spinner
        Cursor cuWatchList = MainActivity.Database.rawQuery("Select * from amp_watch; ", null);
        listWatch = new String[cuWatchList.getCount()];
        listWatchid = new String[cuWatchList.getCount()];
        int i = 0;

        while (cuWatchList.moveToNext()) {
            if(ActualPreferenceSync==cuWatchList.getInt(cuWatchList.getColumnIndex("ampw_ID")))
            curpositionWatch=i;
            listWatch[i] = cuWatchList.getString(cuWatchList.getColumnIndex("ampw_SHORT_TITLE"));
            listWatchid[i] = cuWatchList.getString(cuWatchList.getColumnIndex("ampw_ID"));
            i++;
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, listWatch);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spWatchlist.setAdapter(adapter);
        spWatchlist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                saveName(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spWatchlist.setSelection(curpositionWatch);
        return root;

    }

    public void saveName(int position) {
        // сохраняем выбранную фахту
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putInt("ANDROID_SYNC_WATCH", Integer.valueOf(listWatchid[position]));
        prefEditor.apply();
    }

    public void getName() {
        // получаем сохраненную фахту
        ActualPreferenceSync = settings.getInt("ANDROID_SYNC_WATCH", 1);
    }
}
