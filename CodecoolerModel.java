public class CodecoolerModel extends User {
  public String role = "codecooler";
  public WalletService wallet;
  public Level level;
  public Group<Artifact> artifacts;

  @Override
  public String getRole() {
    return role;
  }

  @Override
  public void setRole(String role) {
    this.role = role;
  }
}
