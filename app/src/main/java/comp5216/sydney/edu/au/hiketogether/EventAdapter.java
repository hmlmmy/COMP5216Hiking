package comp5216.sydney.edu.au.hiketogether;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

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
        Log.d("event name", event.getName());

        // 获取列表项视图中的 TextView，并设置事件属性
        TextView eventNameTextView = rowView.findViewById(R.id.name);
        TextView eventAddressTextView = rowView.findViewById(R.id.address);
        TextView eventDescriptionTextView = rowView.findViewById(R.id.description);

        eventNameTextView.setText(event.getName());
        eventAddressTextView.setText(event.getAddress());
        eventDescriptionTextView.setText(event.getDescription());

        return rowView;
    }

}

