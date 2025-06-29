package com.example.demo.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity; // HTTPレスポンスを柔軟に操作するために使用
import org.springframework.http.HttpStatus; // HTTPステータスコードのために使用

import com.example.demo.entity.User; // ★変更: エンティティクラスをインポート
import com.example.demo.model.UserRequest; // ★変更なし: リクエストDTOをインポート
import com.example.demo.model.UserResponse; // ★追加: レスポンスDTOをインポート
import com.example.demo.service.UserService; // ★変更なし: UserServiceをインポート

import java.util.List;
import java.util.stream.Collectors; // Stream APIのために必要

@RestController
@RequestMapping("/api/users") // ★変更: ベースパスを/api/usersに
public class UserController {

    // ★追加: UserServiceの依存性を注入
    private final UserService userService;

    // コンストラクタインジェクションでUserServiceを注入
    // Spring Boot 2.x以降では、コンストラクタが1つしかない場合、@Autowiredは省略可能です
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ユーザー作成（POST）
    // POST http://localhost:8080/api/users
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        // DTOからエンティティへ変換
        User newUser = new User(userRequest.getUsername(), userRequest.getEmail());
        User savedUser = userService.createUser(newUser);
        // エンティティからレスポンスDTOへ変換して返す
        UserResponse userResponse = new UserResponse(savedUser.getId(), savedUser.getName(), savedUser.getEmail());
        // 201 Created ステータスを返す
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    // 全ユーザー取得（GET）
    // GET http://localhost:8080/api/users
    @GetMapping
    public List<UserResponse> getAllUsers() {
        List<User> users = userService.getAllUsers();
        // エンティティのリストをDTOのリストに変換 (Stream APIを活用！)
        return users.stream()
                .map(user -> new UserResponse(user.getId(), user.getName(), user.getEmail()))
                .collect(Collectors.toList());
    }

    // IDでユーザー取得（GET）
    // GET http://localhost:8080/api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) { // ★引数の型をLongに変更
        User user = userService.getUserById(id);
        if (user != null) {
            UserResponse userResponse = new UserResponse(user.getId(), user.getName(), user.getEmail());
            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ユーザー更新（PUT）
    // PUT http://localhost:8080/api/users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        try {
            // DTOからエンティティに変換し、IDを設定
            User userDetails = new User(userRequest.getUsername(), userRequest.getEmail());
            // userDetails.setId(id); // IDはパス変数から渡すため、ここでは不要（UserServiceで設定される）

            User updatedUser = userService.updateUser(id, userDetails);
            UserResponse userResponse = new UserResponse(updatedUser.getId(), updatedUser.getName(),
                    updatedUser.getEmail());
            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // ユーザーが見つからない場合など
        }
    }

    // ユーザー削除（DELETE）
    // DELETE http://localhost:8080/api/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } catch (Exception e) { // 例外の種類をより具体的に指定することも可能
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // ユーザーが見つからない場合など
        }
    }
}
