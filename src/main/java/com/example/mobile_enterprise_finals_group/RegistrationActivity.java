package com.example.mobile_enterprise_finals_group;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class RegistrationActivity extends AppCompatActivity {

    EditText etFirstName, etMiddleInitial, etLastName, etDateHired, etBasicSalary, etPassword, etConfirmPassword;
    Button btnRegister;
    TextView tvEmployeeId;
    DataAccess dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        dbHelper = new DataAccess(this);

        etFirstName = findViewById(R.id.etFirstName);
        etMiddleInitial = findViewById(R.id.etMiddleInitial);
        etLastName = findViewById(R.id.etLastName);
        etDateHired = findViewById(R.id.etDateHired);
        etBasicSalary = findViewById(R.id.etBasicSalary);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvEmployeeId = findViewById(R.id.tvEmployeeId);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String firstName = etFirstName.getText().toString().trim();
        String middleInitial = etMiddleInitial.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String dateHired = etDateHired.getText().toString().trim();
        String salaryStr = etBasicSalary.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate inputs
        if (firstName.isEmpty() || lastName.isEmpty() || dateHired.isEmpty() ||
                salaryStr.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        double salary;
        try {
            salary = Double.parseDouble(salaryStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid salary", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate unique Employee ID
        String employeeId = generateUniqueEmployeeId(firstName, lastName);

        String fullName = firstName + " " + (middleInitial.isEmpty() ? "" : middleInitial + ". ") + lastName;

        // Add user to database
        boolean success = dbHelper.addUser(employeeId, fullName, dateHired, password, salary);

        if (success) {
            // Show Employee ID in AlertDialog
            new AlertDialog.Builder(this)
                    .setTitle("Registration Successful")
                    .setMessage("Your Employee ID is: " + employeeId + "\nPlease use this to login.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    })
                    .setCancelable(false)
                    .show();

            // Optional: show in TextView
            tvEmployeeId.setText("Your Employee ID: " + employeeId);
            tvEmployeeId.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
        }
    }

    // Generate unique Employee ID (checks database)
    private String generateUniqueEmployeeId(String firstName, String lastName) {
        String employeeId;
        Random random = new Random();
        do {
            int randomNum = random.nextInt(90000) + 10000; // 10000-99999
            employeeId = firstName.substring(0, 1).toUpperCase()
                    + lastName.substring(0, 1).toUpperCase()
                    + randomNum;
        } while (dbHelper.isEmployeeIdExists(employeeId)); // Check DB for uniqueness

        return employeeId;
    }
}