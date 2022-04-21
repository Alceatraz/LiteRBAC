package studio.blacktech.rbac.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import studio.blacktech.rbac.LiteUser;
import studio.blacktech.rbac.RBAC;
import studio.blacktech.rbac.Role;

import java.util.List;
import java.util.TreeMap;

public class LiteRBACTest {

    @Test
    public void test1() {
        Role role = RBAC.getRole();
        role.appendPositive(
            "essential.home.*.set",
            "essential.home.*.goto",
            "essential.home.limit.5"
        );
        role.listPermission().print();
        Assertions.assertTrue(role.check("essential.home.home1.set"));
        Assertions.assertTrue(role.check("essential.home.home1.goto"));
        Assertions.assertEquals(role.getSuffix("essential.home.limit"), "5");
    }

    @Test
    public void test2() {
        Role role = RBAC.getRole();
        role.appendPositive(
            "bukkit.*.help"
        );
        role.appendNegative(
            "bukkit.shutdown.*"
        );
        role.listPermission().print();
        Assertions.assertTrue(role.check("bukkit.title.help"));
        Assertions.assertFalse(role.check("bukkit.shutdown.help"));
    }

    @Test
    public void test3() {
        Role role1 = RBAC.getRole();
        role1.appendPositive(
            "auth-me.pending.effect"
        );
        role1.appendNegative(
        );
        Role role2 = RBAC.getRole();
        role2.appendPositive(

        );
        role2.appendNegative(
            "auth-me.pending.effect"
        );
        role2.appendParent(role1);
        role1.listPermission().print();
        role2.listPermission().print();
        Assertions.assertFalse(role2.check("auth-me.pending.effect"));
    }

    @Test
    public void test4() {

        Role role1 = RBAC.getRole();
        Role role2 = RBAC.getRole();
        Role role3 = RBAC.getRole();
        Role role4 = RBAC.getRole();

        role1.appendPositive(
            "weight.1",
            "name.[Level-1]"
        );


        role2.appendPositive(
            "weight.2",
            "name.[Level-2]"
        );

        role3.appendPositive(
            "weight.3",
            "name.[Level-3]"
        );

        role4.appendPositive(
            "weight.4",
            "name.[Level-4]"
        );

        Role role = RBAC.getRole();

        TreeMap<Integer, Role> treeMap = new TreeMap<>();

        treeMap.put(Integer.valueOf(role1.getSuffix("weight")), role1);
        treeMap.put(Integer.valueOf(role2.getSuffix("weight")), role2);
        treeMap.put(Integer.valueOf(role3.getSuffix("weight")), role3);
        treeMap.put(Integer.valueOf(role4.getSuffix("weight")), role4);

        treeMap.forEach((k, v) -> role.appendParent(v));

        role.listPermission(true).print();

        Assertions.assertEquals(role.getSuffix("weight"), "4");
    }


    @Test
    public void test5() {
        Role role = RBAC.of(
            List.of(
                "ess.command"
            ),
            List.of(
                "ess.command.*",
                "ess.command.**"
            )
        );
        Assertions.assertTrue(role.check("ess.command"));
        Assertions.assertFalse(role.check("ess.command.help"));
    }

    @Test
    public void test6() {

        Role role1 = RBAC.of(
            List.of("ess.tp1.*.invoke"),
            List.of("ess.tp1.admin.invoke")
        );

        Role role2 = RBAC.of(
            List.of("ess.tp2.*.invoke"),
            List.of("ess.tp2.admin.invoke")
        );

        Role role3 = RBAC.of(
            List.of("ess.tp3.*.invoke"),
            List.of("ess.tp3.admin.invoke")
        );

        LiteUser user = RBAC.getUser(role1, role2, role3);
        Assertions.assertTrue(user.check("ess.tp3.home.invoke"));
    }

}