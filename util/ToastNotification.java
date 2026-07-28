package util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToastNotification {
    private static JDialog dialog;
    private static Timer timer;
    private static final int DURATION = 3000; // 3 segundos

    /* public static void showToast(String mensagem) {
        SwingUtilities.invokeLater(() -> {
            if (dialog != null && dialog.isVisible()) {
                dialog.dispose();
            }
            dialog = new JDialog();
            dialog.setUndecorated(true);
            dialog.setAlwaysOnTop(true);
            dialog.setLayout(new BorderLayout());

            JLabel label = new JLabel(mensagem);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setForeground(Color.WHITE);
            label.setBackground(new Color(0, 0, 0, 200));
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
            dialog.add(label, BorderLayout.CENTER);

            dialog.pack();
            // posiciona no canto inferior direito
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int x = screenSize.width - dialog.getWidth() - 30;
            int y = screenSize.height - dialog.getHeight() - 50;
            dialog.setLocation(x, y);

            dialog.setVisible(true);

            if (timer != null) {
                timer.stop();
            }
            timer = new Timer(DURATION, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                    timer.stop();
                }
            });
            timer.setRepeats(false);
            timer.start();
        });
    }*/

    public static void showToast(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, "Notificação", JOptionPane.INFORMATION_MESSAGE);
    }
}