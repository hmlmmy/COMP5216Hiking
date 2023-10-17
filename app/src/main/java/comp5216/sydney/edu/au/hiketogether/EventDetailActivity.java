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
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.squareup.picasso.Picasso;
import androidx.viewpager2.widget.ViewPager2;


public class EventDetailActivity extends AppCompatActivity {
    TextView name;
    TextView address;
    TextView teamSize;
    TextView difficulty;
    TextView description;
    TextView time;
    TextView creatorName;
    TextView creatorEmail;
    TextView creatorPhone;
    ImageView eventImageView;
    FirebaseAuth auth;
    FirebaseUser user;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //Get user email
        userEmail = user.getEmail();
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get UI elements
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        teamSize = findViewById(R.id.teamSize);
        difficulty = findViewById(R.id.difficulty);
        description = findViewById(R.id.description);
        time = findViewById(R.id.time);
        creatorName = findViewById(R.id.creatorName);
        creatorEmail = findViewById(R.id.creatorEmail);
        creatorPhone = findViewById(R.id.creatorPhone);
        //eventImageView = findViewById(R.id.eventImage);

        Button joinButton = findViewById(R.id.joinButton);
        Button quitButton = findViewById(R.id.quitButton);

        Event event = (Event) getIntent().getSerializableExtra("EVENT");
        String eventId = event.getId();
        // update UI
        assert event != null;
        name.setText(event.getName());
        address.setText("Address: " + event.getAddress());
        // set dificulty
        String[] difficultyLevels = {"Easy", "Medium", "Hard"};
        int diffIndex = event.getDifficulty();
        if (diffIndex >= 0 && diffIndex < difficultyLevels.length) {
            difficulty.setText("Difficulty: " + difficultyLevels[diffIndex]);
        }
        // set team size
        teamSize.setText("Team Size: " + Integer.toString(event.getTeamSize()));
        // set publish date
        time.setText("Publish Date: " + convertTimestampToDate(event.getCreateTimeStamp()));
        // set description
        description.setText(event.getDescription());
        
        // set images
        ArrayList<String> imageURLs = event.getImageURLs();
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        ImageAdapter adapter = new ImageAdapter(imageURLs);
        viewPager.setAdapter(adapter);
        // set page indicator for images
        TabLayout tabLayout = findViewById(R.id.imageIndicator);
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                // 这里您可以为每个tab配置标题，但作为指示点，通常我们不设置任何文本
            }
        }).attach();

        // get creator info (name, email, phone) by UID
        String userID = event.getCreatorID();
        db.collection("User Profile").document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()) {
                            // 从文档中获取事件信息
                            String username = document.getString("name");
                            String email = document.getString("email");
                            String phone = document.getString("phone");
                            // set creator info
                            creatorName.setText("Publisher name: " + username);
                            creatorEmail.setText("Publisher email: " + email);
                            creatorPhone.setText("Publisher phone: " + phone);
                        } else {
                            // 没有匹配的文档，处理错误
                            // 这里可以显示错误消息或执行其他操作
                            Log.i("search error", "No matching document for userID: " + userID);
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

    public String convertTimestampToDate(long timestamp) {
        // 1. 创建一个Date对象
        Date date = new Date(timestamp);
        // 2. 使用SimpleDateFormat来格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        // 3. 返回格式化后的字符串
        return sdf.format(date);
    }
}