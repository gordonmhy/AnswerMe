package com.c3330g19.answerme.alarmslist;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.c3330g19.answerme.activities.SettingsActivity;
import com.c3330g19.answerme.data.Alarm;
import com.c3330g19.answerme.R;

import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class AlarmsListFragment extends Fragment implements OnToggleAlarmListener {
    private AlarmRecyclerViewAdapter alarmRecyclerViewAdapter;
    private AlarmsListViewModel alarmsListViewModel;
    private RecyclerView alarmsRecyclerView;
    private ImageButton addAlarm;
    private ImageButton setting;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alarmRecyclerViewAdapter = new AlarmRecyclerViewAdapter(this);
        alarmsListViewModel = ViewModelProviders.of(this).get(AlarmsListViewModel.class);
        alarmsListViewModel.getAlarmsLiveData().observe(this, new Observer<List<Alarm>>() {
            @Override
            public void onChanged(List<Alarm> alarms) {
                if (alarms != null) {
                    alarmRecyclerViewAdapter.setAlarms(alarms);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listalarms, container, false);

        alarmsRecyclerView = view.findViewById(R.id.fragment_listalarms_recyclerView);
        alarmsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        alarmsRecyclerView.setAdapter(alarmRecyclerViewAdapter);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(alarmsRecyclerView);

        addAlarm = view.findViewById(R.id.add_alarm_icon);
        addAlarm.setOnClickListener((v) ->
                Navigation.findNavController(v).navigate(R.id.action_alarmsListFragment_to_createAlarmFragment)
        );

        setting = view.findViewById(R.id.setting_icon);
        setting.setOnClickListener((v) -> {
            Intent myintent = new Intent(view.getContext(), SettingsActivity.class);
            startActivity(myintent);
        });

        return view;
    }

    @Override
    public void onToggle(Alarm alarm) {
        if (alarm.isStarted()) {
            alarm.cancelAlarm(getContext());
            alarmsListViewModel.update(alarm);
            // String toastText = String.format("Alarm cancelled.");
            // Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();
        } else {
            alarm.schedule(getContext());
            alarmsListViewModel.update(alarm);
        }
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (direction == ItemTouchHelper.LEFT) {
                Alarm alarm = AlarmRecyclerViewAdapter.holderToAlarm.get(viewHolder);
                assert alarm != null;
                alarm.cancelAlarm(requireContext());
                alarmsListViewModel.delete(alarm);
                // String toastText = String.format("Alarm deleted.");
                // Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();
            } else if (direction == ItemTouchHelper.RIGHT) {
                Alarm alarm = AlarmRecyclerViewAdapter.holderToAlarm.get(viewHolder);
                assert alarm != null;
                alarm.cancelAlarm(requireContext());
                alarmsListViewModel.delete(alarm);
                //Navigation.createNavigateOnClickListener(direction).navigation
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        alarmsRecyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                Navigation.findNavController(requireView()).navigate(R.id.action_alarmsListFragment_to_createAlarmFragment);
                                //Intent myintent = new Intent(requireContext(), CreateAlarmFragment.class);
                                //startActivity(myintent);
                            }
                        });
                    }
                }).start();

                // Toast.makeText(getContext(), String.format("Alarm editing"), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(), R.color.deletedColor))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    //.addSwipeLeftLabel("Delete")
                    .setSwipeLeftLabelColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))

                    .addSwipeRightBackgroundColor(ContextCompat.getColor(requireContext(), R.color.editedColor))
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_edit_24)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
}