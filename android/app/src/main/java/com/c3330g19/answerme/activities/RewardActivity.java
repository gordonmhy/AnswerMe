package com.c3330g19.answerme.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.c3330g19.answerme.reward.RewardUtils;
import com.c3330g19.answerme.R;

public class RewardActivity extends AppCompatActivity {

    private RewardUtils rewardUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        rewardUtils = new RewardUtils(getApplicationContext());

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setProgress(rewardUtils.getPoints() * 20);

        TextView textViewProgress = findViewById(R.id.text_view_progress);
        textViewProgress.setText(rewardUtils.getPoints() + "/5");

        TextView textViewChance = findViewById(R.id.textViewChances);
        textViewChance.setText("Number of Chances: " + rewardUtils.getChances());
    }
}