package examples.smtp_server.src;

import java.io.IOException;

/**
 * Created by Usuario on 04/08/2017.
 */
public class Log {
    public boolean isInfoEnabled() {
        return true;
    }

    public boolean isDebugEnabled() {
        return false;
    }

    public void debug(String s) {
    }

    public void error(String s) {
    }

    public void warn(String s) {
    }

    public void error(String s, Exception e) {
    }

    public void info(String s) {
    }

    public void debug(String s, Exception ioe) {
    }

    public void fatal(String s) {
    }
}
