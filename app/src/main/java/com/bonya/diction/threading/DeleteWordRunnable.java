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


public class DeleteWordRunnable implements Runnable {
    private static final String TAG = "RetrieveWordsRunnable";
    private WeakReference<Handler> mMainThreadHandler;
    private AppDatabase mDatabase;
    private Word word;

    public DeleteWordRunnable(Context context, Handler mMainThreadHandler, Word word) {
        this.mMainThreadHandler = new WeakReference<>(mMainThreadHandler);
        this.word = word;
        mDatabase = AppDatabase.getDatabase(context);
    }

    @Override
    public void run() {
        Log.d(TAG, "run: deleting word. This is from thread: " + Thread.currentThread().getName());
        
        ArrayList<Word> words = new ArrayList<>(mDatabase.wordDataDao().delete(word));
        Message message = null;
        if (words.size() > 0) {
            message = Message.obtain(null, Constants.WORD_DELETE_SUCCESS);
        } else {
            message = Message.obtain(null, Constants.WORD_DELETE_FAIL);
        }

        mMainThreadHandler.get().sendMessage(message);
    }
}
