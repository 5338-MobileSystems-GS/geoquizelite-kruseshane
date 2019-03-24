package edu.gsu.csci5338.geoquizelite;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private boolean cheat;
    private Button mCheatButton;
    private TextView answerView;
    private boolean answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        Intent intent = getIntent();
        answer = intent.getBooleanExtra("answer", false);

        mCheatButton = findViewById(R.id.show_answer_button);
        answerView = findViewById(R.id.answer_text_view);

        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerView.setText(String.valueOf(answer));
                cheat = true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("cheated", cheat);
        setResult(RESULT_OK, i);
        finish();
    }
}
