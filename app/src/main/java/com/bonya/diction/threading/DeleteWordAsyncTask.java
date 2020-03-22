package com.bonya.diction.threading;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bonya.diction.models.Word;
import com.bonya.diction.persistence.AppDatabase;

import java.util.ArrayList;

public class DeleteWordAsyncTask extends AsyncTask<Word, Void, Integer> {

    private static final String TAG = "DeleteWordAsyncTask";
    private AppDatabase mDatabase;

    public DeleteWordAsyncTask(Context context) {
        mDatabase = AppDatabase.getDatabase(context);
    }

    @Override
    protected Integer doInBackground(Word... words) {
        return deleteWordsAsync(words[0]);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Integer value) {
        super.onPostExecute(value);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    private Integer deleteWordsAsync(Word word){
        Log.d(TAG, "retrieveWordsAsync: deleting word. This is thread: " + Thread.currentThread().getName());
        return mDatabase.wordDataDao().delete(word);
    }
}
