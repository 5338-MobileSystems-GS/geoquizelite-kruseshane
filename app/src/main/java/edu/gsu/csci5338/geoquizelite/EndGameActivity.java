package edu.gsu.csci5338.geoquizelite;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class EndGameActivity extends AppCompatActivity {

    private TextView correctView;
    private TextView incorrectView;
    private TextView cheatedView;
    private int correct;
    private int incorrect;
    private int cheated;
    private Button mResetButton;
    private DBHandler db = new DBHandler(this);

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
        mResetButton = findViewById(R.id.reset_button);

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.reset();
                Intent intent = new Intent(EndGameActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
