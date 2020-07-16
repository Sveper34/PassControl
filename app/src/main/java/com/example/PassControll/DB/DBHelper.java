package com.example.PassControll.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.PassControll.MainActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {
    private static final String Database_name = "amp";
    private static final Integer Database_version = 5;

    public DBHelper(Context context) {
        super(context, Database_name, null, Database_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //процедура создающая базу данных
        //db.execSQL("create table amp_car(ampc_id integer primary key)");
        //пропуск
        db.execSQL("create table amp_pass(ampp_id integer primary key," + // число, ID пропуска в БД;
                "ampp_SYNC_DATETIME date," +//дата и время последней синхронизации;
                "ampp_INDEX text," +//текст, № пропуска;
                "ampp_CREATE_USER_FIO text," +//текст, фамилия и инициалы исполнителя (того, кто создал пропуск);
                "ampp_AGREED_DATE text," +// дата окончательного согласования пропуска;
                "ampp_PLACE_FROM text," +//текст, откуда вывозятся/выносятся ТМЦ по пропуску;
                "ampp_PLACE_TO text," + //текст, куда вывозятся/выносятся ТМЦ по пропуску;
                "ampp_ATTENDANT_FIO text," + //текст, фамилия и инициалы cопровождающего;
                "ampp_TRANSPORT_INFO text," +//текст, марка, модель и гос. номер транспорта, на котором будут выводзить ТМЦ
                "ampp_REASON_TEXT text," + //текст, основание для вывоза ТМЦ;
                "ampp_REASON_FILE_PATH integer," +// число, путь к файлу, который может быть приложен к основанию (сами файлы тоже нужно загружать, чтобы была возможность их открывать на устройстве);
                "ampp_RETURN_DATE date," + //дата, с возвратом до, может быть пустой;
                "ampp_NEED_OTK_CHECK integer," +// 1/0 - да/нет, нужна или нет приемка ОТК;
                "ampp_SIGN_TMC_RESPONSIBLE_FIO text," + //- текст, фамилия и инициалы материально-ответственного лица, подписавшего пропуск;
                "ampp_SIGN_TMC_RESPONSIBLE_DATE date," + //- дата, дата подписи материально-ответственного лица;
                "ampp_SIGN_DEPT_HEAD_FIO text," + //- текст, фамилия и инициалы руководителя структурного подразделения, подписавшего пропуск;
                "ampp_SIGN_DEPT_HEAD_DATE date," + // -  дата подписи руководителя структурного подразделения;
                "ampp_SIGN_BOOKER_FIO text," + //- текст, фамилия и инициалы бухгалтера, подписавшего пропуск;
                "ampp_SIGN_BOOKER_DATE date," + //- дата подписи бухгалтера;
                "ampp_SIGN_PASS_OFFICE_WRKR_FIO text," + //- текст, фамилия и инициалы работника бюро пропусков, подписавшего пропуск;
                "ampp_SIGN_PASS_OFFICE_WRKR_DATE date," + //- дата, дата подписи работника бюро пропусков;
                "ampp_PASSED_IN_CONTROL_POINT_ID integer," + //- число, ID вахты, через которую ввезли/внесли ТМЦ;
                "ampp_PASSED_IN_DATE date," + //дата пропуска ТМЦ через вахту на ввоз/внос;
                "ampp_PASSED_OUT_CONTROL_POINT_ID integer," + //число, ID вахты, через которую вывезли/вынесли ТМЦ;
                "ampp_PASSED_OUT_DATE date)"); //дата пропуска ТМЦ через вахту на вывоз/вынос;

        //состав пропуска
        db.execSQL("create table amp_pass_content(amppc_id integer primary key," + //число, ID позиции пропуска в БД;
                "amppc_SYNC_DATETIME date, " + //дата и время последней синхронизации;
                "amppc_PASS_ID integer, " + // число, ID пропуска, к которому позиция относится;
                "amppc_INDEX text," + // текст, № п/п;
                "amppc_TITLE text ," + //текст, наименование позиции;
                "amppc_INVENTORY_NUMBER text," + //текст, инв. №;
                "amppc_AMOUNT float," + //нецелое число, количество;
                "amppc_UNIT text)"); //текст, краткое наименование единицы измерения.

        //вахты
        db.execSQL("create table amp_watch(ampw_ID integer primary key," + //число, код записи в справочнике вахт Общества;
                "ampw_SHORT_TITLE text," +// - текст, краткое наименование вахты;
                "ampw_FULL_TITLE text)");// - текст, полное наименование вахты.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //процедура для синхронизации :D
        db.execSQL("drop table if exists amp_car");
        db.execSQL("drop table if exists amp_pass");
        db.execSQL("drop table if exists amp_pass_content");
        db.execSQL("drop table if exists amp_watch");
    }

    public void UpdateAmpPassImport(SQLiteDatabase db, Boolean NeedNull) {
        Date currentDate = new Date();// Форматирование времени как "день.месяц.год"
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        //Обновление параметров в бд
        ContentValues contentValues = new ContentValues();
        if (NeedNull) contentValues.put("ampp_PASSED_IN_CONTROL_POINT_ID", -1);
        else
            contentValues.put("ampp_PASSED_IN_CONTROL_POINT_ID", 1);// добавить обращение к настройкам для получения фахты общества к которой привязан сканер
        contentValues.put("ampp_PASSED_IN_DATE", dateFormat.format(currentDate));
        db.update("amp_pass", contentValues, "ampp_id=" + MainActivity.Idpass, null);
    }

    public void UpdateAmpPassExport(SQLiteDatabase db, Boolean NeedNull) {
        Date currentDate = new Date();// Форматирование времени как "день.месяц.год"
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        //Обновление параметров в бд
        ContentValues contentValues = new ContentValues();
        if (NeedNull) contentValues.put("ampp_PASSED_OUT_CONTROL_POINT_ID", -1);
        else
            contentValues.put("ampp_PASSED_OUT_CONTROL_POINT_ID", 1);// добавить обращение к настройкам для получения фахты общества к которой привязан сканер
        contentValues.put("ampp_PASSED_OUT_DATE", dateFormat.format(currentDate));
        db.update("amp_pass", contentValues, "ampp_id=" + MainActivity.Idpass, null);
    }

    public void DropAllTable(SQLiteDatabase db) {
        db.execSQL("drop table if exists amp_car");
        db.execSQL("drop table if exists amp_pass");
        db.execSQL("drop table if exists amp_pass_content");
        db.execSQL("drop table if exists amp_watch");
    }
}
