package askartius.mobctrlsys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import askartius.mobctrlsys.api.EspCommunication;
import askartius.mobctrlsys.databinding.ActivityMainBinding;
import askartius.mobctrlsys.ui.PagerAdapter;

public class MainActivity extends AppCompatActivity implements EspCommunication {
    private final String ESP_IP = "192.168.3.22";
    private final int ESP_PORT = 80;
    private ActivityMainBinding binding;
    private Socket socket;
    protected PrintWriter printWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //DynamicColors.applyToActivityIfAvailable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.navigationBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.program_screen) {
                    binding.pager.setCurrentItem(0);
                } else if (item.getItemId() == R.id.process_screen) {
                    binding.pager.setCurrentItem(1);
                } else if (item.getItemId() == R.id.motion_screen) {
                    binding.pager.setCurrentItem(2);
                }
                return false;
            }
        });

        binding.pager.setAdapter(new PagerAdapter(this));
        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.navigationBar.getMenu().getItem(position).setChecked(true);
            }
        });

        connectToEsp(ESP_IP, ESP_PORT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (socket != null) {
                socket.close();
            }
            if (printWriter != null) {
                printWriter.close();
            }
        } catch (IOException e) {
            Log.e("SOCKET", "Error closing socket: " + e.getMessage());
        }
    }

    protected void connectToEsp(String EspIp, int EspPort) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(EspIp, EspPort);

                    printWriter = new PrintWriter(socket.getOutputStream());

                    //outputStream = socket.getOutputStream();
                    //inputStream = socket.getInputStream();

                    String message = "Hello from MobCtrlSys!\r";
                    //outputStream.write(message.getBytes());
                    printWriter.println(message);

                    /*byte[] buffer = new byte[1024];
                    int bytesRead = inputStream.read(buffer);
                    String response = new String(buffer, 0, bytesRead);
                    Log.d("ESP-01", response);*/
                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Error connecting: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void sendData(String data) {
        if (printWriter != null) {
            printWriter.println(data);
        } else {
            Toast.makeText(this, "Error sending", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getData() {

    }
}