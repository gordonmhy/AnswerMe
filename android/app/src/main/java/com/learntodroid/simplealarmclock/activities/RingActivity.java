package com.learntodroid.simplealarmclock.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.textfield.TextInputEditText;
import com.learntodroid.simplealarmclock.R;
import com.learntodroid.simplealarmclock.reward.RewardUtils;
import com.learntodroid.simplealarmclock.service.AlarmService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RingActivity extends AppCompatActivity {
    @BindView(R.id.questionLine1)
    TextView questionLine1;
    @BindView(R.id.questionLine2)
    TextView questionLine2;
    @BindView(R.id.questionNote)
    TextView questionNote;
    @BindView(R.id.answerField)
    TextInputEditText answerField;
    @BindView(R.id.checkAnswerButton)
    Button checkAnswerButton;
    @BindView(R.id.activity_ring_dismiss)
    Button dismiss;
    @BindView(R.id.instructionText)
    TextView instructionText;
    @BindView(R.id.changeQuestionButton)
    TextView changeQuestionButton;

    final String[] validCategories = {"chin", "eng", "math", "cs"};

    public ArrayList<String> questionLines = new ArrayList<>();
    public String note = "";
    public ArrayList<String> answers = new ArrayList<>();

    private RewardUtils rewardUtils;

    private Long launchTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);

        ButterKnife.bind(this);

        launchTime = RewardUtils.launchTime == null ? System.currentTimeMillis() : RewardUtils.launchTime;
        rewardUtils = new RewardUtils(getApplicationContext());

        questionLine1.setText("Retrieving Question...");
        questionLine2.setText("");
        questionNote.setText("Loading...");
        dismiss.setEnabled(false);  // The dismiss button won't be enabled until a correct answer is received.
        checkAnswerButton.setEnabled(false);  // The check answer button won't be enabled until the question is loaded.
        changeQuestionButton.setEnabled(false);

        dismiss.setOnClickListener((event) -> {
            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
            finish();  // This ends the activity  (Closes this window)
        });

        checkAnswerButton.setOnClickListener((event) -> {
            checkAnswer();
        });

        ArrayList<String> activeCategories = new ArrayList<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        for (String cat : validCategories) {
            if (sharedPreferences.getBoolean(cat, false)) {
                activeCategories.add(cat);
            }
        }

        int randInt = new Random().nextInt(activeCategories.size() > 0 ? activeCategories.size() : validCategories.length);
        String cat = activeCategories.size() > 0 ? activeCategories.get(randInt) : validCategories[randInt];
        if (!obtainData(cat)) {
            questionLine1.setText("Unable to retrieve question.");
        }

        changeQuestionButton.setOnClickListener((view -> {
            new AlertDialog.Builder(this)
                    .setMessage("You will be using a chance to change this question to another one.")
                    .setTitle("Are you sure?")
                    .setCancelable(true)
                    .setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> {
                            }
                    )
                    .setPositiveButton("Sure", (dialog, whichButton) -> {
                        rewardUtils.useChance();
                        changeQuestionButton.setEnabled(false);
                        int newRandInt = new Random().nextInt(activeCategories.size() > 0 ? activeCategories.size() : validCategories.length);
                        String newCat = activeCategories.size() > 0 ? activeCategories.get(newRandInt) : validCategories[newRandInt];
                        if (!obtainData(newCat)) {
                            questionLine1.setText("Unable to retrieve question.");
                        } /*else {
                            launchTime = System.currentTimeMillis();  // Reset timer upon question change
                        }*/
                        Toast toast = Toast.makeText(getApplicationContext(), "Changed question. You are left with " + rewardUtils.getChances() + " chances.", Toast.LENGTH_LONG);
                        toast.show();
                    })
                    .show();
        }));

        // Testing purpose
        // rewardUtils.incrementPoint(100);
    }

    public void parseJson(String JSONString) {
        questionLines.clear();
        answers.clear();
        try {
            JSONObject rootJSONObj = new JSONObject(JSONString);
            JSONArray qL = rootJSONObj.optJSONArray("q");
            for (int i = 0; i < qL.length(); i++) {
                questionLines.add(qL.getString(i));
            }
            note = rootJSONObj.getString("note");
            JSONArray aL = rootJSONObj.optJSONArray("a");
            for (int i = 0; i < aL.length(); i++) {
                answers.add(aL.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateDisplay();
    }

    public void checkAnswer() {
        if (answers.contains(answerField.getText().toString())) {
            dismiss.setEnabled(true);
            checkAnswerButton.setEnabled(false);
            changeQuestionButton.setEnabled(false);
            questionLine1.setText("Answer correct!");
            questionLine2.setText("You can now dismiss the alarm.");
            questionNote.setText("");
            instructionText.setText("Please click the dismiss button.");

            Long currentTime = System.currentTimeMillis();
            int timeLimit = 30;  // in seconds

            if (currentTime - launchTime <= timeLimit * 1000) {  // within half a minute after viewing the question
                rewardUtils.incrementPoint();
                Toast toast = Toast.makeText(getApplicationContext(), "You earned 1 point for answering within " + timeLimit + " seconds!", Toast.LENGTH_LONG);
                toast.show();
            }
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("Please try again.")
                    .setTitle("Wrong Answer")
                    .setCancelable(true)
                    .setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> {
                            }
                    )
                    .show();
        }
    }

    public void updateDisplay() {
        questionLine1.setText(questionLines.get(0));
        questionLine2.setText("");
        if (questionLines.size() > 1) {
            questionLine2.setText(questionLines.get(1));
        }
        questionNote.setText(note);
        checkAnswerButton.setEnabled(true);
        if (rewardUtils.getChances() > 0) {
            changeQuestionButton.setEnabled(true);
        }
    }

    public boolean obtainData(final String category) {
        boolean isCategoryValid = false;
        for (String cat : validCategories) {
            if (cat.equals(category)) {
                isCategoryValid = true;
                break;
            }
        }
        if (!isCategoryValid) return false;

        // URL to obtain a random question from the REST endpoint
        final String url = "https://answermeapi.pythonanywhere.com/ques/" + category;

        AtomicBoolean success = new AtomicBoolean(true);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        final Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            final String jsonString = getJson(url);
            if (jsonString.equals("Fail to login"))
                success.set(false);
            if (success.get())
                handler.post(() -> {
                    parseJson(jsonString);
                });
        });
        return success.get();
    }

    public String ReadBufferedHTML(BufferedReader reader, char[] htmlBuffer, int bufSz) throws java.io.IOException {
        htmlBuffer[0] = '\0';
        int offset = 0;
        do {
            int cnt = reader.read(htmlBuffer, offset, bufSz - offset);
            if (cnt > 0) {
                offset += cnt;
            } else {
                break;
            }
        } while (true);
        return new String(htmlBuffer);
    }

    public String getJson(String url) {
        HttpURLConnection conn_object = null;
        final int HTML_BUFFER_SIZE = 2 * 1024 * 1024;
        char htmlBuffer[] = new char[HTML_BUFFER_SIZE];
        try {
            URL url_object = new URL(url);
            conn_object = (HttpURLConnection) url_object.openConnection();
            conn_object.setInstanceFollowRedirects(true);
            BufferedReader reader_list = new BufferedReader(new InputStreamReader(conn_object.getInputStream()));
            String HTMLSource = ReadBufferedHTML(reader_list, htmlBuffer, HTML_BUFFER_SIZE);
            reader_list.close();
            return HTMLSource;
        } catch (Exception e) {
            System.out.println("Exception caught!");
            return "Fail to login";
        } finally {
            if (conn_object != null) {
                conn_object.disconnect();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }
}