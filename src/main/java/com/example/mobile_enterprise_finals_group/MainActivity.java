package com.example.mobile_enterprise_finals_group;

import android.os.Bundle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    TextView tvWelcome;
    Button btnEmergencyLoan, btnSpecialLoan, btnRegularLoan, btnLoanStatus, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnEmergencyLoan = findViewById(R.id.btnEmergencyLoan);
        btnSpecialLoan = findViewById(R.id.btnSpecialLoan);
        btnRegularLoan = findViewById(R.id.btnRegularLoan);
        btnLoanStatus = findViewById(R.id.btnLoanStatus);
        btnLogout = findViewById(R.id.btnLogout);

        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String employeeId = preferences.getString("employee_id", "");
        tvWelcome.setText("Welcome! ID: " + employeeId);

        btnEmergencyLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoanCalculator("Emergency");
            }
        });
        btnSpecialLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoanCalculator("Special");
            }
        });
        btnRegularLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoanCalculator("Regular");
            }
        });
        btnLoanStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoanStatusActivity.class));
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }
    private void openLoanCalculator(String loanType) {
        Intent intent = new Intent(this, LoanCalculatorActivity.class);
        intent.putExtra("loan_type", loanType);
        startActivity(intent);
    }
    private void logout() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}