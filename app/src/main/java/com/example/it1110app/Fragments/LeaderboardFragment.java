package com.example.it1110app.Fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.it1110app.Adapters.RankAdapter;
import com.example.it1110app.DbQuery;
import com.example.it1110app.MainActivity;
import com.example.it1110app.MyCompleteListener;
import com.example.it1110app.R;


public class LeaderboardFragment extends Fragment {
    private TextView totalUsersTV, myImgTextTV, myScoreTV, myRankTV;
    private RecyclerView usersView;
    private RankAdapter rankAdapter;
    private Dialog progressDialog;
    private TextView dialogText;

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Leaderboard");

        initViews(view);

        progressDialog = new Dialog(getContext());
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false); // Prevent dialog from closing when touched outside
        progressDialog.getWindow().setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        dialogText = progressDialog.findViewById(R.id.dialog_text);
        dialogText.setText("Loading...");
        progressDialog.show();

        //positioning the items in the RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        usersView.setLayoutManager(layoutManager);

        rankAdapter = new RankAdapter(DbQuery.Companion.getG_usersList());
        usersView.setAdapter(rankAdapter);

        DbQuery dbQuery = new DbQuery();
        dbQuery.getTopUsers(new MyCompleteListener() {
                                @Override
                                public void onSuccess() {
                                    rankAdapter.notifyDataSetChanged(); //refresh the adapter

                                    if (DbQuery.Companion.getMyPerformance().getScore() != 0) {
                                        if (!DbQuery.Companion.isMeOnTopList()){
                                            calculateRank();
                                        }

                                        myScoreTV.setText("Score : " + DbQuery.Companion.getMyPerformance().getScore());
                                        myRankTV.setText("Rank - " + DbQuery.Companion.getMyPerformance().getRank());

                                    }
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onFailure() {
                                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();

                                }
                            });
                totalUsersTV.setText("Total Users: " + DbQuery.Companion.getG_usersCount());
                myImgTextTV.setText(DbQuery.Companion.getMyPerformance().getName().toUpperCase().substring(0, 1));
        return view;
    }

    private void initViews(View view) {
        totalUsersTV = view.findViewById(R.id.total_users);
        myImgTextTV = view.findViewById(R.id.img_text);
        myScoreTV = view.findViewById(R.id.total_score);
        myRankTV = view.findViewById(R.id.rank);
        usersView = view.findViewById(R.id.leaderboard_recycler_view);
    }

    private void calculateRank() {
        int lowTopScore = DbQuery.Companion.getG_usersList().get(DbQuery.Companion.getG_usersList().size() - 1).getScore();
        int remaining_slots = DbQuery.Companion.getG_usersCount() - 20;

        //user's relative position among the remaining users
        int mySlot = DbQuery.Companion.getMyPerformance().getScore()*remaining_slots/lowTopScore;

        int rank;
        //If the user's score is not equal to the lowest top score
        if (lowTopScore != DbQuery.Companion.getMyPerformance().getScore()) {
            rank = DbQuery.Companion.getG_usersCount() - mySlot;
        } else {
            rank = 21;
        }

        DbQuery.Companion.getMyPerformance().setRank(rank);
    }
}