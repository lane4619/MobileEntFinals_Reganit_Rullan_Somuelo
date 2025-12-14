package com.example.mobile_enterprise_finals_group;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
public class LoanCalculatorActivity extends AppCompatActivity{
    TextView tvLoanType, tvResults;
    EditText etLoanAmount, etMonths, etYearsService;
    Button btnCalculate, btnApply;
    LinearLayout layoutResults;

    String loanType;
    DataAccess dbHelper;
    double calculatedTotal = 0;
    double calculatedMonthly = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_calculator);

        dbHelper = new DataAccess(this);
        loanType = getIntent().getStringExtra("loan_type");

        tvLoanType = findViewById(R.id.tvLoanType);
        tvResults = findViewById(R.id.tvResults);
        etLoanAmount = findViewById(R.id.etLoanAmount);
        etMonths = findViewById(R.id.etMonths);
        etYearsService = findViewById(R.id.etYearsService);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnApply = findViewById(R.id.btnApply);
        layoutResults = findViewById(R.id.layoutResults);

        tvLoanType.setText(loanType + " Loan Calculator");

        if (loanType.equals("Emergency")) {
            etMonths.setText("6");
            etMonths.setEnabled(false);
        } else if (loanType.equals("Special")) {
            etYearsService.setVisibility(View.VISIBLE);
        }
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateLoan();
            }
        });
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyForLoan();
            }
        });
    }
    private void calculateLoan() {
        String amountStr = etLoanAmount.getText().toString().trim();
        String monthsStr = etMonths.getText().toString().trim();

        if (amountStr.isEmpty() || monthsStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        double loanAmount = Double.parseDouble(amountStr);
        int months = Integer.parseInt(monthsStr);

        if (loanType.equals("Emergency") && (loanAmount < 5000 || loanAmount > 25000)) {
            Toast.makeText(this, "Emergency loan: ₱5,000 to ₱25,000 only", Toast.LENGTH_SHORT).show();
            return;
        }
        if (loanType.equals("Special")) {
            String yearsStr = etYearsService.getText().toString().trim();
            if (yearsStr.isEmpty()) {
                Toast.makeText(this, "Enter years in service", Toast.LENGTH_SHORT).show();
                return;
            }
            int years = Integer.parseInt(yearsStr);
            if (years < 5) {
                Toast.makeText(this, "Special loan requires 5+ years service", Toast.LENGTH_SHORT).show();
                return;
            }
            if (loanAmount < 50000 || loanAmount > 100000) {
                Toast.makeText(this, "Special loan: ₱50,000 to ₱100,000 only", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        String resultText = "";
        double interest = 0;
        double serviceCharge = 0;

        if (loanType.equals("Emergency")) {
            serviceCharge = loanAmount * 0.01;
            interest = loanAmount * 0.006 * months;
            calculatedTotal = loanAmount + serviceCharge + interest;
            calculatedMonthly = calculatedTotal / months;
            resultText = String.format("Loan Amount: ₱%.2f\nService Charge: ₱%.2f\n" + "Total Interest: ₱%.2f\nTotal Amount: ₱%.2f\nMonthly Payment: ₱%.2f", loanAmount, serviceCharge, interest, calculatedTotal, calculatedMonthly);
        }
        else if (loanType.equals("Special")) {
            double interestRate = getSpecialInterestRate(months);
            interest = loanAmount * months * interestRate;
            calculatedTotal = loanAmount + interest;
            calculatedMonthly = calculatedTotal / months;

            resultText = String.format("Loan Amount: ₱%.2f\nInterest Rate: %.2f%%\n" + "Total Interest: ₱%.2f\nTotal Amount: ₱%.2f\nMonthly Payment: ₱%.2f", loanAmount, interestRate * 100, interest, calculatedTotal, calculatedMonthly);
        }
        else if (loanType.equals("Regular")) {
            serviceCharge = loanAmount * 0.02;
            double interestRate = getRegularInterestRate(months);
            interest = loanAmount * months * interestRate;
            double takeHome = loanAmount - (interest + serviceCharge);
            calculatedMonthly = takeHome / months;
            calculatedTotal = loanAmount;

            resultText = String.format("Loan Amount: ₱%.2f\nService Charge: ₱%.2f\n" + "Total Interest: ₱%.2f\nTake Home: ₱%.2f\nMonthly Payment: ₱%.2f", loanAmount, serviceCharge, interest, takeHome, calculatedMonthly);
        }

        tvResults.setText(resultText);
        layoutResults.setVisibility(View.VISIBLE);
    }

    private double getSpecialInterestRate(int months) {
        if (months <= 6) return 0.006;
        else if (months <= 12) return 0.0062;
        else return 0.0065;
    }

    private double getRegularInterestRate(int months) {
        if (months <= 5) return 0.0062;
        else if (months <= 10) return 0.0065;
        else if (months <= 15) return 0.0068;
        else if (months <= 20) return 0.0075;
        else return 0.008;
    }

    private void applyForLoan() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String employeeId = preferences.getString("employee_id", "");
        double loanAmount = Double.parseDouble(etLoanAmount.getText().toString());
        int months = Integer.parseInt(etMonths.getText().toString());
        double interest = loanAmount * 0.01 * months;
        double serviceCharge = loanType.equals("Emergency") ? loanAmount * 0.01 :
                loanType.equals("Regular") ? loanAmount * 0.02 : 0;

        if (dbHelper.addLoan(employeeId, loanType, loanAmount, months, interest,
                serviceCharge, calculatedTotal, calculatedMonthly)) {
            Toast.makeText(this, "Loan application submitted!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Application failed", Toast.LENGTH_SHORT).show();
        }
    }
}