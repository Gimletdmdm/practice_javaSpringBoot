package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.model.UserRequest; // ★追加: 作成したDTOクラスをインポート
import com.example.demo.service.UserService; // ★追加: 作成したUserServiceをインポート
import java.util.List; // ★追加: Listをインポート

@RestController
public class HelloController {

    // ★追加: UserServiceの依存性を注入
    private final UserService userService;

    // コンストラクタインジェクションでUserServiceを注入
    // Spring Boot 2.x以降では、コンストラクタが1つしかない場合、@Autowiredは省略可能です
    public HelloController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot from VS Code!";
    }

    @GetMapping("/hello/greeting")
    public String greeting(@RequestParam(required = false) String name) {
        if (name == null || name.isEmpty()) {
            return "Hello, anonymous user!";
        } else {
            return "Hello, " + name + " from Spring Boot!";
        }
    }

    @GetMapping("/hello/user/{id}")
    public String getUserById(@PathVariable int id) {
        return "You requested user with ID: " + id + ". This is a user-specific greeting!";
    }

    @GetMapping("/hello/order/{orderId}/item/{itemId}")
    public String getOrderItem(@PathVariable String orderId, @PathVariable String itemId) {
        return "You requested order " + orderId + ", item " + itemId + ".";
    }

    // ★修正: POSTリクエストでJSONデータを受け取るAPI (サービス層を利用)
    @PostMapping("/hello/user")
    public String createUser(@RequestBody UserRequest userRequest) {
        // コントローラーはリクエストを受け取り、サービス層に処理を委譲
        UserRequest createdUser = userService.createUser(userRequest);
        System.out.println("Controller: 新しいユーザーが作成されました - " + createdUser.toString());
        return "User created successfully by service! Username: " + createdUser.getUsername() + ", Email: "
                + createdUser.getEmail();
    }

    // ★追加: サービス層から全ユーザーを取得するAPI
    @GetMapping("/hello/users")
    public List<UserRequest> getAllUsers() {
        System.out.println("Controller: 全ユーザー情報をリクエストされました。");
        return userService.getAllUsers();
    }
}
