package com.StruckCroissant.GameDB.core.game;

import config.TestDbConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.data.domain.PageRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:application.yml")
@ContextConfiguration(classes = {TestDbConfig.class, GameDAOImpl.class})
@SpringBootTest
public class GameDAOImplTest implements WithAssertions {

  @Qualifier("db-game")
  @Autowired
  private GameDAOImpl underTest;

  @Test
  public void shouldGetAllGames() {
    List<Game> allGames = underTest.selectAllGames();

    allGames.forEach((game) -> assertThat(game).isInstanceOf(Game.class));
    assertThat(allGames.size()).isGreaterThanOrEqualTo(30250);
  }

  @Test
  public void shouldGetGameById() {
    int[] validGames = {1, 2, 3, 4, 5};
    int[] invalidGames = {-4, -56, -3, 879627};
    List<Optional<Game>> validResult = new ArrayList<>();
    List<Optional<Game>> invalidResult = new ArrayList<>();

    for (int gameInt : validGames) {
      Optional<Game> currentGameOpt = underTest.selectGameById(gameInt);
      validResult.add(currentGameOpt);
    }
    for (int gameInt : invalidGames) {
      Optional<Game> currentGameOpt = underTest.selectGameById(gameInt);
      invalidResult.add(currentGameOpt);
    }

    validResult.forEach((gameOpt) -> assertThat(gameOpt).get().isInstanceOf(Game.class));
    invalidResult.forEach((gameOpt) -> assertThat(gameOpt).isEmpty());
  }

  @Test
  public void shouldGetRelatedGames() {
    int id = 40;

    List<Game> result = underTest.selectRelatedGames(id);

    assertThat(result.size()).isGreaterThan(0);
    result.forEach((game) -> assertThat(game).isInstanceOf(Game.class));
  }

  @Test
  public void shouldGetGamesPaginated() {
    String gname = "rust";

    Page<Game> resultMinimallyPaged = underTest.searchGamesPaginated(PageRequest.of(0, 1), gname, null);
    Page<Game> resultSinglePage = underTest.searchGamesPaginated(PageRequest.of(0, 100), gname, null);

    assertThat(resultMinimallyPaged).allSatisfy(game -> assertThat(game.getGname()).startsWith("Rust"));
    assertThat(resultMinimallyPaged.getTotalPages()).isEqualTo(7);
    assertThat(resultSinglePage.getTotalPages()).isEqualTo(1);
    assertThat(resultMinimallyPaged.getTotalElements()).isEqualTo(resultSinglePage.getTotalElements());
  }
}
