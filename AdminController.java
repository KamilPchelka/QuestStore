class AdminController{
  AdminView view = new AdminView();

  public void createMentor(){
      UserDaoImpl dao = new UserDaoImpl();
      String name = this.view.getStringFromUserInput(view.mentorNameQuestion);
      String email = this.view.getStringFromUserInput(view.mentorEmailQuestion);
      String password = this.view.getStringFromUserInput(view.mentorPasswordQuestion);
      Group<User> mentorsGroup = dao.getUserGroup("mentors");
      new MentorModel(name, email, password, mentorsGroup);
  }

  public void start() {
    view.printLine("From Admin");
  }

  public void assignMentorToGroup(){
      UserDaoImpl userDao = new UserDaoImpl();

      String name = view.getStringFromUserInput(view.mentorNameQuestion);
      String groupName = view.getStringFromUserInput(view.groupNameQuestion);

      User user = userDao.getUser(name);
      userDao.addUserAdherence(user, groupName);
  }

  public void createGroup(){
      UserDaoImpl userDao = new UserDaoImpl();
      String groupName = view.getStringFromUserInput(view.groupNameQuestion);
      //userDao.createUserGroup(groupName);
  }

  public void editMentor(){
      UserDaoImpl dao = new UserDaoImpl();
      String mentorName = view.getStringFromUserInput(view.mentorNameQuestion);
      User mentor = dao.getUser(mentorName);
      String choice = view.getStringFromUserInput(view.mentorChangeQuestion);
      if (choice.equals("1")){
          String name = view.getStringFromUserInput(view.mentorNameQuestion);
          mentor.setName(name);
      }
      else if(choice.equals("2")){
          String email = view.getStringFromUserInput(view.mentorEmailQuestion);
          mentor.setEmail(email);
      }
      else if(choice.equals("3")){
          String password = view.getStringFromUserInput(view.mentorPasswordQuestion);
          mentor.setPassword(password);
      }
      else{
          System.out.println(view.noSuchOption);
      }
  }


  public String getMentorDisplay(){
      UserDaoImpl dao = new UserDaoImpl();
      String mentorName = view.getStringFromUserInput(view.mentorNameQuestion);
      User mentor = dao.getUser(mentorName);
      return mentor.toString();
  }

}
