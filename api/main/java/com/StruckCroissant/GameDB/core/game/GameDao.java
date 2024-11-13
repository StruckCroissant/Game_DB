package com.StruckCroissant.GameDB.core.game;

import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface for Game Data Access Object
 *
 * @author Dakota Vaughn
 * @since 2022-06-20
 */
public interface GameDao {
  List<Game> selectAllGames();

  Optional<Game> selectGameById(int id);

  List<Game> selectRelatedGames(int id);

  Page<Game> searchGamesPaginated(Pageable pageable, @Nullable String name, @Nullable Integer id);
}
