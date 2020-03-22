package com.bonya.diction.threading;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bonya.diction.models.Word;
import com.bonya.diction.persistence.AppDatabase;
import com.bonya.diction.util.Constants;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class RetrieveWordsRunnable implements Runnable {
    private static final String TAG = "RetrieveWordsRunnable";
    private WeakReference<Handler> mMainThreadHandler;
    private AppDatabase mDatabase;
    private String mQuerry;

    public RetrieveWordsRunnable(Context context, Handler mMainThreadHandler, String mQuerry) {
        this.mMainThreadHandler = new WeakReference<>(mMainThreadHandler);
        this.mQuerry = mQuerry;
        mDatabase = AppDatabase.getDatabase(context);
    }

    @Override
    public void run() {
        Log.d(TAG, "run: retrieving words. This is from thread: " + Thread.currentThread().getName());

        ArrayList<Word> words = new ArrayList<>(mDatabase.wordDataDao().getWords(mQuerry));
        Message message = null;
        if (words.size() > 0) {
            message = Message.obtain(null, Constants.WORDS_RETRIEVE_SUCCESS);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("words_retrieve", words);
            message.setData(bundle);
        } else {
            message = Message.obtain(null, Constants.WORDS_RETRIEVE_FAIL);
        }

        mMainThreadHandler.get().sendMessage(message);
    }
}
