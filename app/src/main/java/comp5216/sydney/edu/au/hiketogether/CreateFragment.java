package comp5216.sydney.edu.au.hiketogether;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static comp5216.sydney.edu.au.hiketogether.ViewPagerAdapter.ViewPagerViewHolder.REQUEST_CODE_PICK_IMAGE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CreateFragment extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 123; // 请求代码可以使用任何非负整数
    private View view;
    Button create_event;
    FirebaseAuth auth;
    FirebaseUser user;
    String Address;
    String EventID;
    String CreatorEmail;
    String ImageUrl;
    List<String> MemberEmail = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.create_fragment, container, false);
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
        create_event = view.findViewById(R.id.createButton);
        final ViewPager2 viewPager = view.findViewById(R.id.pager);

        create_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPage = viewPager.getCurrentItem();
                ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
                //event介绍
                Address = adapter.getUserInput(currentPage);
                //eventID
                EventID = UUID.randomUUID().toString();
                //创建者Email
                CreatorEmail = user.getEmail();
                //ImageUrl  这个是下载的URL
                uploadEvent(ImageUrl,EventID,CreatorEmail,Address,MemberEmail);
            }
        });
        return view;
    }

    public void uploadEvent(String ImageUrl, String EventID, String CreatorEmail,String Address, List<String> MemberEmail){
        CollectionReference EventInfo = db.collection("Event List");

        Map<String, Object> data1 = new HashMap<>();

        //单个用户的数据
        // TODO: 16/10/2023 Event name?
        data1.put("Creator Email",CreatorEmail);
        data1.put("Image",ImageUrl);
        data1.put("Description",Address);
        data1.put("Team size",1);
        data1.put("Member Email",MemberEmail);

        //放EventID进去
        EventInfo.document(EventID).set(data1);

        //把EventID放到对应User的数据集中去
        CollectionReference UserInfo = db.collection("User_profile");

        UserInfo.document(CreatorEmail)
                .get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // 获取文档中的数据
                        Map<String, Object> data = documentSnapshot.getData();
                        if (data != null) {
                            // 检索字符串列表
                            ArrayList<String> stringList = (ArrayList<String>) data.get("Created Events");
                            stringList.add(EventID);
                            // 将新字符串添加到列表中
                            UserInfo.document(CreatorEmail).update("Created Events",stringList)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error updating document", e);
                                        }
                                    });

                        }
                    }
                });
    }



    //viewPager滑动效果
    private void setupViewPager() {
        ViewPager2 viewPager = view.findViewById(R.id.pager);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getParentFragment() != null) {
            getParentFragment().onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri imageUri = data.getData();
                ImageView targetImageView = view.findViewById(R.id.create_imageView);
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
        return ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        String[] permissions = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        requestPermissions(permissions, PERMISSION_REQUEST_CODE);
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
                Toast.makeText(getContext(), "没有存储权限，某些功能可能受限", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
