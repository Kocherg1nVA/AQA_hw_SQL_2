package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DataHelper {
    static Faker faker = new Faker();

    @Getter
    public static class PathData {
        private final String loginPath = "api/auth";
        private final String verificationPath = "api/auth/verification";
        private final String cardsInfoPath = "api/cards";
        private final String transferPath = "api/transfer";
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class LoginData {
        private String login;
        private String password;

        public static LoginData getLoginInfo() {
            return new LoginData("vasya", "qwerty123");
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class InvalidLogin {
        private String login;
        private String password;
    }

    public static InvalidLogin generateInvalidInfo() {
        String login = faker.name().username();
        String password = faker.internet().password();
        return new InvalidLogin(login, password);
    }

    @Getter
    public static class LoginErr {
        private String code;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class VerificationData {
        private String login;
        private String code;

        public static VerificationData getVerifyInfo() {
            return new VerificationData(LoginData.getLoginInfo().login, SQLHelper.getVerificationCode());
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class InvalidVerificationData {
        private String login;
        private String code;

        public static VerificationData getInvalidVerifyInfo() {
            String login = faker.name().username();
            String code = faker.code().ean8();
            return new VerificationData(login, code);
        }
    }

    @Getter
    public static class VerifyErr {
        private String code;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class TokenData {
        private String token;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class CardsInfo {

        private String id;
        private String number;
        private String balance;
    }


//    public static class TransferData {
//        String from;
//        String to;
//        int amount;
//
//        public static TransferData moneyTransfer() {
//            return new TransferData();
//        }
//    }
}
