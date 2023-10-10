package comp5216.sydney.edu.au.hiketogether;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        return new ViewPagerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_create_event_1,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerViewHolder holder, int position) {
        return;
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

        public ViewPagerViewHolder(@NonNull View itemView){
            super(itemView);
            mContainer = itemView.findViewById(R.id.container);
            create_image = itemView.findViewById(R.id.create_imageView);


            create_image = itemView.findViewById(R.id.create_imageView);
            addImage = itemView.findViewById(R.id.addImage);

            addImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imagePickerListener != null) {
                        imagePickerListener.onPickImage(create_image,REQUEST_CODE_PICK_IMAGE);
                    }
                }
            });

        }


    }
}
