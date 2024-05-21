package askartius.mobctrlsys.ui;

import android.content.Context;
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

import java.util.Locale;

import askartius.mobctrlsys.R;
import askartius.mobctrlsys.api.EspComms;
import askartius.mobctrlsys.api.EspCommsViewModel;

public class ProcessFragment extends Fragment {
    private MaterialTextView pulseTimeDisplay;
    private MaterialTextView pauseTimeDisplay;
    private MaterialTextView voltageDisplay;
    private MaterialTextView processStateDisplay;
    private EspComms espComms;

    private int pulseTime = 0;
    private int pauseTime = 0;
    private int gapVoltage = 0;
    private String voltage = "?";
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
        voltageDisplay = view.findViewById(R.id.voltage_display);
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

            dataInputLayout.setSuffixText(getString(R.string.unit_microsecond));
            dataInput.setInputType(InputType.TYPE_CLASS_NUMBER);

            new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(R.string.pulse)
                    .setView(dialogView)
                    .setPositiveButton(R.string.set, (dialog, which) -> {
                        if (String.valueOf(dataInput.getText()).isEmpty()) {
                            Toast.makeText(getActivity(), getString(R.string.error_empty_value), Toast.LENGTH_SHORT).show();
                        } else {
                            espComms.sendParameters(Integer.parseInt(String.valueOf(dataInput.getText())), -1, -1);
                        }
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    })
                    .show();
        });

        // Set pause time
        pauseTimeDisplay.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_data_input, null);
            TextInputLayout dataInputLayout = dialogView.findViewById(R.id.data_input_layout);
            TextInputEditText dataInput = dialogView.findViewById(R.id.data_input);

            dataInputLayout.setSuffixText(getString(R.string.unit_microsecond));
            dataInput.setInputType(InputType.TYPE_CLASS_NUMBER);

            new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(R.string.pause)
                    .setView(dialogView)
                    .setPositiveButton(R.string.set, (dialog, which) -> {
                        if (String.valueOf(dataInput.getText()).isEmpty()) {
                            Toast.makeText(getActivity(), getString(R.string.error_empty_value), Toast.LENGTH_SHORT).show();
                        } else {
                            espComms.sendParameters(-1, Integer.parseInt(String.valueOf(dataInput.getText())), -1);
                        }
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    })
                    .show();
        });

        voltageDisplay.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_data_input, null);
            TextInputLayout dataInputLayout = dialogView.findViewById(R.id.data_input_layout);
            TextInputEditText dataInput = dialogView.findViewById(R.id.data_input);

            dataInputLayout.setSuffixText(getString(R.string.unit_volt));
            dataInput.setInputType(InputType.TYPE_CLASS_NUMBER);

            new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(R.string.gap_voltage)
                    .setView(dialogView)
                    .setPositiveButton(R.string.set, (dialog, which) -> {
                        if (String.valueOf(dataInput.getText()).isEmpty()) {
                            Toast.makeText(getActivity(), getString(R.string.error_empty_value), Toast.LENGTH_SHORT).show();
                        } else {
                            espComms.sendParameters(-1, -1, Integer.parseInt(String.valueOf(dataInput.getText())));
                        }
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
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
        if (voltageDisplay != null) {
            pulseTimeDisplay.setText(String.format(Locale.getDefault(), "%s " + getString(R.string.unit_microsecond), pulseTime));
            pauseTimeDisplay.setText(String.format(Locale.getDefault(), "%s " + getString(R.string.unit_microsecond), pauseTime));
            voltageDisplay.setText(String.format(Locale.getDefault(), "%s/%s " + getString(R.string.unit_volt), voltage, gapVoltage));
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

    public void updateVoltage(String voltage) {
        this.voltage = voltage;
        if (voltageDisplay != null) {
            voltageDisplay.setText(String.format(Locale.getDefault(), "%s/%s " + getString(R.string.unit_volt), voltage, gapVoltage));
        }
    }
}