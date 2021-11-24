package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;

    TextView TV_signup;
    TextView TV_sample; // 샘플코드
    Button BTN_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        // 상단 타이틀바 제거
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        // 회원가입 textview, 로그인 button 변수 설정
        TV_signup = (TextView) findViewById(R.id.TV_signup);
        TV_sample = (TextView) findViewById(R.id.TV_sample); // 샘플코드
        BTN_login = (Button) findViewById(R.id.BTN_login);
    }

    // 로그인 버튼 클릭
    public void btn_LoginClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
        startActivity(intent);
    }

    // 회원가입 텍스트뷰 클릭
    public void tv_SignupClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), SignUp.class);
        startActivity(intent);
    }

    // 샘플 코드
    public void btn_SampleClicked(View v) {
        //getData("user", "asdf", 1);
        getAllData("user", 1);
    }

    // Firestore에 데이터 추가
    public void putData(String collec, String doc, String key, Object value) {
        CollectionReference collecRef = db.collection(collec);
        Map<String, Object> data1 = new HashMap<>();

        data1.put(key, value);
        collecRef.document(doc).set(data1);
        Log.d("데이터 추가", collec + ": "+ key + " => " + value);
    }

    // Firestore에서 데이터 가져오기
    public void getData(String collec, String doc, int what) {
        DocumentReference docRef = db.collection(collec).document(doc);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("데이터 가져오기", "DocumentSnapshot data: " + document.getData());
                        useData(document.getData(), what);
                    } else {
                        Log.d("데이터 가져오기", "No such document");
                    }
                } else {
                    Log.d("데이터 가져오기", "get failed with ", task.getException());
                }
            }
        });
    }

    // Firestore에서 데이터 모두가져오기
    public void getAllData(String collec, int what) {
        ArrayList<Map<String, Object>> arr = new ArrayList<>();
        db.collection(collec)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("데이터 모두 읽기", document.getId() + " => " + document.getData());
                                arr.add(document.getData());
                            }
                            useData(arr, what);
                        } else {
                            Log.d("데이터 모두 읽기", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    // Firestore에서 가져온 데이터 사용
    public void useData(Map<String, Object> data, int what) {
        switch (what) {
            case 1:
                TV_sample.setText(data.toString()); // 샘플코드
        }
    }

    // Firestore에서 가져온 모든 데이터 사용
    public void useData(ArrayList<Map<String, Object>> data, int what) {
        switch (what) {
            case 1:
                TV_sample.setText(data.toString()); // 샘플코드
        }
    }

    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    TV_signup.setText(msg.obj.toString());
                    break;
            }
        }
    };
}

class ClientThread extends Thread {

    private Handler mMainHandler;
    FirebaseFirestore db;
    Map<String,Object> data;

    public ClientThread(Handler mainHandler) {
        db = FirebaseFirestore.getInstance();
        mMainHandler = mainHandler;
    }

    public void getData(String collec, String doc) {
        DocumentReference docRef = db.collection(collec).document(doc);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("데이터 추가", documentSnapshot.getData().toString());
                data = documentSnapshot.getData();
                doPrintln();
            }
        });
    }

    public void doPrintln() {
        Message msg = Message.obtain();
        msg.what = 1;
        msg.obj = data.toString() + "\n";
        mMainHandler.sendMessage(msg);
    }
}