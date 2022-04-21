package studio.blacktech.rbac;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This is a static Role implement. It will always return yes.
 */
public class FreePassRole extends Role {

    private static final List<Role> parents = List.of();
    private static final PermissionList permissionList = PermissionList.of(List.of("**"), List.of());

    @Override
    public boolean check(String permission) {
        return true;
    }

    @Override
    protected Boolean check(LinkedList<String> permission) {
        return true;
    }

    @Override
    public void appendPositive(String... permissions) {

    }

    @Override
    public void appendNegative(String... permissions) {

    }

    @Override
    public void appendPositive(Collection<String> permissions) {

    }

    @Override
    public void appendNegative(Collection<String> permissions) {

    }

    @Override
    public void clearPermission() {

    }

    @Override
    public void appendParent(Role... roles) {

    }

    @Override
    public void removeParent(Role... roles) {

    }

    @Override
    public void clearParents() {

    }

    @Override
    public List<Role> listParents() {
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
    public void removeSuffixCache(String... permission) {

    }

    @Override
    public void removeSuffixesCache(String... permission) {

    }

    @Override
    public void removeSuffixCacheUnder(String... permission) {

    }

    @Override
    public void removeSuffixesCacheUnder(String... permission) {

    }

    @Override
    public PermissionList listPermission() {
        return permissionList;
    }

    @Override
    public PermissionList listPermission(boolean includeInherit) {
        return permissionList;
    }

    @Override
    public PermissionList listPermission(boolean includeInherit, boolean sort) {
        return permissionList;
    }

    @Override
    protected void listPermission(
        Collection<String> positive,
        Collection<String> negative,
        boolean inherit
    ) {
        positive.add("**");
    }
}
