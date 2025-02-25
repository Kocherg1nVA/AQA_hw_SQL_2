package ru.netology.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.netology.data.APIHelper;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;

import java.util.List;

public class AppApiTest {

    @AfterAll
    public static void tearDown() {
        SQLHelper.cleanDataBase();
    }

    @Test
    public void shouldSuccessLogin() {
        APIHelper.validLogin();
    }

    @Test
    public void shouldNotLoginWithInvalidParams() {
        var loginErr = APIHelper.invalidLogin();
        Assertions.assertNotNull(loginErr);
        Assertions.assertEquals("AUTH_INVALID", loginErr);
    }

    @Test
    public void shouldGetAccessToken() {
        APIHelper.getAccessToken();
    }

    @Test
    public void shouldNotVerifyWithoutAccessToken() {
        var verifyErr = APIHelper.invalidVerify();
        Assertions.assertNotNull(verifyErr);
        Assertions.assertEquals("AUTH_INVALID", verifyErr);
    }

    @Test
    public void shouldGetUserCardsInfo() {
        APIHelper.getUserCardsInfo();
    }

    @Test
    public void shouldNotGetUserCardsInfoWithoutAccessToken() {
        APIHelper.UnAuthorizationCardInfo();
    }

    @Test
    public void shouldSuccessTransferFromFirstToSecond() {
        List<DataHelper.CardsInfo> cards = SQLHelper.getUserCards();

        int initialFirstCardBalance = cards.get(0).getBalance();
        int initialSecondCardBalance = cards.get(1).getBalance();
        int amount = DataHelper.generateAmount(initialSecondCardBalance);
        int expectedFirstCardBalance = initialFirstCardBalance - amount;
        int expectedSecondCardBalance = initialSecondCardBalance + amount;

        APIHelper.moneyTransfer(cards, 0, 1, amount);

        List<DataHelper.CardsInfo> newCardsInfo = SQLHelper.getUserCards();
        Assertions.assertEquals(expectedFirstCardBalance, newCardsInfo.get(0).getBalance());
        Assertions.assertEquals(expectedSecondCardBalance, newCardsInfo.get(1).getBalance());
    }

    @Test
    public void shouldNotTransferWhenAmountMoreThanBalance() {
        List<DataHelper.CardsInfo> cards = SQLHelper.getUserCards();

        int initialFirstCardBalance = cards.get(0).getBalance();
        int initialSecondCardBalance = cards.get(1).getBalance();
        int amount = DataHelper.generateInvalidAmount(initialSecondCardBalance);

        APIHelper.moneyTransfer(cards, 0, 1, amount);

        List<DataHelper.CardsInfo> newCardsInfo = SQLHelper.getUserCards();
        Assertions.assertEquals(initialFirstCardBalance, newCardsInfo.get(0).getBalance());
        Assertions.assertEquals(initialSecondCardBalance, newCardsInfo.get(1).getBalance());
    }
}
