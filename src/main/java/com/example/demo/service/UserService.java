package com.example.demo.service;

import org.springframework.stereotype.Service;
import com.example.demo.model.UserRequest; // UserRequestをインポート

import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Java 8のOptionalを使用

@Service
public class UserService {
    private final List<UserRequest> users = new ArrayList<>();
    private int nextId = 1;

    public UserService() {
        // 初期データ
        users.add(new UserRequest("Alice", "alice@example.com"));
        users.add(new UserRequest("Bob", "bob@example.com"));
    }

    public List<UserRequest> getAllUsers() {
        return new ArrayList<>(users);
    }

    // 注意: UserRequestには現在IDフィールドがありません。
    // もしIDでユーザーを特定したい場合は、UserRequestクラスにidフィールドを追加し、
    // このメソッドのロジックもID検索に修正する必要があります。
    // 今回は簡単なデモのため、このメソッドはここでは使用しません。
    public Optional<UserRequest> getUserById(int id) {
        return Optional.empty(); // 仮の実装
    }

    public UserRequest createUser(UserRequest userRequest) {
        // 実際のDB保存処理の代わりにリストに追加
        users.add(userRequest);
        System.out.println("UserService: ユーザーが追加されました: " + userRequest.getUsername());
        return userRequest; // 追加されたユーザーを返す
    }

    // 今後、更新や削除のロジックもここに書くことができます。
}
