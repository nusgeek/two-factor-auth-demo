package ch.rasc.twofa.entity;

public class UserDto {
    private String uuid;
    private String name;
    private int age;
    private String address;

    public UserDto(String uuid, String name, int age, String address) {
        this.uuid = uuid;
        this.name = name;
        this.age = age;
        this.address = address;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", num=" + age +
                ", location='" + address + '\'' +
                '}';
    }
}
