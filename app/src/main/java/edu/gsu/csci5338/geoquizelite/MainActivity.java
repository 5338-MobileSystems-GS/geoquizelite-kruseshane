package edu.gsu.csci5338.geoquizelite;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //private ArrayList<Question> mArrayList = new ArrayList<>();
    private RecyclerView questionList;
    private CustomQuestionAdapter mAdapter;
    private Button mSubmitButton;
    private int questionCount;
    private boolean cheat;
    private QuestionBank qBank;
    private DBHandler db = new DBHandler(this);
    private PopupWindow popUp;

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
            db.readTable();
        } else {
            System.out.println(db.getRowsInTable() + " records exist");
            db.readTable();
        }

        mAdapter = new CustomQuestionAdapter(new OnRecyclerClickListener() {
            @Override
            public void onRecyclerViewItemClicked(Question question, Button btn, View itemView, int position) {
                Toast.makeText(getApplicationContext(), "Cheated: " + String.valueOf(cheat),Toast.LENGTH_SHORT).show();
                if (cheat) {
                    db.registerQuestionAnswered(question, 1, question.isAnswer() ? 1 : 0, 0,1, 1);
                    cheat = false;
                } else {
                    if (question.isAnswer() && btn.getText().equals(getString(R.string.true_button))) {
                        db.registerQuestionAnswered(question, 1, 1, 1,0, 0);
                    } else if (question.isAnswer() && btn.getText().equals(getString(R.string.false_button))) {
                        db.registerQuestionAnswered(question, 1, 0, 0, 1, 0);
                    } else if (!question.isAnswer() && btn.getText().equals(getString(R.string.false_button))) {
                        db.registerQuestionAnswered(question, 1, 0, 1, 0, 0);
                    } else if (!question.isAnswer() && btn.getText().equals(getString(R.string.true_button))) {
                        db.registerQuestionAnswered(question, 1, 1, 0, 1, 0);
                    }
                }

                questionCount++;
                if (questionCount == qBank.getBank().length) {
                    mSubmitButton.setEnabled(true);
                }
            }
        }, MainActivity.this, db);

        questionList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        questionList.setItemAnimator( new DefaultItemAnimator());
        questionList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        questionList.setAdapter(mAdapter);

        mSubmitButton = findViewById(R.id.submit_button);
        mSubmitButton.setEnabled(false);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] quizSummary = db.getQuizSummary();
                Intent intent = new Intent(MainActivity.this, EndGameActivity.class);
                intent.putExtra("correctCount", quizSummary[0]);
                intent.putExtra("incorrectCount", quizSummary[1]);
                intent.putExtra("cheatedCount", quizSummary[2]);
                startActivity(intent);
            }
        });

        if (db.questionsAnswered() == 10) {
            mSubmitButton.setEnabled(true);
        }

        if (db.findCurrentQuiz()) {
            popUpDisplay();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 123 && resultCode == RESULT_OK) {
            cheat = data.getBooleanExtra("cheated", false);
            System.out.println(cheat);
        }
    }

    public void popUpDisplay() {
        AlertDialog popUp = new AlertDialog.Builder(this).create();

        // Set Custom Title
        TextView title = new TextView(this);
        // Title Properties
        title.setText("Current Quiz Detected");
        title.setPadding(20, 20, 20, 20);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
        popUp.setCustomTitle(title);

        // Set Message
        TextView msg = new TextView(this);
        // Message Properties
        msg.setText("Would you like to continue with your current quiz or start over?");
        msg.setPadding(20, 20, 20, 20);
        msg.setGravity(Gravity.CENTER_HORIZONTAL);
        msg.setTextColor(Color.BLACK);
        popUp.setView(msg);

        // Set Button
        popUp.setButton(AlertDialog.BUTTON_NEUTRAL,"CONTINUE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing in this method as clicking "CONTINUE" closes dialogue box
            }
        });

        popUp.setButton(AlertDialog.BUTTON_NEGATIVE,"RESTART", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                db.reset();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        new Dialog(getApplicationContext());
        popUp.show();

        // Set Properties for OK Button
        final Button okBT = popUp.getButton(AlertDialog.BUTTON_NEUTRAL);
        LinearLayout.LayoutParams neutralBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        neutralBtnLP.gravity = Gravity.FILL_HORIZONTAL;
        okBT.setPadding(50, 10, 10, 10);   // Set Position
        okBT.setTextColor(Color.BLUE);
        okBT.setLayoutParams(neutralBtnLP);

        final Button cancelBT = popUp.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams negBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        negBtnLP.gravity = Gravity.FILL_HORIZONTAL;
        cancelBT.setTextColor(Color.RED);
        cancelBT.setLayoutParams(negBtnLP);
    }

}
