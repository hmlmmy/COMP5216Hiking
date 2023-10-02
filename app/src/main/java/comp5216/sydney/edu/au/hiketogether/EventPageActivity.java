package comp5216.sydney.edu.au.hiketogether;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import android.os.Bundle;

public class EventPageActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page); // 设置活动的布局

        // 获取事件列表视图
        ListView eventListView = findViewById(R.id.eventList);

        //
        String[] eventListItems = {
                "Event 1: Description 1",
                "Event 2: Description 2",
                "Event 3: Description 3",

        };

        // 创建适配器以将事件数据绑定到ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventListItems);

        // 将适配器设置到ListView
        eventListView.setAdapter(adapter);

        // 为ListView的项添加点击事件监听器
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //
                String selectedItem = (String) parent.getItemAtPosition(position);
                Toast.makeText(EventPageActivity.this, "点击了：" + selectedItem, Toast.LENGTH_SHORT).show();
            }
        });
    }
}