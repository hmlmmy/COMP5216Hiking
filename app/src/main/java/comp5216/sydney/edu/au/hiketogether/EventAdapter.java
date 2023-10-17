package comp5216.sydney.edu.au.hiketogether;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Event> eventList;

    public EventAdapter(Context context, ArrayList<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.event_list_item, parent, false);

        // 获取事件对象
        Event event = eventList.get(position);

        if (event != null && event.getName() != null) {
            Log.d("event name", event.getName());
        } else {
            Log.d("event name", "event or event name is null");
        }

        // 获取列表项视图中的 TextView，并设置事件属性
        TextView name = rowView.findViewById(R.id.name);
        TextView address = rowView.findViewById(R.id.address);
        TextView difficulty = rowView.findViewById(R.id.difficulty);
        TextView teamSize = rowView.findViewById(R.id.teamSize);
        TextView publishDate = rowView.findViewById(R.id.publishDate);
        ImageView image = rowView.findViewById(R.id.image);


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
        publishDate.setText("Publish Date: " + convertTimestampToDate(event.getCreateTimeStamp()));
        // set image
        String imageUrl = event.getImageURLs().get(0);
        Glide.with(context)
                .load(imageUrl)
                .into(image);

        return rowView;
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

