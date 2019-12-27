import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GeocodingSample {
    private final HashMap<String, List<Entrance>> knownPois = new HashMap<>();


    public Entrance getSingleEntranceOf(String name) {
        List<Entrance> entrances = getEntrancesOfs(name);
        if(entrances == null){
            return null;
        }

        return entrances.get(0);
    }
    public List<Entrance> getEntrancesOfs(String name) {
        if (knownPois.containsKey(name)) {
            return knownPois.get(name);
        }
        return new ArrayList<>();
    }

    public void add(Entrance entrance) {
        List<Entrance> entrances =
                (knownPois.containsKey(entrance.name))
                        ? knownPois.get(entrance.name)
                        : new ArrayList<Entrance>();
        entrances.add(entrance);
        knownPois.put(entrance.name, entrances);
    }

    public void addAll(Entrance... pois) {
        for (Entrance entrance : pois) {
            add(entrance);
        }
    }
}
