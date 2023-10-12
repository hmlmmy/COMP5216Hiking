package comp5216.sydney.edu.au.hiketogether;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EventPageActivity extends AppCompatActivity {

    Button homeBtn;
    Button eventBtn;
    Button createEventBtn;
    Button profileBtn;

    Fragment Event;
    Fragment Create;
    Fragment Profile;
    BottomNavigationView bottomNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page); // 设置活动的布局

        bottomNav = findViewById(R.id.bottom_nav);
        Event = new MainFragment(); // 需要你有一个名为MainFragment的Fragment类
        Create = new CreateFragment(); // 需要你有一个名为RecordsFragment的Fragment类
        Profile = new ProfileFragment(); // 需要你有一个名为ProfileFragment的Fragment类

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, Event)
                    .commit();
        }

        //为底部导航栏设置监听器
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.navigation_Event) {
                selectedFragment = Event;
            } else if (item.getItemId() == R.id.navigation_Create) {
                selectedFragment = Create;
            } else if (item.getItemId() == R.id.navigation_Profile) {
                selectedFragment = Profile;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });


//        homeBtn = findViewById(R.id.buttonHome);
//        eventBtn = findViewById(R.id.buttonEvent);
//        createEventBtn = findViewById(R.id.buttonCreateEvent);
//        profileBtn = findViewById(R.id.buttonProfile);
//
//        homeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(EventPageActivity.this, MainActivity.class));
//            }
//        });
//
//        createEventBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(EventPageActivity.this, CreateEventActivity.class));
//            }
//        });
//
//        profileBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(EventPageActivity.this, ProfileActivity.class));
//            }
//        });

    }
}