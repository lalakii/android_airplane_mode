package cn.lalaki.air;

import java.io.IOException;

public class ShellExecuteService extends IShellExecuteService.Stub {
    @Override
    public int setAirplaneModeWithShell(boolean isEnabled) {
        try {
            return Runtime.getRuntime().exec(new String[]{"sh", "-c", String.format("cmd connectivity airplane-mode %s", isEnabled ? "enable" : "disable")}).waitFor();
        } catch (IOException | InterruptedException ignored) {
        }
        return -2;
    }

    @Override
    public void destroy() {
    }
}
