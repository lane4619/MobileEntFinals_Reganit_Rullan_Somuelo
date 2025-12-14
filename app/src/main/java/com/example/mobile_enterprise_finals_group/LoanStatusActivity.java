package com.example.mobile_enterprise_finals_group;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class LoanStatusActivity extends AppCompatActivity{
    LinearLayout layoutNoLoans, containerLoans;
    ScrollView scrollLoans;
    DataAccess dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_status);

        dbHelper = new DataAccess(this);
        layoutNoLoans = findViewById(R.id.layoutNoLoans);
        scrollLoans = findViewById(R.id.scrollLoans);
        containerLoans = findViewById(R.id.containerLoans);
        loadUserLoans();
    }
    private void loadUserLoans() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String employeeId = preferences.getString("employee_id", "");
        Cursor cursor = dbHelper.getUserLoans(employeeId);
        if (cursor.getCount() == 0) {
            layoutNoLoans.setVisibility(View.VISIBLE);
            scrollLoans.setVisibility(View.GONE);
        } else {
            layoutNoLoans.setVisibility(View.GONE);
            scrollLoans.setVisibility(View.VISIBLE);
            containerLoans.removeAllViews();

            while (cursor.moveToNext()) {
                int loanId = cursor.getInt(0);
                String loanType = cursor.getString(2);
                double amount = cursor.getDouble(3);
                int months = cursor.getInt(4);
                String status = cursor.getString(9);

                addLoanCard(loanId, loanType, amount, months, status);
            }
        }
        cursor.close();
    }
    private void addLoanCard(int loanId, String loanType, double amount, int months, String status) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 20);
        card.setLayoutParams(params);
        card.setCardElevation(4);
        card.setRadius(8);
        card.setContentPadding(20, 20, 20, 20);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView tvType = new TextView(this);
        tvType.setText(loanType + " Loan - ₱" + String.format("%.2f", amount));
        tvType.setTextSize(16);
        tvType.setTextColor(Color.BLACK);
        tvType.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(tvType);

        TextView tvMonths = new TextView(this);
        tvMonths.setText("Months: " + months);
        tvMonths.setTextSize(14);
        tvMonths.setTextColor(Color.DKGRAY);
        layout.addView(tvMonths);

        TextView tvStatus = new TextView(this);
        tvStatus.setText("Status: " + status);
        tvStatus.setTextSize(14);
        tvStatus.setTypeface(null, android.graphics.Typeface.BOLD);

        if (status.equals("Approved")) {
            tvStatus.setTextColor(Color.GREEN);
        } else if (status.equals("Pending")) {
            tvStatus.setTextColor(Color.BLUE);
        } else {
            tvStatus.setTextColor(Color.RED);
        }
        layout.addView(tvStatus);

        card.addView(layout);
        containerLoans.addView(card);
    }
}
