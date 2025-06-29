package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.UserRequest; // 必要に応じてインポート
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach; // JUnit 5 のアノテーション
import org.junit.jupiter.api.Test; // JUnit 5 のアノテーション
import org.mockito.InjectMocks; // モックを注入する対象
import org.mockito.Mock; // モックオブジェクト
import org.mockito.MockitoAnnotations; // モックの初期化

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*; // アサーションメソッド
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*; // Mockitoのメソッド

public class UserServiceTest {

    @Mock // UserRepositoryのモックオブジェクトを作成
    private UserRepository userRepository;

    @InjectMocks // モックを注入する対象（テスト対象のUserServiceインスタンス）
    private UserService userService;

    // 各テストメソッド実行前に実行される初期化処理
    @BeforeEach
    void setUp() {
        // Mockitoアノテーションを初期化する
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        // モックの振る舞いを定義: userRepository.findAll() が呼ばれたら、
        // 指定したユーザーリストを返すように設定
        List<User> mockUsers = Arrays.asList(
                new User("Alice", "alice@example.com"),
                new User("Bob", "bob@example.com"));
        when(userRepository.findAll()).thenReturn(mockUsers);

        // テスト対象メソッドの実行
        List<User> users = userService.getAllUsers();

        // 結果の検証 (JUnit Jupiterのアサーション)
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("Alice", users.get(0).getName());
        assertEquals("Bob", users.get(1).getName());

        // userRepository.findAll() が1回だけ呼ばれたことを検証 (Mockitoの検証)
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserByIdFound() {
        Long userId = 1L;
        User mockUser = new User("Charlie", "charlie@example.com");
        mockUser.setId(userId); // IDを設定

        // モックの振る舞いを定義: userRepository.findById(userId) が呼ばれたら、
        // Optional.of(mockUser) を返すように設定
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // テスト対象メソッドの実行
        User foundUser = userService.getUserById(userId);

        // 結果の検証
        assertNotNull(foundUser);
        assertEquals("Charlie", foundUser.getName());

        // userRepository.findById() が1回だけ呼ばれたことを検証
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserByIdNotFound() {
        Long userId = 99L;

        // モックの振る舞いを定義: userRepository.findById(userId) が呼ばれたら、
        // Optional.empty() を返すように設定
        // ★例外をスローするように変更したので、このモックの振る舞い定義は有効なままです
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // テスト対象メソッドの実行と例外の検証
        // ★assertThrows を使用して、指定した例外がスローされることを検証します
        ResourceNotFoundException thrown = assertThrows(
                ResourceNotFoundException.class, // 期待する例外の型
                () -> userService.getUserById(userId), // 例外が発生するはずのコードブロック
                "Expected getUserById to throw ResourceNotFoundException, but it didn't" // エラーメッセージ
        );

        // 例外メッセージの検証 (オプション)
        assertTrue(thrown.getMessage().contains("User not found with id: " + userId));

        // userRepository.findById() が1回だけ呼ばれたことを検証
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testCreateUser() {
        User userToSave = new User("Diana", "diana@example.com");
        userToSave.setId(null); // 新規作成時はIDがない

        User savedUser = new User("Diana", "diana@example.com");
        savedUser.setId(1L); // 保存後にIDが割り振られることを想定

        // モックの振る舞いを定義: userRepository.save(any(User.class)) が呼ばれたら、
        // savedUserを返すように設定。any(User.class)は任意のUserオブジェクトにマッチする
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // テスト対象メソッドの実行
        User resultUser = userService.createUser(userToSave);

        // 結果の検証
        assertNotNull(resultUser.getId());
        assertEquals("Diana", resultUser.getName());
        assertEquals("diana@example.com", resultUser.getEmail());

        // userRepository.save() が1回だけ呼ばれたことを検証
        verify(userRepository, times(1)).save(userToSave);
    }

}
