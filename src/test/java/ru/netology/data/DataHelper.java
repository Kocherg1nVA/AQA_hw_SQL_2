package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Random;

public class DataHelper {
    static Faker faker = new Faker();

    @AllArgsConstructor
    @Getter
    public static class LoginData {
        private String login;
        private String password;

        public static LoginData getLoginInfo() {
            return new LoginData("vasya", "qwerty123");
        }

        public static LoginData generateInvalidInfo() {
            String login = faker.name().username();
            String password = faker.internet().password();
            return new LoginData(login, password);
        }
    }

    @Getter
    public static class LoginErr {
        private String code;
    }

    @AllArgsConstructor
    @Getter
    public static class VerificationData {
        private String login;
        private String code;

        public static VerificationData getVerifyInfo() {
            return new VerificationData(LoginData.getLoginInfo().login, SQLHelper.getVerificationCode());
        }

        public static VerificationData getInvalidVerifyInfo() {
            String validLogin = LoginData.getLoginInfo().getLogin();
            String code = faker.code().ean8();
            return new VerificationData(validLogin, code);
        }
    }

    @Getter
    public static class VerifyErr {
        private String code;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class TokenData {
        private String token;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class CardsInfo {
        private String id;
        private String number;
        private Integer balance;
    }

    @AllArgsConstructor
    @Getter
    public static class TransferData {
        private String from;
        private String to;
        private int amount;

        public static TransferData moneyTransfer(List<CardsInfo> cards, int indexFrom, int indexTo, int amount) {
            String cardFrom = cards.get(indexFrom).getNumber();
            String cardTo = cards.get(indexTo).getNumber();
            return new TransferData(cardFrom, cardTo, amount);
        }
    }

    public static int generateAmount(int balance) {
        return new Random().nextInt(balance) + 1;
    }

    public static int generateInvalidAmount(int balance) {
        return Math.abs(balance) + new Random().nextInt(10_000);
    }
}
