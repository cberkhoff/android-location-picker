package cl.cberkhoff.locationpicker;

/**
 * Created by Christian on 08-11-13.
 */
public class Location {
    final private Integer id, parentId;
    final private String name;

    public Location(Integer id, Integer parentId, String name) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }
}
