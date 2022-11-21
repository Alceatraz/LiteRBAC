package studio.blacktech.rbac;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implements of Role. Using node based permission store. Use cache for
 * speed up query, And support simple value store function.
 * <p>
 * Constructor and instantiation method is protected, Therefore you must invoke
 * factory method in RBAC.class
 */
public class LiteRole extends Role {

    //= ================================================================================================================
    //= Settings
    //= ================================================================================================================

    protected static boolean defaultBehavior = false;

    //= ================================================================================================================
    //= Filed
    //= ================================================================================================================

    private final Node node; // This is root node, Store all permission data inside.

    private final LinkedList<Role> inherit;

    private final ConcurrentHashMap<String, Boolean> checkCache;

    private final ConcurrentHashMap<String, String> suffixCache;
    private final ConcurrentHashMap<String, List<String>> suffixesCache;

    //= ================================================================================================================
    //= Efficient java section
    //= ================================================================================================================

    protected static LiteRole getInstance() {
        return new LiteRole();
    }

    protected static LiteRole ofParent(LiteRole... parents) {
        LiteRole role = new LiteRole();
        Collections.addAll(role.inherit, parents);
        return role;
    }

    protected static LiteRole ofPositive(String... permission) {
        LiteRole role = new LiteRole();
        role.appendPositive(permission);
        return role;
    }

    protected static LiteRole ofPositive(Collection<String> permission) {
        LiteRole role = new LiteRole();
        role.appendPositive(permission);
        return role;
    }

    protected static LiteRole ofNegative(String... permission) {
        LiteRole role = new LiteRole();
        role.appendNegative(permission);
        return role;
    }

    protected static LiteRole ofNegative(Collection<String> permission) {
        LiteRole role = new LiteRole();
        role.appendNegative(permission);
        return role;
    }

    protected static LiteRole of(
        Collection<String> positivePermission,
        Collection<String> negativePermission
    ) {
        LiteRole role = new LiteRole();
        role.appendPositive(positivePermission);
        role.appendNegative(negativePermission);
        return role;
    }

    protected static LiteRole of(
        Collection<String> positivePermission,
        Collection<String> negativePermission,
        Collection<LiteRole> parents
    ) {
        LiteRole role = new LiteRole();
        role.appendPositive(positivePermission);
        role.appendNegative(negativePermission);
        role.inherit.addAll(parents);
        return role;
    }

    //= ================================================================================================================
    //= Constructor section
    //= ================================================================================================================

    private LiteRole() {
        this(
            new Node(false),
            new LinkedList<>(),
            new ConcurrentHashMap<>(),
            new ConcurrentHashMap<>(),
            new ConcurrentHashMap<>()
        );
    }

    private LiteRole(
        Node node,
        LinkedList<Role> inherit,
        ConcurrentHashMap<String, Boolean> checkCache,
        ConcurrentHashMap<String, String> suffixCache,
        ConcurrentHashMap<String, List<String>> suffixesCache
    ) {
        this.node = node;
        this.inherit = inherit;
        this.checkCache = checkCache;
        this.suffixCache = suffixCache;
        this.suffixesCache = suffixesCache;
    }

    //= ================================================================================================================
    //= Public API: Permission
    //= ================================================================================================================

    /**
     * Append positive permission
     *
     * @param permissions permission
     */
    @Override
    public void appendPositive(String... permissions) {
        for (String permission : permissions) {
            this.node.appendPermission(false, permission);
        }
    }

    /**
     * Append positive permission
     *
     * @param permissions permission
     */
    @Override
    public void appendPositive(Collection<String> permissions) {
        for (String permission : permissions) {
            this.node.appendPermission(false, permission);
        }
    }

    /**
     * Append negative permission
     *
     * @param permissions permission
     */
    @Override
    public void appendNegative(String... permissions) {
        for (String permission : permissions) {
            this.node.appendPermission(true, permission);
        }
    }

