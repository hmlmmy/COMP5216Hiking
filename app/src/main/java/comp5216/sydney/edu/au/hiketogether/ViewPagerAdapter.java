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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>{
    private List<String> create2= new ArrayList<>();

    public interface ImagePickerListener {
        void onPickImage(ImageView targetImageView, int requestCode);
    }

    private ImagePickerListener imagePickerListener;

    public void setImagePickerListener(ImagePickerListener listener) {
        this.imagePickerListener = listener;
    }


    public ViewPagerAdapter(){
        create2.add("hello");
        create2.add("world");
    }
    @NonNull
    @Override
    public ViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_create_event_1, parent, false);
        return new ViewPagerViewHolder(view, viewType);
    }

    public String getUserInput(int position) {
        return create2.get(position);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewPagerViewHolder holder, int position) {
        String userInput = create2.get(position);
        holder.userInputEditText.setText(userInput);
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
        int position; // 添加一个变量来保存ViewHolder的位置
        public ViewPagerViewHolder(@NonNull View itemView, int position) {
            super(itemView);
            this.position = position; // 保存ViewHolder的位置

            userInputEditText = itemView.findViewById(R.id.user_input_edittext);
            mContainer = itemView.findViewById(R.id.container);
            create_image = itemView.findViewById(R.id.create_imageView);
            addImage = itemView.findViewById(R.id.addImage);

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
