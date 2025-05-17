package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static hexlet.code.repository.BaseRepository.dataSource;

public class UrlChecksRepository {

    public static UrlCheck getUrlCheckFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int urlId = resultSet.getInt("url_id");
        int statusCode = resultSet.getInt("status_code");
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        String title = resultSet.getString("title");
        String h1 = resultSet.getString("h1");
        String description = resultSet.getString("description");

        return new UrlCheck(id, statusCode, title, h1, description, urlId, createdAt);
    }

    public static List<UrlCheck> getAllChecksForUrl(long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY id DESC";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, urlId);
            var resultSet = stmt.executeQuery();

            List<UrlCheck> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(getUrlCheckFromResultSet(resultSet));
            }
            return result;
        }
    }

    public static Map<Integer, UrlCheck> getAllUrlsLastChecks() throws SQLException {
        String sql = "SELECT DISTINCT ON (url_id, id) * FROM url_checks ORDER BY url_id, id";

        Map<Integer, UrlCheck> result = new HashMap<>();

        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                int urlId = resultSet.getInt("url_id");
                UrlCheck urlCheck = getUrlCheckFromResultSet(resultSet);
                result.put(urlId, urlCheck);
            }
        }
        return result;
    }

    public static void save(UrlCheck urlCheck) throws SQLException {
        String sql = "INSERT INTO url_checks (url_id, status_code, title, h1, description, created_at)"
                + " VALUES (?, ?, ?, ?, ?, ?)";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, urlCheck.getUrlId());
            stmt.setInt(2, urlCheck.getStatusCode());
            stmt.setString(3, urlCheck.getTitle());
            stmt.setString(4, urlCheck.getH1());
            stmt.setString(5, urlCheck.getDescription());

            Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            urlCheck.setCreatedAt(createdAt);
            stmt.setTimestamp(6, createdAt);
            stmt.executeUpdate();

            var generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                urlCheck.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }
}
