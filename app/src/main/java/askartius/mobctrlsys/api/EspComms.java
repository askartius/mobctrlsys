package askartius.mobctrlsys.api;

import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import askartius.mobctrlsys.MainActivity;
import askartius.mobctrlsys.ui.MotionFragment;
import askartius.mobctrlsys.ui.ProcessFragment;
import askartius.mobctrlsys.ui.TerminalFragment;

/*
    All codes start with '*' so the Arduino will know that they are commands

    Code names:
        W - WiFi:
            0 - connecting
            1 - connected
        C - client:
            0 - disconnected
            1 - connected
        A - stop the process
        Z - run the process
        P - parameters:
            0 - receive
            1 - send
            2 - applied
        J - jog to
        * - message

    Author: Askar Idrisov
*/

public class EspComms {
    private final MainActivity activity;
    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    private String terminalData = "";
    private TerminalFragment terminalFragment;

    public EspComms(MainActivity activity) {
        this.activity = activity;
    }

    public void connectToEsp(String EspIp, int EspPort) {
        // Get fragments to update their data
        List<Fragment> fragments = activity.getPagerAdapter().getFragments();
        terminalFragment = (TerminalFragment) fragments.get(0);
        ProcessFragment processFragment = (ProcessFragment) fragments.get(1);
        MotionFragment motionFragment = (MotionFragment) fragments.get(2);

        new Thread(() -> {
            try {
                socket = new Socket(EspIp, EspPort);

                printWriter = new PrintWriter(socket.getOutputStream(), true);
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Monitor the connection to receive data from ESP
                String data;
                while ((data = bufferedReader.readLine()) != null && data.charAt(0) == '*') {
                    switch (data.charAt(1)) {
                        case '*': // Messages
                            makeToast(data.substring(3));
                            updateTerminal("-> " + data.substring(3));
                            break;

                        case 'P': // Parameters
                            String[] parametersData = data.substring(3).split(" ");
                            activity.runOnUiThread(() -> processFragment.updateParameters(Integer.parseInt(parametersData[0]), Integer.parseInt(parametersData[1])));
                            updateTerminal(String.format("-> Current parameters:\n    - Pulse: %s ms\n    - Pause: %s ms", parametersData[0], parametersData[1]));
                            break;

                        case 'J': // Motion
                            String motionData = data.substring(3);
                            activity.runOnUiThread(() -> motionFragment.updatePosition(Float.parseFloat(motionData)));
                            break;
                    }
                }
            } catch (IOException e) {
                makeToast("Error connecting: " + e.getMessage());
            }
        }).start();
    }

    public void disconnectFromEsp() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        } catch (IOException e) {
            Log.e("SOCKET", "Error closing socket: " + e.getMessage());
        }
    }

    public void sendData(String data) {
        if (printWriter == null) {
            makeToast("Error sending data");
            updateTerminal("-> Error: no connection available!");
        } else {
            new Thread(() -> {
                printWriter.println('*' + data);
            }).start();
        }
    }

    public void sendParameters(int pulseLength, int pauseLength) {
        updateTerminal(String.format("<- Set parameters:\n    - Pulse: %s ms\n    - Pause: %s ms", pulseLength, pauseLength));
        sendData("P " + pulseLength + " " + pauseLength);
    }

    public void sendTargetPosition(float targetPosition, int speedMultiplier) {
        updateTerminal(String.format("<- Jog to %s", targetPosition));
        sendData("J " + targetPosition + " " + speedMultiplier);
    }

    public void makeToast(String message) {
        activity.runOnUiThread(() -> Toast.makeText(activity, message, Toast.LENGTH_SHORT).show());
    }

    public void updateTerminal(String data) {
        terminalData += '\n' + data;

        activity.runOnUiThread(() -> terminalFragment.updateTerminal(terminalData));
    }
}
