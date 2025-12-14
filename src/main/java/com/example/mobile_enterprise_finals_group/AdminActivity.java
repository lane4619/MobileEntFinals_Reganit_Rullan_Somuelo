package com.example.mobile_enterprise_finals_group;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
public class AdminActivity extends AppCompatActivity{
    LinearLayout layoutNoLoans, containerLoans;
    ScrollView scrollLoans;
    DataAccess dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        dbHelper = new DataAccess(this);
        layoutNoLoans = findViewById(R.id.layoutNoLoans);
        scrollLoans = findViewById(R.id.scrollLoans);
        containerLoans = findViewById(R.id.containerLoans);
        loadAllLoans();
    }

    private void loadAllLoans() {
        Cursor cursor = dbHelper.getAllLoans();

        if (cursor.getCount() == 0) {
            layoutNoLoans.setVisibility(View.VISIBLE);
            scrollLoans.setVisibility(View.GONE);
        } else {
            layoutNoLoans.setVisibility(View.GONE);
            scrollLoans.setVisibility(View.VISIBLE);
            containerLoans.removeAllViews();

            while (cursor.moveToNext()) {
                int loanId = cursor.getInt(0);
                String employeeId = cursor.getString(1);
                String loanType = cursor.getString(2);
                double amount = cursor.getDouble(3);
                int months = cursor.getInt(4);
                String status = cursor.getString(9);
                addLoanCard(loanId, employeeId, loanType, amount, months, status);
            }
        }
        cursor.close();
    }
    private void addLoanCard(int loanId, String employeeId, String loanType, double amount, int months, String status) {
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

        TextView tvEmployee = new TextView(this);
        tvEmployee.setText("Employee: " + employeeId);
        tvEmployee.setTextSize(14);
        tvEmployee.setTextColor(Color.BLACK);
        tvEmployee.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(tvEmployee);

        TextView tvDetails = new TextView(this);
        tvDetails.setText(loanType + " Loan - ₱" + String.format("%.2f", amount) + " - " + months + " months");
        tvDetails.setTextSize(14);
        tvDetails.setTextColor(Color.DKGRAY);
        layout.addView(tvDetails);

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

        if (status.equals("Pending")) {
            LinearLayout buttonLayout = new LinearLayout(this);
            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
            Button btnApprove = new Button(this);
            btnApprove.setText("Approve");
            btnApprove.setBackgroundColor(Color.GREEN);
            btnApprove.setTextColor(Color.WHITE);
            btnApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateLoanStatus(loanId, "Approved");
                }
            });
            Button btnReject = new Button(this);
            btnReject.setText("Reject");
            btnReject.setBackgroundColor(Color.RED);
            btnReject.setTextColor(Color.WHITE);
            btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateLoanStatus(loanId, "Rejected");
                }
            });
            buttonLayout.addView(btnApprove);
            buttonLayout.addView(btnReject);
            layout.addView(buttonLayout);
        }
        card.addView(layout);
        containerLoans.addView(card);
    }
    private void updateLoanStatus(int loanId, String status) {
        if (dbHelper.updateLoanStatus(loanId, status)) {
            Toast.makeText(this, "Loan " + status.toLowerCase(), Toast.LENGTH_SHORT).show();
            loadAllLoans(); // Refresh the list
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }
}
