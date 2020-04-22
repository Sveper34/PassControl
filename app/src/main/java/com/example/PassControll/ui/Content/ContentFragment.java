package com.example.PassControll.ui.Content;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.w3c.dom.Text;

public class ContentFragment extends Fragment {

    private ContentViewModel contentViewModel;
    private Cursor cur;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        contentViewModel =
                ViewModelProviders.of(this).get(ContentViewModel.class);
        View root = inflater.inflate(R.layout.fragment_content, container, false);
        DBHelper DBHelper = new DBHelper(getActivity());

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //MainActivity.Idpass id последнего отсканированного пропуска
        Button btImport = getView().findViewById(R.id.btImport);
        Button btExport = getView().findViewById(R.id.btExport);

        Cursor getInfoButton = MainActivity.Database.rawQuery("Select * from amp_pass where ampp_ID=" + MainActivity.Idpass + ";", null);
        while (getInfoButton.moveToNext()) {
            btImport.setText("Привет");
            if (getInfoButton.getInt(getInfoButton.getColumnIndex("ampp_PASSED_IN_CONTROL_POINT_ID")) <= 0)
                btImport.setText("Ввоз ТМЦ");
            else btImport.setText("Отменить ввоз ТМЦ");
            if (getInfoButton.getInt(getInfoButton.getColumnIndex("ampp_PASSED_OUT_CONTROL_POINT_ID")) <= 0)
                btExport.setText("Вывоз ТМЦ");
            else btExport.setText("Отменить вывоз ТМЦ");
        }
        TableLayout tl = (TableLayout) getView().findViewById(R.id.TableContent);
        Cursor cursor = MainActivity.Database.rawQuery("Select * from amp_pass_content where amppc_PASS_ID=" + MainActivity.Idpass + ";", null);
        while (cursor.moveToNext()) {
            //Создание строк
            TableRow tr = new TableRow(getActivity());
            TextView tvCell = new TextView(getActivity());
            tvCell.setTextSize(18);
            tvCell.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvCell.setText(cursor.getString(cursor.getColumnIndex("amppc_INDEX")));
            tr.addView(tvCell);

            tvCell = new TextView(getActivity());
            tvCell.setTextSize(18);
            tvCell.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            tvCell.setText(cursor.getString(cursor.getColumnIndex("amppc_TITLE")));
            tr.addView(tvCell);

            tvCell = new TextView(getActivity());
            tvCell.setTextSize(18);
            tvCell.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            tvCell.setText(cursor.getString(cursor.getColumnIndex("amppc_INVENTORY_NUMBER")));
            tr.addView(tvCell);

            tvCell = new TextView(getActivity());
            tvCell.setTextSize(18);
            tvCell.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvCell.setText(cursor.getString(cursor.getColumnIndex("amppc_AMOUNT")));
            tr.addView(tvCell);

            tvCell = new TextView(getActivity());
            tvCell.setTextSize(18);
            tvCell.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            tvCell.setText(cursor.getString(cursor.getColumnIndex("amppc_UNIT")));
            tr.addView(tvCell);
            //добавление строки
            tl.addView(tr);
        }
        // or  (ImageView) view.findViewById(R.id.foo);
    }
}
