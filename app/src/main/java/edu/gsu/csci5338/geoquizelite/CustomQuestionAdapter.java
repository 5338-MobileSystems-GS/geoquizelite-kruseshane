package edu.gsu.csci5338.geoquizelite;

import android.content.Context;
import android.content.Intent;
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
    private boolean answered;

    // A constructor.
    public CustomQuestionAdapter(OnRecyclerClickListener listener, Context context) {
        this.listener = listener;
        this.context = context;
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
                    mFalseButton.setEnabled(false);
                    mFalseButton.setBackgroundColor(Color.GRAY);
                    mCheatButton.setBackgroundColor(Color.GRAY);
                    mCheatButton.setEnabled(false);
                    answered = true;
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
                    mTrueButton.setEnabled(false);
                    mCheatButton.setBackgroundColor(Color.GRAY);
                    mCheatButton.setEnabled(false);
                    answered = true;
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

        //holder.itemView.setBackgroundColor(Color.LTGRAY);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}
