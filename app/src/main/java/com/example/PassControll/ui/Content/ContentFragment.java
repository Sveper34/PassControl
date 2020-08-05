package com.example.PassControll.ui.Content;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.PassControll.DB.DBHelper;
import com.example.PassControll.MainActivity;
import com.example.PassControll.R;

import org.w3c.dom.Text;

public class ContentFragment extends Fragment {

    private ContentViewModel contentViewModel;
    private Cursor cur;
    SharedPreferences preferences;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        contentViewModel =
                ViewModelProviders.of(this).get(ContentViewModel.class);
        View root = inflater.inflate(R.layout.fragment_content, container, false);
        preferences=this.getActivity().getSharedPreferences("ANDROID_SYNC_WATCH", Context.MODE_PRIVATE);

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //MainActivity.Idpass id последнего отсканированного пропуска
        Button btImport = getView().findViewById(R.id.btImport);
        Button btExport = getView().findViewById(R.id.btExport);

        Cursor getInfoButton = MainActivity.Database.rawQuery("Select * from amp_pass where ampp_ID=" + MainActivity.Idpass + ";", null);
        while (getInfoButton.moveToNext()) {
            int type = getInfoButton.getInt(getInfoButton.getColumnIndex("ampp_type_pass"));
            if (getInfoButton.getInt(getInfoButton.getColumnIndex("ampp_type_pass")) == 1) {
                if (!getInfoButton.getString(getInfoButton.getColumnIndex("ampp_TRANSPORT_INFO")).equals("null"))
                    btImport.setText("Ввоз ТМЦ");
                else
                    btImport.setText("Внос ТМЦ");
                btImport.setVisibility(View.VISIBLE);
                btExport.setVisibility(View.INVISIBLE);
            }
            if (getInfoButton.getInt(getInfoButton.getColumnIndex("ampp_type_pass")) == 0) {//внос
                if (!getInfoButton.getString(getInfoButton.getColumnIndex("ampp_TRANSPORT_INFO")).equals("null"))
                    btExport.setText("Вывоз ТМЦ");
                else btExport.setText("Вынос ТМЦ");
                btExport.setVisibility(View.VISIBLE);
                btImport.setVisibility(View.INVISIBLE);
            }
            if (getInfoButton.getInt(getInfoButton.getColumnIndex("ampp_type_pass")) == 2) {//вынос внос
                if (getInfoButton.getInt(getInfoButton.getColumnIndex("ampp_PASSED_IN_CONTROL_POINT_ID")) <= 0) {
                    if (!getInfoButton.getString(getInfoButton.getColumnIndex("ampp_TRANSPORT_INFO")).equals("null"))
                        btImport.setText("Ввоз ТМЦ");
                    else
                        btImport.setText("Внос ТМЦ");
                    btImport.setVisibility(View.VISIBLE);
                    btExport.setVisibility(View.INVISIBLE);
                } else {
                    if (getInfoButton.getInt(getInfoButton.getColumnIndex("ampp_PASSED_OUT_CONTROL_POINT_ID")) <= 0) {
                        if (!getInfoButton.getString(getInfoButton.getColumnIndex("ampp_TRANSPORT_INFO")).equals("null"))
                            btExport.setText("Вывоз ТМЦ");
                        else btExport.setText("Вынос ТМЦ");
                        btImport.setVisibility(View.INVISIBLE);
                        btExport.setVisibility(View.VISIBLE);
                    }
                }

            }
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
            tvCell.setText(cursor.getString(cursor.getColumnIndex("amppc_TITLE")) + "(" + cursor.getString(cursor.getColumnIndex("amppc_AMOUNT")) + " " + cursor.getString(cursor.getColumnIndex("amppc_UNIT")) + ").");
            tr.addView(tvCell);

            tvCell = new TextView(getActivity());
            tvCell.setTextSize(18);
            tvCell.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tvCell.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            }
            tvCell.setText(cursor.getString(cursor.getColumnIndex("amppc_INVENTORY_NUMBER")));
            tr.addView(tvCell);

            tl.addView(tr);
        }
    }
}
