package comp5216.sydney.edu.au.hiketogether;
import static comp5216.sydney.edu.au.hiketogether.ViewPagerAdapter.ViewPagerViewHolder.REQUEST_CODE_PICK_IMAGE;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

        //按下create按钮，读取用户输入
        create_event = findViewById(R.id.createButton);
        final ViewPager2 viewPager = findViewById(R.id.pager);
        create_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPage = viewPager.getCurrentItem();
                ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
                String userInput = adapter.getUserInput(currentPage);
                //这里是用户的输入，可以用来上传
                Log.i("user input",userInput);

            }
        });
    }

    public Context context;
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
                //这里的imageUri是图片的Uri可以用来上传
                upload_firebase(imageUri);
            }
        }
    }

    public void upload_firebase(Uri imageUri){
        // 获取Firebase存储的引用
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

// 创建一个存储路径，其中"images"是存储桶的名称，"eventID.jpg"是要上传的文件名
        StorageReference imageRef = storageRef.child("images/your_image.jpg");

// 将本地文件（通过imageUri指定）上传到Firebase存储
        UploadTask uploadTask = imageRef.putFile(imageUri);

// 监听上传任务的完成
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // 上传成功，获取图片的下载URL
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUri) {
                        // 在这里可以获取到上传后的图片的下载URL（downloadUri）
                        String imageUrl = downloadUri.toString();
                        // 将该URL保存到Firebase数据库或在应用中使用
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // 上传失败，处理失败情况
            }
        });

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
