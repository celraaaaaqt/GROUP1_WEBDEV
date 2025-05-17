import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class HoverEffect {

    private Color defaultBg;
    private Color hoverBg;
    private Color defaultFg = Color.BLACK;
    private Color hoverFg = Color.WHITE;

    private static List<JButton> buttonList = new ArrayList<>();
    private static JButton selectedButton = null;

    public HoverEffect(Color defaultBg, Color hoverBg) {
        this.defaultBg = defaultBg;
        this.hoverBg = hoverBg;
    }

    public void applyTo(JButton button) {
        button.setBackground(defaultBg);
        button.setForeground(defaultFg);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        buttonList.add(button);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBg);
                button.setForeground(hoverFg); // always change fg on hover
            }

            public void mouseExited(MouseEvent e) {
                if (button != selectedButton) {
                    button.setBackground(defaultBg);
                    button.setForeground(defaultFg);
                } else {
                    button.setBackground(hoverBg); // keep clicked bg
                    button.setForeground(hoverFg); // keep clicked fg
                }
            }
        });

        button.addActionListener(e -> {
            for (JButton b : buttonList) {
                b.setBackground(defaultBg);
                b.setForeground(defaultFg);
            }

            selectedButton = button;
            button.setBackground(hoverBg);
            button.setForeground(hoverFg);
        });
    }
}
