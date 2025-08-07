package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsWebTest {

    private static final Config CFG = Config.getInstance();

    @Test
    @User(
            friends = 1
    )
    void friendShouldBePresentInFriendsTable(UserJson user) {
        UserJson friend = user.testData().friends().getFirst();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .checkThatPageLoaded()
                .goToUserFriendsPage()
                .searchPeople(friend.username())
                .checkFriends(friend.username());
    }

    @Test
    @User
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .checkThatPageLoaded()
                .goToUserFriendsPage()
                .checkNoFriend();
    }

    @Test
    @User(
            incomeInvitations = 1
    )
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        UserJson income = user.testData().incomeInvitations().getFirst();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .checkThatPageLoaded()
                .goToUserFriendsPage()
                .searchPeople(income.username())
                .checkIncomeInvitation(income.username());
    }

    @Test
    @User(
            outcomeInvitations = 1
    )
    void outcomeInvitationBePresentInAllPeopleTable(UserJson user) {
        UserJson outcome = user.testData().outcomeInvitations().getFirst();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .checkThatPageLoaded()
                .goToUserFriendsPage()
                .clickOnAllPeopleTable()
                .searchPeople(outcome.username())
                .checkOutcomeInvitation(outcome.username());
    }
}
