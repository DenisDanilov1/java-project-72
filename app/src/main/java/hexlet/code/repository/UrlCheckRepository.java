package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;

public class UrlCheckRepository extends BaseRepository {
    public static void save(UrlCheck check) throws SQLException {
        String sql = "INSERT INTO url_checks "
                + "(url_id, status_code, h1, title, description, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        var createdAt = new Timestamp(System.currentTimeMillis());

        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, check.getUrlId());
            stmt.setInt(2, check.getStatusCode());
            stmt.setString(3, check.getH1());
            stmt.setString(4, check.getTitle());
            stmt.setString(5, check.getDescription());
            stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));

            stmt.executeUpdate();
            var keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                check.setId(keys.getLong(1));
                check.setCreatedAt(createdAt.toLocalDateTime());
            } else {
                throw new SQLException("Url not found");
            }
        }
    }

    public static List<UrlCheck> getChecks(Long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY created_at DESC LIMIT 1";

        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, urlId);
            var checks = stmt.executeQuery();
            var result = new ArrayList<UrlCheck>();

            while (checks.next()) {
                var id = checks.getLong("id");
                var statusCode = checks.getInt("status_code");
                var title = checks.getString("title");
                var h1 = checks.getString("h1");
                var desc = checks.getString("description");
                var createdAt = checks.getTimestamp("created_at");
                var check = new UrlCheck(statusCode, title, h1, desc, urlId);
                check.setId(id);
                check.setCreatedAt(createdAt.toLocalDateTime());
                result.add(check);
            }
            return result;
        }
    }

    public static Optional<UrlCheck> getCheck(Long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY created_at DESC LIMIT 1";

        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, urlId);
            var result = stmt.executeQuery();

            if (result.next()) {
                var id = result.getLong("id");
                var statusCode = result.getInt("status_code");
                var title = result.getString("title");
                var h1 = result.getString("h1");
                var desc = result.getString("description");
                var createdAt = result.getTimestamp("created_at");
                var check = new UrlCheck(statusCode, title, h1, desc, urlId);
                check.setId(id);
                check.setCreatedAt(createdAt.toLocalDateTime());

                return Optional.of(check);
            }
            return Optional.empty();
        }
    }

    public static Map<Long, UrlCheck> getAllChecks() throws SQLException {
        var sql = "SELECT DISTINCT ON (url_id) * FROM url_checks ORDER BY url_id, created_at";

        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var checks = stmt.executeQuery();
            var result = new LinkedHashMap<Long, UrlCheck>();

            while (checks.next()) {
                var id = checks.getLong("id");
                var urlId = checks.getLong("url_id");
                var statusCode = checks.getInt("status_code");
                var title = checks.getString("title");
                var h1 = checks.getString("h1");
                var desc = checks.getString("description");
                var createdAt = checks.getTimestamp("created_at");
                var check = new UrlCheck(statusCode, title, h1, desc, urlId);
                check.setId(id);
                check.setCreatedAt(createdAt.toLocalDateTime());

                result.put(id, check);
            }
            return result;
        }
    }

//    public static Map<Long, UrlCheck> getLastUrlsCheck() throws SQLException {
//        var sql = "SELECT url_id, status_code, MAX(created_at) AS created_at "
//                + "FROM url_checks GROUP BY url_id, status_code";
//        try (var conn = dataSource.getConnection();
//             var preparedStatement = conn.prepareStatement(sql)) {
//            var resultSet = preparedStatement.executeQuery();
//            var result = new LinkedHashMap<Long, UrlCheck>();
//            while (resultSet.next()) {
//                var urlId = resultSet.getLong("url_id");
//                var statusCode = resultSet.getInt("status_code");
//                var title = resultSet.getString("title");
//                var h1 = resultSet.getString("h1");
//                var desc = resultSet.getString("description");
//                var createdAt = resultSet.getTimestamp("created_at");
//                var check = new UrlCheck(statusCode, title, h1, desc, urlId);
//                check.setUrlId(urlId);
//                check.setStatusCode(statusCode);
//                check.setCreatedAt(createdAt.toLocalDateTime());
//                result.put(urlId, check);
//            }
//            return result;
//        }
//    }
}
