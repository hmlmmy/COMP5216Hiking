package comp5216.sydney.edu.au.hiketogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    Button logoutBtn;
    Button editBtn;
    Button darkmodeBtn;
    Button createEventBtn;
    Button bookedEventBtn;
    Button myEventBtn;
    TextView Username;

    TextView userEmail;
    TextView PhoneNum;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        logoutBtn = findViewById(R.id.logout);

        Username = findViewById(R.id.usernameTextView);
        editBtn = findViewById(R.id.editProfile);
        userEmail = findViewById(R.id.emailtextView);
        PhoneNum = findViewById(R.id.phonetextView);
        darkmodeBtn = findViewById(R.id.darkModeButton);
        createEventBtn = findViewById(R.id.CreateEventButton);
        bookedEventBtn = findViewById(R.id.bookedEventButton);
        myEventBtn = findViewById(R.id.MyEventButton);



        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Username.setText(user.getEmail());
            userEmail.setText(user.getEmail());
            PhoneNum.setText(user.getPhoneNumber());
        }

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditProfile.class);
                startActivityForResult(intent, 1);
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        createEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开CreateEvent页面
                Intent intent = new Intent(ProfileActivity.this,CreateEventActivity.class);
                startActivity(intent);
            }
        });

        bookedEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取创建者的uid
                String query = user.getUid().trim();

                // 使用Firebase Firestore来搜索创建者uid符合的事件
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Event List")
                        .whereArrayContains("members", query)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                ArrayList<Event> matchedEvents = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Event event = document.toObject(Event.class); // 将文档转换为Event对象
                                    matchedEvents.add(event);
                                }
                                Intent intent = new Intent(ProfileActivity.this, EventPageActivity.class);
                                if (matchedEvents.isEmpty()) {
                                    intent.putExtra("ERROR_MESSAGE", "Cannot find appropriate event.");
                                } else {
                                    intent.putExtra("MATCHED_EVENTS", matchedEvents);
                                }
                                startActivity(intent);
                            } else {
                                Toast.makeText(ProfileActivity.this, "Error occurred while searching.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        myEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取创建者的uid
                String query = user.getUid().trim();

                // 使用Firebase Firestore来搜索创建者uid符合的事件
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Event List")
                        .whereEqualTo("creatorID",query)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                ArrayList<Event> matchedEvents = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Event event = document.toObject(Event.class); // 将文档转换为Event对象
                                    matchedEvents.add(event);
                                }
                                Intent intent = new Intent(ProfileActivity.this, EventPageActivity.class);
                                if (matchedEvents.isEmpty()) {
                                    intent.putExtra("ERROR_MESSAGE", "Cannot find appropriate event.");
                                } else {
                                    intent.putExtra("MATCHED_EVENTS", matchedEvents);
                                }
                                startActivity(intent);
                            } else {
                                Toast.makeText(ProfileActivity.this, "Error occurred while searching.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        darkmodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 修改整个页面的背景颜色
                View rootView = getWindow().getDecorView().getRootView();
                rootView.setBackgroundColor(getResources().getColor(R.color.darkmode_color));

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String updatedName = data.getStringExtra("updatedName");
                String updatedEmail = data.getStringExtra("updatedEmail");
                String updatedPhone = data.getStringExtra("updatedPhone");

                Username.setText(updatedName);
                userEmail.setText(updatedEmail);
                PhoneNum.setText(updatedPhone);

            }

        }
    }


}
