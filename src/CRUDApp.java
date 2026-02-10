import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.math.BigDecimal; // para manejar precios sin perder centavos

public class CRUDApp {
    static Connection conexion; // objeto para mantener la conexion activa
    static String tablaActual; // guarda el nombre de la tabla con la que trabajamos
    public static void main(String[] args) {
        // ventana de registro
        JFrame f = new JFrame("Acceso Base de Datos");
        f.setSize(400, 350);
        f.setLayout(new GridLayout(0, 1, 10, 10));
        f.setLocationRelativeTo(null);
        // campos para ingresar datos de conexion
        JTextField txtBD = new JTextField("PV");
        JTextField txtUser = new JTextField("postgres");
        JPasswordField txtPass = new JPasswordField();
        JTextField txtTabla = new JTextField("productos");

        f.add(new JLabel(" Base de Datos:")); f.add(txtBD);
        f.add(new JLabel(" Usuario:")); f.add(txtUser);
        f.add(new JLabel(" Contraseña:")); f.add(txtPass);
        f.add(new JLabel(" Tabla:")); f.add(txtTabla);

        JButton btn = new JButton("CONECTAR");
        btn.setBackground(new Color(100, 149, 237)); 
        btn.setForeground(Color.BLACK); 
        f.add(btn);
        // evento para conectar a la base de datos
        btn.addActionListener(e -> {
            try {
                // Driver JDBC de PostgreSQL
                String url = "jdbc:postgresql://localhost:5432/" + txtBD.getText();
                tablaActual = txtTabla.getText();
                conexion = DriverManager.getConnection(url, txtUser.getText(), new String(txtPass.getPassword()));
                f.dispose(); // Cerramos el login
                mostrarCRUD(); // Abrimos la interfaz principal
            } catch (Exception ex) { JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage()); }
        });
        f.setVisible(true);
    }
    static void mostrarCRUD() {
        // ventana principal 
        JFrame v = new JFrame("Panel Control - " + tablaActual);
        v.setSize(950, 600);
        v.setLocationRelativeTo(null);


    



