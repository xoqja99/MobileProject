package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class BoardPlatformPSActivity extends AppCompatActivity {
    FirebaseFirestore db;
    Button firstButton;
    ScrollView scrollView;
    LinearLayout layout_ps;
    String[] splitID;
    Map<String, Object> target;
    EditText et_search;
    Button btn_search;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.board_platform_ps4ps5);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        firstButton = (Button) findViewById(R.id.btn_samplereview_ps4ps5);
        layout_ps = (LinearLayout) findViewById(R.id.layout_ps);
        et_search = (EditText) findViewById(R.id.et_search);
        btn_search = (Button) findViewById(R.id.btn_search);
        getAllData("playstation");
        //상단 바 제거
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        // 스피너
        String[] array = {"제목","작성자","게임명"};
        Spinner SPN_ps = (Spinner) findViewById(R.id.spn_ps4ps5);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        SPN_ps.setAdapter(adapter);

        firstButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String comment = firstButton.getText().toString();
                if (comment.equals("업로드된 글이 없습니다.")){
                    Toast.makeText(getApplicationContext(), "글을 등록해주세요!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), BoardPlatformPSActivity.class);
                    startActivity(intent);
                } else if(comment.equals("게임이름 | 작성자 | 제목")){

                } else {
                    Intent intent = new Intent(getApplicationContext(), BoardPlatformReviewActivity.class);
                    startActivity(intent);

                }
            }
        });

    }

    // Firestore에서 데이터 모두가져오기
    public void getAllData(String collec) {
        ArrayList<Map<String, Object>> arr = new ArrayList<>();
        db.collection(collec).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("데이터 모두 읽기", document.getId() + " => " + document.getData());
                        arr.add(document.getData());
                    }
                    useData(arr);
                } else {
                    Log.d("데이터 모두 읽기", "Error getting documents: ", task.getException());
                }
            }
        });
    }
    // Firestore에서 가져온 모든 데이터 사용
    public void useData(ArrayList<Map<String, Object>> data) {
        Button btn;
        String buttonText =""; // 버튼에 기록될 문자열
        firstButton.setText("게임이름 | 작성자 | 제목");//제목출력
        for (int i = 0; i < data.size();i++){
            buttonText ="";
            btn = new Button(this);
            target = data.get(i);
            Map<String,Object> tmp = data.get(i);
            splitID = target.toString().split(",");
            for (int k = 0; k < splitID.length; k++){// 괄호제거
                if (k == 0){
                    splitID[k] = splitID[k].replace("{","");
                } else if (k == splitID.length-1){
                    splitID[k] = splitID[k].replace("}","");
                }
            }
            for (int j = 0; j< splitID.length; j++){//제목출력
                String str1 = splitID[j].trim();
                String space = "  |  ";
                if (str1.contains("rating")){

                }else if (str1.contains("gametitle")){
                    str1 = str1.replace("gametitle=", "");
                    buttonText += str1;
                    if (j != splitID.length-1){
                        buttonText += space;
                    }
                }else if (str1.contains("writer")){
                    str1 = str1.replace("writer=", "");
                    buttonText += str1;
                    if (j != splitID.length-1){
                        buttonText += space;
                    }
                } else if (str1.contains("attribute")){
                    j += 7;
                } else if (str1.contains("recommend")){

                } else {
                    str1 = str1.replace("title=","");
                    buttonText += str1;
                    if (j != splitID.length-1){
                        buttonText += space;
                    }
                }
            }
            btn.setText(buttonText);//제목출력
            // 버튼에 배경 색 추가
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = 10;
            btn.setLayoutParams(params);
            btn.setBackgroundColor(0xFF60CBF6);
            btn.setTextColor(0xFFFFFFFF);

            btn.setOnClickListener(new View.OnClickListener(){//새로 생성된 버튼 클릭시
                public void onClick(View v){
                    putDatas(tmp.toString());
                    Intent intent = new Intent(getApplicationContext(), BoardPlatformReviewActivity.class);
                    startActivity(intent);
                }
            });
            layout_ps.addView(btn);
        }
    }

    protected void putDatas(String string) { //누른 버튼의 데이터를 저장

        SharedPreferences.Editor editor;
        SharedPreferences memberPref = getSharedPreferences("Info_Pref", 0);
        editor = memberPref.edit();
        editor.clear();
        editor.putString("playstation", string);
        editor.apply();
    }

}