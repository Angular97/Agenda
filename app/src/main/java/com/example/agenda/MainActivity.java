package com.example.agenda;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.agenda.Adapter.ToDoAdapter;
import com.example.agenda.Model.ToDoModel;
import com.example.agenda.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnDialogCloseListener{

    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
//    private FloatingActionButton addbutton;
    private ActivityMainBinding binding;
    private ToDoAdapter adapter;
    private List<ToDoModel> list;
    private Query query;
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
//        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycleview);
//         addbutton = findViewById(R.id.addicon);
        firestore = FirebaseFirestore.getInstance();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

      binding.addicon.setOnClickListener(view -> {
         AddNewTask.newInstance().show(getSupportFragmentManager() , AddNewTask.TAG);
            Log.d("Add Button", "onClick: I am Add Button");
        });

      list = new ArrayList<>();
      adapter = new ToDoAdapter(MainActivity.this,list);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        showData();
      recyclerView.setAdapter(adapter);
    }
    private void showData(){
       query =  firestore.collection("task").orderBy("time", Query.Direction.DESCENDING);
              listenerRegistration = query .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange: value.getDocumentChanges()){
                    if(documentChange.getType()==DocumentChange.Type.ADDED){
                        String id = documentChange.getDocument().getId();
                        ToDoModel toDoModel = documentChange.getDocument().toObject(ToDoModel.class).withId(id);

                        list.add(toDoModel);
                        adapter.notifyDataSetChanged();
                    }
                }
//                Collections.reverse(list);
                listenerRegistration.remove();
            }
        });
    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface) {

        list.clear();
        showData();
        adapter.notifyDataSetChanged();
    }
}