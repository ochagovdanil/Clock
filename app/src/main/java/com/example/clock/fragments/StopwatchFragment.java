package com.example.clock.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;

import com.example.clock.R;
import com.example.clock.adapters.LapsRecyclerViewAdapter;
import com.example.clock.models.Lap;
import com.example.clock.ui.ViewAnimations;

public class StopwatchFragment extends Fragment {

    private static boolean sRunning = false;
    private static long sPauseOffSet = 0;

    private Button mButtonStart, mButtonStopResume, mButtonResetLap;
    private Chronometer mChronometer;
    private LapsRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stopwatch, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mButtonResetLap = getActivity().findViewById(R.id.button_reset_lap_stopwatch);
        mButtonStart = getActivity().findViewById(R.id.button_start_stopwatch);
        mButtonStopResume = getActivity().findViewById(R.id.button_stop_resume_stopwatch);
        mChronometer = getActivity().findViewById(R.id.chronometer_stopwatch);

        playBehaviourOfButtons();
        initListOfLaps();
    }

    private void playBehaviourOfButtons() {
        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start stopwatch
                mButtonStart.setVisibility(View.GONE);
                mButtonStopResume.setVisibility(View.VISIBLE);
                mButtonStopResume.setTextColor(getResources().getColor(R.color.colorRed));
                mButtonStopResume.setText(R.string.button_stop);
                mButtonResetLap.setVisibility(View.VISIBLE);
                mButtonResetLap.setText(R.string.button_lap);
                ViewAnimations.divergeTwoViewsHorizontal(
                        getContext(),
                        mButtonStopResume,
                        mButtonResetLap);

                startStopwatch();
            }
        });
        mButtonStopResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButtonStopResume.getText().toString().equals("Resume")) {
                    // resume stopwatch
                    mButtonStopResume.setText(R.string.button_stop);
                    mButtonStopResume.setTextColor(getResources().getColor(R.color.colorRed));
                    mButtonResetLap.setText(R.string.button_lap);

                    startStopwatch();
                } else {
                    // pause stopwatch
                    mButtonStopResume.setText(R.string.button_resume);
                    mButtonStopResume.setTextColor(getResources().getColor(R.color.colorGreen));
                    mButtonResetLap.setText(R.string.button_reset);

                    pauseStopwatch();
                }
            }
        });
        mButtonResetLap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButtonResetLap.getText().toString().equals("Lap")) {
                    // add the list under TextView with animation at the first time
                    if (mAdapter.getItemCount() == 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        ViewAnimations.moveToTheTopVertical(
                                getContext(),
                                mChronometer,
                                mRecyclerView);
                    }

                    createNewLap();
                } else {
                    // reset stopwatch
                    if (mAdapter.getItemCount() > 0) {
                        ViewAnimations.fadeView(getContext(), mRecyclerView);
                    }
                    ViewAnimations.comeCloserTwoViewsHorizontal(
                            getContext(),
                            mButtonStopResume,
                            mButtonResetLap);
                    mButtonStart.setVisibility(View.VISIBLE);
                    mButtonStopResume.setVisibility(View.GONE);
                    mButtonStopResume.setTextColor(getResources().getColor(R.color.colorRed));
                    mButtonResetLap.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);

                    resetStopwatch();
                }
            }
        });
    }

    private void startStopwatch() {
        if (!sRunning) {
            mChronometer.setBase(SystemClock.elapsedRealtime() - sPauseOffSet);
            mChronometer.start();
            sRunning = true;
        }
    }

    private void pauseStopwatch() {
        if (sRunning) {
            mChronometer.stop();
            sPauseOffSet = SystemClock.elapsedRealtime() - mChronometer.getBase();
            sRunning = false;
        }
    }

    private void resetStopwatch() {
        mChronometer.stop();
        mChronometer.setBase(SystemClock.elapsedRealtime());
        sRunning = false;
        sPauseOffSet = 0;

        mAdapter.deleteAllLaps();
    }

    private void initListOfLaps() {
        mRecyclerView = getActivity().findViewById(R.id.recycler_view_stopwatch_laps);
        mAdapter = new LapsRecyclerViewAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.VERTICAL,
                true);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(
                getContext(),
                DividerItemDecoration.HORIZONTAL));
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void createNewLap() {
        mAdapter.addNewLap(
                new Lap(SystemClock.elapsedRealtime() - mChronometer.getBase()));
        if (mAdapter.getItemCount() > 0) {
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
        }
    }

}
