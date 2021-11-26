package com.learntodroid.simplealarmclock.reward;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class RewardUtils {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public RewardUtils(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public int getPoints(){
        return sharedPreferences.getInt("points", 0);
    }

    public int getChances(){
        return sharedPreferences.getInt("chances", 0);
    }

    public void incrementPoint(int points) {
        int currentPoints = getPoints();
        editor.putInt("points",  currentPoints + points);
        editor.apply();
        while (currentPoints + points >= 5) {
            incrementChance();
            editor.putInt("points",  currentPoints + points - 5);
            editor.apply();
            currentPoints -= 5;
        }
    }

    public void incrementPoint() {
        incrementPoint(1);
    }

    public void useChance() {
        editor.putInt("chances",  getChances() - 1);
        editor.apply();
    }

    private void incrementChance() {
        editor.putInt("chances",  getChances() + 1);
        editor.apply();
    }

}
