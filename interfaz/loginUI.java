package interfaz;

import clases.usuario;
import dao.usuarioDAO;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class loginUI extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public loginUI() {
        setTitle("Amazon - Iniciar sesion");
        setSize(420, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(232, 232, 232));

        JLabel titulo = new JLabel("AMAZON", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setForeground(new Color(35, 47, 62));
        titulo.setBorder(new EmptyBorder(25, 10, 10, 10));
        add(titulo, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        txtUsuario = new JTextField(15);
        panel.add(txtUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Contrasena:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        panel.add(txtPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        btnLogin = new JButton("Entrar");
        btnLogin.setBackground(new Color(255, 153, 0));
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(160, 38));
        panel.add(btnLogin, gbc);

        add(panel, BorderLayout.CENTER);

        btnLogin.addActionListener(e -> validarLogin());
        txtPassword.addActionListener(e -> validarLogin());

        setVisible(true);
    }

    private void validarLogin() {
        String user = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Rellena todos los campos.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        usuarioDAO dao = new usuarioDAO();
        usuario u = dao.login(user, pass);

        if (u == null) {
            JOptionPane.showMessageDialog(this, "Usuario o contrasena incorrectos.", "Error de acceso", JOptionPane.ERROR_MESSAGE);
            return;
        }

        dispose();

        if (u.getRol().equalsIgnoreCase("admin")) {
            new adminUI(u);
        } else {
            new clienteUI(u);
        }
    }
}
