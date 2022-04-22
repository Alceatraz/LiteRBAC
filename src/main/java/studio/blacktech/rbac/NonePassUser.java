package studio.blacktech.rbac;

import java.util.LinkedList;
import java.util.List;

/**
 * This is a static User implement. It will always return no.
 */
public class NonePassUser extends User {

    private static final List<Role> parents = List.of();

    protected static NonePassUser getInstance() {
        return new NonePassUser();
    }

    protected NonePassUser() {

    }

    @Override
    public boolean check(String permission) {
        return false;
    }

    @Override
    protected Boolean check(LinkedList<String> sections) {
        return false;
    }

    @Override
    public void appendRole(Role... roles) {

    }

    @Override
    public void removeRole(Role... roles) {

    }

    @Override
    public void clearRoles() {

    }

    @Override
    public List<Role> listRoles() {
        return parents;
    }

    @Override
    public String getSuffix(String permission) {
        return null;
    }

    @Override
    public List<String> getSuffixes(String permission) {
        return null;
    }

    @Override
    protected String getSuffix(LinkedList<String> permission) {
        return null;
    }

    @Override
    protected List<String> getSuffixes(LinkedList<String> permission) {
        return null;
    }

    @Override
    public void clearCacheCache() {

    }

    @Override
    public void removeCheckCache(String... permissions) {

    }

    @Override
    public void removeCheckCacheUnder(String... permissions) {

    }

    @Override
    public void clearSuffixCache() {

    }

    @Override
    public void clearSuffixesCache() {

    }

    @Override
    public void removeSuffixCache(String... permissions) {

    }

    @Override
    public void removeSuffixesCache(String... permissions) {

    }

    @Override
    public void removeSuffixCacheUnder(String... permissions) {

    }

    @Override
    public void removeSuffixesCacheUnder(String... permissions) {

    }
}
