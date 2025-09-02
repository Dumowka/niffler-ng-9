package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsWebTest {

    private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);

    private static final Config CFG = Config.getInstance();

    @Test
    @User(
            friends = 1
    )
    void friendShouldBePresentInFriendsTable(UserJson user) {
        UserJson friend = user.testData().friends().getFirst();

        driver.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .checkThatPageLoaded()
                .getHeader().toFriendsPage()
                .searchPeople(friend.username())
                .checkFriends(friend.username());
    }

    @Test
    @User
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        driver.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .checkThatPageLoaded()
                .getHeader().toFriendsPage()
                .checkNoFriend();
    }

    @Test
    @User(
            incomeInvitations = 1
    )
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        UserJson income = user.testData().incomeInvitations().getFirst();

        driver.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .checkThatPageLoaded()
                .getHeader().toFriendsPage()
                .searchPeople(income.username())
                .checkIncomeInvitation(income.username());
    }

    @Test
    @User(
            incomeInvitations = 1
    )
    void acceptIncomeInvitationInFriendsTable(UserJson user) {
        UserJson income = user.testData().incomeInvitations().getFirst();

        driver.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .checkThatPageLoaded()
                .getHeader().toFriendsPage()
                .searchPeople(income.username())
                .acceptIncomeInvitation(income.username())
                .checkFriends(income.username());
    }

    @Test
    @User(
            incomeInvitations = 1
    )
    void declineIncomeInvitationInFriendsTable(UserJson user) {
        UserJson income = user.testData().incomeInvitations().getFirst();

        driver.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .checkThatPageLoaded()
                .getHeader().toFriendsPage()
                .searchPeople(income.username())
                .declineIncomeInvitation(income.username())
                .checkNoFriend();
    }

    @Test
    @User(
            outcomeInvitations = 1
    )
    void outcomeInvitationBePresentInAllPeopleTable(UserJson user) {
        UserJson outcome = user.testData().outcomeInvitations().getFirst();

        driver.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .checkThatPageLoaded()
                .getHeader().toFriendsPage()
                .clickOnAllPeopleTable()
                .searchPeople(outcome.username())
                .checkOutcomeInvitation(outcome.username());
    }
}
