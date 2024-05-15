package askartius.mobctrlsys.ui;

import android.content.Context;
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

import java.util.Objects;

import askartius.mobctrlsys.R;
import askartius.mobctrlsys.api.EspComms;
import askartius.mobctrlsys.api.EspCommsViewModel;

public class ProcessFragment extends Fragment {
    private MaterialTextView pulseTimeDisplay;
    private MaterialTextView pauseTimeDisplay;
    private EspComms espComms;

    private int pulseTime;
    private int pauseTime;

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

        pulseTimeDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        pauseTimeDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        return view;
    }

    public void updateParameters(int pulseTime, int pauseTime) {
        this.pulseTime = pulseTime;
        this.pauseTime = pauseTime;
        if (pauseTimeDisplay != null) {
            pulseTimeDisplay.setText(String.valueOf(pulseTime));
            pauseTimeDisplay.setText(String.valueOf(pauseTime));
        }
    }
}