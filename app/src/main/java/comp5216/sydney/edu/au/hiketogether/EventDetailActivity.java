package comp5216.sydney.edu.au.hiketogether;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;


public class EventDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // 获取从 EventPageActivity 传递的事件数据
        String eventName = getIntent().getStringExtra("eventName");
        String eventAddress = getIntent().getStringExtra("eventAddress");
        int eventTeamSize = getIntent().getIntExtra("eventTeamSize", 0);

        // 在事件详情页面上显示事件数据
        TextView nameTextView = findViewById(R.id.eventNameTextView);
        TextView addressTextView = findViewById(R.id.addressTextView);
        TextView teamSizeTextView = findViewById(R.id.teamSizeTextView);

        nameTextView.setText(eventName);
        addressTextView.setText(eventAddress);
        teamSizeTextView.setText(String.valueOf(eventTeamSize));
    }
}