package ru.avalon.java.j30.labs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Класс описывает представление о коде товара и отражает соответствующую
 * таблицу базы данных Sample (таблица PRODUCT_CODE).
 */
public class ProductCode {                                                           //Код товара

    private int id;
    private String name;
    private double price;

    public ProductCode(int id, String name, double price) {                         // конструктор
        this.id = id;
        this.name = name;
        this.price = price;
    }

    private ProductCode(ResultSet set) throws SQLException {
        this.id = set.getInt("ID");
        this.name = set.getString("NAME");
        this.price = set.getFloat("PRICE");
    }

    public PreparedStatement getSelectQuery(Connection connection) throws SQLException {

        String sql = Main.sqlMap.get("FindProductByID");
        System.out.println("sql: " + sql + "  id = " + id);
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, this.id);                                                  // меняем ? на id
        return st;
    }

    public static PreparedStatement getSelectAllQuery(Connection conn) throws SQLException {
        String sql = Main.sqlMap.get("selectAll");
//        System.out.println("sql_all = " + sql);
        PreparedStatement st = conn.prepareStatement(sql);
        return st;
    }

    /**
     * Возвращает запрос на добавление записи в таблицу PRODUCT_CODE базы данных
     * Sample
     *
     * @param connection действительное соединение с базой данных
     * @return Запрос в виде объекта класса {@link PreparedStatement}
     */
    public PreparedStatement getInsertQuery(Connection connection) throws SQLException {

        String sql = Main.sqlMap.get("InsertNewProduct");
        PreparedStatement st = connection.prepareStatement(sql);
        st.setString(1, this.name);
        st.setDouble(2, price);
        return st;
    }

    /**
     * Возвращает запрос на обновление значений записи в таблице PRODUCT_CODE
     * базы данных Sample
     *
     * @param connection действительное соединение с базой данных
     * @return Запрос в виде объекта класса {@link PreparedStatement}
     */
    public PreparedStatement getUpdateQuery(Connection connection) throws SQLException {

        String sql = Main.sqlMap.get("UpdateProductWithID");
        PreparedStatement st = connection.prepareStatement(sql);
        st.setString(1, this.name);
        st.setDouble(2, price);
        st.setInt(3, this.id);
        return st;
    }

    /**
     * Преобразует {@link ResultSet} в коллекцию объектов типа {@link Product}
     *
     * @param set {@link ResultSet}, полученный в результате запроса,
     * содержащего все поля таблицы PRODUCT_CODE базы данных Sample
     * @return Коллекция объектов типа {@link Product}
     * @throws SQLException
     */
    public static Collection<ProductCode> convert(ResultSet set) throws SQLException {
        Collection<ProductCode> cpro = new LinkedList<>();
        while (set.next()) {
            cpro.add(new ProductCode(set));
        }
        return cpro;
    }

    /**
     * Сохраняет текущий объект в базе данных.
     * <p>
     * Если запись ещё не существует, то выполняется запрос типа INSERT.
     * <p>
     * Если запись уже существует в базе данных, то выполняется запрос типа
     * UPDATE.
     *
     * @param connection действительное соединение с базой данных
     */

    public void save(Connection connection) throws SQLException {
        // проверяем наличие в базе ID
        PreparedStatement pst = getSelectQuery(connection);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            // есть такой продукт - делаем UPDATE
            pst = getUpdateQuery(connection);
        } else {
            // нет такого продукта в базе  - делаем ISERT
            pst = getInsertQuery(connection);
        }
        pst.executeUpdate();
        pst.close();
    }

    /**
     * Возвращает все записи таблицы PRODUCT_CODE в виде коллекции объектов типа
     * {@link Product}
     *
     * @return коллекция объектов типа {@link Product}
     * @throws SQLException
     */
    public static Collection<ProductCode> all(Connection connection) throws SQLException {
        try (PreparedStatement statement = getSelectAllQuery(connection)) {
            try (ResultSet result = statement.executeQuery()) {
                return convert(result);
            }
        } catch (SQLException ex) {
            System.out.println("Ошибка в all: " + ex.getMessage());
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product{" + "id=" + id + ", name=" + name + ", price=" + price + '}';
    }
}
