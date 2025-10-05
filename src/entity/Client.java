package entity;

public record Client(int id, String nom, String email, String telephone, String password) {
    public String getNom() {
        return nom;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return telephone;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getPassword() {
        return password;
    }
}
