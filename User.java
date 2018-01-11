public abstract class User {
  protected String nickname;
  protected String password;
  protected String email;
  protected Group<Group<User>> associatedGroups;

  public String getName() {
    return nickname;
  }

  public void setName(String nickname) {
    this.nickname = nickname;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Group<Group<User>> getAssociatedGroups() {
    return associatedGroups;
  }

  public void setAssociatedGroups(Group<Group<User>> associatedGroups) {
    this.associatedGroups = associatedGroups;
  }

  public abstract String getRole();
  public abstract void setRole(String role);

  public String toString() {
    return nickname + "|" + email  + "|" + password + "|" + associatedGroups  + "|";
  }
}
