package app.ultratechies.api.AppUsers.UserAuth;

import app.ultratechies.api.AppUsers.AppUser;
import app.ultratechies.api.AppUsers.UserDTO.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("api/user/email/auth")
public class UserEmailAuthController {

    private final UserEmailAuthService userEmailAuthService;

    @Autowired
    public UserEmailAuthController(UserEmailAuthService userEmailAuthService) {
        this.userEmailAuthService = userEmailAuthService;
    }

    @GetMapping()
    public ResponseEntity<Optional<UserDto>> getUser(@RequestBody AppUser appUser){
        Optional<UserDto> appuser=userEmailAuthService.authUser(appUser.getEmail(), appUser.getPassword());
        return ResponseEntity.ok(appuser);
    }
}
