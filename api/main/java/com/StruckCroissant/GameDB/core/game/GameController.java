package com.StruckCroissant.GameDB.core.game;

import com.StruckCroissant.GameDB.core.web.GameDBCoreController;
import com.StruckCroissant.GameDB.exception.exceptions.GameNotFoundException;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:4200") // Replace with proxy later
@RequestMapping("/game")
@RestController
public class GameController extends GameDBCoreController {
  private final GameService gameService;

  @Autowired
  public GameController(GameService gameService) {
    this.gameService = gameService;
  }

  @GetMapping(path = "/all")
  public List<Game> getAllGames() {
    return gameService.getAllGames();
  }

  @GetMapping(path = "/related")
  public List<Game> getRelatedGames(@RequestParam("id") int id) {
    return gameService.getRelatedGames(id);
  }

  @GetMapping
  public Page<Game> search(Pageable pageable, @Valid GameSearchRequest request) {
    return gameService.searchGames(pageable, request.name(), request.id());
  }

  @GetMapping("/{id}")
  public Game getGame(@PathVariable @Valid @DecimalMin(value = "1") Integer id)
      throws GameNotFoundException {
    return gameService.getGameById(id);
  }
}
