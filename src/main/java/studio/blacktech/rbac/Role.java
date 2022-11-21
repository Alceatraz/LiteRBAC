package studio.blacktech.rbac;

import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * LiteRBAC provide and define of Role and an implement called LiteRole
 * <p>
 * HINT: Protected method is proprietary for LiteRole, To avoid multiple times
 * parse which cost much.
 *
 * @see LiteRole The core function of LiteRBAC
 * @see FreePassRole Always return true
 * @see NonePassRole Always return false
 */
public abstract class Role {

    //= ========================================================================
    //= API for Permission check

    public abstract boolean check(String permission);

    protected abstract Boolean check(LinkedList<String> sections);

    //= ========================================================================
    //= API for Permission operation

    public abstract void appendPositive(String... permissions);

    public abstract void appendNegative(String... permissions);

    public abstract void appendPositive(Collection<String> permissions);

    public abstract void appendNegative(Collection<String> permissions);

    //= ========================================================================
    //= API for Permission operation

    public abstract void clearPermission();

    //= ========================================================================
    //= API for Inherit operation

    public abstract void appendParent(Role... roles);

    public abstract void removeParent(Role... roles);

    public abstract void clearParents();

    public abstract List<Role> listParents();

    //= ========================================================================
    //= API for Value store

    public abstract String getSuffix(String permission);

    public abstract List<String> getSuffixes(String permission);

    protected abstract String getSuffix(LinkedList<String> sections);

    protected abstract List<String> getSuffixes(LinkedList<String> sections);

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

    //= ========================================================================
    //= API for Nodes

    public abstract PermissionList listPermission();

    public abstract PermissionList listPermission(boolean includeInherit);

    public abstract PermissionList listPermission(boolean includeInherit, boolean sort);

    protected abstract void listPermission(Collection<String> positive, Collection<String> negative, boolean inherit);

    //= And its Data record

    public record PermissionList(
        Collection<String> positive,
        Collection<String> negative
    ) {

        public static PermissionList of(
            Collection<String> positive,
            Collection<String> negative
        ) {
            return new PermissionList(
                positive,
                negative
            );
        }

        public void print() {
            this.print(System.out);
        }

        public void print(PrintStream printStream) {
            StringBuilder builder = new StringBuilder();
            builder.append(">> Positive ");
            builder.append(this.positive.size());
            builder.append("\n");
            for (String item : this.positive) {
                builder.append(item);
                builder.append("\n");
            }
            builder.append(">> Negative ");
            builder.append(this.negative.size());
            builder.append("\n");
            for (String item : this.negative) {
                builder.append(item);
                builder.append("\n");
            }
            printStream.println(builder);
        }
    }
}