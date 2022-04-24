package com.example.agenda.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class TaskId {
    @Exclude                  // it means what manupulation we are doing not affected thee id again & again
    public String TaskId;
    public  <T extends  TaskId> T withId(@NonNull final String id){
        this.TaskId = id;
        return (T) this;
    }
}
