package main.java.com.nwo.queststore.dao;

import main.java.com.nwo.queststore.model.GroupModel;
import main.java.com.nwo.queststore.model.RawUserModel;
import main.java.com.nwo.queststore.model.UserModel;
import main.java.com.nwo.queststore.enums.Role;

import java.sql.*;

import static main.java.com.nwo.queststore.enums.Role.*;


public class UserDaoImpl implements UserDao {

    private static String JDBC = "jdbc:sqlite:database/database.db";

    public RawUserModel getUser(String nickname) throws SQLException{

      Connection connect = establishConnection();
      Statement statement = connect.createStatement();
      ResultSet extractRole = null;
      RawUserModel tempUsr = null;

      try{

        String extractRoleQuery = "SELECT users.user_id, user_privilege_levels.privilege_name "
        + "FROM users "
        + "LEFT JOIN ( user_roles "
        + "JOIN user_privilege_levels "
        + "ON user_roles.user_privilege_level_id = "
        + "user_privilege_levels.privilege_id) "
        + "ON users.user_id = user_roles.user_id "
        + "WHERE nickname='" + nickname + "';";

        extractRole = statement.executeQuery(extractRoleQuery);
        Role role;
        int userId;

        while(extractRole.next()){
          role = convertRole(extractRole.getString("privilege_name"));
          userId = extractRole.getInt("user_id");

          tempUsr = createUserObject(role, userId);
        }


      }finally{
        close(extractRole);
        close(connect, statement);
      }
      return tempUsr;
    }

    public void addUser(UserModel userModel) throws SQLException{
        Connection connect = establishConnection();
        Statement statement = connect.createStatement();
        try{
          String userName = null, password = null, email = null, role = null;
          int userId = 0, userPrivilegeLevelId = 0, balance = 0;//, expGained = 0, ;
          GroupModel<Integer> groupModelIds;

          userName = userModel.getName();
          password = userModel.getPassword();
          email = userModel.getEmail();
          role = convertRole(userModel.getRole());

          updateCredentials(userName, password, email);

          userId = getUserId(userName);
          userPrivilegeLevelId = getUserPrivilegeLevelId(role);
          groupModelIds = getUserGroupIds(userModel);

          /* execute updates */

          updatePrivileges(userId, userPrivilegeLevelId);
          updateUserAssociations(groupModelIds, userId);
      }finally{
        close(connect, statement);
      }
    }

    public void updateUser(UserModel userModel) throws SQLException{
        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        try{
        String userName = null, password = null, email = null;
        int userId = 0;
        GroupModel<Integer> groupModelIds = null;

        // get basic userModel credentials (all strings + role)
        userName = userModel.getName();
        password = userModel.getPassword();
        email = userModel.getEmail();

        userId = getUserId(userName);
        groupModelIds = getUserGroupIds(userModel);

        upgradeCredentials(password, email, userId);
        upgradeUserAssociations(groupModelIds, userId);

      }finally{
        close(connect, statement);
      }
    }

    public GroupModel<String> getUserGroupNames() throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();
        ResultSet results = null;
        GroupModel<String> groupsNames = new GroupModel<>("generic_group.GroupModel names");

