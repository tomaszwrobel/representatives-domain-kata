package rdk.model;

public class User {

    private static final User USER_NO_ORGANISATION = UserBuilder.user("without organisation").withRole(UserRole.REGULAR)
            .build();

    private String name;

    private UserRole role;

    public User() {
    }

    public static User userWithoutOrganisation() {
        return USER_NO_ORGANISATION;
    }

    public String getName() {
        return name;
    }

    public UserRole getRole() {
        return role;
    }

    public static class UserBuilder {

        private String name;

        private UserRole role;

        public UserBuilder(String name) {
            this.name = name;
        }

        public static UserBuilder user(String name) {
            return new UserBuilder(name);
        }

        public UserBuilder withRole(UserRole role) {
            this.role = role;
            return this;
        }

        public User build() {
            User user = new User();

            user.name = this.name;
            user.role = this.role;

            return user;
        }

    }

    public void setOwnerRole() {
        this.role = UserRole.OWNER;
    }

    public void setRepresentativeRole() {
        this.role = UserRole.REPRESENTATIVE;
    }

}
