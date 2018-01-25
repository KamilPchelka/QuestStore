package user.mentor;

import console.menu.AbstractConsoleView;
import console.menu.Menu;

public class MentorView extends AbstractConsoleView {

    public MentorView() {
        // a no-menu constructor - not all uses require a menu
    }

    public MentorView(Menu menu) {

        this.menu = menu;
    }

  public String userNicknameQuestion = "Provide user Nickname : ";
  public String userEmailQuestion = "Provide user Email: ";
  public String userPasswordQuestion = "Provide user Password: ";
  public String userGroupQuestion = "Provide user generic_group.Group: ";
  public String editNicknameQuestion = "Provide new user Nickname";
  public String editEmailQuestion = "Provide new user Email: ";
  public String editPasswordQuestion = "Provide new user Password: ";
  public String editGroupQuestion = "Provide new user generic_group.Group: ";
  public String markQuestNameQuestion = "Provide quest name: ";
  public String markArtifactUsedQuestion = "Provide name of used artifact: ";
  public String codecoolerAlreadyInGroupOrGroupAbsent = "The codecooler *had* been added to the given group or the group does not exist.";
  public String userAlreadyInDatabase = "user already in database.";
  public String availableQuests = "Available quests: ";
  public String invalidNickname = "user not in database or not a user.codecooler.";
  public String questNotFoundError = "quest name not found";
  public String artifactNameQuestion = "Enter the artifact's name: ";
  public String artifactNotFoundError = "No such artifact found!";

}