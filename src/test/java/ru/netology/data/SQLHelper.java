package ru.netology.data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class SQLHelper {

    public SQLHelper() {
    }

    private static final QueryRunner runner = new QueryRunner();

    @SneakyThrows
    private static Connection getConnection() {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
    }

    @SneakyThrows
    public static String getVerificationCode() {
        var verificationCodeSQL = "SELECT code FROM auth_codes ORDER BY created DESC LIMIT 1";
        try (var conn = getConnection()) {
            return runner.query(conn, verificationCodeSQL, new ScalarHandler<>());
        }
    }

    @SneakyThrows
    public static void cleanDataBase() {
        var conn = getConnection();
        runner.execute(conn, "DELETE FROM auth_codes");
        runner.execute(conn, "DELETE FROM card_transactions");
        runner.execute(conn, "DELETE FROM cards");
        runner.execute(conn, "DELETE FROM users");
    }

    @SneakyThrows
    public static List<DataHelper.CardsInfo> getUserCards() {
        var getUserCards = "SELECT cards.id, number, balance_in_kopecks / 100 AS balance FROM cards";
        try (var conn = getConnection()) {
            return runner.query(conn, getUserCards, new BeanListHandler<>(DataHelper.CardsInfo.class));
        }
    }
}
