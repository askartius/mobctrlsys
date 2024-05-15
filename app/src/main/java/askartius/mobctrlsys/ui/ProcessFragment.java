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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import askartius.mobctrlsys.R;
import askartius.mobctrlsys.api.EspComms;
import askartius.mobctrlsys.api.EspCommsViewModel;

public class ProcessFragment extends Fragment {

    private MaterialButton applyButton;
    private TextInputEditText pulseTimeInput;
    private TextInputEditText pauseTimeInput;
    private EspComms espComms;

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

        applyButton = view.findViewById(R.id.apply_button);
        pulseTimeInput = view.findViewById(R.id.pulse_time_input);
        pauseTimeInput = view.findViewById(R.id.pause_time_input);

        applyButton.setOnClickListener(v -> {
            if (String.valueOf(pulseTimeInput.getText()).isEmpty() || String.valueOf(pauseTimeInput.getText()).isEmpty()) {
                Toast.makeText(getActivity(), "Error: empty value(s)", Toast.LENGTH_SHORT).show();
            } else {
                espComms.sendParameters(
                        Integer.parseInt(String.valueOf(pulseTimeInput.getText())),
                        Integer.parseInt(String.valueOf(pauseTimeInput.getText()))
                );
            }

            view.findFocus().clearFocus();
        });

        return view;
    }

    public void updateParameters(int pulseTime, int pauseTime) {
        if (pauseTimeInput != null) {
            pulseTimeInput.setText(String.format("%s", pulseTime));
            pauseTimeInput.setText(String.format("%s", pauseTime));
        }
    }
}