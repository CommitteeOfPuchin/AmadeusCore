package mjaroslav.bots.core.amadeus.permissions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import com.google.gson.annotations.SerializedName;
import mjaroslav.bots.core.amadeus.utils.JSONUtils;

public class PermissionInfo {
    public PermissionInfo() {}

    public PermissionInfo(List<String> defaultRole) {
        roles.put("default", new PermissionRole(defaultRole));
    }

    public void createFile(File file) {
        try {
            if (file.createNewFile())
                JSONUtils.toJson(file, this, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SerializedName("roles")
    public HashMap<String, PermissionRole> roles = new HashMap<>();

    public void set(PermissionInfo newInfo) {
        roles = newInfo.roles;
        if (roles == null)
            roles = new HashMap<>();
    }

    public PermissionRole getRole(String role) {
        return roles.getOrDefault(role, roles.getOrDefault("default", null));
    }

    public PermissionRole getParent(String role) {
        return getRole(getRole(role).parent);
    }

    public HashSet<PermissionRole> getTree(String role) {
        HashSet<PermissionRole> result = new HashSet<>();
        PermissionRole thisRole = getRole(role);
        result.add(thisRole);
        if (!thisRole.isDefault)
            result.addAll(getTree(thisRole.parent));
        return result;
    }

    public HashSet<String> getPermission(String role) {
        HashSet<String> result = new HashSet<>();
        for (PermissionRole permissionRole : getTree(role))
            result.addAll(permissionRole.permissions);
        return result;
    }

    public void clear() {
        roles.clear();
    }

    public static class PermissionRole {
        @SerializedName("parent")
        public String parent;
        @SerializedName("is_default")
        public boolean isDefault;
        @SerializedName("permissions")
        public HashSet<String> permissions;

        public PermissionRole(List<String> permissions) {
            parent = null;
            isDefault = true;
            this.permissions = new HashSet<>(permissions);
        }

        public PermissionRole() {
            parent = null;
            isDefault = true;
            permissions = new HashSet<>();
        }

        public PermissionRole(String parent, List<String> permissions) {
            this.parent = parent;
            isDefault = false;
            this.permissions = new HashSet<>(permissions);
        }

        public PermissionRole(String parent, HashSet<String> permissions) {
            this.parent = parent;
            isDefault = false;
            this.permissions = permissions;
        }
    }

}