        try{
        String query = "SELECT DISTINCT group_names.group_name " +
        "FROM user_associations " +
        "LEFT JOIN group_names " +
        "ON user_associations.group_id = group_names.group_id ;";

        results = statement.executeQuery(query);

        while(results.next()){
            groupsNames.add(results.getString("group_name"));
        }
      }finally{
        close(results);
        close(connect, statement);
      }
        return groupsNames;
    }

    public GroupModel<UserModel> getUserGroup(String groupName) throws SQLException{

      GroupModel<UserModel> userGroupModel = new GroupModel<>(groupName);
      insertUsersTo(userGroupModel);
      return userGroupModel;
    }

    public boolean addUserAdherence(UserModel userModel, String groupName) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();
        boolean addedAdherence = false;
        try {

            int userId = getUserId(userModel.getName());
            int groupId = getGroupId(groupName);

            Statement checkStatement = connect.createStatement();
            String query = "SELECT user_id FROM user_associations WHERE user_id='" + userId +  "' AND group_id='" + groupId + "';";
            ResultSet checkRecord = checkStatement.executeQuery(query);
            boolean addNew = !checkRecord.next();
            checkStatement.close();

            if(addNew && userId > 0 && groupId > 0){
                String insert = "INSERT INTO user_associations(user_id, group_id) " +
                    "VALUES (" + userId + ", " + groupId + ");";
                statement.executeUpdate(insert);
                connect.commit();
                addedAdherence =  true;
            }

        } finally {

            close(connect, statement);
        }
        return addedAdherence;
    }

    public void addUserGroup(GroupModel<UserModel> groupModel) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        try{
        String query = "INSERT INTO group_names(group_name) " +
            "VALUES ('" + groupModel.getName() + "');";

        statement.executeUpdate(query);
        connect.commit();
      }finally{
        close(connect, statement);
      }
    }

    public GroupModel<String> getAllGroupNames() throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();
        ResultSet results = null;
        GroupModel<String> groups = new GroupModel<>("all groups");
        try{
          String query = "SELECT group_name FROM group_names ;";
          results = statement.executeQuery(query);

          while(results.next()){
              groups.add(results.getString("group_name"));
          }
        }finally{
          close(results);
          close(connect, statement);
        }
          return groups;
    }

    public int getGroupId(String groupName) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();
        ResultSet results = null;
        int id = -1;
      try{
        String query = "SELECT group_id FROM group_names WHERE group_name='"
        + groupName + "';";
        results = statement.executeQuery(query);

        if(results.next()){
            id = results.getInt("group_id");
        }
      }finally{
        close(results);
        close(connect, statement);
      }
        return id;
    }

    public int getUserId(String userName) throws SQLException{

      Connection connect = establishConnection();
      Statement statement = connect.createStatement();
      ResultSet results = null;
      int id = -1;
      try{
        String query = "SELECT user_id FROM users WHERE nickname='" + userName + "';";
        results = statement.executeQuery(query);
        while(results.next()){
            id = results.getInt("user_id");
            break;
        }
      }finally{
        close(results);
        close(connect, statement);
      }
        return id;
    }


    // helper methods for pubic methods

    private RawUserModel createUserObject(Role role, int userId)  throws SQLException{

      Connection connect = establishConnection();
      Statement statement = connect.createStatement();
      ResultSet results = null;
      RawUserModel user = null;

      try{
        String name, password, email;
        GroupModel<String> userGroups = getUserGroups(userId);

        String query = "SELECT * FROM users WHERE user_id="
        + userId + " ;";

        results = statement.executeQuery(query);
        if(results.next()){
          name = results.getString("nickname");
          password = results.getString("password");
          email = results.getString("email");

          user = new RawUserModel(role, name, email, password, userGroups);
        }

      }finally{
        close(results);
        close(connect, statement);
      }
      return user;
    }

    private void insertUsersTo(GroupModel<UserModel> groupModel) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();
        ResultSet results = null;
      try{
        String name, password, email;
        Role role = null;

        String query = "SELECT users.*, user_privilege_levels.privilege_name FROM users " +
            "JOIN user_associations ON user_associations.user_id = users.user_id " +
            "JOIN group_names ON group_names.group_id = user_associations.group_id " +
            "JOIN user_roles ON user_roles.user_id = users.user_id " +
            "JOIN user_privilege_levels ON " +
            "user_privilege_levels.privilege_id = user_roles.user_privilege_level_id " +
            "WHERE group_name = '" + groupModel.getName() + "' ;" ;

        results = statement.executeQuery(query);

        while(results.next()){

            name = results.getString("nickname");
            password = results.getString("password");
            email = results.getString("email");
            role = convertRole(results.getString("privilege_name"));

            groupModel.add(new RawUserModel(role, name, email, password, getUserGroups(results.getInt("user_id"))));
        }
      }finally{
        close(results);
        close(connect, statement);
      }
    }

    private GroupModel<String> getUserGroups(int userId) throws SQLException{

      Connection connect = establishConnection();
      Statement statement = connect.createStatement();
      GroupModel<String> associatedGroups = new GroupModel<>("associated user groups");
      ResultSet results = null;
      try{
        String query = "SELECT DISTINCT group_name " +
                "FROM group_names " +
                "JOIN user_associations  " +
                "ON group_names.group_id=user_associations.group_id " +
                "WHERE user_associations.user_id=" + userId +";";

        results = statement.executeQuery(query);

        String resultGroupName;
        while(results.next()){
            resultGroupName = results.getString("group_name");
            associatedGroups.add(resultGroupName);
        }
      }finally{
        close(results);
        close(connect, statement);
      }
        return associatedGroups;
    }

    private GroupModel<Integer> getUserGroupIds(UserModel userModel) throws SQLException{
      Connection connect = establishConnection();
      Statement statement = connect.createStatement();
      ResultSet results = null;
      GroupModel<Integer> groupModelIds = new GroupModel<>("GroupModel ids");
      try{
        String query;

        for(String groupName : userModel.getAssociatedGroupModelNames()){
          query = "SELECT group_id FROM group_names " +
                "WHERE group_name='" + groupName + "' ;";
          results = statement.executeQuery(query);
          if(results.next()){
            groupModelIds.add(results.getInt("group_id"));
          }
        }
      }finally{
        close(results);
        close(connect, statement);
      }
      return groupModelIds;
    }

    private int getUserPrivilegeLevelId(String role) throws SQLException{

      Connection connect = establishConnection();
      Statement statement = connect.createStatement();
      int userPrivilegeLevelId = -1;
      ResultSet getPrivLevelResult = null;
      try{
        String getPrivLevel = "SELECT privilege_id FROM user_privilege_levels " +
                "WHERE privilege_name='" + role + "';";
        getPrivLevelResult = statement.executeQuery(getPrivLevel);
        while(getPrivLevelResult.next()){
            userPrivilegeLevelId = getPrivLevelResult.getInt("privilege_id");
        }
      }finally{
        close(getPrivLevelResult);
        close(connect, statement);
      }
        return userPrivilegeLevelId;
    }

    private Role convertRole(String roleName){
        switch(roleName){
            case "codecooler":
                return CODECOOLER;
            case "mentor":
                return MENTOR;
            case "admin":
                return ADMIN;
        }
        return null;
    }

    private String convertRole(Role role){
        switch(role){
            case CODECOOLER:
                return "codecooler";
            case MENTOR:
                return "mentor";
            case ADMIN:
                return "admin";
        }
        return null;
    }

    // updaters

    private void updateCredentials(String userName, String password, String email) throws SQLException{


        Connection connect = establishConnection();
        Statement statement = connect.createStatement();
      try{
        String updateUsers = "INSERT INTO users(nickname, password, email) " +
                "VALUES ('" + userName + "', '" + password + "', '" + email + "');";
        statement.executeUpdate(updateUsers);
        connect.commit();
      }finally{
        close(connect, statement);
      }
    }

    private void updatePrivileges(int userId, int userPrivilegeLevelId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();
      try{
        String updatePrivileges = "INSERT INTO user_roles" +
                "(user_id, user_privilege_level_id) " +
                "VALUES (" + userId + ", " + userPrivilegeLevelId + ");";
        statement.executeUpdate(updatePrivileges);
        connect.commit();
      }finally{
        close(connect, statement);
      }
    }

    private void updateUserAssociations(GroupModel<Integer> groupModelIds, int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();
      try{
        String updateAssociations;
        for(Integer groupId : groupModelIds){
            updateAssociations = "INSERT INTO user_associations(user_id, group_id) " +
                    "VALUES (" + userId + ", " + groupId + ");";
            statement.executeUpdate(updateAssociations);
            connect.commit();
        }
      }finally{
        close(connect, statement);
      }
    }

    // upgraders

    private void upgradeCredentials(String password, String email, int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();
        try{
        String updateUsers = "UPDATE users " +
                "SET password='" + password + "', email='" + email + "' " +
                "WHERE user_id=" + userId + ";";

        statement.executeUpdate(updateUsers);
        connect.commit();
      }finally{
        close(connect, statement);
      }
    }

    private void upgradeUserAssociations(GroupModel<Integer> groupModelIds, int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();
        try{
        String updateAssociations;
        for(Integer groupId : groupModelIds){
            updateAssociations = "UPDATE user_associations " +
                    "SET group_id=" + groupId + " WHERE user_id=" + userId + ";";
            statement.executeUpdate(updateAssociations);
            connect.commit();
        }
      }finally{
        close(connect, statement);
      }
    }

    // ----- basic database operations -----/

    private Connection establishConnection() throws SQLException{

      Connection connect = null;
        try{
            Class.forName("org.sqlite.JDBC");
        }catch(ClassNotFoundException e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        connect = DriverManager.getConnection(UserDaoImpl.JDBC);
        connect.setAutoCommit(false);
        return connect;
    }

    private void close(Connection connect, Statement statement) throws SQLException{
        if(statement != null){
            statement.close();
        }
        if(connect != null){
            connect.close();
        }
    }

    private void close(ResultSet results) throws SQLException{
      if(results != null){
        results.close();
      }
    }

}
