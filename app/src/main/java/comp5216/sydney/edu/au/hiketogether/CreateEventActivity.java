package comp5216.sydney.edu.au.hiketogether;

import static android.content.ContentValues.TAG;
import static comp5216.sydney.edu.au.hiketogether.ViewPagerAdapter.ViewPagerViewHolder.REQUEST_CODE_PICK_IMAGE;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123; // 请求代码可以使用任何非负整数
    Button create_event;
    FirebaseAuth auth;
    FirebaseUser user;
    String Description;
    String EventID;
    String CreatorID;
    String ImageUrl;
    String EventName;
    String Address;
    String Difficulty;

    List<String> Members = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_main);

        EventID = UUID.randomUUID().toString();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

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
                //event介绍
                assert adapter != null;
                EventName = adapter.getUserInput(0);
                Description = adapter.getUserInput(1);
                Address = adapter.getUserInput(2);
                Difficulty = adapter.getUserInput(3);

                //eventID

                //创建者Email
                CreatorID = user.getUid();

                //ImageUrl  这个是下载的URL
                uploadEvent(EventName, ImageUrl, EventID, CreatorID, Members, Description, Address, Difficulty);
            }
        });
    }

    public void uploadEvent(String EventName, String ImageUrl, String EventID, String CreatorID, List<String> Members, String Description, String Address, String Difficulty) {
        CollectionReference EventInfo = db.collection("Event List");
        Map<String, Object> data1 = new HashMap<>();

        // validate string inputs
        if (EventName == null || EventName.equals("")) {
            Toast.makeText(CreateEventActivity.this, "Please enter event name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Address == null || Address.equals("")) {
            Toast.makeText(CreateEventActivity.this, "Please enter address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Description == null || Description.equals("")) {
            Toast.makeText(CreateEventActivity.this, "Please enter description", Toast.LENGTH_SHORT).show();
            return;
        }
        // get difficulty int
        int DifficultyInt;
        if (Objects.equals(Difficulty.toLowerCase(), "easy")) {
            DifficultyInt = 0;
        } else if (Objects.equals(Difficulty.toLowerCase(), "medium")) {
            DifficultyInt = 1;
        } else if (Objects.equals(Difficulty.toLowerCase(), "hard")) {
            DifficultyInt = 2;
        } else {
            Log.i("Wrong difficulty string", Difficulty);
            Toast.makeText(CreateEventActivity.this, "Invalid difficulty, only accepts 'easy', 'medium' or 'hard'.", Toast.LENGTH_SHORT).show();
            return;
        }
        // imageURL check
        if (ImageUrl == null || ImageUrl.equals("")) {
            Toast.makeText(CreateEventActivity.this, "Please provide image.", Toast.LENGTH_SHORT).show();
            return;
        }

        // get current time
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Long timestampLong = timestamp.getTime();

        ArrayList<String> imageURLs = new ArrayList<String>();
        imageURLs.add(ImageUrl);

        //单个event的数据
        data1.put("name", EventName);
        data1.put("creatorID", CreatorID);
        data1.put("address", Address);
        data1.put("difficulty", DifficultyInt);
        data1.put("teamSize", 4);
        data1.put("createTimeStamp", timestampLong);
        data1.put("description", Description);
        data1.put("members", Members);
        data1.put("imageURLs", imageURLs);

        //放EventID进去
        EventInfo.document(EventID).set(data1);
        //Toast.makeText(CreateEventActivity.this, "Event created successfully.", Toast.LENGTH_SHORT).show();

        db.collection("User Profile").document(CreatorID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()) {
                            // add eventID to createdEvents
                            // 获取用户的 createdEvents 数组
                            List<String> createdEvents = (List<String>) document.get("createdEvents");
                            if (createdEvents == null) {
                                createdEvents = new ArrayList<>();
                            }
                            createdEvents.add(EventID);
                            // 更新用户文档
                            document.getReference().update("createdEvents", createdEvents)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(CreateEventActivity.this, "Event created successfully.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // 更新失败，处理错误
                                            Toast.makeText(CreateEventActivity.this, "Event creation failed." + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(CreateEventActivity.this, "Unable to find user profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateEventActivity.this, "Get Firebase collection failed.", Toast.LENGTH_SHORT).show();
                    }
                });
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
                Uri originalUri = data.getData();
                // 使用resizeAndCompressImage方法处理原始Uri
                Uri compressedUri = resizeAndCompressImage(originalUri);
                if (compressedUri != null) {
                    ImageView targetImageView = findViewById(R.id.create_imageView);
                    targetImageView.setImageURI(compressedUri);
                    // 如果需要，您还可以使用压缩后的Uri进行上传
                    upload_firebase(compressedUri);
                } else {
                    // 错误处理（例如，显示一个Toast消息）
                    Toast.makeText(CreateEventActivity.this, "Error occurred while compressing images.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private Uri resizeAndCompressImage(Uri originalUri) {
        try {
            // 1. 从URI中读取原始图片
            Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), originalUri);
            // 2. 使用Bitmap.createScaledBitmap方法缩放图片
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 300, 500, true);
            // 3. 使用Bitmap.compress方法将缩放后的位图压缩并保存到文件或输出流中
            // 创建一个新的输出文件
            File outputDir = getCacheDir(); // context method
            File outputFile = File.createTempFile("resized", ".jpg", outputDir);

            try (FileOutputStream out = new FileOutputStream(outputFile)) {
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out); // 压缩图片，90表示JPEG压缩率
            }
            // 返回新文件的Uri
            return Uri.fromFile(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // 如果出错，返回null或其他默认值
        }
    }

    public void upload_firebase(Uri imageUri) {

        // 获取Firebase存储的引用
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

// 创建一个存储路径，其中"images"是存储桶的名称，"eventID.jpg"是要上传的文件名
        String UserName = user.toString();
        StorageReference imageRef = storageRef.child(UserName + "/" + EventID);

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
                        ImageUrl = imageUrl;
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
