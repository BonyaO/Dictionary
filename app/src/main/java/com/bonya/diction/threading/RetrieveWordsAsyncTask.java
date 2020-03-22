package com.bonya.diction.threading;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bonya.diction.models.Word;
import com.bonya.diction.persistence.AppDatabase;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class RetrieveWordsAsyncTask extends AsyncTask<String, Void, ArrayList<Word>> {

    private static final String TAG = "RetrieveWordsAsyncTask";
    private AppDatabase mDatabase;
    private WeakReference<TaskDelegate> mDelegate;

    public RetrieveWordsAsyncTask(Context context, TaskDelegate delegate) {
        super();
        mDatabase = AppDatabase.getDatabase(context);
        mDelegate = new WeakReference<>(delegate);
    }

    @Override
    protected ArrayList<Word> doInBackground(String... strings) {
        return retrieveWordsAsync(strings[0]);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(ArrayList<Word> words) {
        super.onPostExecute(words);
        mDelegate.get().onWordsReceived(words);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    private ArrayList<Word> retrieveWordsAsync(String query){
        Log.d(TAG, "retrieveWordsAsync: retrieving words. This is thread: " + Thread.currentThread().getName());
        return new ArrayList<>(mDatabase.wordDataDao().getWords(query));
    }
}
