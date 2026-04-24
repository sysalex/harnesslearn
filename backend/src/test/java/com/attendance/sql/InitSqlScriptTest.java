package com.attendance.sql;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/**
 * 用结构性测试提前挡住 SQL 初始化脚本里最容易漏掉的建表顺序问题。
 */
class InitSqlScriptTest {

    private static final Path INIT_SQL_PATH = Path.of("..", "sql", "init.sql");
    private static final Pattern CREATE_TABLE_PATTERN =
            Pattern.compile("CREATE TABLE\\s+`([^`]+)`\\s*\\(", Pattern.CASE_INSENSITIVE);
    private static final Pattern ALTER_TABLE_PATTERN =
            Pattern.compile("ALTER TABLE\\s+`([^`]+)`", Pattern.CASE_INSENSITIVE);
    private static final Pattern REFERENCES_PATTERN =
            Pattern.compile("REFERENCES\\s+`([^`]+)`", Pattern.CASE_INSENSITIVE);

    @Test
    void initSqlDefinesSchemaWithoutForwardForeignKeyReferences() throws IOException {
        String script = Files.readString(INIT_SQL_PATH);

        Map<String, Integer> createTableOrder = extractCreateTableOrder(script);
        Map<String, Set<String>> createTableReferences = extractCreateTableReferences(script);

        assertTrue(createTableOrder.keySet().containsAll(Set.of(
                "department",
                "user",
                "attendance_rule",
                "attendance_record",
                "leave_application",
                "makeup_application")));
        assertTrue(script.contains("INSERT INTO `user`"));
        assertTrue(script.contains("'admin'"));
        assertTrue(script.contains("INSERT INTO `attendance_rule`"));
        assertTrue(script.contains("`createdByUserId`"));
        assertTrue(script.contains("`createdByUserName`"));
        assertTrue(script.contains("`createdTime`"));
        assertTrue(script.contains("`updatedByUserId`"));
        assertTrue(script.contains("`updatedByUserName`"));
        assertTrue(script.contains("`updatedTime`"));
        assertTrue(script.contains("`enabledFlag`"));
        assertTrue(script.contains("`deletedFlag`"));

        Set<String> violations = new HashSet<>();
        for (Map.Entry<String, Set<String>> entry : createTableReferences.entrySet()) {
            int sourceOrder = createTableOrder.get(entry.getKey());
            for (String referencedTable : entry.getValue()) {
                Integer referencedOrder = createTableOrder.get(referencedTable);
                if (referencedOrder != null && referencedOrder > sourceOrder) {
                    violations.add(entry.getKey() + " -> " + referencedTable);
                }
            }
        }

        assertTrue(
                violations.isEmpty(),
                "Foreign keys in CREATE TABLE must not reference tables defined later: " + violations);
    }

    @Test
    void deferredForeignKeysAreAddedAfterAllReferencedTablesExist() throws IOException {
        String script = Files.readString(INIT_SQL_PATH);
        Map<String, Integer> createTableOrder = extractCreateTableOrder(script);

        Matcher matcher = ALTER_TABLE_PATTERN.matcher(script);
        while (matcher.find()) {
            String statement = extractStatement(script, matcher.start());
            String sourceTable = matcher.group(1);
            int sourceOrder = createTableOrder.getOrDefault(sourceTable, Integer.MAX_VALUE);

            Matcher referencesMatcher = REFERENCES_PATTERN.matcher(statement);
            while (referencesMatcher.find()) {
                String referencedTable = referencesMatcher.group(1);
                int referencedOrder = createTableOrder.getOrDefault(referencedTable, Integer.MAX_VALUE);
                assertTrue(
                        referencedOrder < sourceOrder || referencedOrder != Integer.MAX_VALUE,
                        "ALTER TABLE " + sourceTable + " references an undefined table: " + referencedTable);
            }
        }
    }

    private Map<String, Integer> extractCreateTableOrder(String script) {
        Map<String, Integer> createTableOrder = new HashMap<>();
        Matcher matcher = CREATE_TABLE_PATTERN.matcher(script);
        int order = 0;
        while (matcher.find()) {
            createTableOrder.put(matcher.group(1), order++);
        }
        return createTableOrder;
    }

    private Map<String, Set<String>> extractCreateTableReferences(String script) {
        Map<String, Set<String>> referencesByTable = new HashMap<>();
        Matcher matcher = CREATE_TABLE_PATTERN.matcher(script);
        while (matcher.find()) {
            String tableName = matcher.group(1);
            String statement = extractStatement(script, matcher.start());
            Matcher referencesMatcher = REFERENCES_PATTERN.matcher(statement);
            Set<String> references = new HashSet<>();
            while (referencesMatcher.find()) {
                references.add(referencesMatcher.group(1));
            }
            referencesByTable.put(tableName, references);
        }
        return referencesByTable;
    }

    private String extractStatement(String script, int statementStart) {
        int statementEnd = script.indexOf(';', statementStart);
        if (statementEnd < 0) {
            return script.substring(statementStart);
        }
        return script.substring(statementStart, statementEnd + 1);
    }
}
