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
    Button eventBtn;
    Button createEventBtn;
    Button profileBtn;
    Button searchBtn;
    EventAdapter eventAdapter;
    ListView eventListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page); // 设置活动的布局

        eventBtn = findViewById(R.id.buttonEvent);
        createEventBtn = findViewById(R.id.buttonCreateEvent);
        profileBtn = findViewById(R.id.buttonProfile);
        searchBtn = findViewById(R.id.searchButton);
        eventListView = findViewById(R.id.eventList);

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

        eventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 重新启动EventPageActivity
                Intent intent = new Intent(EventPageActivity.this, EventPageActivity.class);
                startActivity(intent);
                // 结束当前的EventPageActivity
                finish();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EventPageActivity.this, SearchActivity.class));
            }
        });

        // 检查是否从 SearchActivity 接收到了数据
        Intent intent = getIntent();
        ArrayList<Event> matchedEvents = (ArrayList<Event>) intent.getSerializableExtra(" MATCHED EVENT");
        if (matchedEvents != null && !matchedEvents.isEmpty()) {
            // 显示匹配的活动
            eventAdapter = new EventAdapter(EventPageActivity.this, matchedEvents);
            eventListView.setAdapter(eventAdapter);
            setEventClickListener(matchedEvents);
        } else {
            String errorMessage = intent.getStringExtra("ERROR_MESSAGE");
            if (errorMessage != null) {
                // 显示错误消息，例如使用Toast
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            } else {
                // 如果没有从 SearchActivity 接收到数据，就加载所有的事件
                // 初始化 Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // 获取事件列表视图
//        ListView eventListView = findViewById(R.id.eventList);

                // 创建一个事件数据列表
                ArrayList<Event> eventList = new ArrayList<>();
                // 获取所有事件
                db.collection("Event List")
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
                        // 创建一个Intent来启动EventDetailActivity
                        Intent intent = new Intent(EventPageActivity.this, EventDetailActivity.class);
                        // 将事件文档数据传递给EventDetailActivity
                        intent.putExtra("EVENT", selectedEvent);
                        // 启动EventDetailActivity
                        startActivity(intent);
                    }
                });
            }
        }
    }

    private void setEventClickListener(ArrayList<Event> events) {
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 获取选定的事件
                Event selectedEvent = events.get(position);
                // 创建一个Intent来启动EventDetailActivity
                Intent intent = new Intent(EventPageActivity.this, EventDetailActivity.class);
                // 将事件文档数据传递给EventDetailActivity
                intent.putExtra("EVENT", selectedEvent);
                // 启动EventDetailActivity
                startActivity(intent);
            }
        });
    }
}