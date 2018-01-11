public interface UserDao{
    public Group<Group<User>> getAllUsers();
    public User getUser(String nickname);
    public Group<String> getUserGroupNames();
    public Group<User> getUserGroup(String groupName);
    public void addUser(User user);
    public void updateUser(User user);
    public boolean deleteUser(User user);
    public boolean addUserAdherence(User user, String name);
}
