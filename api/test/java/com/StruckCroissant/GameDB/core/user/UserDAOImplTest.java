package com.StruckCroissant.GameDB.core.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

import com.StruckCroissant.GameDB.core.game.Game;
import config.TestDbConfig;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:application.yml")
@ContextConfiguration(classes = {TestDbConfig.class, UserDAOImpl.class})
@SpringBootTest
public class UserDAOImplTest {

  @Qualifier("db-user")
  @Autowired
  private UserDAOImpl underTest;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @After
  public void tearDown() {
    JdbcTestUtils.deleteFromTables(jdbcTemplate, "user");
  }

  @NotNull
  public String insertDefaultUserDb() {
    User user = new User("test_username", "test_password", UserRoleEnum.USER, false, true);
    underTest.insertUser(user);
    return "test_username";
  }

  @NotNull
  public String insertUserDb(String username) {
    User user = new User(username, "test_password", UserRoleEnum.USER, false, true);
    underTest.insertUser(user);
    return username;
  }

  @NotNull
  public Integer getUidFromUserObj(User userFromDb) {
    return userFromDb.getId().orElseThrow();
  }

  @NotNull
  public Integer getTestUidFromUsernameDb(String test_username) {
    return underTest
        .selectUserByUsername(test_username)
        .flatMap(User::getId)
        .orElseThrow(() -> new IllegalStateException("User not found in DB"));
  }

  @Test
  @Order(1)
  public void shouldInsertAndSelectNewUser() {
    // given
    String test_username = insertDefaultUserDb();

    // when
    boolean userExists = underTest.selectUserByUsername(test_username).isPresent();

    // then
    assertThat(userExists).isTrue();
  }

  @Test
  public void shouldGetUidFromuser() {
    // given
    String test_username = insertDefaultUserDb();

    // when
    int uid = underTest.selectUserByUsername(test_username).flatMap(User::getId).orElse(0);

    // then
    assertThat(uid).isNotEqualTo(0);
  }

  @Test
  public void shouldUpdateUser() {
    String test_username = insertDefaultUserDb();
    int initUidDb = getTestUidFromUsernameDb(test_username);

    String new_username = "new_account_420_updated";
    String new_password = "password_updated";
    User updatedUser = new User(new_username, new_password, UserRoleEnum.USER, false, true);

    underTest.updateUserById(initUidDb, updatedUser);
    User updatedUserDb = getTestUserFromUidDb(initUidDb);
    int updatedUidDb = getUidFromUserObj(updatedUserDb);

    assertThat(updatedUidDb).isEqualTo(initUidDb);
    assertThat(updatedUserDb.getUsername()).isEqualTo(new_username);
    assertThat(updatedUserDb.getPassword()).isEqualTo(new_password);
  }

  public User getTestUserFromUidDb(int initUidDb) {
    return underTest
        .selectUserById(initUidDb)
        .orElseThrow(() -> new RuntimeException("User not found in database"));
  }

  @Test
  public void shouldDeleteUser() {
    String test_username = insertDefaultUserDb();
    User userFromDb = underTest.selectUserByUsername(test_username).get();
    int uidFromDb = getUidFromUserObj(userFromDb);

    underTest.deleteUserById(uidFromDb);
    boolean userExists = underTest.selectUserByUsername(test_username).isPresent();

    assertThat(userExists).isFalse();
  }

  @Test
  public void shouldGetSavedGames() {
    int[] games = {1, 2, 3}; // Test games

    String test_username = insertDefaultUserDb();
    User userFromDb = underTest.selectUserByUsername(test_username).orElseThrow();
    int uid = getUidFromUserObj(userFromDb);

    for (int gid : games) {
      underTest.insertSavedGame(uid, gid);
    }

    assertThat(underTest.selectSavedGames(uid).size()).isNotEqualTo(0);
    underTest.selectSavedGames(uid).forEach(g -> assertThat(g).isExactlyInstanceOf(Game.class));
  }

  @Test
  public void shouldDeleteSavedGame() {
    int[] games = {1, 2, 3}; // Test games

    String test_username = insertDefaultUserDb();
    User userFromDb = underTest.selectUserByUsername(test_username).orElseThrow();
    int uid = getUidFromUserObj(userFromDb);

    for (int gid : games) {
      underTest.insertSavedGame(uid, gid);
    }
    underTest.deleteSavedGame(uid, games[0]);
    underTest.deleteSavedGame(uid, games[1]);
    underTest.deleteSavedGame(uid, games[2]);

    assertThat(underTest.selectSavedGames(uid).get(0).getGid()).isEqualTo(0);
  }

  @Test
  public void shouldDetectNonUniqueUser() {
    String test_username = insertDefaultUserDb();
    User userFromDb = underTest.selectUserByUsername(test_username).orElseThrow();

    boolean userIsUnique = underTest.userIsUnique(userFromDb);

    assertThat(userIsUnique).isFalse();
  }

  @Test
  public void shouldSelectAllUsers() {
    String test_username = insertUserDb("test_username1");
    String test_username2 = insertUserDb("test_username2");
    String test_username3 = insertUserDb("test_username3");

    List<User> users = underTest.selectAllUsers();

    assertThat(users.size()).isEqualTo(3);
    users.forEach(
        (User u) -> {
          assertThat(u).isExactlyInstanceOf(User.class);
          assertThat(u.getUsername()).isIn(test_username, test_username2, test_username3);
        });
  }

  @Test
  public void shouldThrowOnNullId() {
    User user = new User("test_username", "test_password", UserRoleEnum.USER, false, true);

    Throwable thrown = catchThrowable(() -> underTest.userIsUnique(user));

    assertThat(thrown).isExactlyInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void selectUserByUsernameOrThrowThrowsOnAbsentUser() {
    String testUsername = "test_username";

    Throwable thrown = catchThrowable(() -> underTest.selectUserByUsernameOrThrow(testUsername));

    assertThat(thrown).isExactlyInstanceOf(UsernameNotFoundException.class);
  }
}
