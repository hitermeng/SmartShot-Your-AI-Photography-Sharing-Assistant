package cn.aicamera.backend.dto;

public class UserProfile {
    private String username;
    private Integer gender;
    private Integer age;
    private String email;
    private String preference;

    public UserProfile(String username, Integer gender, Integer age, String email, String preference) {
        this.username = username;
        this.gender = gender;
        this.age = age;
        this.email = email;
        this.preference = preference;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }
}
