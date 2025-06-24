package com.example.demo.service;

import org.springframework.stereotype.Service;
// import com.example.demo.model.UserRequest; // UserRequestをインポート
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

import java.util.List;
import java.util.Optional; // Java 8のOptionalを使用

@Service
public class UserService {
    // private final List<UserRequest> users = new ArrayList<>();
    // private int nextId = 1;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        // 初期データ
        // users.add(new UserRequest("Alice", "alice@example.com"));
        // users.add(new UserRequest("Bob", "bob@example.com"));
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll(); // JpaRepositoryが提供する全件取得メソッド
    }

    public Optional<User> getUserById(Long id) { // ★引数の型をLongに変更
        return userRepository.findById(id); // JpaRepositoryが提供するID検索メソッド
    }

    public User createUser(User user) { // ★引数の型をUserエンティティに変更
        return userRepository.save(user); // JpaRepositoryが提供する保存/更新メソッド
    }

    public User updateUser(Long id, User userDetails) { // ★追加: 更新メソッド
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id)); // 例外処理

        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());

        return userRepository.save(user); // 更新されたユーザーを保存
    }

    public void deleteUser(Long id) { // ★追加: 削除メソッド
        userRepository.deleteById(id); // JpaRepositoryが提供する削除メソッド
    }
}
