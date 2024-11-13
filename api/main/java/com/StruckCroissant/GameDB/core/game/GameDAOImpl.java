package com.StruckCroissant.GameDB.core.game;

import com.StruckCroissant.GameDB.core.DBAL.SimpleQueryBuilder;

import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * Data Access Implementation for accessing Game objects.
 *
 * @author Dakota Vaughn
 * @since 2022-06-20
 */
@Service
@Repository("db-game")
public class GameDAOImpl implements GameDao {
  private final NamedParameterJdbcTemplate jdbcTemplate;

  /**
   * Constructor that initializes Object with a jdbcTemplate via Autowire
   *
   * @param jdbcTemplate SQL execution object
   */
  @Autowired
  public GameDAOImpl(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private static SimpleQueryBuilder setGameFormat(SimpleQueryBuilder builder) {
    return builder
        .addSelect("game.gid")
        .addSelect("game.gname")
        .addSelect("game.cost")
        .addSelect("game.discounted_cost")
        .addSelect("game.url")
        .addSelect("game.age_rating")
        .addSelect("game.indie")
        .addSelect("game.description")
        .addSelect("GROUP_CONCAT(genre.genre_name) as genres")
        .addSelect("franchise.fname as franchise")
        .addSelect("game.rdate")
        .addSelect("game.rawgId")
        .addFrom("game LEFT JOIN gamegenre game_genre ON game.gid = game_genre.gid")
        .addFrom("LEFT JOIN genre ON genre.genre_id = game_genre.genre_id")
        .addFrom("LEFT JOIN franchise franchise ON game.gid = franchise.gid")
        .addGroupBy("game.gid");
  }

  /**
   * Selects all games from database
   *
   * @return List<Game>
   */
  @Override
  public List<Game> selectAllGames() {
    final String sql = setGameFormat(new SimpleQueryBuilder()).build();
    return this.jdbcTemplate.query(
        sql,
        new SQLGameAccessor()
    );
  }

  /**
   * Selects game from database by id
   *
   * @param id game id
   * @return Optional<Game>
   */
  @Override
  public Optional<Game> selectGameById(int id) {
    final String sql = setGameFormat(new SimpleQueryBuilder())
        .addWhere("game.gid = :id")
        .setLimit(1)
        .build();

    final List<Game> result = this.jdbcTemplate.query(
        sql,
        new MapSqlParameterSource().addValue("id", id),
        new SQLGameAccessor()
    );

    return Optional.ofNullable(!result.isEmpty() ? result.get(0) : null);
  }

  @Override
  public List<Game> selectRelatedGames(int id) {
    final String SQL =
        setGameFormat(new SimpleQueryBuilder())
            .addFrom(
                "INNER JOIN gamegenre game_genre2 ON game_genre.genre_id = game_genre2.genre_id AND"
                    + " game_genre.gid <> game_genre2.gid")
            .addWhere("game_genre2.gid = :genreGid")
            .addOrderBy("COUNT(game_genre2.genre_id) DESC")
            .setLimit(10)
            .build();
    return jdbcTemplate.query(
        SQL,
        new MapSqlParameterSource()
            .addValue("genreGid", id),
        new SQLGameAccessor()
    );
  }

  private static SimpleQueryBuilder getSearchGamesBuilder(@Nullable String name, @Nullable Integer id) {
    return (new SimpleQueryBuilder())
        .addFrom("game")
        .addWhere("game.gname LIKE CONCAT(:gname, '%')")
        .addWhere("OR game.gid = :gid")
        .setParameter("gname", name)
        .setParameter("gid", id);
  }

  public Page<Game> searchGamesPaginated(Pageable pageable, @Nullable String name, @Nullable Integer id) {
    final SimpleQueryBuilder countBuilder = getSearchGamesBuilder(name, id)
        .addSelect("count(*) as count");
    final SimpleQueryBuilder gameBuilder = setGameFormat(getSearchGamesBuilder(name, id))
        .setLimit(pageable.getPageSize())
        .setOffset(pageable.getOffset());

    final long total = (long) jdbcTemplate.queryForList(
        countBuilder.build(),
        countBuilder.getParameters()
    ).get(0).get("count");

    final List<Game> games = jdbcTemplate.query(
        gameBuilder.build(),
        gameBuilder.getParameters(),
        new SQLGameAccessor()
    );

    return new PageImpl<>(games, pageable, total);
  }
}
