package studio.blacktech.rbac;

import java.util.Collection;
import java.util.LinkedList;

/**
 * RBAC is unified util class for entrance
 * <p>
 * Using static factory method to get instance of User and Role
 */
public class RBAC {

    //= ================================================================================================================
    //= Setting
    //= ================================================================================================================

    /**
     * This will change default result of "check" method
     * <p>
     * WARN: This is advance usage, You don't even ever never need change this.
     *
     * @param value Default behavior
     */
    public static void setLiteRoleDefaultBehavior(boolean value) {
        LiteRole.defaultBehavior = value;
    }

    /**
     * This will change default result of "check" method
     * <p>
     * WARN: This is advance usage, You don't even ever never need change this.
     *
     * @param value Default behavior
     */
    public static void setLiteUserDefaultBehavior(boolean value) {
        LiteUser.defaultBehavior = value;
    }

    //= ================================================================================================================
    //= Factory
    //= ================================================================================================================

    /**
     * And static Role. Always return true
     *
     * @return FreePassRole
     *
     * @see FreePassRole
     */
    public static FreePassRole getFreePassRole() {
        return new FreePassRole();
    }

    /**
     * And static Role. Always return false
     *
     * @return NonePassRole
     *
     * @see NonePassRole
     */
    public static NonePassRole getNonePassRole() {
        return new NonePassRole();
    }

    /**
     * Get a brand-new empty Role instance
     *
     * @return Shiny new LiteRole instance
     */
    public static LiteRole getRole() {
        return LiteRole.getInstance();
    }

    public static LiteRole ofParent(LiteRole... parents) {
        return LiteRole.ofParent(parents);
    }

    public static LiteRole ofPositive(String... permission) {
        return LiteRole.ofPositive(permission);
    }

    public static LiteRole ofNegative(String... permission) {
        return LiteRole.ofNegative(permission);
    }

    public static LiteRole ofPositive(Collection<String> permission) {
        return LiteRole.ofPositive(permission);
    }

    public static LiteRole ofNegative(Collection<String> permission) {
        return LiteRole.ofNegative(permission);
    }

    public static LiteRole of(Collection<String> positive, Collection<String> negative) {
        return LiteRole.of(positive, negative);
    }

    public static LiteRole of(Collection<String> positive, Collection<String> negative, Collection<LiteRole> parents) {
        return LiteRole.of(positive, negative, parents);
    }

    public static LiteUser getUser(Role... roles) {
        return LiteUser.getInstance(roles);
    }

    public static FreePassUser getFreePassUser() {
        return FreePassUser.getInstance();
    }

    public static NonePassUser getNonePassUser() {
        return NonePassUser.getInstance();
    }

    //= ================================================================================================================

    protected static LinkedList<String> splitPermission(String permission) {
        LinkedList<String> sections = new LinkedList<>();
        StringBuilder builder = new StringBuilder();
        for (char chat : permission.toCharArray()) {
            if (chat == '.') {
                sections.add(builder.toString());
                builder.setLength(0);
            } else {
                builder.append(chat);
            }
        }
        sections.add(builder.toString());
        return sections;
    }
}

