package comp5216.sydney.edu.au.hiketogether;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>{
    private List<String> create2= new ArrayList<>();
    private List<String> memberlist= new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getUserInfo(){
        //default路径
        CollectionReference cities = db.collection("cities");

        Map<String, Object> data1 = new HashMap<>();
        //单条数据
        data1.put("name", "San Francisco");
//        data1.put("state", "CA");
//        data1.put("country", "USA");
//        data1.put("capital", false);
//        data1.put("population", 860000);
//        data1.put("regions", Arrays.asList("west_coast", "norcal"));

        //放user email进去
        cities.document("SF").set(data1);
    }


    public interface ImagePickerListener {
        void onPickImage(ImageView targetImageView, int requestCode);
    }

    private ImagePickerListener imagePickerListener;

    public void setImagePickerListener(ImagePickerListener listener) {
        this.imagePickerListener = listener;
    }


    public ViewPagerAdapter(){
        create2.add("");
        create2.add("");
    }

    private ArrayAdapter<String> adapter; // 添加适配器变量
    ListView list;
    public void addMember(String memberID) {
        String newItem = memberID; // 假设 "New Member" 是要添加的新元素
        memberlist.add(newItem);
        adapter.notifyDataSetChanged(); // 更新适配器
    }

    @NonNull
    @Override
    public ViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_create_event_1, parent, false);
        list = view.findViewById(R.id.create_lst); // 初始化 list
        adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, memberlist); // 初始化适配器
        list.setAdapter(adapter); // 设置适配器
        addMember("12345");
        getUserInfo();
        return new ViewPagerViewHolder(view, viewType);
    }

    public String getUserInput(int position) {
        return create2.get(position);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewPagerViewHolder holder, int position) {
        if (position == 0) {
            // 第一页的内容
            String userInput = create2.get(position);
            holder.userInputEditText.setText(userInput);
            holder.list.setVisibility(View.GONE);
            holder.create_image.setVisibility(View.VISIBLE); // 显示ImageView
        } else if (position == 1) {
            // 第二页的内容
            String userInput = create2.get(position);
            holder.userInputEditText.setText(userInput);

            holder.list.setVisibility(View.VISIBLE);
            holder.create_image.setVisibility(View.GONE); // 隐藏ImageView
            holder.userInputEditText.setVisibility(View.GONE);
            holder.addImage.setVisibility(View.GONE);

        }
    }



    @Override
    public int getItemCount() {
        return 2;
    }

    class ViewPagerViewHolder extends RecyclerView.ViewHolder{
        Button addImage;
        public static final int REQUEST_CODE_PICK_IMAGE = 1;
        RelativeLayout mContainer;
        ImageView create_image;
        EditText userInputEditText;
        ListView list;
        int position; // 添加一个变量来保存ViewHolder的位置
        public ViewPagerViewHolder(@NonNull View itemView, int position) {
            super(itemView);
            this.position = position; // 保存ViewHolder的位置
            userInputEditText = itemView.findViewById(R.id.user_input_edittext);
            mContainer = itemView.findViewById(R.id.container);
            create_image = itemView.findViewById(R.id.create_imageView);
            addImage = itemView.findViewById(R.id.addImage);
            list = itemView.findViewById(R.id.create_lst);

            userInputEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // 在文本变化之前的回调
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // 在文本变化时的回调，保存用户输入
                    create2.set(position, s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // 在文本变化之后的回调
                }
            });

            addImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imagePickerListener != null) {
                        imagePickerListener.onPickImage(create_image, REQUEST_CODE_PICK_IMAGE);
                    }
                }
            });
        }


    }
}
