package ru.avalon.java.j30.labs;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Main {

    public static HashMap<String, String> sqlMap;

    public static void main(String[] args) throws SQLException, IOException {

        Connection connection;
        if (loadDriver()) {                                                    // работаем с базой

            
            System.out.println("Trying to get connection");
            connection = getConnection();
            System.out.println("Прогресс conn = " + connection);
            //------------------------------------------------------ 
            sqlMap = getQueries("sql/queries.sql");
            printAllQuieries(sqlMap); // для отладки загрузки строк запросов

            System.out.println("*****Работа с базой*****");
            ProductCode product = new ProductCode(1, "SW", 1.5);
            System.out.println("============done Product================");

            product.save(connection);

            printAllCodes(connection);

            product.save(connection);

            printAllCodes(connection);

        
        }
    }

    /**
     * Выводит в кодсоль все коды товаров
     *
     * @param connection действительное соединение с базой данных
     * @throws SQLException
     */
    private static void printAllCodes(Connection connection) throws SQLException {
        Collection<ProductCode> codes = ProductCode.all(connection);
        for (ProductCode code : codes) {
            System.out.println(code);
        }
    }

    /**
     * Возвращает URL, описывающий месторасположение базы данных
     *
     * @return URL в виде объекта класса {@link String}
     */
    private static String getUrl() {
       return  "jdbc:hsqldb:~/db1";
    }

    /**
     * Возвращает параметры соединения
     *
     * @return Объект класса {@link Properties}, содержащий параметры user и
     * password
     */
    private static Properties getProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream in = ClassLoader.getSystemResourceAsStream("resources/database.properties")) {
            properties.load(in);
            return properties;
        }
    }
    /**
     * Возвращает соединение с базой данных Sample
     *
     * @return объект типа {@link Connection}
     * @throws SQLException
     */
    private static Connection getConnection() throws IOException {

        String url = getUrl();
        Connection conn = null;
        String user = getProperties().getProperty("user");
        String password = getProperties().getProperty("password");
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Соединение получено: " + conn);

            return conn;
        } catch (SQLException ex) {
            System.out.println("Ошибка соединения " + ex.getMessage());
            return null;
        }
    }

    private static boolean loadDriver() {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            System.out.println("Драйвер загружен");
            return true;
        } catch (ClassNotFoundException ex) {
            System.out.println("не найден драйвер "
                    + ex.getMessage());
            return false;
        }
    }

    private static HashMap<String, String> getQueries(String path) {
        LinkedList<String> lines = new LinkedList<>();
        try (InputStream is = ClassLoader.getSystemResourceAsStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String s;
            while ((s = br.readLine()) != null) {
                lines.add(s);
                 System.out.println("строка:" + s);
            }
        } catch (IOException ex) {
            System.out.println("Ошибка чтения " + path);
        }
        HashMap<String, String> hss = new HashMap<>();
        boolean entryStarted = false;
        String key = null;
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (line.startsWith("-->")) {
                entryStarted = true;
                key = line.substring(3).trim();
            } else {
                if (entryStarted) {
                    sb.append(line);
                }
                if (line.trim().endsWith(";")) {
                    entryStarted = false;
                    hss.put(key, sb.toString());
                    sb.delete(0, sb.length());
                }
            }
        }
        return hss;
    
    }

    private static void printAllQuieries(HashMap<String, String> sqlMap) {
        for (Map.Entry<String, String> entry : sqlMap.entrySet()) {
            System.out.println("имя: " + entry.getKey()
                    + "\nзапрос: " + entry.getValue());
        }
    }
}
