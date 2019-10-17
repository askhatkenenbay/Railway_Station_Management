package railway_system.entity;

public class Station {

    private String city;
    private String name;
    private int id;

    public Station(String city, String name, int id){
        this.city = city;
        this.name = name;
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Station{" +
                "city='" + city + '\'' +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
