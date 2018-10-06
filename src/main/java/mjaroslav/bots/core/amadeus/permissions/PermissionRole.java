package mjaroslav.bots.core.amadeus.permissions;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PermissionRole {
    @SerializedName("name")
    public String name = "default";
    @SerializedName("parent")
    public String parent = "";
    @SerializedName("permissions")
    public List<String> permissions;
    
    public PermissionRole() {
    }
    
    public PermissionRole(String name) {
        this.name = name;
    }
    
    public PermissionRole(String name, String parent) {
        this.name = name;
        this.parent = parent;
    }
    
    public PermissionRole(String name, List<String> permissions) {
        this.name = name;
        this.permissions = permissions;
    }
    
    public PermissionRole(String name, String parent, List<String> permissions) {
        this.name = name;
        this.parent = parent;
        this.permissions = permissions;
    }
}
