package com.StruckCroissant.GameDB.core.user;

import com.StruckCroissant.GameDB.core.game.Game;
import com.StruckCroissant.GameDB.core.game.SQLGameAccessor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@Repository("db-user")
public class UserDAOImpl implements UserDao {
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public UserDAOImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<User> selectAllUsers() {
    final String sql =
        "SELECT u.uid, u.username, u.password, u.locked, u.enabled, r.role "
            + "FROM user u, role r where u.uid = r.uid";
    return jdbcTemplate.query(sql, (resultSet, i) -> getUser(resultSet));
  }

  @Override
  public Optional<User> selectUserById(int id) {
    final String sql =
        "SELECT u.uid, u.username, u.password, u.locked, u.enabled, r.role "
            + "FROM user u, role r WHERE u.uid = ? AND u.uid = r.uid LIMIT 1";
    return Optional.ofNullable(
        jdbcTemplate.query(
            sql,
            (resultSet) -> {
              if (resultSet.next()) {
                return getUser(resultSet);
              } else {
                return null;
              }
            },
            id));
  }

  @NotNull
  private User getUser(ResultSet resultSet) throws SQLException {
    int uid = resultSet.getInt("uid");
    String username = resultSet.getString("username");
    String password = resultSet.getString("password");
    UserRoleEnum role = UserRoleEnum.valueOf(resultSet.getString("role"));
    Boolean locked = resultSet.getBoolean("locked");
    Boolean enabled = resultSet.getBoolean("enabled");
    return new User(uid, username, password, role, locked, enabled);
  }

  @Override
  public Optional<User> selectUserByUsername(String username) {
    final String sql =
        "SELECT u.uid, u.username, u.password, u.locked, u.enabled, r.role FROM user u,"
            + " role r WHERE u.username = ? AND u.uid = r.uid LIMIT 1";
    return Optional.ofNullable(
        jdbcTemplate.query(
            sql,
            (ResultSet resultSet) -> {
              if (resultSet.next()) {
                return getUser(resultSet);
              } else {
                return null;
              }
            },
            username));
  }

  public User selectUserByUsernameOrThrow(String username) throws UsernameNotFoundException {
    return this.selectUserByUsername(username)
        .orElseThrow(
            () ->
                new UsernameNotFoundException(
                    String.format("User with username %s not found", username)));
  }

  public Optional<Integer> getUidByUsername(String username) {
    final String sql = "SELECT uid FROM user WHERE username = ? LIMIT 1";
    return Optional.ofNullable(
        jdbcTemplate.query(
            sql,
            (resultSet) -> {
              if (resultSet.next()) {
                return resultSet.getInt("uid");
              } else {
                return null;
              }
            },
            username));
  }

  public void insertSavedGame(int uid, int gid) {
    final String sql = "INSERT INTO plays (uid, gid) VALUES (?, ?)";
    jdbcTemplate.update(sql, uid, gid);
  }

  @Override
  public List<Game> selectSavedGames(int uid) {
    final String sql =
        """
        SELECT
            g.gid,
            g.gname,
            g.cost,
            g.discounted_cost,
            g.url,
            g.age_rating,
            g.indie,
            g.description,
            GROUP_CONCAT(gn.genre_name) as genres,
            f.fname as franchise,
            g.rdate,
            g.rawgId
        FROM
            game g LEFT JOIN gamegenre ggn ON
                g.gid = ggn.gid
            LEFT JOIN genre gn ON
                gn.genre_id = ggn.genre_id
            LEFT JOIN franchise f ON
                g.gid = f.gid
            INNER JOIN plays p ON g.gid = p.gid
            INNER JOIN user u ON p.uid = u.uid
        WHERE u.uid = ?;
        """;
    return jdbcTemplate.query(
        sql, new SQLGameAccessor(), uid);
  }

  @Override
  public int deleteSavedGame(int uid, int gid) {
    final String sql = "DELETE FROM plays WHERE uid = ? AND gid = ?";
    return jdbcTemplate.update(sql, uid, gid);
  }

  @Override
  public int deleteUserById(int id) {
    final String sql = "DELETE FROM user WHERE uid = ?";
    return jdbcTemplate.update(sql, id);
  }

  @Override
  public int updateUserById(int id, User user) {
    final String sql =
        """
        UPDATE
          user u join role r
          on u.uid = r.uid
        SET
          u.username = ?,
          u.password = ?,
          u.locked = ?,
          u.enabled = ?,
          r.role = ?
        WHERE
          u.uid = ?
        ;
        """;
    return jdbcTemplate.update(
        sql,
        user.getUsername(),
        user.getPassword(),
        user.isAccountNonLocked(),
        user.isEnabled(),
        user.getRole().toString(),
        id);
  }

  @Override
  public boolean insertUser(User user) {
    final String SQL_INSERT_USER =
        "INSERT INTO user (username, password, locked, enabled) VALUES (?, ?, ?, ?);";
    final String SQL_INSERT_ROLE_USER = "INSERT INTO role (uid, role) VALUES (?, ?);";

    boolean insertSuccess = false;

    jdbcTemplate.update(
        SQL_INSERT_USER,
        user.getUsername(),
        user.getPassword(),
        !user.isAccountNonLocked(),
        user.isEnabled());
    Optional<Integer> uidOpt = getUidByUsername(user.getUsername());

    if (uidOpt.isPresent()) {
      int UID = uidOpt.get();
      if (jdbcTemplate.update(SQL_INSERT_ROLE_USER, UID, user.getRole().toString()) == 1) {
        insertSuccess = true;
      }
    }

    return insertSuccess;
  }

  @Override
  public boolean userIsUnique(User user) {
    final String sql = "SELECT COUNT(*) FROM user WHERE username = ? OR uid = ?";
    String username = user.getUsername();
    int uid =
        user.getId().orElseThrow(() -> new IllegalArgumentException("User id cannot be null"));

    Integer amnt =
        jdbcTemplate.query(
            sql,
            (resultSet) -> {
              if (resultSet.next()) {
                return resultSet.getInt("count(*)");
              } else {
                return null;
              }
            },
            username,
            uid);
    assert amnt != null;
    return amnt == 0;
  }
}
