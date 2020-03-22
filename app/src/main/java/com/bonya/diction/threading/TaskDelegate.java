package com.bonya.diction.threading;

import com.bonya.diction.models.Word;

import java.util.ArrayList;

public interface TaskDelegate {

    void onWordsReceived(ArrayList<Word>  words);
}
