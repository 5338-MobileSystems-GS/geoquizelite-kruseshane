package edu.gsu.csci5338.geoquizelite;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class EndGameActivity extends AppCompatActivity {

    private TextView correctView;
    private TextView incorrectView;
    private TextView cheatedView;
    private int correct;
    private int incorrect;
    private int cheated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        Intent intent = getIntent();
        correct = intent.getIntExtra("correctCount", 0);
        incorrect = intent.getIntExtra("incorrectCount", 0);
        cheated = intent.getIntExtra("cheatedCount", 0);

        correctView = findViewById(R.id.correct_text);
        incorrectView = findViewById(R.id.incorrect_text);
        cheatedView = findViewById(R.id.cheated_text);

        correctView.setText("Correct: " + String.valueOf(correct));
        incorrectView.setText("Incorrect: " + String.valueOf(incorrect));
        cheatedView.setText(("Cheated: " + String.valueOf(cheated)));
    }
}
