package com.example.letschat;

import android.app.Activity;
import android.content.Intent;
import android.content.UriMatcher;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.example.letschat.model.UserModel;
import com.example.letschat.utils.AndroidUtil;
import com.example.letschat.utils.FireBaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class ProfileFragment extends Fragment {


        ImageView profilepic;
        EditText usernameInput;
        EditText phoneInput;
        Button updateProfilebtn;
        ProgressBar progressBar;
        TextView logoutbtn;

        UserModel currentUserModel;

        ActivityResultLauncher<Intent> imagePickLuncher;
        Uri SelectedImageUri;


    public ProfileFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLuncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result ->{
                    if (result.getResultCode()==Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!= null && data.getData() != null){
                            SelectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(getContext(),SelectedImageUri,profilepic);
                        }
                    }
                }
                );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        profilepic = view.findViewById(R.id.profle_pic_image_view);
        usernameInput = view.findViewById(R.id.profile_username);
        phoneInput = view.findViewById(R.id.prfile_phone);
        updateProfilebtn = view.findViewById(R.id.profile_update_btn);
        progressBar = view.findViewById(R.id.profile_progress_bar);
        logoutbtn = view.findViewById(R.id.Log_out);

        getUserData();


        updateProfilebtn.setOnClickListener((v->{
            upadteBtnClick();
        }));
        logoutbtn.setOnClickListener((v -> {
            FireBaseUtil.logout();
            Intent intent = new Intent(getContext(),SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }));

        profilepic.setOnClickListener((v)->{
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLuncher.launch(intent);
                            return null;
                        }
                    });
        });



        return view;
    }
    void upadteBtnClick(){
        String newusername = usernameInput.getText().toString();
        if(newusername.isEmpty()||newusername.length()<3){
            usernameInput.setError("Username Incorrect");
            return;
        }
        currentUserModel.setUsername(newusername);
        setInProgress(true);
        if(SelectedImageUri!=null){
            FireBaseUtil.getCurrentProfilepicStorageRef().putFile(SelectedImageUri)
                    .addOnCompleteListener(task -> {
                        upatetoFirestore();
                    });
        }else {
                upatetoFirestore();
    }



        upatetoFirestore();
    }

    void upatetoFirestore(){
        FireBaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if(task.isSuccessful()){
                        AndroidUtil.showToast(getContext(),"Updated successful");
                    }else {
                        AndroidUtil.showToast(getContext(),"Updated failed");
                    }
                });

    }
    void getUserData(){
        setInProgress(true);

        FireBaseUtil.getCurrentProfilepicStorageRef().getDownloadUrl()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Uri uri = task.getResult();
                                AndroidUtil.setProfilePic(getContext(),uri,profilepic);
                            }
                        });
        FireBaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            currentUserModel = task.getResult().toObject(UserModel.class);
            usernameInput.setText(currentUserModel.getUsername());
            phoneInput.setText(currentUserModel.getPhone());
        });
    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            updateProfilebtn.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            updateProfilebtn.setVisibility(View.VISIBLE);
        }
    }
}