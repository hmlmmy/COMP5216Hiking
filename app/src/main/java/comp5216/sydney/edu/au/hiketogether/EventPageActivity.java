package comp5216.sydney.edu.au.hiketogether;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EventPageActivity extends AppCompatActivity {

    ListView eventListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        initializeButtons();
        handleIncomingData();
    }

    private void initializeButtons() {
        findViewById(R.id.buttonCreateEvent).setOnClickListener(v -> startActivity(new Intent(this, CreateEventActivity.class)));
        findViewById(R.id.buttonProfile).setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        findViewById(R.id.buttonEvent).setOnClickListener(v -> {
            startActivity(new Intent(this, EventPageActivity.class));
            finish();
        });
        findViewById(R.id.searchButton).setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
    }

    private void handleIncomingData() {
        Intent intent = getIntent();
        ArrayList<Event> matchedEvents = (ArrayList<Event>) intent.getSerializableExtra("MATCHED_EVENTS");

        if (matchedEvents != null && !matchedEvents.isEmpty()) {
            populateListView(matchedEvents);
        } else {
            String errorMessage = intent.getStringExtra("ERROR_MESSAGE");
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            } else {
                fetchAllEvents();
            }
        }
    }

    private void fetchAllEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<Event> eventList = new ArrayList<>();
        db.collection("Event List").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Event event = document.toObject(Event.class);
                event.setId(document.getId());
                eventList.add(event);
            }
            populateListView(eventList);
        });
    }

    private void populateListView(ArrayList<Event> events) {
        eventListView = findViewById(R.id.eventList);
        EventAdapter eventAdapter = new EventAdapter(this, events);
        eventListView.setAdapter(eventAdapter);
        eventListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, EventDetailActivity.class);
            Event event = events.get(position);
            Log.i("event: ", event.getName());
            intent.putExtra("EVENT", events.get(position));
            startActivity(intent);
        });
    }
}
