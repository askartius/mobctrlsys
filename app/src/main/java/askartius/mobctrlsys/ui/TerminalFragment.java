package askartius.mobctrlsys.ui;

import android.content.Context;
import android.net.InetAddresses;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
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

        terminalDisplay.setMovementMethod(new ScrollingMovementMethod());

        connectButton.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_data_input, null);
            TextInputEditText dataInput = dialogView.findViewById(R.id.data_input);

            dataInput.setInputType(InputType.TYPE_CLASS_TEXT);

            new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(R.string.ip_address)
                    .setView(dialogView)
                    .setPositiveButton(R.string.connect, (dialog, which) -> {
                        String value = String.valueOf(dataInput.getText());
                        if (value.isEmpty() || !InetAddresses.isNumericAddress(value)) {
                            Toast.makeText(getActivity(), getString(R.string.error_wrong_value), Toast.LENGTH_SHORT).show();
                        } else {
                            espComms.connectToEsp(value);
                            connectButton.setEnabled(false);
                        }
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    })
                    .show();
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