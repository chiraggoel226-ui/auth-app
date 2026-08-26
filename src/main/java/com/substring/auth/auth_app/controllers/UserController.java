package com.substring.auth.auth_app.controllers;

import com.substring.auth.auth_app.dtos.UserDto;
import com.substring.auth.auth_app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
     UserService userService;



    @PostMapping
    public ResponseEntity<?>  createUser(@RequestBody UserDto userDto){

    return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDto));

    }

@GetMapping
    public ResponseEntity<?>getAllUsers( ){

        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUser());
}


@GetMapping("/email/{email}")
public ResponseEntity<?> getUserByEmail(@PathVariable String email){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserByEmail(email));
}

@DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable String userId){
         userService.deleteUserById(userId);

}
@PutMapping("/{userId}")
public ResponseEntity<UserDto>  updateUser(@RequestBody UserDto userDto,@PathVariable("userId") String userId){
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userDto,userId));
}
@GetMapping("/{userId}")
public ResponseEntity<?> getUserById(@PathVariable String userId){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userId));
}


}
