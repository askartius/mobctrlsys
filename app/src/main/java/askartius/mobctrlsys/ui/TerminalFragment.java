package askartius.mobctrlsys.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import askartius.mobctrlsys.R;
import askartius.mobctrlsys.api.EspComms;
import askartius.mobctrlsys.api.EspCommsViewModel;

public class TerminalFragment extends Fragment {
    private MaterialTextView terminalDisplay;
    private MaterialButton connectButton;
    private EspComms espComms;
    private String terminalText = "Terminal running";

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
        View view = inflater.inflate(R.layout.fragment_terminal, container, false);

        terminalDisplay = view.findViewById(R.id.terminal_display);
        connectButton = view.findViewById(R.id.connect_button);

        // Update displayed values if they have been changed before the view was created
        updateTerminalText("");

        connectButton.setEnabled(false); // The app automatically starts connecting, no need for the button at first

        terminalDisplay.setMovementMethod(new ScrollingMovementMethod());

        connectButton.setOnClickListener(v -> {
            espComms.connectToEsp(espComms.getDefaultEspIp());
            connectButton.setEnabled(false);
        });

        return view;
    }

    public void updateTerminalText(String text) {
        terminalText += '\n' + text;
        if (terminalDisplay != null) {
            terminalDisplay.setText(terminalText);
            terminalDisplay.scrollTo(0, terminalDisplay.getLineCount());
        }
    }

    public void updateConnectionState(boolean connected) {
        if (connectButton != null) {
            if (connected) {
                connectButton.setText(R.string.connected_to_machine);
            } else {
                connectButton.setText(R.string.connect_to_machine);
                connectButton.setEnabled(true);
            }
        }
    }
}