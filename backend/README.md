# backend · 后端服务（M3 / M4 负责）

Spring Boot 3.2.5 + JDK 17 + MyBatis-Plus 3.5.7 + MySQL 8.0。一个进程同时提供 **REST API** 与 **WebSocket**，默认 8080 端口。

## 一、快速开始

```bash
# 1) 建库（只需一次）
mysql -u root -p classroom < src/main/resources/db/schema.sql

# 2) 编译打包（跳过测试）
mvn -DskipTests package

# 3) 运行（用本地配置覆盖密码）
java -jar target/classroom-0.1.0.jar --spring.profiles.active=local
```

- 接口文档：<http://localhost:8080/swagger-ui.html>
- 健康检查：<http://localhost:8080/api/auth/ping> → `{"code":0,"message":"pong"}`
- 本机没有 MySQL 也能启动（`hikari.initialization-fail-timeout=-1`），只是数据接口会报错。

## 二、目录约定（不要越界改）

| 目录 | 归属 | 职责 |
| :--- | :--- | :--- |
| `controller/` | M3 | 接参、校验、调用 Service、返回 `Result` |
| `service/` | M3 | 业务规则与事务边界；可以调用 `websocket` 推送 |
| `mapper/`（Java + `resources/mapper/*.xml`） | M3 | 只写 SQL，不写业务判断 |
| `entity/` | M3 | 与表一一对应，`@TableName` 映射 |
| `config/` | M3 | 跨域、MyBatis-Plus、Swagger、鉴权 |
| `utils/` | M3 | `Result`、`ResultCode`、`JwtUtil`、`MaskUtil` 等 |
| `exception/` | M3 | `BizException` + 全局异常处理 |
| `websocket/` | **M4** | 握手、会话管理、房间广播、心跳 |

## 三、新增一个接口的标准流程（5 步）

1. **先改文档**：在 `docs/04-接口契约模板.md` 补请求/响应/错误码，PR 描述里贴出来。
2. **写 Mapper**：单表用 `BaseMapper`，复杂统计写 XML 并 `EXPLAIN` 验证走索引。
3. **写 Service**：业务规则放这里；可预期失败抛 `BizException`，不要 `return null` 让上层猜。
4. **写 Controller**：`@Valid` 校验 + `Result.ok(...)` 返回；Controller 里不写 if/else 业务分支。
5. **自测**：Swagger 里点一遍 + Postman 存用例；涉及并发（签到/抢答）必须补压测记录。

## 四、统一响应与错误码

```java
return Result.ok(data);                            // 成功
return Result.ok();                                // 成功无数据
return Result.fail(ResultCode.DUPLICATE_CHECKIN);  // 业务失败（1003）
throw new BizException(ResultCode.CLASS_CLOSED);   // 由全局处理器转成响应
```

错误码字典唯一来源是 `ResultCode`，与 `docs/04 §0.3` 一一对应；新增码必须两边同时改。

> 注意：当前 Controller 里的占位方法仍返回 `Map`（按初始化要求），M3 实现时统一改成 `Result<T>`。

## 五、配置说明

| 文件 | 用途 | 是否入库 |
| :--- | :--- | :--- |
| `application.yml` | 公共配置，开箱即跑 | ✅ 入库 |
| `application-local.yml.example` | 本地密码模板 | ✅ 入库（仅模板） |
| `application-local.yml` | 你本机的真实密码 | ❌ 已被 `.gitignore` 忽略 |

环境变量优先：`DB_USERNAME` / `DB_PASSWORD`（未设置时回退到 `root` / `123456`）。

## 六、自测清单（提 PR 前必过）

```bash
mvn -q -DskipTests package     # 编译 + 打包
mvn -q test                    # 单元测试（不依赖数据库）
java -jar target/classroom-0.1.0.jar   # 起得来
curl http://localhost:8080/api/auth/ping
```

## 七、常见问题

| 现象 | 原因 | 处理 |
| :--- | :--- | :--- |
| 启动报 `Communications link failure` | MySQL 未启动 / 密码错 | 起服务，或用 `--spring.profiles.active=local` 填对密码 |
| `No MyBatis mapper was found` 警告 | 还没有 Mapper 接口 | 属正常，M3 添加后消失 |
| Swagger 打不开 | 端口被占用 | `netstat -ano | findstr :8080`，杀掉占用进程 |
| 中文乱码 | 库字符集不是 `utf8mb4` | 重建库时指定 `DEFAULT CHARSET=utf8mb4` |
