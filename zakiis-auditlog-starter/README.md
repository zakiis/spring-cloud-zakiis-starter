## zakiis-auditlog-starter

### Introduce

`zakiis-auditlog-starter` is designed for collecting audit log for java programs which is focused on the security of every request and need to keep the ability to query any request that happend.

It's consisted by two modules as the following describes:

- zakiis-auditlog-client-starter : using by each business java program, It's responsible for collecting audit log and publish it to the message queue.

- zakiis-auditlog-server-starter : using it standalone, It's not related to any of your business program, `zakiis-auditlog-server-starter` provides the ability to consume audit log from message queue and  persist it (by default to Elastic search), and provide a query interface to make the audit log visible.

The whole flow can refer the following picture.

![audit log flow](readme_files/audit%20log%20flow.drawio.png)

### How to use it

#### Integrated with zakiis-auditlog-client-starter

1. add dependency in your project

```xml
<dependencyManagement>
	<dependencies>
		<dependency>
			<groupId>io.github.zakiis</groupId>
			<artifactId>spring-cloud-zakiis-dependencies</artifactId>
			<version>${spring-cloud-zakiis-dependencies.version}</version>
			<scope>import</scope>
			<type>pom</type>
		</dependency>
	</dependencies>
</dependencyManagement>

<dependencies>
	<dependency>
		<groupId>io.github.zakiis</groupId>
		<artifactId>zakiis-auditlog-client-starter</artifactId>
	</dependency>
    <!-- choose one MQ broker binder, we using rocketmq now, you can use rabbitmq instead -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-stream-rocketmq</artifactId>
        <version>2022.0.0.0-RC1</version>
    </dependency>
</dependencies>
```

2. implement the interface `LoginUserService` and `RequestService`

Each system has it's own IDaaS and trace logic, we need you implements that two interface and register it to spring context, so that we can retrieve the information and build the audit log message. as the most scenario, you can implement is like the following.

**XxxSystemLoginUserService**

```java
@Service
public class XxxSystemLoginUserService implements LoginUserService {

	@Override
	public String getUserId() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = request.getHeader("your token header name");
        //and get user id from token
		return "xxx";
	}

	@Override
	public String getLoginAccount() {
        //TODO get login account from request
		return "xxx";
	}

	@Override
	public String getRealName() {
        //TODO get real name from request
		return "xxx";
	}

}
```

**XxxSystemRequestService**

```java
public class XxxSystemRequestService implements RequestService {

	@Override
	public String getIp() {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        //TODO how to retrieve real ip depends on your project environment
        return request.getRemoteAddr();
	}

	@Override
	public String getUserAgent() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("user-agent");
	}

	@Override
	public String getTraceId() {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("your trace id header");
	}

}
```

3. add MQ binder config

```yaml
spring:
  cloud:
    stream:
      bindings:
        auditLog-out-0: #producer
          destination: audit-log #topic
          content-type: application/json
      output-bindings: auditLog #由于没用使用Supplier, Function函数接口的Bean作为发送者，这里手动绑定,多个用;分隔
      binders:
        rocketmq1: #可以指定多个
          type: rocketmq
          environment:
            spring:
              cloud:
                stream:
                  rocketmq:
                    binder:
                      name-server: 192.168.137.101:9876
                      access-key:
                      secret-key:
```

4. using `@AuditLog` annotation on the method if you need recored.

```java
@GetMapping("/user/detail/{userId}")
@AuditLog(operateGroup = "System Management", operateTarget = "User",
          operateType = "VIEW",operateTargetIdEL = "#userId",
          operateDesc = "view user info")
public CommonResp<UserInfo> userInfo(@PathVariable Long userId) {
	UserInfo dbUser = fakeUserService.selectByUserId(userId);
	AssertUtil.notNull(dbUser, "User not exists");
	return CommonResp.success(dbUser);
}

// if operate target id is array, using join method, if it is list, using joinList
@DeleteMapping("/user/batch")
@AuditLog(operateGroup = "System Management", operateTarget = "User",
          operateType = "DELETE",operateTargetIdEL = "#join(#userIds)",
          operateDesc = "delete user")
public CommonResp<Object> deleteUser(@RequestBody Long[] userIds) {
	log.info("Received user delete request, user Ids:{}", userIds.toString());
	return CommonResp.success();
}

```

More detail you can refer the demo project [zakiis-auditlog-client-demo](https://github.com/zakiis/zakiis-framework-demo/tree/main/zakiis-auditlog-demo/zakiis-auditlog-client-demo)

#### Integrated with zakiis-auditlog-server-starter

start a audit log server instance is quite easier than client. you just need add the dependency and configure the configuration file.

1. add dependency in your project

```
<dependencyManagement>
	<dependencies>
		<dependency>
			<groupId>io.github.zakiis</groupId>
			<artifactId>spring-cloud-zakiis-dependencies</artifactId>
			<version>${spring-cloud-zakiis-dependencies.version}</version>
			<scope>import</scope>
			<type>pom</type>
		</dependency>
	</dependencies>
</dependencyManagement>

<dependencies>
	<dependency>
		<groupId>io.github.zakiis</groupId>
		<artifactId>zakiis-auditlog-server-starter</artifactId>
	</dependency>
    <!-- choose one MQ broker binder, we using rocketmq now, you can use rabbitmq instead -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-stream-rocketmq</artifactId>
        <version>2022.0.0.0-RC1</version>
    </dependency>
</dependencies>
```

2. modify the configuration file

```yaml
spring:
  cloud:
    stream:
      bindings:
        auditLog-in-0: #consumer
          destination: audit-log #topic
          content-type: application/json
          # binder: rocketmq1
      default-binder: rocketmq1  #默认binder
      binders:
        rocketmq1: #可以指定多个
          type: rocketmq
          environment:
            spring:
              cloud:
                stream:
                  rocketmq:
                    binder:
                      name-server: 192.168.137.101:9876
                      access-key:
                      secret-key:
  elasticsearch:
    uris:
    - http://192.168.137.101:9200
    username: elastic
    password: ${ELASTICSEARCH_PASSWORD:elastic}
```

more details you can refer in [zakiis-auditlog-server-demo](https://github.com/zakiis/zakiis-framework-demo/tree/main/zakiis-auditlog-demo/zakiis-auditlog-server-demo)
