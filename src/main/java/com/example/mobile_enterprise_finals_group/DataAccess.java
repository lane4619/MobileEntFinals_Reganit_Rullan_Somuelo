package com.example.mobile_enterprise_finals_group;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DataAccess extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "LoanApp.db";
    private static final int DATABASE_VERSION = 1;


    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_EMPLOYEE_ID = "employee_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE_HIRED = "date_hired";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_SALARY = "salary";
    public static final String COLUMN_USER_TYPE = "user_type";


    public static final String TABLE_LOANS = "loans";
    public static final String COLUMN_LOAN_ID = "loan_id";
    public static final String COLUMN_LOAN_TYPE = "loan_type";
    public static final String COLUMN_LOAN_AMOUNT = "loan_amount";
    public static final String COLUMN_MONTHS = "months";
    public static final String COLUMN_INTEREST = "interest";
    public static final String COLUMN_SERVICE_CHARGE = "service_charge";
    public static final String COLUMN_TOTAL_AMOUNT = "total_amount";
    public static final String COLUMN_MONTHLY_PAYMENT = "monthly_payment";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_DATE_APPLIED = "date_applied";

    public DataAccess(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUserTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMPLOYEE_ID + " TEXT UNIQUE, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_DATE_HIRED + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_SALARY + " REAL, " +
                COLUMN_USER_TYPE + " TEXT)";
        db.execSQL(createUserTable);


        String createLoanTable = "CREATE TABLE " + TABLE_LOANS + " (" +
                COLUMN_LOAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMPLOYEE_ID + " TEXT, " +
                COLUMN_LOAN_TYPE + " TEXT, " +
                COLUMN_LOAN_AMOUNT + " REAL, " +
                COLUMN_MONTHS + " INTEGER, " +
                COLUMN_INTEREST + " REAL, " +
                COLUMN_SERVICE_CHARGE + " REAL, " +
                COLUMN_TOTAL_AMOUNT + " REAL, " +
                COLUMN_MONTHLY_PAYMENT + " REAL, " +
                COLUMN_STATUS + " TEXT, " +
                COLUMN_DATE_APPLIED + " TEXT)";
        db.execSQL(createLoanTable);


        ContentValues admin = new ContentValues();
        admin.put(COLUMN_EMPLOYEE_ID, "A00001");
        admin.put(COLUMN_NAME, "Admin User");
        admin.put(COLUMN_DATE_HIRED, "2020-01-01");
        admin.put(COLUMN_PASSWORD, "admin123");
        admin.put(COLUMN_SALARY, 0);
        admin.put(COLUMN_USER_TYPE, "admin");
        db.insert(TABLE_USERS, null, admin);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOANS);
        onCreate(db);
    }


    public boolean checkUser(String employeeId, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COLUMN_EMPLOYEE_ID + " = ? AND " +
                        COLUMN_PASSWORD + " = ?",
                new String[]{employeeId, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }


    public String getUserType(String employeeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_USER_TYPE + " FROM " + TABLE_USERS +
                        " WHERE " + COLUMN_EMPLOYEE_ID + " = ?",
                new String[]{employeeId});
        if (cursor.moveToFirst()) {
            String type = cursor.getString(0);
            cursor.close();
            return type;
        }
        cursor.close();
        return "user";
    }


    public boolean addUser(String employeeId, String name, String dateHired, String password, double salary) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMPLOYEE_ID, employeeId);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_DATE_HIRED, dateHired);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_SALARY, salary);
        values.put(COLUMN_USER_TYPE, "user");

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean isEmployeeIdExists(String employeeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_USERS + " WHERE " + COLUMN_EMPLOYEE_ID + " = ?",
                new String[]{employeeId});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean addLoan(String employeeId, String loanType, double loanAmount, int months,
                           double interest, double serviceCharge, double totalAmount, double monthlyPayment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMPLOYEE_ID, employeeId);
        values.put(COLUMN_LOAN_TYPE, loanType);
        values.put(COLUMN_LOAN_AMOUNT, loanAmount);
        values.put(COLUMN_MONTHS, months);
        values.put(COLUMN_INTEREST, interest);
        values.put(COLUMN_SERVICE_CHARGE, serviceCharge);
        values.put(COLUMN_TOTAL_AMOUNT, totalAmount);
        values.put(COLUMN_MONTHLY_PAYMENT, monthlyPayment);
        values.put(COLUMN_STATUS, "Pending");
        values.put(COLUMN_DATE_APPLIED, "2024-01-01"); // Simple date for demo

        long result = db.insert(TABLE_LOANS, null, values);
        return result != -1;
    }


    public Cursor getUserLoans(String employeeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_LOANS +
                        " WHERE " + COLUMN_EMPLOYEE_ID + " = ? ORDER BY " + COLUMN_LOAN_ID + " DESC",
                new String[]{employeeId});
    }


    public Cursor getAllLoans() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_LOANS + " ORDER BY " + COLUMN_LOAN_ID + " DESC", null);
    }


    public boolean updateLoanStatus(int loanId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);

        int result = db.update(TABLE_LOANS, values, COLUMN_LOAN_ID + " = ?",
                new String[]{String.valueOf(loanId)});
        return result > 0;
    }
}
