package comp5216.sydney.edu.au.hiketogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.squareup.picasso.Picasso;


public class EventDetailActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView addressTextView;
    private TextView teamSizeTextView;
    private ImageView eventImageView;
    FirebaseAuth auth;
    FirebaseUser user;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //Log.i("userID",userEmail);

        //Get user email
        userEmail = user.getEmail();
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get UI elements
        nameTextView = findViewById(R.id.eventNameTextView);
        addressTextView = findViewById(R.id.addressTextView);
        teamSizeTextView = findViewById(R.id.teamSizeTextView);
        eventImageView = findViewById(R.id.eventImage);

        Button joinButton = findViewById(R.id.joinButton);
        Button quitButton = findViewById(R.id.quitButton);

        String eventId = getIntent().getStringExtra("eventId");

        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()) {
                            // 从文档中获取事件信息
                            String eventName = document.getString("name");
                            String eventAddress = document.getString("address");
                            long eventTeamSize = document.getLong("teamSize");
                            String eventPicture = document.getString("picture");

                            // 更新 UI 以显示完整的事件信息
                            nameTextView.setText(eventName);
                            addressTextView.setText(eventAddress);
                            teamSizeTextView.setText(String.valueOf(eventTeamSize));

                            // 使用 Picasso 或其他库加载事件图片
                            if (eventPicture != null && !eventPicture.isEmpty()) {
                                // 将图片地址字符串转换为 Uri
                                Uri imageUri = Uri.parse(eventPicture);

                                // 使用 Picasso 或其他库加载图片到 ImageView
                                Picasso.get().load(imageUri).into(eventImageView);
                            }
                        } else {
                            // 没有匹配的文档，处理错误
                            // 这里可以显示错误消息或执行其他操作
                            Log.i("search error", "No matching document for ID: " + eventId);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 查询失败，处理错误
                        // 这里可以显示错误消息或执行其他操作
                        Log.e("search error", "Query failed: " + e.getMessage());
                    }
                });

        // 设置按钮的点击事件监听器
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里执行 "Join" 操作的代码

                // 创建对用户文档的引用，使用 whereEqualTo 查询邮箱地址
                db.collection("userProfiles")
                        .whereEqualTo("email", userEmail) // userEmail 是当前用户的邮箱地址
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    // 获取匹配邮箱的用户文档
                                    DocumentSnapshot userDocument = queryDocumentSnapshots.getDocuments().get(0);

                                    // 在此处执行 "Join" 操作，如将事件添加到用户的 bookedEvents 数组中
                                    // 假设你已经获取了当前事件的 ID（eventId）。

                                    // 获取用户的 bookedEvents 数组
                                    List<String> bookedEvents = (List<String>) userDocument.get("bookedEvents");

                                    if (bookedEvents == null) {
                                        bookedEvents = new ArrayList<>();
                                    }

                                    // 检查用户是否已经预定了这个事件
                                    if (!bookedEvents.contains(eventId)) {
                                        // 如果用户没有预定这个事件，将事件 ID 添加到 bookedEvents 数组
                                        bookedEvents.add(eventId);

                                        // 更新用户文档
                                        userDocument.getReference().update("bookedEvents", bookedEvents)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // 更新成功，用户已成功加入事件
                                                        // 可以在这里执行相应的操作，例如显示消息
                                                        Toast.makeText(EventDetailActivity.this, "加入成功！", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // 更新失败，处理错误
                                                        Toast.makeText(EventDetailActivity.this, "加入失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        // 用户已经预定了这个事件，你可以在这里显示相应的消息
                                        Toast.makeText(EventDetailActivity.this, "您已经预定了这个事件！", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // 没有匹配邮箱的用户文档，处理错误
                                    Toast.makeText(EventDetailActivity.this, "找不到用户文档", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // 查询失败，处理错误
                                Toast.makeText(EventDetailActivity.this, "查询用户文档失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // 设置按钮的点击事件监听器
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里执行 "Quit" 操作的代码

                // 创建对用户文档的引用，使用 whereEqualTo 查询邮箱地址
                db.collection("userProfiles")
                        .whereEqualTo("email", userEmail) // userEmail 是当前用户的邮箱地址
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    // 获取匹配邮箱的用户文档
                                    DocumentSnapshot userDocument = queryDocumentSnapshots.getDocuments().get(0);

                                    // 在此处执行 "Quit" 操作，如从用户的 bookedEvents 数组中移除事件 ID
                                    // 假设你已经获取了当前事件的 ID（eventId）。

                                    // 获取用户的 bookedEvents 数组
                                    List<String> bookedEvents = (List<String>) userDocument.get("bookedEvents");

                                    if (bookedEvents != null) {
                                        // 检查用户是否已经预定了这个事件
                                        if (bookedEvents.contains(eventId)) {
                                            // 如果用户已经预定这个事件，从 bookedEvents 数组中移除事件 ID
                                            bookedEvents.remove(eventId);

                                            // 更新用户文档
                                            userDocument.getReference().update("bookedEvents", bookedEvents)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // 更新成功，用户已成功退出事件
                                                            // 可以在这里执行相应的操作，例如显示消息
                                                            Toast.makeText(EventDetailActivity.this, "退出成功！", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // 更新失败，处理错误
                                                            Toast.makeText(EventDetailActivity.this, "退出失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            // 用户没有预定这个事件，你可以在这里显示相应的消息
                                            Toast.makeText(EventDetailActivity.this, "您未预定这个事件！", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    // 没有匹配邮箱的用户文档，处理错误
                                    Toast.makeText(EventDetailActivity.this, "找不到用户文档", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // 查询失败，处理错误
                                Toast.makeText(EventDetailActivity.this, "查询用户文档失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}