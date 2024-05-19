package askartius.mobctrlsys.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import askartius.mobctrlsys.R;
import askartius.mobctrlsys.api.EspComms;
import askartius.mobctrlsys.api.EspCommsViewModel;

public class ProcessFragment extends Fragment {
    private MaterialTextView pulseTimeDisplay;
    private MaterialTextView pauseTimeDisplay;
    private MaterialTextView processStateDisplay;
    private EspComms espComms;

    private int pulseTime = 1;
    private int pauseTime = 1;
    private boolean processRunning = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Get espComms from ViewModel
        EspCommsViewModel viewModel = new ViewModelProvider(requireActivity()).get(EspCommsViewModel.class);
        espComms = viewModel.getSelectedEspComms().getValue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_process, container, false);

        pulseTimeDisplay = view.findViewById(R.id.pulse_time_display);
        pauseTimeDisplay = view.findViewById(R.id.pause_time_display);
        processStateDisplay = view.findViewById(R.id.process_state_display);
        MaterialButton startProcessButton = view.findViewById(R.id.start_process_button);
        MaterialButton stopProcessButton = view.findViewById(R.id.stop_process_button);

        // Update displayed values if they have been changed before the view was created
        updateProcessState(processRunning);
        updateParameters(pulseTime, pauseTime);

        // Set pulse time
        pulseTimeDisplay.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_data_input, null);
            TextInputLayout dataInputLayout = dialogView.findViewById(R.id.data_input_layout);
            TextInputEditText dataInput = dialogView.findViewById(R.id.data_input);

            dataInputLayout.setSuffixText("ms");

            new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle("Pulse time")
                    .setView(dialogView)
                    .setPositiveButton("Set", (dialog, which) -> {
                        if (String.valueOf(dataInput.getText()).isEmpty()) {
                            Toast.makeText(getActivity(), "Error: empty value", Toast.LENGTH_SHORT).show();
                        } else {
                            pulseTimeDisplay.setText(String.valueOf(Integer.parseInt(String.valueOf(dataInput.getText()))).concat(" ms"));
                            pulseTime = Integer.parseInt(String.valueOf(dataInput.getText()));
                            espComms.sendParameters(pulseTime, pauseTime);
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                    })
                    .show();
        });

        // Set pause time
        pauseTimeDisplay.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_data_input, null);
            TextInputLayout dataInputLayout = dialogView.findViewById(R.id.data_input_layout);
            TextInputEditText dataInput = dialogView.findViewById(R.id.data_input);

            dataInputLayout.setSuffixText("ms");

            new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle("Pause time")
                    .setView(dialogView)
                    .setPositiveButton("Set", (dialog, which) -> {
                        if (String.valueOf(dataInput.getText()).isEmpty()) {
                            Toast.makeText(getActivity(), "Error: empty value", Toast.LENGTH_SHORT).show();
                        } else {
                            pauseTimeDisplay.setText(String.valueOf(Integer.parseInt(String.valueOf(dataInput.getText()))).concat(" ms"));
                            pauseTime = Integer.parseInt(String.valueOf(dataInput.getText()));
                            espComms.sendParameters(pulseTime, pauseTime);
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                    })
                    .show();
        });

        startProcessButton.setOnClickListener(v -> espComms.startProcess());

        stopProcessButton.setOnClickListener(v -> espComms.stopProcess());

        return view;
    }

    public void updateParameters(int pulseTime, int pauseTime) {
        this.pulseTime = pulseTime;
        this.pauseTime = pauseTime;
        if (pauseTimeDisplay != null) {
            pulseTimeDisplay.setText(String.valueOf(pulseTime).concat(" ms"));
            pauseTimeDisplay.setText(String.valueOf(pauseTime).concat(" ms"));
        }
    }

    public void updateProcessState(boolean processRunning) {
        this.processRunning = processRunning;
        if (processStateDisplay != null) {
            if (processRunning) {
                processStateDisplay.setText(R.string.running);
                processStateDisplay.setTextColor(Color.GREEN);
            } else {
                processStateDisplay.setText(R.string.stopped);
                processStateDisplay.setTextColor(Color.RED);
            }
        }
    }
}