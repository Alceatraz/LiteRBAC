# LiteRBAC

**We keep it Simple as Lite and Fast as Light**

## Design

We think RBAC mean `Role Based Authorization Control`, Use `Node` build `Role`, And grant `Role` to `User`.

**Philosophy**

- We want it easy because it's Lite
- We want it fast because it's Light

**So**

- We don't want to use immutable design, Copy on write is performance killer
- We don't want search data everytime, Cache result, Use space trade the speed
- We don't want check status, We trust user can understand it and operation the cache manually
- We don't want it become fancy and complex, Nested call and wrapped method over and over and over
- We don't want use reflect, Its performance killer, If multi-implements needed, Just use interface

## Beginning

Sorry for boring theory, But using LiteRBAC you must know those basic, But I promise you it will as simple as possible.

### Node

Node is a permission sentence, It's a String like

```text
foo
foobar
foo.bar
foo.bar.hello
foo.bar.world
```

In some RBAC implements such as minecraft bukkit eco, permission split by `.` into **sections** and support wildcard like `essential.wrap.*`. But LiteRBAC go further, introduced wildcard `*`, trailing `**` and positive/negative.

| mode     | behavior                          |
|----------|-----------------------------------|
| asis     | match node asis the name          |
| wildcard | match any name in this section    |
| trailing | match anything after this section |

So those form permission claim are supported:

```text
foobar
foo.bar.*
foo.bar.*.help
foo.bar.admin.**
```

### Role

> Use `Node` build `Role`

On node can't do anything, So we wrap a bunch of node into Role. Role using node based storage, After permissions split into sections store them in `Node` with tree form.

Work with a bunch Node, Will introduce priority problem. Then, This is matching priority:

```text
Asis > Wildcard > Trailing
```

#### An example

Build a Role with this code

```java
Role role = RBAC.getRole();
role.appendPositive("foo.*.bar");
role.appendNegative("foo.aaa.bar");
role.check("foo.aaa.bar"); // false
```

Here is how it works:

1. From root node, find "foo". Exist, go on
2. From "foo" node, find "aaa". Exist, go on
3. From "aaa" node, find "bar". Exist, go on
4. Node "bar" is the end, This Node represents result
5. Node mark as deny, So return false.

**Notice with the step2**

Because Asis node is higher priority then Wildcard, So will ignore "*" node when "aaa" exist.

#### Inherit

When handle Role inherit, Higher priority Role will override the result of lower priority Role.

For example:

```java
role.appendParent(role1);
role.appendParent(role2);
role.appendParent(role3);
```

Internally, The LinkedList of inherit store them in this order: `role3 - role2 - role1`. When check, Role3 will invoke first, If Role3 reject this permission, `check` will return false immediately even if Role2 or Role1 accept this request. If no one answer this request, return default behavior.

### User

> Grant `Role` to `User`

User use List associated to Roles, And provide wrapped method. Also like Role, There are caches.

Difference between Role and User is policy of final decision

When checking permission:

- Role will check itself first then all inherits, And return false immediately when a reject appears. They are in series.
- User will check all roles one by one, And return true immediately when a Role return accepted. They are parallel (Logically parallel, They run in single thread).

For example:

```java
role.appednRole(role1);
role.appednRole(role2);
role.appednRole(role3);
```

Internally, The LinkedList of Roles store them in this order: `role3 - role2 - role1`. When check, Role3 will invoke first, If Role3 reject this permission, `check` will still go one invoke Role2 then Role1 util any Role accept this request. If no one answer this request, return default behavior.

### One more thing - The value store

Using limit or name-tag is very common usage in permission module, So Role provide handful function: The value store.

In-fact it's not a feature, The value store is permission node itself. Those stored value can be trade as Node and can be checked.

```java
role.appendPositive("foo.bar.hello.world.rbac");
role.appendPositive("foo.bar.limit.rate1.17.73");
role.appendPositive("foo.bar.limit.rate2.24.42");

role.getSuffix("foo.bar.hello"); // Will return "world", String
role.getSuffixes("foo.bar.hello"); // Will return "world" "rbac", List<String>
role.getSuffixes("foo.bar.limit.rate1"); // Will return "17" "73", List<String>
role.getSuffixes("foo.bar.limit.rate2"); // Will return "24" "42", List<String>
```

> Tips: Value store function dont care Node's mode, No matter positive or negative.

> Notice: The specific Node should contain one and only one sub-Node, Otherwise will rise exception.

### Example

```java
role.appendPositive(
    "foobar.**",
    "foo.bar.*.help",
    "foobar.limit1.100"
);
role.appendNegative(
    "foo.bar.secret.help",
    "foobar.limit2.100.200"
);
```

- All permission starts with "foobar"
- All "help" permission under "foo.bar" but not "foo.bar.secret.help"
- Get the value of "foobar.limit1" which is "100" (A String)
- Get the value of "foobar.limit2" which is "100","200" (A List<String>)

## Attention

> **TL,DR:** If you modify User/Role instance after query, You may feel haunted. Because they have result cache for `check` `getSuffix` `getSuffixes`. You need clean the cache to speed up.

> **BTW:** If you don't modify permission after build, Cache won't be a problem, And thread safe won't be an issue.

### Cache issue

To make thing going easier, We presume you using immutable design in your RBAC system, Which mean once User/Role initialized, They are read only. So that's why only appendPermission/clearPermission method but no removePermission.

Depend on this presume, We introduce result cache to avoid full Node search in each `check` `suffix` `suffixes`. Once queried, The cache will save into Map for next query.

If you truly need hot reloaded feature, Remember clear cache after modify Role. And if you don't want clean all cache which will cause performance impact, You can clean specific caches with asis or prefix matching:

**NOTICE: clear/remove cache is recursive, All parent will execute clean process**

| method                 | behavior                                                                    |
|------------------------|-----------------------------------------------------------------------------|
| clearCheckCache        | clear cache of check method, Clear all or specific one/many                 |
| clearSuffixCache       | clear cache of getSuffix method, Clear all or specific one/many             |
| clearSuffixesCache     | clear cache of getSuffixes method, Clear all or specific one/many           |
| removeCheckCacheUnder  | remove cache of check method, Clear items starts with specific prefix       |
| removeSuffixCacheUnder | remove cache of getSuffix method, Clear items starts with specific prefix   |
| removeSuffixCacheUnder | remove cache of getSuffixes method, Clear items starts with specific prefix |

### Thread safe issue

Due to the immutable design, modify operation not thread safe:

| Field          | Implements        | Is it safe | And why?                         |
|----------------|-------------------|------------|----------------------------------|
| Nodes          | HashMap           | No         | HashMap used for sub-Nodes store |
| Inherit        | LinkedList        | No         | Parent store in LinkedList       |
| Check cache    | ConcurrentHashMap | Yes        | ConcurrentHashMap is thread safe |
| Suffix cache   | ConcurrentHashMap | Yes        | ConcurrentHashMap is thread safe |
| Suffixes cache | ConcurrentHashMap | Yes        | ConcurrentHashMap is thread safe |

## Advance

**CAUTION: THOSE OPERATIONS ARE SUPER ADVANCED, BE REALLY CAREFULLY**

### Default Behavior

**This function not fully tested**

Inherit introduce third status of permission check: Yes/No/Unknown, So if all Roles not config the specific permission. There will be a default result. You can set it with `RBAC.setLiteRoleDefaultBehavior(true/false)`, And default behavior is false.
