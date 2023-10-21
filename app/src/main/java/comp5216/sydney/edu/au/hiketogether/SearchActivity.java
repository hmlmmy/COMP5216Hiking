package comp5216.sydney.edu.au.hiketogether;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SearchActivity extends AppCompatActivity {
    // 声明界面组件
    private EditText searchEditText;
    private Button searchButton;
    private ListView historyListView;

    // 声明搜索历史适配器和列表
    private ArrayAdapter<String> historyAdapter;
    private List<String> searchHistory;

    // 定义SharedPreferences的名字和键名
    private static final String PREFS_NAME = "search_prefs";
    private static final String KEY_HISTORY = "search_history";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 绑定界面组件
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        historyListView = findViewById(R.id.historyListView);

        // 从SharedPreferences加载搜索历史
        loadSearchHistory();

        // 初始化适配器并设置到ListView
        historyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchHistory);
        historyListView.setAdapter(historyAdapter);

        // 设置搜索按钮的点击事件监听
        searchButton.setOnClickListener(v -> performSearch());

        // 设置历史搜索记录的点击事件，点击后填充搜索框并执行搜索
        historyListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSearch = searchHistory.get(position);
            searchEditText.setText(selectedSearch);
            performSearch();
        });
    }

    // 从SharedPreferences加载搜索历史
    private void loadSearchHistory() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedHistory = prefs.getString(KEY_HISTORY, "");
        searchHistory = new ArrayList<>(Arrays.asList(savedHistory.split(",")));
        // 如果列表只有一个空元素，清空它
        if (searchHistory.size() == 1 && searchHistory.get(0).isEmpty()) {
            searchHistory.clear();
        }
    }

    // 将搜索历史保存到SharedPreferences
    private void saveSearchHistory() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_HISTORY, TextUtils.join(",", searchHistory));
        editor.apply();
    }

    // 执行搜索操作
    private void performSearch() {
        String query = searchEditText.getText().toString().trim();

        // 如果搜索内容不为空且历史记录中不存在，则保存到搜索历史
        if (!query.isEmpty() && !searchHistory.contains(query)) {
            searchHistory.add(0, query);
            historyAdapter.notifyDataSetChanged();
            saveSearchHistory();
        }

        // 使用Firebase Firestore来搜索匹配的事件
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 创建name和address的范围查询
        Query nameQuery = db.collection("Event List")
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + "\uf8ff");

        Query addressQuery = db.collection("Event List")
                .whereGreaterThanOrEqualTo("address", query)
                .whereLessThanOrEqualTo("address", query + "\uf8ff");

        // 使用Task的组合功能来合并这两个查询的结果
        Task<QuerySnapshot> nameSearch = nameQuery.get();
        Task<QuerySnapshot> addressSearch = addressQuery.get();

        Task<List<QuerySnapshot>> combinedTask = Tasks.whenAllSuccess(nameSearch, addressSearch);

        combinedTask.addOnSuccessListener(querySnapshots -> {
            ArrayList<Event> matchedEvents = new ArrayList<>();
            for (QuerySnapshot snapshot : querySnapshots) {
                for (QueryDocumentSnapshot document : snapshot) {
                    Event event = document.toObject(Event.class); // 将文档转换为Event对象
                    if (!matchedEvents.contains(event)) {
                        matchedEvents.add(event);
                    }
                }
            }

            Intent intent = new Intent(this, EventPageActivity.class);
            if (matchedEvents.isEmpty()) {
                intent.putExtra("ERROR_MESSAGE", "Cannot find appropriate event.");
            } else {
                intent.putExtra("MATCHED_EVENTS", matchedEvents);
            }
            startActivity(intent);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error occurred while searching.", Toast.LENGTH_SHORT).show();
        });
    }

}