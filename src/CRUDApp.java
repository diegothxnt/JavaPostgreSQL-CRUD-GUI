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

    