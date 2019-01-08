package edu.upc.citm.android.speakerfeedback;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class EnterRoomID extends AppCompatActivity {

    TextView enter_room_id;
    private String password_text = "";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private App app;
    private Adapter adapter;
    private RecyclerView recent_rooms_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_room);
        enter_room_id = findViewById(R.id.room_id);
        adapter = new Adapter();
        app = (App)getApplication();

        recent_rooms_view = findViewById(R.id.rooms_view);
        recent_rooms_view.setLayoutManager(new LinearLayoutManager(this));
        recent_rooms_view.setAdapter(adapter);
    }

    public void onClickEnterRoom(View view)
    {
        final String room_id = enter_room_id.getText().toString();

        if(room_id.equals(""))
        {
            Toast.makeText(this,"Enter a room id :/", Toast.LENGTH_SHORT).show();
        }
        else
        {
            db.collection("rooms").document(room_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.i("SpeakerFeedback", documentSnapshot.toString());
                    if (documentSnapshot.exists() && documentSnapshot.contains("open")) {
                        if (documentSnapshot.contains("password") && !documentSnapshot.getString("password").isEmpty()) { // Contains password
                            comparePasswordPopUp(documentSnapshot.get("password").toString());
                            App.Room room = new App.Room(documentSnapshot.getString("name"), room_id);
                            app.addRecentRoom(room);
                        }else {
                            Intent data = new Intent();
                            data.putExtra("room_id", enter_room_id.getText().toString());
                            setResult(RESULT_OK, data);
                            App.Room room = new App.Room(documentSnapshot.getString("name"), room_id);
                            app.addRecentRoom(room);
                            finish();
                        }

                    } else {

                        if (!documentSnapshot.exists()) {
                            Toast.makeText(EnterRoomID.this,
                                    "Room with ID " + "'" + room_id + ":" + " NOT EXIST", Toast.LENGTH_SHORT).show();
                        } else if (!documentSnapshot.contains("open")) {
                            Toast.makeText(EnterRoomID.this,
                                    "Room with ID " + "'" + room_id + "'" + " NOT OPEN", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EnterRoomID.this,
                            e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("SpeakerFeedback", e.getMessage());
                }
            });
        }
    }

    protected  void comparePasswordPopUp(final String password)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter password:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                password_text = input.getText().toString();
                if (password_text.equals(password))
                {
                    Toast.makeText(EnterRoomID.this, "Password correct", Toast.LENGTH_SHORT).show();

                    Intent data = new Intent();
                    data.putExtra("room_id", enter_room_id.getText().toString());
                    setResult(RESULT_OK, data);
                    finish();
                }
                else
                    Toast.makeText(EnterRoomID.this, "Password incorrect!", Toast.LENGTH_SHORT).show();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    class Adapter extends RecyclerView.Adapter<EnterRoomID.ViewHolder> {

        @NonNull
        @Override
        public EnterRoomID.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.recent_rooms_view, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull EnterRoomID.ViewHolder holder, int position) {
            App.Room room = app.recent_rooms.get(position);

            holder.room_name_text.setText(room.getName());
            holder.room_id_text.setText(room.getId());
        }

        @Override
        public int getItemCount() {
            return app.recent_rooms.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView room_name_text;
        private TextView room_id_text;

        public ViewHolder(final View itemView) {
            super(itemView);

            room_name_text = itemView.findViewById(R.id.room_name_text);
            room_id_text = itemView.findViewById(R.id.room_id_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final App.Room room = app.recent_rooms.get(getAdapterPosition());

                    db.collection("rooms").document(room.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                if (documentSnapshot.contains("open") && documentSnapshot.getBoolean("open")) {
                                    //TEST
                                    enter_room_id.setText(room.getId());
                                    onClickEnterRoom(itemView);
                                }
                                else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(EnterRoomID.this);
                                    String roomName = documentSnapshot.getString("name");
                                    builder.setTitle(String.format("The room '%s' is closed. Do you want to delete it from Recent Rooms?", roomName));
                                    builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            app.deleteRecentRoom(room);
                                            adapter.notify();
                                        }
                                    });
                                    builder.setNegativeButton("Close", null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(EnterRoomID.this);
                                builder.setTitle(String.format("The room doesn't exist. Do you want to delete it from Recent Rooms?"));
                                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        app.deleteRecentRoom(room);
                                        adapter.notify();
                                    }
                                });
                                builder.setNegativeButton("Close", null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                }
            });
        }
    }

}
