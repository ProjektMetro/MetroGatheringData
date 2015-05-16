package pl.warszawa.gdg.metrodatacollector.subway;

import java.util.ArrayList;


public class SubwayLine {
    public String name;
    public ArrayList<Station> stations = new ArrayList<>();
    public ArrayList<MapElement> line = new ArrayList<>();

    public void addStation(Station station) {
        if(!stations.contains(station)) {
           stations.add(station);
        }
    }

    public void addMapElemnent(MapElement mapElement) {
        if(!line.contains(mapElement)) {
            line.add(mapElement);
        }
        if(mapElement instanceof  Station && !stations.contains((Station) mapElement)) {
            stations.add((Station)mapElement);
        }
    }
}
