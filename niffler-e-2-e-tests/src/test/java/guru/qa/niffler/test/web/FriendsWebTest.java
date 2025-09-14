package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.FriendsPage;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsWebTest {

    private static final Config CFG = Config.getInstance();

    @Test
    @User(
            friends = 1
    )
    @ApiLogin
    void friendShouldBePresentInFriendsTable(UserJson user) {
        UserJson friend = user.testData().friends().getFirst();

        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .searchPeople(friend.username())
                .checkFriends(friend.username());
    }

    @Test
    @User
    @ApiLogin
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkNoFriend();
    }

    @Test
    @User(
            incomeInvitations = 1
    )
    @ApiLogin
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        UserJson income = user.testData().incomeInvitations().getFirst();

        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .searchPeople(income.username())
                .checkIncomeInvitation(income.username());
    }

    @Test
    @User(
            incomeInvitations = 1
    )
    @ApiLogin
    void acceptIncomeInvitationInFriendsTable(UserJson user) {
        UserJson income = user.testData().incomeInvitations().getFirst();

        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .searchPeople(income.username())
                .acceptIncomeInvitation(income.username())
                .checkFriends(income.username());
    }

    @Test
    @User(
            incomeInvitations = 1
    )
    @ApiLogin
    void declineIncomeInvitationInFriendsTable(UserJson user) {
        UserJson income = user.testData().incomeInvitations().getFirst();

        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .searchPeople(income.username())
                .declineIncomeInvitation(income.username())
                .checkNoFriend();
    }

    @Test
    @User(
            outcomeInvitations = 1
    )
    @ApiLogin
    void outcomeInvitationBePresentInAllPeopleTable(UserJson user) {
        UserJson outcome = user.testData().outcomeInvitations().getFirst();

        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .clickOnAllPeopleTable()
                .searchPeople(outcome.username())
                .checkOutcomeInvitation(outcome.username());
    }
}
