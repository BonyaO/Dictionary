package com.bonya.diction.threading;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bonya.diction.models.Word;
import com.bonya.diction.persistence.AppDatabase;
import com.bonya.diction.util.Constants;
import com.bonya.diction.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class MyThread extends Thread {
    private static final String TAG = "MyThread";
    private MyThreadHandler mMyHandler = null;
    private Handler mMainThreadHandler = null;
    private boolean isRunning = false;
    private AppDatabase mDatabase;

    public MyThread(Context context, Handler mMainThreadHandler) {
        this.mMainThreadHandler = mMainThreadHandler;
        isRunning = true;
        mDatabase = AppDatabase.getDatabase(context);
    }

    @Override
    public void run() {
        if(isRunning){
            Looper.prepare();
            mMyHandler = new MyThreadHandler(Looper.myLooper());
            Looper.loop();
        }

    }
    public void quitThread(){
        isRunning = false;
        mMainThreadHandler = null;
    }

    public void sendMessageToBackgroundThread(Message msg){
        try{
            mMyHandler.sendMessage(msg);
            //break;
        }catch (NullPointerException e){
            Log.e(TAG, "sendMessageToBackgroundThread: NullPointer: " + e.getMessage());
            try{
                Thread.sleep(200);
            }catch (InterruptedException e1){
                e1.printStackTrace();
            }
        }

    }
    private long[] saveNewWord(Word word){
        long[] returnValue = mDatabase.wordDataDao().insertWords(word);
        if(returnValue.length > 0){
            Log.d(TAG, "saveNewWord: return value: " + returnValue.toString());
        }
        return returnValue;
    }

    private List<Word> retrieveWords(String title){
        return mDatabase.wordDataDao().getWords(title);
    }

    private int updateWord(Word word){
        return mDatabase.wordDataDao().updateWord(word.getTitle(), word.getContent(), Utility.getCurrentTimeStamp(), word.getUid());
    }

    private int deleteWord(Word word){
        return mDatabase.wordDataDao().delete(word);
    }

    private  class MyThreadHandler extends Handler{
        public MyThreadHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case Constants.WORD_INSERT_NEW: {
                    Log.d(TAG, "handleMessage: saving word on thread: " + Thread.currentThread().getName());
                    Word word = msg.getData().getParcelable("word_new");
                    Message message = null;
                    if (saveNewWord(word).length > 0) {
                        message = Message.obtain(null, Constants.WORD_INSERT_SUCCESS);
                    } else {
                        message = Message.obtain(null, Constants.WORD_INSERT_FAIL);
                    }
                    mMainThreadHandler.sendMessage(message);

                    break;
                }

                case Constants.WORD_UPDATE: {
                    Log.d(TAG, "handleMessage: updating word on thread: " + Thread.currentThread().getName());
                    Word word = msg.getData().getParcelable("word_update");
                    Message message = null;
                    int updateInt = updateWord(word);
                    if (updateInt > 0) {
                        message = Message.obtain(null, Constants.WORD_UPDATE_SUCCESS);
                    } else {
                        message = Message.obtain(null, Constants.WORD_UPDATE_FAIL);
                    }
                    mMainThreadHandler.sendMessage(message);
                    break;
                }

                case Constants.WORDS_RETRIEVE: {
                    Log.d(TAG, "handleMessage: retrieving words on thread: " + Thread.currentThread().getName());
                    String title = msg.getData().getString("title");
                    ArrayList<Word> words = new ArrayList<>(retrieveWords(title));
                    Message message = null;
                    if (words.size() > 0) {
                        message = Message.obtain(null, Constants.WORDS_RETRIEVE_SUCCESS);
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("words_retrieve", words);
                        message.setData(bundle);
                    } else {
                        message = Message.obtain(null, Constants.WORDS_RETRIEVE_FAIL);
                    }

                    mMainThreadHandler.sendMessage(message);

                    break;
                }

                case Constants.WORD_DELETE: {
                    Log.d(TAG, "handleMessage: deleting word on thread: " + Thread.currentThread().getName());
                    Word word = msg.getData().getParcelable("word_delete");
                    Message message = null;
                    if (deleteWord(word) > 0) {
                        message = Message.obtain(null, Constants.WORD_DELETE_SUCCESS);
                    } else {
                        message = Message.obtain(null, Constants.WORD_DELETE_FAIL);
                    }

                    mMainThreadHandler.sendMessage(message);

                    break;
                }
            }
        }
    }


}
