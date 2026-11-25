package com.classroom.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * M3：按课堂导出签到与答题数据。
 *
 * <p>导出直接读取现有五张表，不依赖 M1/M2/M4 的 Controller 或 Mapper，
 * 避免并行开发时互相等待。接口地址与字段顺序遵循 docs/API_CONTRACT.md。</p>
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

    private static final String[] CSV_HEADER = {
            "账号", "姓名", "签到时间", "题目", "选择", "是否正确"
    };

    private static final String REPORT_SQL = """
            SELECT
                u.username AS account,
                u.real_name AS real_name,
                DATE_FORMAT(c.create_time, '%Y-%m-%d %H:%i:%s') AS checkin_time,
                e.title AS exercise_title,
                a.choose_option AS choose_option,
                CASE WHEN a.choose_option = e.answer THEN '是' ELSE '否' END AS correct
            FROM answer a
            JOIN exercise e ON e.id = a.exercise_id
            LEFT JOIN checkin c
                ON c.classroom_id = e.classroom_id
                AND c.student_id = a.student_id
            JOIN `user` u ON u.id = a.student_id
            WHERE e.classroom_id = ?

            UNION ALL

            SELECT
                u.username AS account,
                u.real_name AS real_name,
                DATE_FORMAT(c.create_time, '%Y-%m-%d %H:%i:%s') AS checkin_time,
                NULL AS exercise_title,
                NULL AS choose_option,
                NULL AS correct
            FROM checkin c
            JOIN `user` u ON u.id = c.student_id
            WHERE c.classroom_id = ?
              AND NOT EXISTS (
                  SELECT 1
                  FROM answer a
                  JOIN exercise e ON e.id = a.exercise_id
                  WHERE e.classroom_id = ?
                    AND a.student_id = c.student_id
              )

            ORDER BY account, exercise_title, checkin_time
            """;

    private final JdbcTemplate jdbcTemplate;

    public ReportController(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @GetMapping(value = "/export", produces = "text/csv;charset=UTF-8")
    public ResponseEntity<byte[]> export(@RequestParam(required = false) Long classroomId) {
        if (classroomId == null || classroomId <= 0) {
            return errorResponse(HttpStatus.BAD_REQUEST, "课堂 ID 不正确");
        }

        try {
            Long classroomCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM classroom WHERE id = ?",
                    Long.class,
                    classroomId
            );
            if (classroomCount == null || classroomCount == 0) {
                return errorResponse(HttpStatus.BAD_REQUEST, "课堂不存在");
            }

            List<ReportRow> rows = jdbcTemplate.query(
                    REPORT_SQL,
                    (resultSet, rowNum) -> new ReportRow(
                            resultSet.getString("account"),
                            resultSet.getString("real_name"),
                            resultSet.getString("checkin_time"),
                            resultSet.getString("exercise_title"),
                            resultSet.getString("choose_option"),
                            resultSet.getString("correct")
                    ),
                    classroomId,
                    classroomId,
                    classroomId
            );

            byte[] csv = buildCsv(rows).getBytes(StandardCharsets.UTF_8);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
            headers.set(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"classroom_" + classroomId + "_report.csv\""
            );
            headers.setCacheControl("no-store");
            return new ResponseEntity<>(csv, headers, HttpStatus.OK);
        } catch (DataAccessException exception) {
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "报表数据读取失败");
        }
    }

    static String buildCsv(List<ReportRow> rows) {
        StringBuilder csv = new StringBuilder("\uFEFF");
        appendRow(csv, CSV_HEADER);

        for (ReportRow row : rows) {
            appendRow(
                    csv,
                    row.account(),
                    row.realName(),
                    row.checkinTime(),
                    row.exerciseTitle(),
                    row.chooseOption(),
                    row.correct()
            );
        }
        return csv.toString();
    }

    private static void appendRow(StringBuilder csv, String... values) {
        for (int index = 0; index < values.length; index++) {
            if (index > 0) {
                csv.append(',');
            }
            csv.append(csvCell(values[index]));
        }
        csv.append("\r\n");
    }

    private static String csvCell(String value) {
        if (value == null) {
            return "";
        }

        String safeValue = value;
        if (!safeValue.isEmpty() && "=+-@\t\r".indexOf(safeValue.charAt(0)) >= 0) {
            // 防止 Excel 把用户输入解释为公式。
            safeValue = "'" + safeValue;
        }

        if (safeValue.indexOf(',') >= 0
                || safeValue.indexOf('"') >= 0
                || safeValue.indexOf('\n') >= 0
                || safeValue.indexOf('\r') >= 0) {
            return "\"" + safeValue.replace("\"", "\"\"") + "\"";
        }
        return safeValue;
    }

    private static ResponseEntity<byte[]> errorResponse(HttpStatus status, String message) {
        String json = "{\"code\":" + status.value()
                + ",\"msg\":\"" + message
                + "\",\"data\":null}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        headers.setCacheControl("no-store");
        return new ResponseEntity<>(json.getBytes(StandardCharsets.UTF_8), headers, status);
    }

    record ReportRow(
            String account,
            String realName,
            String checkinTime,
            String exerciseTitle,
            String chooseOption,
            String correct
    ) {
    }
}
