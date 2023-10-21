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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;


public class EventDetailActivity extends AppCompatActivity {
    TextView name;
    TextView address;
    //TextView teamSize;
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
        assert user != null;
        String currentUID = user.getUid();
        Log.i("currentUID", currentUID);

        //Get user email
        userEmail = user.getEmail();
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get UI elements
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        //teamSize = findViewById(R.id.teamSize);
        difficulty = findViewById(R.id.difficulty);
        description = findViewById(R.id.description);
        time = findViewById(R.id.time);
        creatorName = findViewById(R.id.creatorName);
        creatorEmail = findViewById(R.id.creatorEmail);
        creatorPhone = findViewById(R.id.creatorPhone);
        //eventImageView = findViewById(R.id.eventImage);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        Button joinButton = findViewById(R.id.joinButton);
        Button quitButton = findViewById(R.id.quitButton);

        Event event = (Event) getIntent().getSerializableExtra("EVENT");
        Log.i("event name: ", event.getName());
        String eventId = event.getId();
        Log.i("event id: ", eventId);
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
        //teamSize.setText("Team Size: " + Integer.toString(event.getTeamSize()));
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

        db.collection("Event List").document(eventId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            List<String> memberIds = (List<String>) documentSnapshot.get("members");
                            List<String> memberNames = new ArrayList<>();

                            if (memberIds != null) {
                                for (String memberId : memberIds) {
                                    db.collection("User Profile").document(memberId)
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot userDocument) {
                                                    if (userDocument.exists()) {
                                                        String userName = userDocument.getString("name");
                                                        memberNames.add(userName);

                                                        // 检查是否获取了所有成员的信息
                                                        if (memberNames.size() == memberIds.size()) {
                                                            // 所有成员信息已获取，更新适配器
                                                            updateRecyclerView(recyclerView,memberNames);
                                                        }
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // 处理获取用户文档失败的情况
                                                    Log.e("Query", "Error getting user document: " + e.getMessage());
                                                }
                                            });
                                }
                            }
                        } else {
                            // 处理事件文档不存在的情况
                            Log.d("Document", "Event document does not exist");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 处理获取事件文档失败的情况
                        Log.e("Query", "Error getting event document: " + e.getMessage());
                    }
                });

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
                // 创建对用户文档的引用，使用 whereEqualTo 查询邮箱地址
                db.collection("User Profile").document(currentUID)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot document) {
                                if (document.exists()) {
                                    // 获取用户的 joinedEvents 数组
                                    List<String> joinedEvents = (List<String>) document.get("joinedEvents");

                                    if (joinedEvents == null) {
                                        joinedEvents = new ArrayList<>();
                                    }

                                    // 检查用户是否已经预定了这个事件
                                    if (!joinedEvents.contains(eventId)) {
                                        // 如果用户没有预定这个事件，将事件 ID 添加到 joinedEvents 数组
                                        joinedEvents.add(eventId);

                                        // 更新用户文档
                                        document.getReference().update("joinedEvents", joinedEvents)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // 更新成功，用户已成功加入事件
                                                        // 可以在这里执行相应的操作，例如显示消息
                                                        //Toast.makeText(EventDetailActivity.this, "Join successfully!", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // 更新失败，处理错误
                                                        Toast.makeText(EventDetailActivity.this, "Join failure!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        // 用户已经预定了这个事件
                                        //Toast.makeText(EventDetailActivity.this, "You have joined this event", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // 没有匹配邮箱的用户文档，处理错误
                                    Toast.makeText(EventDetailActivity.this, "Can not find user documentation", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // 查询失败，处理错误
                                Toast.makeText(EventDetailActivity.this, "Fail to query user documents: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                db.collection("Event List").document(eventId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    // 获取事件文档中的成员数组
                                    List<String> members = (List<String>) documentSnapshot.get("members");

                                    if (members == null) {
                                        members = new ArrayList<>();
                                    }

                                    // 检查用户是否已经是该事件的成员
                                    if (!members.contains(currentUID)) {
                                        // 如果用户不是该事件的成员，将用户ID添加到成员数组
                                        members.add(currentUID);

                                        // 更新事件文档
                                        documentSnapshot.getReference().update("members", members)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // 更新成功，用户已成功加入事件
                                                        // 可以在这里执行相应的操作，例如显示消息
                                                        Toast.makeText(EventDetailActivity.this, "Join successfully!", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // 更新失败，处理错误
                                                        Log.e("Update", "Error updating document: " + e.getMessage());
                                                    }
                                                });
                                    } else {
                                        // 用户已经是该事件的成员，可以显示相应消息
                                        Toast.makeText(EventDetailActivity.this, "You have joined this event", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // 处理文档不存在的情况
                                    Log.d("Document", "Event document does not exist");
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // 查询失败，处理错误
                                Log.e("Query", "Error getting event document: " + e.getMessage());
                            }
                        });

            }
        });

        // 设置按钮的点击事件监听器
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建对用户文档的引用，使用 whereEqualTo 查询邮箱地址
                db.collection("User Profile").document(currentUID)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot document) {
                                if (document.exists()) {
                                    // 获取用户的 joinedEvents 数组
                                    List<String> joinedEvents = (List<String>) document.get("joinedEvents");

                                    if (joinedEvents != null) {
                                        // 检查用户是否已经预定了这个事件
                                        if (joinedEvents.contains(eventId)) {
                                            // 如果用户已经预定这个事件，从 joinedEvents 数组中移除事件 ID
                                            joinedEvents.remove(eventId);

                                            // 更新用户文档
                                            document.getReference().update("joinedEvents", joinedEvents)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // 更新成功，用户已成功退出事件
                                                            // 可以在这里执行相应的操作，例如显示消息
                                                            //Toast.makeText(EventDetailActivity.this, "Quit successfully!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // 更新失败，处理错误
                                                            Toast.makeText(EventDetailActivity.this, "Quit failure: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            // 用户没有预定这个事件，你可以在这里显示相应的消息
                                            //Toast.makeText(EventDetailActivity.this, "You have not joined this event yet", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    // 没有匹配邮箱的用户文档，处理错误
                                    Toast.makeText(EventDetailActivity.this, "Can not find user documentation", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // 查询失败，处理错误
                                Toast.makeText(EventDetailActivity.this, "Fail to query user documents: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                db.collection("Event List").document(eventId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    // 获取事件文档中的成员数组
                                    List<String> members = (List<String>) documentSnapshot.get("members");

                                    if (members != null) {
                                        // 检查成员数组中是否包含当前用户ID
                                        if (members.contains(currentUID)) {
                                            // 如果成员数组包含当前用户ID，将其从数组中移除
                                            members.remove(currentUID);

                                            // 更新事件文档
                                            documentSnapshot.getReference().update("members", members)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // 更新成功，从事件中移除用户ID
                                                            // 可以在这里执行相应的操作，例如显示消息
                                                            Toast.makeText(EventDetailActivity.this, "Quit successfully!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // 更新失败，处理错误
                                                            Log.e("Update", "Error updating document: " + e.getMessage());
                                                        }
                                                    });
                                        } else {
                                            // 如果成员数组不包含当前用户ID，可以显示相应消息
                                            Toast.makeText(EventDetailActivity.this, "You have not joined this event yet!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    // 处理文档不存在的情况
                                    Log.d("Document", "Event document does not exist");
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // 查询失败，处理错误
                                Log.e("Query", "Error getting event document: " + e.getMessage());
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
    private void updateRecyclerView(RecyclerView recyclerView, List<String> memberNames) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MemberAdapter memberAdapter = new MemberAdapter(memberNames);
        recyclerView.setAdapter(memberAdapter);
    }
}