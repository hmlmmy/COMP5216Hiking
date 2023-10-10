package comp5216.sydney.edu.au.hiketogether;
import static comp5216.sydney.edu.au.hiketogether.ViewPagerAdapter.ViewPagerViewHolder.REQUEST_CODE_PICK_IMAGE;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class CreateEventActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123; // 请求代码可以使用任何非负整数
    Button create_event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_main);

        // 检查权限状态
        if (checkPermissions()) {
            // 权限已经被授予，可以执行读写存储的操作
            setupViewPager();
        } else {
            // 请求权限
            requestPermissions();
        }


    }




    //viewPager滑动效果
    private void setupViewPager() {
        ViewPager2 viewPager = findViewById(R.id.pager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);

        viewPagerAdapter.setImagePickerListener(new ViewPagerAdapter.ImagePickerListener() {
            @Override
            public void onPickImage(ImageView targetImageView, int requestCode) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode);
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri imageUri = data.getData();
                ImageView targetImageView = findViewById(R.id.create_imageView); // 根据你的需求获取ImageView的实例
                targetImageView.setImageURI(imageUri);
            }
        }
    }











    //读取权限
    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        String[] permissions = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了权限，可以执行读写存储的操作
                setupViewPager();
            } else {
                // 用户拒绝了权限，可以显示一条消息或采取适当的操作来处理没有权限的情况
                Toast.makeText(this, "没有存储权限，某些功能可能受限", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
