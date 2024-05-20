package askartius.mobctrlsys.ui;

import static askartius.mobctrlsys.R.color.colorStart;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
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
    private MaterialTextView gapVoltageDisplay;
    private MaterialTextView processStateDisplay;
    private EspComms espComms;

    private int pulseTime = 0;
    private int pauseTime = 0;
    private int gapVoltage = 0;
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
        gapVoltageDisplay = view.findViewById(R.id.gap_voltage_display);
        processStateDisplay = view.findViewById(R.id.process_state_display);
        MaterialButton startProcessButton = view.findViewById(R.id.start_process_button);
        MaterialButton stopProcessButton = view.findViewById(R.id.stop_process_button);

        // Update displayed values if they have been changed before the view was created
        updateProcessState(processRunning);
        updateParameters(pulseTime, pauseTime, gapVoltage);

        // Set pulse time
        pulseTimeDisplay.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_data_input, null);
            TextInputLayout dataInputLayout = dialogView.findViewById(R.id.data_input_layout);
            TextInputEditText dataInput = dialogView.findViewById(R.id.data_input);

            dataInputLayout.setSuffixText("μs");
            dataInput.setInputType(InputType.TYPE_CLASS_NUMBER);

            new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle("Pulse time")
                    .setView(dialogView)
                    .setPositiveButton("Set", (dialog, which) -> {
                        if (String.valueOf(dataInput.getText()).isEmpty()) {
                            Toast.makeText(getActivity(), "Error: empty value", Toast.LENGTH_SHORT).show();
                        } else {
                            espComms.sendParameters(Integer.parseInt(String.valueOf(dataInput.getText())), -1, -1);
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

            dataInputLayout.setSuffixText("μs");
            dataInput.setInputType(InputType.TYPE_CLASS_NUMBER);

            new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle("Pause time")
                    .setView(dialogView)
                    .setPositiveButton("Set", (dialog, which) -> {
                        if (String.valueOf(dataInput.getText()).isEmpty()) {
                            Toast.makeText(getActivity(), "Error: empty value", Toast.LENGTH_SHORT).show();
                        } else {
                            espComms.sendParameters(-1, Integer.parseInt(String.valueOf(dataInput.getText())), -1);
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                    })
                    .show();
        });

        gapVoltageDisplay.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_data_input, null);
            TextInputLayout dataInputLayout = dialogView.findViewById(R.id.data_input_layout);
            TextInputEditText dataInput = dialogView.findViewById(R.id.data_input);

            dataInputLayout.setSuffixText("V");
            dataInput.setInputType(InputType.TYPE_CLASS_NUMBER);

            new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle("Gap voltage")
                    .setView(dialogView)
                    .setPositiveButton("Set", (dialog, which) -> {
                        if (String.valueOf(dataInput.getText()).isEmpty()) {
                            Toast.makeText(getActivity(), "Error: empty value", Toast.LENGTH_SHORT).show();
                        } else {
                            espComms.sendParameters(-1, -1, Integer.parseInt(String.valueOf(dataInput.getText())));
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

    public void updateParameters(int pulseTime, int pauseTime, int gapVoltage) {
        this.pulseTime = pulseTime;
        this.pauseTime = pauseTime;
        this.gapVoltage = gapVoltage;
        if (pauseTimeDisplay != null) {
            pulseTimeDisplay.setText(String.valueOf(pulseTime).concat(" μs"));
            pauseTimeDisplay.setText(String.valueOf(pauseTime).concat(" μs"));
            gapVoltageDisplay.setText(String.valueOf(gapVoltage).concat(" V"));
        }
    }

    public void updateProcessState(boolean processRunning) {
        this.processRunning = processRunning;
        if (processStateDisplay != null) {
            if (processRunning) {
                processStateDisplay.setText(R.string.running);
                processStateDisplay.setTextColor(getResources().getColor(R.color.colorStart, null));
            } else {
                processStateDisplay.setText(R.string.stopped);
                processStateDisplay.setTextColor(getResources().getColor(R.color.colorStop, null));
            }
        }
    }
}