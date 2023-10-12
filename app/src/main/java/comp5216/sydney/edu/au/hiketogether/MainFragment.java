package comp5216.sydney.edu.au.hiketogether;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
public class MainFragment extends Fragment {
    private ImageView iv_mic;
    private TextView tv_Speech_to_text;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    private boolean isListening = true;
    private ListView eventList;
    private EventAdapter eventAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_fragment, container, false);

        // 初始化 Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 获取事件列表视图
        ListView eventListView = view.findViewById(R.id.eventList);

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
                            eventList.add(event);
                        }

                        // 创建适配器并将事件列表绑定到 ListView
                        eventAdapter = new EventAdapter(getActivity(), eventList);
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
                Intent intent = new Intent(getActivity(), EventDetailActivity.class);
                intent.putExtra("eventName", selectedEvent.getName());
                intent.putExtra("eventAddress", selectedEvent.getAddress());
                intent.putExtra("eventTeamSize", selectedEvent.getTeamSize());
                intent.putExtra("eventId", selectedEvent.getId());

                // 启动EventDetailActivity
                startActivity(intent);
            }
        });

        return view;
    }


}
