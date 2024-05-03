package askartius.mobctrlsys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.navigation.NavigationBarView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import askartius.mobctrlsys.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private final String ESP_IP = "192.168.3.22";
    private final int ESP_PORT = 80;
    private ActivityMainBinding binding;
    protected OutputStream outputStream;
    protected InputStream inputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DynamicColors.applyToActivityIfAvailable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.navigationBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_home) {
                    binding.pager.setCurrentItem(0);
                } else if (item.getItemId() == R.id.navigation_process) {
                    binding.pager.setCurrentItem(1);
                } else if (item.getItemId() == R.id.navigation_motion) {
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

    protected void connectToEsp (String EspIp, int EspPort) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(EspIp, EspPort);
                    outputStream = socket.getOutputStream();
                    inputStream = socket.getInputStream();

                    /*DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    String message = "Hello from MobCtrlSys!\r";
                    dataOutputStream.writeUTF(message);
                    dataOutputStream.flush();
                    dataOutputStream.close();*/

                    String message = "Hello from MobCtrlSys!\r";
                    outputStream.write(message.getBytes());

                    byte[] buffer = new byte[1024];
                    int bytesRead = inputStream.read(buffer);
                    String response = new String(buffer, 0, bytesRead);
                    Log.d("ESP-01", response);

                    outputStream.close();
                    socket.close();
                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Error connecting: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}