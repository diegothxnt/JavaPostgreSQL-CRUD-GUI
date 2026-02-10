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
        // modelo de tabla: bloqueamos la edicion directa en las celdas
        DefaultTableModel modelo = new DefaultTableModel() { 
            @Override public boolean isCellEditable(int r, int c) { return false; } 
        };
        JTable tabla = new JTable(modelo);
        cargarTabla(modelo, "");
         // panel superior para los botones de accion
        JPanel pAcciones = new JPanel();
        JButton btnAdd = new JButton("NUEVO");
        JButton btnEdit = new JButton("EDITAR");
        JButton btnDel = new JButton("ELIMINAR");

        // Estilos visuales (Colores llamativos con texto negro)
        btnAdd.setBackground(Color.GREEN); btnAdd.setForeground(Color.BLACK);
        btnEdit.setBackground(Color.YELLOW); btnEdit.setForeground(Color.BLACK);
        btnDel.setBackground(Color.PINK); btnDel.setForeground(Color.BLACK);
        
        pAcciones.add(btnAdd); pAcciones.add(btnEdit); pAcciones.add(btnDel);

        // Panel inferior para busqueda
        JPanel pBusqueda = new JPanel();
        JTextField txtBus = new JTextField(15);
        JButton btnBus = new JButton("BUSCAR");
        btnBus.setForeground(Color.BLACK);
        pBusqueda.add(new JLabel("Filtro:")); pBusqueda.add(txtBus); pBusqueda.add(btnBus);

        // Asignacion de eventos a los botones
        btnAdd.addActionListener(e -> formulario(modelo, null)); // null = modo Agregar
        btnEdit.addActionListener(e -> {
            int f = tabla.getSelectedRow();
            if (f != -1) formulario(modelo, modelo.getValueAt(f, 0)); // Enviamos el ID para editar
            else JOptionPane.showMessageDialog(null, "Selecciona una fila");
        });
        btnDel.addActionListener(e -> eliminar(tabla, modelo));
        btnBus.addActionListener(e -> cargarTabla(modelo, txtBus.getText()));

        v.add(pAcciones, BorderLayout.NORTH);
        v.add(new JScrollPane(tabla), BorderLayout.CENTER);
        v.add(pBusqueda, BorderLayout.SOUTH);
        v.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        v.setVisible(true);
    }

    // METODO PARA CARGAR DATOS EN LA TABLA
    static void cargarTabla(DefaultTableModel modelo, String filtro) {
        try {
            String sql = "SELECT * FROM " + tablaActual;
            // CAST(id AS TEXT) nos permite buscar numeros como si fueran texto
            if (!filtro.isEmpty()) sql += " WHERE CAST(id AS TEXT) ILIKE '%"+filtro+"%' OR nombre ILIKE '%"+filtro+"%'";
            sql += " ORDER BY id ASC";

            ResultSet rs = conexion.createStatement().executeQuery(sql);
            ResultSetMetaData meta = rs.getMetaData(); // Obtenemos info de las columnas
            int nCols = meta.getColumnCount();

            // Seteamos las cabeceras automaticamente
            Object[] cabeceras = new Object[nCols];
            for (int i = 1; i <= nCols; i++) cabeceras[i-1] = meta.getColumnName(i).toUpperCase();
            modelo.setColumnIdentifiers(cabeceras);

            modelo.setRowCount(0); // Limpiar tabla antes de recargar
            while (rs.next()) {
                Object[] fila = new Object[nCols];
                for (int i = 1; i <= nCols; i++) fila[i-1] = rs.getObject(i);
                modelo.addRow(fila);
            }
        } catch (Exception e) { System.out.println(e.getMessage()); }
    }

    // FORMULARIO DINAMICO: Crea los campos segun la tabla de la BD
    static void formulario(DefaultTableModel modelo, Object idEdit) {
        try {
            // Consulta la estructura de la tabla
            ResultSet rsMeta = conexion.createStatement().executeQuery("SELECT * FROM "+tablaActual+" LIMIT 1");
            ResultSetMetaData meta = rsMeta.getMetaData();
            JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
            ArrayList<JTextField> campos = new ArrayList<>();
            ArrayList<String> nombres = new ArrayList<>();
            ArrayList<String> tipos = new ArrayList<>();

            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String col = meta.getColumnName(i);
                String tipo = meta.getColumnTypeName(i).toLowerCase();

                // Regla: No pide el ID (es serial) ni la FECHA_CREACION (es automatica)
                if (i == 1 || col.equalsIgnoreCase("fecha_creacion")) continue; 

                p.add(new JLabel(" " + col.toUpperCase() + ":"));
                JTextField tf = new JTextField();
                
                // Si es modo EDITAR, busca el valor actual en la BD y se coloca en el cuadro
                if (idEdit != null) {
                    Statement st = conexion.createStatement();
                    ResultSet rsVal = st.executeQuery("SELECT "+col+" FROM "+tablaActual+" WHERE id="+idEdit);
                    if(rsVal.next()) tf.setText(rsVal.getString(1));
                }
                
                campos.add(tf);
                nombres.add(col);
                tipos.add(tipo);
                p.add(tf);
            }

            if (JOptionPane.showConfirmDialog(null, p, idEdit == null ? "Nuevo Registro" : "Editar", 2) == 0) {
                String sql;
                if (idEdit == null) {
                    sql = "INSERT INTO "+tablaActual+" ("+String.join(",", nombres)+") VALUES ("+"?,".repeat(nombres.size()).replaceAll(",$", "")+")";
                } else {
                    sql = "UPDATE "+tablaActual+" SET "+String.join("=?,", nombres)+"=? WHERE id=" + idEdit;
                }

                PreparedStatement ps = conexion.prepareStatement(sql);
                for (int i = 0; i < campos.size(); i++) {
                    String val = campos.get(i).getText().trim();
                    String t = tipos.get(i);

                    // --- VALIDACION DE TIPOS DE DATOS ---
                    if (t.contains("int") || t.contains("serial")) {
                        // Caso Integer (Stock): Quita todo lo que no sea numero
                        ps.setInt(i + 1, val.isEmpty() ? 0 : Integer.parseInt(val.replaceAll("[^0-9]", "")));
                    } 
                    else if (t.contains("numeric") || t.contains("decimal") || t.contains("double")) {
                        // Caso Decimal (Precio): Unifica comas y puntos
                        String limpio = val.replace(",", ".").replaceAll("[^0-9.]", "");
                        ps.setBigDecimal(i + 1, limpio.isEmpty() ? BigDecimal.ZERO : new BigDecimal(limpio));
                    } 
                    else {
                        // Caso Texto (Nombre, Descripcion)
                        ps.setObject(i + 1, val.isEmpty() ? null : val);
                    }
                }
                ps.executeUpdate();
                cargarTabla(modelo, ""); // Refrescar
            }
        } catch (Exception e) { 
            JOptionPane.showMessageDialog(null, "Error: Revisa que el Stock sea número entero y el Precio decimal."); 
        }
    }

    static void eliminar(JTable t, DefaultTableModel m) {
        int fila = t.getSelectedRow();
        if (fila == -1) return;
        try {
            Object id = m.getValueAt(fila, 0);
            if (JOptionPane.showConfirmDialog(null, "¿Eliminar ID " + id + "?") == 0) {
                conexion.createStatement().executeUpdate("DELETE FROM "+tablaActual+" WHERE id = " + id);
                cargarTabla(m, "");
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(null, e.getMessage()); }
    }
}
