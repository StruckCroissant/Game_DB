package com.StruckCroissant.GameDB.core.user;

import com.StruckCroissant.GameDB.core.web.GameDBCoreController;
import com.StruckCroissant.GameDB.core.game.Game;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
@CrossOrigin("*") // Replace with proxy later
@RestController
public class UserController extends GameDBCoreController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping(path = "/all")
  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }

  @GetMapping(path = "/byId")
  public User getUserById(@RequestParam("id") int uid) throws ResourceNotFoundException {
    return userService.getUserById(uid);
  }

  @GetMapping(path = "/saved-games")
  public List<Game> getSavedGames(@RequestParam("id") int uid) throws ResourceNotFoundException {
    return userService.getSavedGames(uid);
  }

  // Gets principal from Spring Security and returns the user object
  @GetMapping()
  public Principal user(@Autowired Principal user) {
    return user;
  }
}
