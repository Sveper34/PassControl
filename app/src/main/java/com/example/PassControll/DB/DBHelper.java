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
        db.execSQL("insert into amp_pass(ampp_id,ampp_INDEX,ampp_AGREED_DATE,ampp_PLACE_FROM,ampp_PLACE_TO,ampp_ATTENDANT_FIO,ampp_TRANSPORT_INFO)" +
                " values(1,'2078690491543','13.04.20','Цех №6','ЭМП №1','Рябцев С.О.','Малиновая девятка');");
        db.execSQL("insert into amp_pass(ampp_id,ampp_INDEX,ampp_AGREED_DATE,ampp_PLACE_FROM,ampp_PLACE_TO,ampp_ATTENDANT_FIO,ampp_TRANSPORT_INFO) " +
                "values(2,'201812100052','12.04.20','Цех №1','ЭМП №6','Соснин С.А.','volkswagen tiguan м279кр29рус');");
        //состав пропуска
        db.execSQL("create table amp_pass_content(amppc_id integer primary key," + //число, ID позиции пропуска в БД;
                "amppc_SYNC_DATETIME date, " + //дата и время последней синхронизации;
                "amppc_PASS_ID integer, " + // число, ID пропуска, к которому позиция относится;
                "amppc_INDEX text," + // текст, № п/п;
                "amppc_TITLE text ," + //текст, наименование позиции;
                "amppc_INVENTORY_NUMBER text," + //текст, инв. №;
                "amppc_AMOUNT float," + //нецелое число, количество;
                "amppc_UNIT text)"); //текст, краткое наименование единицы измерения.
        db.execSQL("insert into amp_pass_content(amppc_id,amppc_SYNC_DATETIME,amppc_PASS_ID,amppc_INDEX,amppc_TITLE,amppc_INVENTORY_NUMBER,amppc_AMOUNT,amppc_UNIT) " +
                "values(1,'14/04/2020',1,'1','Ложка','00001312',10,'шт')");
        db.execSQL("insert into amp_pass_content(amppc_id,amppc_SYNC_DATETIME,amppc_PASS_ID,amppc_INDEX,amppc_TITLE,amppc_INVENTORY_NUMBER,amppc_AMOUNT,amppc_UNIT) " +
                "values(2,'14/04/2020',1,'2','Цемент','00001313',10,'т')");
        db.execSQL("insert into amp_pass_content(amppc_id,amppc_SYNC_DATETIME,amppc_PASS_ID,amppc_INDEX,amppc_TITLE,amppc_INVENTORY_NUMBER,amppc_AMOUNT,amppc_UNIT) " +
                "values(3,'14/04/2020',1,'3','Щит пиротехника','00011312',100,'шт')");
        db.execSQL("insert into amp_pass_content(amppc_id,amppc_SYNC_DATETIME,amppc_PASS_ID,amppc_INDEX,amppc_TITLE,amppc_INVENTORY_NUMBER,amppc_AMOUNT,amppc_UNIT) " +
                "values(4,'14/04/2020',1,'4','Стакан','00002312',10,'шт')");
        db.execSQL("insert into amp_pass_content(amppc_id,amppc_SYNC_DATETIME,amppc_PASS_ID,amppc_INDEX,amppc_TITLE,amppc_INVENTORY_NUMBER,amppc_AMOUNT,amppc_UNIT) " +
                "values(5,'14/04/2020',1,'5','Тумбочка','00121312',1,'шт')");
        db.execSQL("insert into amp_pass_content(amppc_id,amppc_SYNC_DATETIME,amppc_PASS_ID,amppc_INDEX,amppc_TITLE,amppc_INVENTORY_NUMBER,amppc_AMOUNT,amppc_UNIT) " +
                "values(6,'14/04/2020',1,'6','Стул','00111312',10,'шт')");
        db.execSQL("insert into amp_pass_content(amppc_id,amppc_SYNC_DATETIME,amppc_PASS_ID,amppc_INDEX,amppc_TITLE,amppc_INVENTORY_NUMBER,amppc_AMOUNT,amppc_UNIT) " +
                "values(7,'14/04/2020',2,'1','Бантик','00001312',10,'шт')");
        db.execSQL("insert into amp_pass_content(amppc_id,amppc_SYNC_DATETIME,amppc_PASS_ID,amppc_INDEX,amppc_TITLE,amppc_INVENTORY_NUMBER,amppc_AMOUNT,amppc_UNIT) " +
                "values(8,'14/04/2020',2,'2','Песок','00001313',10,'т')");
        db.execSQL("insert into amp_pass_content(amppc_id,amppc_SYNC_DATETIME,amppc_PASS_ID,amppc_INDEX,amppc_TITLE,amppc_INVENTORY_NUMBER,amppc_AMOUNT,amppc_UNIT) " +
                "values(9,'14/04/2020',2,'3','Танк Т-34','00011312',1,'шт')");
        db.execSQL("insert into amp_pass_content(amppc_id,amppc_SYNC_DATETIME,amppc_PASS_ID,amppc_INDEX,amppc_TITLE,amppc_INVENTORY_NUMBER,amppc_AMOUNT,amppc_UNIT) " +
                "values(10,'14/04/2020',2,'4','Бронебойный снаряд','00002312',10,'шт')");
        db.execSQL("insert into amp_pass_content(amppc_id,amppc_SYNC_DATETIME,amppc_PASS_ID,amppc_INDEX,amppc_TITLE,amppc_INVENTORY_NUMBER,amppc_AMOUNT,amppc_UNIT) " +
                "values(11,'14/04/2020',2,'5','Фугасный снаряд','00121312',5,'шт')");
        db.execSQL("insert into amp_pass_content(amppc_id,amppc_SYNC_DATETIME,amppc_PASS_ID,amppc_INDEX,amppc_TITLE,amppc_INVENTORY_NUMBER,amppc_AMOUNT,amppc_UNIT) " +
                "values(12,'14/04/2020',2,'6','Зажигательный снаряд','00111312',10,'шт')");
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
}
