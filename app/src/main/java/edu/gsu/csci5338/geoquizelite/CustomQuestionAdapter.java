package edu.gsu.csci5338.geoquizelite;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;

public class CustomQuestionAdapter extends RecyclerView.Adapter<CustomQuestionAdapter.MyViewHolder> {

    // declaring some fields.
    private ArrayList<Question> questions;
    private QuestionBank qBank;
    private OnRecyclerClickListener listener;
    private Context context;
    private DBHandler dbh;
    private SQLiteDatabase db;
    private Cursor cursor;

    // A constructor.
    public CustomQuestionAdapter(OnRecyclerClickListener listener, Context context, DBHandler dbh) {
        this.listener = listener;
        this.context = context;
        this.dbh = dbh;
        qBank = new QuestionBank();
        questions = new ArrayList<>();
        qBank.populateBank();
        this.questions = new ArrayList<>(Arrays.asList(qBank.getBank()));
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        Button mTrueButton, mFalseButton, mCheatButton;
        TextView questionText;

        public MyViewHolder(final View itemView) {
            super(itemView);
            Log.v("ViewHolder","in View Holder");
            mTrueButton = itemView.findViewById(R.id.true_button);
            mFalseButton = itemView.findViewById(R.id.false_button);
            mCheatButton = itemView.findViewById(R.id.cheat_button);
            questionText = itemView.findViewById(R.id.question_text);

            mTrueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRecyclerViewItemClicked(questions.get(getAdapterPosition()),
                            mTrueButton,
                            itemView,
                            getAdapterPosition());
                    mFalseButton.setBackgroundColor(Color.GRAY);
                    mCheatButton.setBackgroundColor(Color.GRAY);
                    mFalseButton.setEnabled(false);
                    mCheatButton.setEnabled(false);
                }
            });

            mFalseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRecyclerViewItemClicked(questions.get(getAdapterPosition()),
                            mFalseButton,
                            itemView,
                            getAdapterPosition());
                    mTrueButton.setBackgroundColor(Color.GRAY);
                    mCheatButton.setBackgroundColor(Color.GRAY);
                    mTrueButton.setEnabled(false);
                    mCheatButton.setEnabled(false);
                }
            });

            mCheatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CheatActivity.class);
                    intent.putExtra("currentQuestion", questions.get(getAdapterPosition()).getQuestion());
                    intent.putExtra("answer", questions.get(getAdapterPosition()).isAnswer());
                    ((MainActivity) context).startActivityForResult(intent, 123);
                }
            });
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.v("CreateViewHolder", "in onCreateViewHolder");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_list_layout,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int  position) {
        Log.v("BindViewHolder", "in onBindViewHolder");
        Question question = questions.get(position);
        holder.questionText.setText(question.getQuestion());

        holder.mTrueButton.setBackgroundColor(Color.parseColor("#ff669900"));
        holder.mFalseButton.setBackgroundColor(Color.parseColor("#ffff4444"));
        holder.mCheatButton.setBackgroundColor(Color.parseColor("#ffffbb33"));
        holder.mTrueButton.setEnabled(true);
        holder.mFalseButton.setEnabled(true);
        holder.mCheatButton.setEnabled(true);

        // Determine the bit for the
        String bitQuery = "Select ANSWERED from questions where question = '" + question.getQuestion() +"'";
        db = dbh.getReadableDatabase();
        cursor = db.rawQuery(bitQuery, null);
        cursor.moveToFirst();
        int bit = cursor.getInt(0);

        // Determine user answer
        String userAnswerQuery = "Select USER_ANSWER from questions where question = '" + question.getQuestion() +"'";
        cursor = db.rawQuery(userAnswerQuery, null);
        cursor.moveToFirst();
        int userAnswer = cursor.getInt(0);

        // bit == 0 means question has not been answered
        if (bit == 1) {
            holder.mTrueButton.setEnabled(false);
            holder.mFalseButton.setEnabled(false);
            holder.mCheatButton.setEnabled(false);
            if (userAnswer == 1) {
                holder.mTrueButton.setBackgroundColor(Color.parseColor("#ff669900"));
                holder.mFalseButton.setBackgroundColor(Color.GRAY);
                holder.mCheatButton.setBackgroundColor(Color.GRAY);
            } else if (userAnswer == 0) {
                holder.mFalseButton.setBackgroundColor(Color.parseColor("#ffff4444"));
                holder.mTrueButton.setBackgroundColor(Color.GRAY);
                holder.mCheatButton.setBackgroundColor(Color.GRAY);
            }
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}
