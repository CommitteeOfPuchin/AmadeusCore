package mjaroslav.bots.core.amadeus.permissions;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PermissionRole {
    @SerializedName("parent")
    public String parent;
    @SerializedName("permissions")
    public ArrayList<String> permissions;

    public PermissionRole() {
        parent = null;
        permissions = new ArrayList<>();
    }

    public PermissionRole(String parent, List<String> permissions) {
        this.parent = parent;
        this.permissions = new ArrayList<>(permissions);
    }
}
