package askartius.mobctrlsys.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import askartius.mobctrlsys.R;
import askartius.mobctrlsys.api.EspCommunication;

public class ProcessFragment extends Fragment {
    private EspCommunication communication;

    private MaterialButton applyButton;
    private TextInputEditText pulseTimeInput;
    private TextInputEditText pauseTimeInput;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof EspCommunication) {
            communication = (EspCommunication) context;
        } else {
            Log.e("API", "No EspCommunication");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_process, container, false);

        applyButton = view.findViewById(R.id.apply_button);
        pulseTimeInput = view.findViewById(R.id.pulse_time_input);
        pauseTimeInput = view.findViewById(R.id.pause_time_input);

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (communication != null) {
                    // TODO: Add a code to check for errors in inputs
                    communication.sendData("P\n" + pulseTimeInput.getText() + "\n" + pauseTimeInput.getText() + "\r\n");
                    view.findFocus().clearFocus();
                }
            }
        });

        return view;
    }
}