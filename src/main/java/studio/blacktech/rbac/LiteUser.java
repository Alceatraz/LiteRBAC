package studio.blacktech.rbac;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implements of User. Use cache for speed up query. Provide roles
 * method wrapper.
 * <p>
 * New instance must use factory method in class RBAC
 */
public class LiteUser extends User {

    //= ================================================================================================================
    //= Settings
    //= ================================================================================================================

    protected static boolean defaultBehavior = false;

    //= ================================================================================================================
    //= Filed
    //= ================================================================================================================

    private final LinkedList<Role> roles;

    private final ConcurrentHashMap<String, Boolean> checkCache;

    private final ConcurrentHashMap<String, String> suffixCache;
    private final ConcurrentHashMap<String, List<String>> suffixesCache;

    //= ================================================================================================================
    //= Efficient java section
    //= ================================================================================================================

    protected static LiteUser getInstance(Role... roles) {
        LiteUser user = new LiteUser();
        for (Role role : roles) {
            user.roles.addFirst(role);
        }
        return user;
    }

    //= ================================================================================================================
    //= Constructor section
    //= ================================================================================================================

    public LiteUser() {
        this(
            new LinkedList<>(),
            new ConcurrentHashMap<>(),
            new ConcurrentHashMap<>(),
            new ConcurrentHashMap<>()
        );
    }

    public LiteUser(
        LinkedList<Role> roles,
        ConcurrentHashMap<String, Boolean> checkCache,
        ConcurrentHashMap<String, String> suffixCache,
        ConcurrentHashMap<String, List<String>> suffixesCache
    ) {
        this.roles = roles;
        this.checkCache = checkCache;
        this.suffixCache = suffixCache;
        this.suffixesCache = suffixesCache;
    }

    //= ================================================================================================================
    //= Public API: Permission
    //= ================================================================================================================

    /**
     * Check permission
     *
     * @param permission permission to check
     *
     * @return true if accept
     */
    @Override
    public boolean check(String permission) {
        Boolean result = this.checkCache.get(permission);
        if (result != null) return result;
        LinkedList<String> sections = RBAC.splitPermission(permission);
        result = check(sections);
        this.checkCache.put(permission, result);
        return result;
    }

    @Override
    protected Boolean check(LinkedList<String> sections) {
        Boolean check;
        for (Role role : this.roles) {
            check = role.check(sections);
            if (check == null) continue;
            if (check) return true;
        }
        return defaultBehavior;
    }

    //= ================================================================================================================
    //= Public API: Permission
    //= ================================================================================================================

    /**
     * Grant Role to User
     *
     * @param roles Role
     */
    @Override
    public void appendRole(Role... roles) {
        for (Role role : roles) {
            this.roles.addFirst(role);
        }
    }

    /**
     * Forfeit Role from User
     *
     * @param roles Role
     */
    @Override
    public void removeRole(Role... roles) {
        for (Role role : roles) {
            this.roles.remove(role);
        }
    }

    /**
     * Forfeit all Role from User
     */
    @Override
    public void clearRoles() {
        this.roles.clear();
    }

    /**
     * List all Role from User
     */
    @Override
    public List<Role> listRoles() {
        return List.copyOf(this.roles);
    }

    //= ================================================================================================================
    //= Public API: Permission
    //= ================================================================================================================

    /**
     * Wrapped method of role, Execute one by one for all Roles
     */
    @Override
    public String getSuffix(String permission) {
        String suffix = this.suffixCache.get(permission);
        if (suffix != null) return suffix;
        LinkedList<String> sections = RBAC.splitPermission(permission);
        suffix = getSuffix(sections);
        this.suffixCache.put(permission, suffix);
        return suffix;
    }

    @Override
    protected String getSuffix(LinkedList<String> sections) {
        for (Role role : this.roles) {
            String suffix = role.getSuffix(sections);
            if (suffix != null) return suffix;
        }
        return null;
    }

    /**
     * Wrapped method of role, Execute one by one for all Roles
     */
    @Override
    public List<String> getSuffixes(String permission) {
        List<String> suffixes = this.suffixesCache.get(permission);
        if (suffixes != null) return suffixes;
        LinkedList<String> sections = RBAC.splitPermission(permission);
        suffixes = getSuffixes(sections);
        this.suffixesCache.put(permission, suffixes);
        return suffixes;
    }

    @Override
    protected List<String> getSuffixes(LinkedList<String> sections) {
        for (Role role : this.roles) {
            List<String> suffixes = role.getSuffixes(sections);
            if (suffixes != null) return suffixes;
        }
        return null;
    }

    /**
     * Wrapped method of role, Execute one by one for all Roles
     */
    @Override
    public void clearCacheCache() {
        for (Role role : this.roles) {
            role.clearCacheCache();
        }
    }

    /**
     * Wrapped method of role, Execute one by one for all Roles
     */
    @Override
    public void removeCheckCache(String... permissions) {
        for (Role role : this.roles) {
            role.removeCheckCache(permissions);
        }
    }

    /**
     * Wrapped method of role, Execute one by one for all Roles
     */
    @Override
    public void removeCheckCacheUnder(String... permissions) {
        for (Role role : this.roles) {
            role.removeCheckCacheUnder(permissions);
        }
    }

    /**
     * Wrapped method of role, Execute one by one for all Roles
     */
    @Override
    public void clearSuffixCache() {
        for (Role role : this.roles) {
            role.clearSuffixCache();
        }
    }

    /**
     * Wrapped method of role, Execute one by one for all Roles
     */
    @Override
    public void clearSuffixesCache() {
        for (Role role : this.roles) {
            role.clearSuffixesCache();
        }
    }

    /**
     * Wrapped method of role, Execute one by one for all Roles
     */
    @Override
    public void removeSuffixCache(String... permissions) {
        for (Role role : this.roles) {
            role.removeSuffixCache(permissions);
        }
    }

    /**
     * Wrapped method of role, Execute one by one for all Roles
     */
    @Override
    public void removeSuffixesCache(String... permissions) {
        for (Role role : this.roles) {
            role.removeSuffixesCache(permissions);
        }
    }

    /**
     * Wrapped method of role, Execute one by one for all Roles
     */
    @Override
    public void removeSuffixCacheUnder(String... permissions) {
        for (Role role : this.roles) {
            role.removeSuffixCacheUnder(permissions);
        }
    }

    /**
     * Wrapped method of role, Execute one by one for all Roles
     */
    @Override
    public void removeSuffixesCacheUnder(String... permissions) {
        for (Role role : this.roles) {
            role.removeSuffixesCacheUnder(permissions);
        }
    }
}
