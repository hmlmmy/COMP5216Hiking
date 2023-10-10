package comp5216.sydney.edu.au.hiketogether;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>{
    private List<String> create2= new ArrayList<>();

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
        holder.create_text.setText(create2.get(position));
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    class ViewPagerViewHolder extends RecyclerView.ViewHolder{
        LinearLayout LiLayout;
        RelativeLayout mContainer;
        TextView create_text;
        ImageView create_image;

        public ViewPagerViewHolder(@NonNull View itemView){
            super(itemView);
            mContainer = itemView.findViewById(R.id.container);
            create_text = itemView.findViewById(R.id.create_textView);
            create_image = itemView.findViewById(R.id.create_imageView);
            Bitmap bmImg = BitmapFactory.decodeFile("/storage/emulated/0/Android/media/image.jpg");
            create_image.setImageBitmap(bmImg);
        }
    }
}
