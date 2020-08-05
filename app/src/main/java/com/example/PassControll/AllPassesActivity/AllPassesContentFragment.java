package com.example.PassControll.AllPassesActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.PassControll.DB.DBHelper;
import com.example.PassControll.MainActivity;
import com.example.PassControll.R;

public class AllPassesContentFragment extends Fragment {

    private AllPassesContentViewModel allPassesContentViewModel;
    private Cursor cur;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        allPassesContentViewModel =
                ViewModelProviders.of(this).get(AllPassesContentViewModel.class);
        View root = inflater.inflate(R.layout.allpassesfragment_content, container, false);
        DBHelper DBHelper = new DBHelper(getActivity());

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        TableLayout tl = (TableLayout) getView().findViewById(R.id.TableContent);
        Cursor cursor = MainActivity.Database.rawQuery("Select * from amp_pass ;", null);
        while (cursor.moveToNext()) {
            //Создание строк
            TableRow tr = new TableRow(getActivity());
            TextView tvCell = new TextView(getActivity());
            tvCell.setTextSize(18);
            tvCell.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvCell.setText(cursor.getString(cursor.getColumnIndex("ampp_INDEX")));
            tr.addView(tvCell);

            tvCell = new TextView(getActivity());
            tvCell.setTextSize(18);
            tvCell.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            tvCell.setText(cursor.getString(cursor.getColumnIndex("ampp_AGREED_DATE")));
            tr.addView(tvCell);

            tvCell = new TextView(getActivity());
            tvCell.setTextSize(18);
            tvCell.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            tvCell.setText(cursor.getString(cursor.getColumnIndex("ampp_ATTENDANT_FIO")));
            tr.addView(tvCell);

            tvCell = new TextView(getActivity());
            tvCell.setTextSize(18);
            tvCell.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvCell.setText(cursor.getString(cursor.getColumnIndex("ampp_PLACE_FROM")));
            tr.addView(tvCell);

            tvCell = new TextView(getActivity());
            tvCell.setTextSize(18);
            tvCell.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            tvCell.setText(cursor.getString(cursor.getColumnIndex("ampp_PLACE_TO")));
            tr.addView(tvCell);
            //добавление строки
            tl.addView(tr);
        }
        cursor.close();
        // or  (ImageView) view.findViewById(R.id.foo);
    }
}
