package com.example.mobile_enterprise_finals_group;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity{
    EditText etEmployeeId, etPassword;
    Button btnLogin;
    TextView tvRegister;
    DataAccess dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DataAccess(this);
        etEmployeeId = findViewById(R.id.etEmployeeId);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });
    }

    private void loginUser() {
        String employeeId = etEmployeeId.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (employeeId.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.checkUser(employeeId, password)) {
            String userType = dbHelper.getUserType(employeeId);

            // Save login info
            SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("employee_id", employeeId);
            editor.putString("user_type", userType);
            editor.apply();

            Intent intent;
            if (userType.equals("admin")) {
                intent = new Intent(this, AdminActivity.class);
            } else {
                intent = new Intent(this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid login", Toast.LENGTH_SHORT).show();
        }
    }
}