    /**
     * Append negative permission
     *
     * @param permissions permission
     */
    @Override
    public void appendNegative(Collection<String> permissions) {
        for (String permission : permissions) {
            this.node.appendPermission(true, permission);
        }
    }

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
        Boolean result = this.node.checkPermission(sections);
        if (result != null) return result;
        for (Role role : this.inherit) {
            result = role.check(sections);
            if (result != null) return result;
        }
        return defaultBehavior;
    }

    /**
     * Clear all permission of this role
     * <p>
     * NOTICE: This will not clear cache
     * NOTICE: This will not clear inherit
     */
    @Override
    public void clearPermission() {
        this.node.node.clear();
    }

    /**
     * Clear all permission cache
     */
    @Override
    public void clearCacheCache() {
        this.checkCache.clear();
        for (Role role : this.inherit) {
            role.clearCacheCache();
        }
    }

    /**
     * Clear specific permission cache by asis
     */
    @Override
    public void removeCheckCache(String... permissions) {
        for (String permission : permissions) {
            this.checkCache.remove(permission);
            for (Role role : this.inherit) {
                role.removeCheckCache(permission);
            }
        }
    }

    /**
     * Clear specific permission cache by prefix matching
     */
    @Override
    public void removeCheckCacheUnder(String... permissions) {
        for (String permission : permissions) {
            this.checkCache.keySet().removeIf(name -> name.startsWith(permission));
            for (Role role : this.inherit) {
                role.removeCheckCacheUnder(permissions);
            }
        }
    }

    //= ================================================================================================================
    //= Public API: Inherit
    //= ================================================================================================================

    /**
     * Add parent into inherit, Insert into index 0
     *
     * @param roles parents
     */
    @Override
    public void appendParent(Role... roles) {
        for (Role role : roles) {
            this.inherit.add(0, role);
        }
    }

    /**
     * Delete parent from inherit
     * <p>
     * NOTICE: This will not clear cache
     *
     * @param roles parents
     */
    @Override
    public void removeParent(Role... roles) {
        for (Role role : roles) {
            this.inherit.remove(role);
        }
    }

    /**
     * Remove all parent
     * <p>
     * NOTICE: This will not clear cache
     */
    @Override
    public void clearParents() {
        this.inherit.clear();
    }

    @Override
    public List<Role> listParents() {
        return List.copyOf(this.inherit);
    }

    //= ================================================================================================================
    //= Public API: Value store
    //= ================================================================================================================

    /**
     * Get value in path pointed node
     * <p>
     * NOTICE: Make sure only one node in pointed nodes, Or rise exception
     *
     * @param permission path
     *
     * @return value
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
        String suffix = this.node.getSuffix(sections);
        if (suffix != null) return suffix;
        for (Role role : this.inherit) {
            suffix = role.getSuffix(sections);
            if (suffix != null) return suffix;
        }
        return null;
    }

    /**
     * Get value in path pointed nodes
     * <p>
     * NOTICE: return value is un-modifiable List
     * NOTICE: Make sure only one node in chain of pointed nodes, Or rise exception
     *
     * @param permission path
     *
     * @return value
     */
    @Override
    public List<String> getSuffixes(String permission) {
        List<String> suffix = this.suffixesCache.get(permission);
        if (suffix != null) return suffix;
        LinkedList<String> sections = RBAC.splitPermission(permission);
        suffix = getSuffixes(sections);
        this.suffixesCache.put(permission, suffix);
        return suffix;
    }

    @Override
    protected List<String> getSuffixes(LinkedList<String> sections) {
        List<String> suffix = this.node.getSuffixes(sections);
        if (suffix != null) return suffix;
        for (Role role : this.inherit) {
            suffix = role.getSuffixes(sections);
            if (suffix != null) return suffix;
        }
        return null;
    }

    /**
     * Clear suffix result cache
     */
    @Override
    public void clearSuffixCache() {
        this.suffixCache.clear();
        for (Role role : this.inherit) {
            role.clearSuffixCache();
        }
    }

    /**
     * Clear suffixes result cache
     */
    @Override
    public void clearSuffixesCache() {
        this.suffixesCache.clear();
        for (Role role : this.inherit) {
            role.clearSuffixesCache();
        }
    }

    /**
     * Remove specific suffix result cache by asis
     */
    @Override
    public void removeSuffixCache(String... permissions) {
        for (String permission : permissions) {
            this.suffixCache.remove(permission);
            for (Role role : this.inherit) {
                role.removeSuffixCache(permissions);
            }
        }
    }

    /**
     * Remove specific suffixes result cache by asis
     */
    @Override
    public void removeSuffixesCache(String... permissions) {
        for (String permission : permissions) {
            this.suffixesCache.remove(permission);
            for (Role role : this.inherit) {
                role.removeSuffixesCache(permissions);
            }
        }
    }

    /**
     * Remove specific suffix result cache by prefix matching
     */
    @Override
    public void removeSuffixCacheUnder(String... permissions) {
        for (String permission : permissions) {
            this.suffixCache.keySet().removeIf(name -> name.startsWith(permission));
            for (Role role : this.inherit) {
                role.removeSuffixCacheUnder(permissions);
            }
        }
    }

    /**
     * Remove specific suffixes result cache by prefix matching
     */
    @Override
    public void removeSuffixesCacheUnder(String... permissions) {
        for (String permission : permissions) {
            this.suffixesCache.keySet().removeIf(name -> name.startsWith(permission));
            for (Role role : this.inherit) {
                role.removeSuffixesCacheUnder(permissions);
            }
        }
    }

    //= ================================================================================================================
    //= Public API: Misc
    //= ================================================================================================================

    /**
     * List all permission
     *
     * @return permissions
     *
     * @see PermissionList
     */
    @Override
    public PermissionList listPermission() {
        return this.listPermission(false);
    }

    /**
     * List all permission
     *
     * @param includeInherit include inherit permissions
     *
     * @return permissions
     *
     * @see PermissionList
     */
    @Override
    public PermissionList listPermission(boolean includeInherit) {
        return this.listPermission(includeInherit, false);
    }

    /**
     * List all permission
     *
     * @param includeInherit include inherit permissions
     * @param sort           sort result
     *
     * @return permissions
     *
     * @see PermissionList
     */
    @Override
    public PermissionList listPermission(boolean includeInherit, boolean sort) {
        LinkedList<String> positive = new LinkedList<>();
        LinkedList<String> negative = new LinkedList<>();
        this.listPermission(positive, negative, includeInherit);
        if (sort) {
            Collections.sort(positive);
            Collections.sort(negative);
        }
        return PermissionList.of(positive, negative);
    }

    @Override
    protected void listPermission(Collection<String> positive, Collection<String> negative, boolean includeInherit) {
        this.node.listPermission(this.node, null, positive, negative);
        if (!includeInherit) return;
        for (Role role : this.inherit) {
            role.listPermission(positive, negative, true);
        }
    }

    //= ================================================================================================================
    //=
    //= Private section: Unless you want know how LiteRBAC work, Leave it alone
    //=
    //= ================================================================================================================

    private static class Node {

        private final boolean deny;
        private final HashMap<String, Node> node;

        private Node(boolean deny) {
            this.deny = deny;
            this.node = new HashMap<>();
        }

        private void appendPermission(boolean deny, String permission) {
            LinkedList<String> sections = RBAC.splitPermission(permission);
            this.appendPermission(deny, sections);
        }

        private void appendPermission(boolean deny, LinkedList<String> sections) {
            Node temp = this;
            while (true) {
                String name = sections.removeFirst();
                boolean last = sections.isEmpty();
                Node next = temp.node.get(name);
                if (next == null) {
                    next = new Node(last && deny);
                    temp.node.put(name, next);
                }
                if (last) break;
                temp = next;
            }
        }

        private void listPermission(Node instance, String prefix, Collection<String> positive, Collection<String> negative) {
            for (Entry<String, Node> entry : instance.node.entrySet()) {
                var k = entry.getKey();
                var v = entry.getValue();
                String subPrefix;
                if (prefix == null) {
                    subPrefix = k;
                } else {
                    subPrefix = prefix + "." + k;
                }
                if (v.node.isEmpty()) {
                    (v.deny ? negative : positive).add(subPrefix);
                } else {
                    this.listPermission(v, subPrefix, positive, negative);
                }
            }
        }

        private Boolean checkPermission(LinkedList<String> sections) {
            Node current = this;
            for (String section : sections) {
                if (current.node.isEmpty()) return null;
                Node next = current.node.get(section);
                if (next == null) {
                    Node wildcard = current.node.get("*");
                    if (wildcard == null) {
                        Node trailing = current.node.get("**");
                        if (trailing == null) return null;
                        current = trailing;
                        break;
                    } else current = wildcard;
                } else current = next;
            }
            return !current.deny;
        }

        private String getSuffix(LinkedList<String> sections) {
            Node current = this;
            for (String section : sections) {
                Node next = current.node.get(section);
                if (next == null) return null;
                current = next;
            }
            int size = current.node.size();
            if (size == 0) throw new IllegalStateException("No sub-node found, Check the tree.");
            if (size > 1) throw new IllegalStateException("Found more then one sub-node, Check the tree.");
            return current.node.keySet().iterator().next();
        }

        private List<String> getSuffixes(LinkedList<String> sections) {
            Node current = this;
            for (String section : sections) {
                Node next = current.node.get(section);
                if (next == null) return null;
                current = next;
            }
            LinkedList<String> temp = new LinkedList<>();
            while (true) {
                int size = current.node.size();
                if (size == 0) break;
                if (size > 1) throw new IllegalStateException("Found more then one sub-node, Check the tree.");
                Entry<String, Node> entry = current.node.entrySet().iterator().next();
                temp.add(entry.getKey());
                current = entry.getValue();
            }
            return Collections.unmodifiableList(temp);
        }
    }
}