package comp5216.sydney.edu.au.hiketogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class EventPageActivity extends AppCompatActivity {

    Button homeBtn;
    Button eventBtn;
    Button createEventBtn;
    Button profileBtn;
    private ListView eventList;
    private EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page); // 设置活动的布局

        homeBtn = findViewById(R.id.buttonHome);
        eventBtn = findViewById(R.id.buttonEvent);
        createEventBtn = findViewById(R.id.buttonCreateEvent);
        profileBtn = findViewById(R.id.buttonProfile);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EventPageActivity.this, MainActivity.class));
            }
        });

        createEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EventPageActivity.this, CreateEventActivity.class));
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EventPageActivity.this, ProfileActivity.class));
            }
        });

        // 初始化 Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // 获取事件列表视图
        ListView eventListView = findViewById(R.id.eventList);
        // 创建一个事件数据列表
        ArrayList<Event> eventList = new ArrayList<>();
        // 获取所有事件
        db.collection("events")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // 遍历查询结果并添加到事件列表
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Event event = document.toObject(Event.class);
                            // 获取每个事件的文档ID
                            String eventId = document.getId();
                            event.setId(eventId);
                            //Log.i("event id",eventId);
                            eventList.add(event);
                        }

                        // 创建适配器并将事件列表绑定到 ListView
                        eventAdapter = new EventAdapter(EventPageActivity.this, eventList);
                        eventListView.setAdapter(eventAdapter);
                    }
                });

        // 为ListView的项添加点击事件监听器
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 获取选定的事件
                Event selectedEvent = eventList.get(position);

                // 获取选定事件的文档ID
                String eventId = selectedEvent.getId();
                Log.i("event id",eventId);
                // 创建一个Firestore引用
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // 创建一个Intent来启动EventDetailActivity
                Intent intent = new Intent(EventPageActivity.this, EventDetailActivity.class);

                // 将事件文档数据传递给EventDetailActivity
                intent.putExtra("eventId", eventId);

                // 启动EventDetailActivity
                startActivity(intent);
//                // 获取事件文档
//                db.collection("events")
//                        .document(eventId)
//                        .get()
//                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                            @Override
//                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                if (documentSnapshot.exists()) {
//                                    // 获取文档数据
//                                    Map<String, Object> eventMap = documentSnapshot.getData();
//
//                                    // 创建一个Intent来启动EventDetailActivity
//                                    Intent intent = new Intent(EventPageActivity.this, EventDetailActivity.class);
//
//                                    // 将事件文档数据传递给EventDetailActivity
//                                    intent.putExtra("eventData", (Serializable) eventMap);
//
//                                    // 启动EventDetailActivity
//                                    startActivity(intent);
//                                } else {
//                                    // 处理文档不存在的情况
//                                    Log.d("Document", "Document does not exist");
//                                }
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                // 处理获取文档失败的情况
//                                Log.w("Document", "Error getting document", e);
//                            }
//                        });
            }
        });
    }
}