package edu.gsu.csci5338.geoquizelite;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //private ArrayList<Question> mArrayList = new ArrayList<>();
    private RecyclerView questionList;
    private CustomQuestionAdapter mAdapter;
    private Button mSubmitButton;
    private int correct;
    private int incorrect;
    private int cheated;
    private int questionCount;
    private boolean cheat;
    private QuestionBank qBank;
    private DBHandler db = new DBHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        questionList = findViewById(R.id.question_list);

        qBank = new QuestionBank();
        qBank.populateBank();

        // Determine if table for quiz exists
        if (db.doesTableExist() && db.getRowsInTable() != qBank.getBank().length) {
            db.fillTable();
            System.out.println("Just created " + db.getRowsInTable() + " records");
        } else {
            System.out.println(db.getRowsInTable() + " records exist");
        }

        mAdapter = new CustomQuestionAdapter(new OnRecyclerClickListener() {
            @Override
            public void onRecyclerViewItemClicked(Question question, Button btn, View itemView, int position) {
                Toast.makeText(getApplicationContext(), "Cheated: " + String.valueOf(cheat),Toast.LENGTH_SHORT).show();
                if (cheat) {
                    cheated++;
                    cheat = false;
                } else {
                    if (question.isAnswer() && btn.getText().equals(getString(R.string.true_button))) {
                        //Toast.makeText(getApplicationContext(), "Correct",Toast.LENGTH_SHORT).show();
                        correct ++;
                    } else if (question.isAnswer() && btn.getText().equals(getString(R.string.false_button))) {
                        //Toast.makeText(getApplicationContext(), "Incorrect",Toast.LENGTH_SHORT).show();
                        incorrect ++;
                    } else if (!question.isAnswer() && btn.getText().equals(getString(R.string.false_button))) {
                        //Toast.makeText(getApplicationContext(), "Correct",Toast.LENGTH_SHORT).show();
                        correct ++;
                    } else if (!question.isAnswer() && btn.getText().equals(getString(R.string.true_button))) {
                        //Toast.makeText(getApplicationContext(), "Incorrect",Toast.LENGTH_SHORT).show();
                        incorrect ++;
                    }
                }

                questionCount++;
                if (questionCount == qBank.getBank().length) {
                    mSubmitButton.setEnabled(true);
                }
            }
        }, MainActivity.this);

        questionList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        questionList.setItemAnimator( new DefaultItemAnimator());
        questionList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        questionList.setAdapter(mAdapter);

        mSubmitButton = findViewById(R.id.submit_button);
        mSubmitButton.setEnabled(false);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EndGameActivity.class);
                intent.putExtra("correctCount", correct);
                intent.putExtra("incorrectCount", incorrect);
                intent.putExtra("cheatedCount", cheated);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 123 && resultCode == RESULT_OK) {
            cheat = data.getBooleanExtra("cheated", false);
            System.out.println(cheat);
        }
    }

}
