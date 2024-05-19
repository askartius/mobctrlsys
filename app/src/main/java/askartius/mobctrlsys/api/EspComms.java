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
        P - parameters
        J - jog to
        R - reset position
        * - message
        # - replica of a message to Serial Monitor (no need to process it)

    Author: Askar Idrisov
*/

public class EspComms {
    private final MainActivity activity;
    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    private TerminalFragment terminalFragment;
    private ProcessFragment processFragment;
    private MotionFragment motionFragment;
    private boolean processRunning;

    public EspComms(MainActivity activity) {
        this.activity = activity;
    }

    public void connectToEsp(String EspIp) {
        // Get fragments to update their data
        List<Fragment> fragments = activity.getPagerAdapter().getFragments();
        terminalFragment = (TerminalFragment) fragments.get(0);
        processFragment = (ProcessFragment) fragments.get(1);
        motionFragment = (MotionFragment) fragments.get(2);

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
                            data = data.substring(3);
                            int pulseTime = Integer.parseInt(data.substring(0, data.indexOf(' ')));
                            int pauseTime = Integer.parseInt(data.substring(data.indexOf(' ') + 1));
                            activity.runOnUiThread(() -> processFragment.updateParameters(pulseTime, pauseTime));
                            updateTerminalText("-> Current parameters:" +
                                    "\n    - Pulse: " + pulseTime + " ms" +
                                    "\n    - Pause: " + pauseTime + " ms");
                            break;

                        case 'J': // Motion
                            float zPosition = Float.parseFloat(data.substring(3)) / 1000;
                            activity.runOnUiThread(() -> motionFragment.updatePosition(zPosition));
                            break;

                        case 'R':
                            updateTerminalText("-> Position reset");
                            activity.runOnUiThread(() -> motionFragment.updatePosition(0.0F));
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
        } else if (processRunning && data.charAt(0) != 'A') {
            makeToast("Error sending");
            updateTerminalText("-> Error sending: process is running");
        } else {
            new Thread(() -> {
                printWriter.print('*' + data + '\n');
                printWriter.flush();
            }).start();
        }
    }

    public void sendParameters(int pulseLength, int pauseLength) {
        updateTerminalText("<- Set parameters:" +
                "\n    - Pulse: " + pulseLength + " ms" +
                "\n    - Pause: " + pauseLength + " ms");
        sendData("P " + pulseLength + " " + pauseLength);
    }

    public void sendTargetPosition(float targetZPosition, int speedMultiplier) {
        updateTerminalText("<- Jog to " + targetZPosition);
        sendData("J " + Math.round(targetZPosition * 1000) + " " + speedMultiplier); // Round the target position to "3 decimal digits"
    }

    public void startProcess() {
        updateTerminalText("<- Start the process");
        sendData("Z");
    }

    public void stopProcess() {
        updateTerminalText("<- Stop the process");
        sendData("A");
    }

    public void resetPosition() {
        updateTerminalText("<- Reset position");
        sendData("R");
    }

    public void makeToast(String message) {
        activity.runOnUiThread(() -> Toast.makeText(activity, message, Toast.LENGTH_SHORT).show());
    }

    public void updateTerminalText(String text) {
        activity.runOnUiThread(() -> terminalFragment.updateTerminalText(text));
    }
}
