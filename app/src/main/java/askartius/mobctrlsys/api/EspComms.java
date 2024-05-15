package askartius.mobctrlsys.api;

import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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

    private TerminalFragment terminalFragment;
    private boolean processRunning;

    public EspComms(MainActivity activity) {
        this.activity = activity;
    }

    public void connectToEsp(String EspIp) {
        // Get fragments to update their data
        List<Fragment> fragments = activity.getPagerAdapter().getFragments();
        terminalFragment = (TerminalFragment) fragments.get(0);
        ProcessFragment processFragment = (ProcessFragment) fragments.get(1);
        MotionFragment motionFragment = (MotionFragment) fragments.get(2);

        new Thread(() -> {
            try {
                socket = new Socket(EspIp, 2048);

                printWriter = new PrintWriter(socket.getOutputStream(), true);
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Monitor the connection to receive data from ESP
                String data;
                while ((data = bufferedReader.readLine()) != null && data.charAt(0) == '*') {
                    switch (data.charAt(1)) {
                        case '*': // Messages
                            makeToast(data.substring(3));
                            updateTerminalText("-> " + data.substring(3));
                            break;

                        case 'A': // Process stopped
                            processRunning = false;
                            activity.runOnUiThread(() -> processFragment.updateProcessState(false));
                            updateTerminalText("-> Process stopped");
                            break;

                        case 'Z': // Process started
                            processRunning = true;
                            activity.runOnUiThread(() -> processFragment.updateProcessState(true));
                            updateTerminalText("-> Process started");
                            break;

                        case 'P': // Parameters
                            String[] parametersData = data.substring(3).split(" ");
                            activity.runOnUiThread(() -> processFragment.updateParameters(Integer.parseInt(parametersData[0]), Integer.parseInt(parametersData[1])));
                            updateTerminalText("-> Current parameters:" +
                                    "\n    - Pulse: " + parametersData[0] + " ms" +
                                    "\n    - Pause: " + parametersData[1] + " ms");
                            break;

                        case 'J': // Motion
                            String motionData = data.substring(3);
                            activity.runOnUiThread(() -> motionFragment.updatePosition(Float.parseFloat(motionData)));
                            break;
                    }
                }
            } catch (IOException e) {
                makeToast("Error connecting");
                updateTerminalText("-> Error connecting: " + e.getMessage());
                disconnectFromEsp();
            }
        }).start();
    }

    public void disconnectFromEsp() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (printWriter != null) {
                printWriter.close();
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
            makeToast("Error sending");
            updateTerminalText("-> Error sending: no connection");
        } else if (data.charAt(0) == 'A' || !processRunning) {
            new Thread(() -> printWriter.println('*' + data)).start();
        } else {
            makeToast("Error sending");
            updateTerminalText("-> Error sending: process is running");
        }
    }

    public void sendParameters(int pulseLength, int pauseLength) {
        updateTerminalText("<- Set parameters:" +
                "\n    - Pulse: " + pulseLength + " ms" +
                "\n    - Pause: " + pauseLength + " ms");
        sendData("P " + pulseLength + " " + pauseLength);
    }

    public void sendTargetPosition(float targetPosition, int speedMultiplier) {
        updateTerminalText("<- Jog to " + targetPosition);
        sendData("J " + targetPosition + " " + speedMultiplier);
    }

    public void startProcess() {
        updateTerminalText("<- Start the process");
        sendData("Z");
    }

    public void stopProcess() {
        updateTerminalText("<- Stop the process");
        sendData("A");
    }

    public void makeToast(String message) {
        activity.runOnUiThread(() -> Toast.makeText(activity, message, Toast.LENGTH_SHORT).show());
    }

    public void updateTerminalText(String text) {
        activity.runOnUiThread(() -> terminalFragment.updateTerminalText(text));
    }
}
