import ui.LoginForm;
import util.Theme;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Theme.applyDarkTheme();
        SwingUtilities.invokeLater(LoginForm::new);
    }
}
