package edu.gsu.csci5338.geoquizelite;

import android.view.View;
import android.widget.Button;

public interface OnRecyclerClickListener {

    void onRecyclerViewItemClicked(Question question, Button btn, View itemView, int position);

}
