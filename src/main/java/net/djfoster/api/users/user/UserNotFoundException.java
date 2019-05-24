package net.djfoster.api.users.user;

class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("Could not find user "+id);
    }
}
