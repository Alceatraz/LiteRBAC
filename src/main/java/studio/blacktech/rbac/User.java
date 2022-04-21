package studio.blacktech.rbac;

import java.util.LinkedList;
import java.util.List;

public abstract class User {

    //= ========================================================================
    //= API for Permission check

    public abstract boolean check(String permission);

    protected abstract Boolean check(LinkedList<String> sections);

    //= ========================================================================
    //= API for Roles

    public abstract void appendRole(Role... roles);

    public abstract void removeRole(Role... roles);

    public abstract void clearRoles();

    public abstract List<Role> listRoles();

    //= ========================================================================
    //= API for Value store

    public abstract String getSuffix(String permission);

    public abstract List<String> getSuffixes(String permission);

    protected abstract String getSuffix(LinkedList<String> permission);

    protected abstract List<String> getSuffixes(LinkedList<String> permission);

    //= ========================================================================
    //= API for Cache

    public abstract void clearCacheCache();

    public abstract void removeCheckCache(String... permissions);

    public abstract void removeCheckCacheUnder(String... permissions);

    public abstract void clearSuffixCache();

    public abstract void clearSuffixesCache();

    public abstract void removeSuffixCache(String... permissions);

    public abstract void removeSuffixesCache(String... permissions);

    public abstract void removeSuffixCacheUnder(String... permissions);

    public abstract void removeSuffixesCacheUnder(String... permissions);

}
