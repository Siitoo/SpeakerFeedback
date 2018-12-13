package edu.upc.citm.android.speakerfeedback;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class EnterRoomID extends AppCompatActivity {

    TextView enter_room_id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_room);
        enter_room_id = findViewById(R.id.room_id);
    }

    public void onClickEnterRoom(View view)
    {
        if(enter_room_id.getText().toString().equals(""))
        {
            Toast.makeText(this,"Enter a room id :/", Toast.LENGTH_SHORT).show();
        }
        else
        {

        }
    }
}
