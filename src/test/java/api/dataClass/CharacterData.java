package api.dataClass;

public class CharacterData {
    private String name;
    private String lastEp;
    private String species;
    private String location;

    public CharacterData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastEp() {
        return lastEp;
    }

    public void setLastEp(String lastEp) {
        this.lastEp = lastEp;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

