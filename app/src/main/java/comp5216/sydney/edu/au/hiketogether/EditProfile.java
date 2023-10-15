package comp5216.sydney.edu.au.hiketogether;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.widget.ImageView;
public class EditProfile extends AppCompatActivity {

    // 定义变量存储UI组件
    private EditText usernameEditText, emailEditText, phoneEditText;
    private Button updateButton;
    private Button backButton;
    // Firestore 数据库引用
    private FirebaseFirestore db;
    // 用来存储查询到的文档ID
    private String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // 初始化 Firestore
        db = FirebaseFirestore.getInstance();

        // 关联变量与UI组件
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        updateButton = findViewById(R.id.updateButton);
        backButton = findViewById(R.id.backButton); // 添加返回按钮的初始化

        // 获取从其他Activity传递来的email
        String userEmail = getIntent().getStringExtra("email");

        // 根据email获取用户数据
        fetchData(userEmail);

        // 设置更新按钮的点击监听
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 当按钮被点击时，执行更新操作
                updateProfile();
            }
        });

        // 设置返回按钮的点击监听
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击时返回ProfileActivity
                finish();
            }
        });
    }

    // 根据提供的email从Firestore中获取用户数据
    private void fetchData(String email) {
        // 在"User Profile"集合中查询与给定email匹配的文档
        db.collection("User_profile")
                .whereEqualTo("Email", email)
                .get()
                .addOnCompleteListener(task -> {
                    // 如果查询成功并且结果非空
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // 遍历查询结果中的文档
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // 设置UI组件的文本为从文档中获取到的值
                            usernameEditText.setText(document.getString("name"));
                            emailEditText.setText(document.getString("Email"));
                            phoneEditText.setText(document.getString("phone"));
                            // 存储当前文档的ID，以便后续更新操作
                            documentId = document.getId();
                        }
                    } else {
                        // 如果查询失败，显示错误消息
                        Toast.makeText(EditProfile.this, "File does not exist.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 更新用户数据
    private void updateProfile() {
        // 获取指向Firestore中特定文档
        DocumentReference userRef = db.collection("User_profile").document(documentId);
        // 更新该文档的字段为新输入的值
        userRef.update(
                "name", usernameEditText.getText().toString(),
                "Email", emailEditText.getText().toString(),
                "phone", phoneEditText.getText().toString()
        ).addOnSuccessListener(aVoid -> {
            // 更新成功时显示成功消息
            Toast.makeText(EditProfile.this, "Profile updated.", Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedName", usernameEditText.getText().toString());
            resultIntent.putExtra("updatedEmail", emailEditText.getText().toString());
            resultIntent.putExtra("updatedPhone", phoneEditText.getText().toString());
            setResult(RESULT_OK, resultIntent);
            finish();
        }).addOnFailureListener(e -> {
            // 更新失败时显示错误消息
            Toast.makeText(EditProfile.this, "Error updating profile.", Toast.LENGTH_SHORT).show();
        });
    }
}
