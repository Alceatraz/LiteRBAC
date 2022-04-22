package studio.blacktech.rbac.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import studio.blacktech.rbac.LiteUser;
import studio.blacktech.rbac.RBAC;
import studio.blacktech.rbac.Role;
import studio.blacktech.rbac.User;

import java.util.List;

/**
 * Too much minecraft example. But hey, It's well known :)
 */
public class LiteRBACTest {

    @Test
    public void test1() {

        Role role = RBAC.getRole();

        role.appendPositive(
            "essential.home.*.set",
            "essential.home.*.goto",
            "essential.home.limit.5"
        );

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

        Assertions.assertTrue(role.check("bukkit.title.help"));
        Assertions.assertFalse(role.check("bukkit.shutdown.help"));
    }

    @Test
    public void test3() {

        Role role1 = RBAC.getRole();

        role1.appendPositive(
            "authme.pending.effect"
        );

        role1.appendNegative(

        );

        Role role2 = RBAC.getRole();

        role2.appendPositive(

        );

        role2.appendNegative(
            "authme.pending.effect"
        );

        role2.appendParent(role1);

        Assertions.assertFalse(role2.check("authme.pending.effect"));
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

        role.appendParent(role1);
        role.appendParent(role2);
        role.appendParent(role3);
        role.appendParent(role4);

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


    @Test
    public void test7() {

        Role role1 = RBAC.getRole();

        role1.appendPositive(
            "authme.command.login",
            "authme.command.register",
            "authme.command.helpop"
        );

        Role role2 = RBAC.getRole();

        role2.appendPositive(
            "authme.command.email",
            "authme.command.recovery",
            "essential.command.spawn",
            "essential.command.*.spawn"
        );

        Role role3 = RBAC.getRole();

        role3.appendPositive(
            "essential.home",
            "essential.home.limit.5",
            "essential.sethome",
            "essential.tpa.*",
            "essential.wrap.*",
            "worldguard.*.build",
            "worldguard.*.pick",
            "worldguard.*.drop",
            "worldguard.*.interact",
            "worldguard.*.container",
            "worldguard.*.use",
            "worldguard.spawn.use",
            "worldguard.spawn.interact"
        );

        role3.appendNegative(
            "worldguard.spawn.*"
        );

        Role role4 = RBAC.getRole();

        role4.appendPositive(
            "essential.**",
            "essential.home.limit.20",
            "worldguard.spawn.*"
        );

        User user1 = RBAC.getUser(role1);
        User user2 = RBAC.getUser(role1, role2);
        User user3 = RBAC.getUser(role1, role2, role3);
        User user4 = RBAC.getUser(role1, role2, role3, role4);
        User user5 = RBAC.getFreePassUser();

        Assertions.assertTrue(user1.check("authme.command.helpop"));
        Assertions.assertTrue(user2.check("essential.command.world.spawn"));
        Assertions.assertTrue(user3.check("worldguard.spawn.use"));
        Assertions.assertTrue(user4.check("worldguard.spawn.build"));
        Assertions.assertTrue(user5.check("bukkit.command.op"));

        Assertions.assertEquals(user4.getSuffix("essential.home.limit"), "20");
    }

}
