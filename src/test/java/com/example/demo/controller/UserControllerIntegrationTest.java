package com.example.demo.controller;

import com.example.demo.DemoApplication; // メインアプリケーションクラスをインポート
import com.example.demo.entity.User;
import com.example.demo.model.UserRequest;
import com.example.demo.model.UserResponse; // ★必要に応じて追加：レスポンス検証のためにあった方が良い
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper; // JSONのオブジェクト変換用
import org.junit.jupiter.api.AfterEach; // テスト後処理
import org.junit.jupiter.api.BeforeEach; // テスト前処理
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // MockMvcを自動設定
import org.springframework.boot.test.context.SpringBootTest; // Spring Bootアプリケーションをロード
import org.springframework.http.MediaType; // メディアタイプ (application/jsonなど)
import org.springframework.test.web.servlet.MockMvc; // HTTPリクエストのモック

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; // GET, POST, PUT, DELETE
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*; // ステータスコード、JSONパスなど
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = DemoApplication.class) // Spring Bootアプリケーションのコンテキストをロード
@AutoConfigureMockMvc // MockMvcを自動設定してインジェクト可能にする
public class UserControllerIntegrationTest {

    @Autowired // HTTPリクエストをモックするためのオブジェクト
    private MockMvc mockMvc;

    @Autowired // JSONとJavaオブジェクトを相互変換するためのオブジェクト
    private ObjectMapper objectMapper;

    @Autowired // 実際のデータベース操作を行うリポジトリ (テスト用)
    private UserRepository userRepository;

    // 各テストメソッドの前に実行される初期化処理
    @BeforeEach
    void setUp() {
        // テスト前にデータベースをクリーンアップ
        userRepository.deleteAll();
    }

    // 各テストメソッドの後に実行されるクリーンアップ処理
    @AfterEach
    void tearDown() {
        // テスト後にデータベースをクリーンアップ
        userRepository.deleteAll();
    }

    @Test
    void testCreateUser() throws Exception {
        // リクエストボディとなるユーザーデータ (UserRequestに相当するオブジェクト)
        // User userRequest = new User("Frank", "frank@example.com"); // エンティティをそのまま使用
        // または、専用のUserRequest DTOを定義して使用することも可能
        // UserRequest userRequestDto = new UserRequest("Frank", "frank@example.com");
        // ★修正点：UserRequest DTOのインスタンスを作成し、適切な値を設定する
        UserRequest userRequestDto = new UserRequest("NewUserTest", "newuser.test@example.com");

        // HTTP POSTリクエストをシミュレートし、検証する
        mockMvc.perform(post("/api/users") // POSTリクエストを/api/usersへ
                .contentType(MediaType.APPLICATION_JSON) // Content-TypeヘッダーをJSONに設定
                .content(objectMapper.writeValueAsString(userRequestDto))) // JavaオブジェクトをJSON文字列に変換してリクエストボディに設定
                .andExpect(status().isCreated()) // HTTPステータスが201 Createdであることを検証
                .andExpect(jsonPath("$.id").isNumber()) // レスポンスJSONにidフィールドがあり、数値であることを検証
                .andExpect(jsonPath("$.username").value("NewUserTest")) // usernameフィールドが"NewUserTest"であることを検証
                .andExpect(jsonPath("$.email").value("newuser.test@example.com")); // emailフィールドが"newuser.test@example.com"であることを検証

        // データベースに実際にユーザーが保存されたかを確認 (オプションの検証)
        assertEquals(1, userRepository.count());
    }

    @Test
    void testGetAllUsers() throws Exception {
        // テストデータをデータベースに挿入
        userRepository.save(new User("Grace", "grace@example.com"));
        userRepository.save(new User("Henry", "henry@example.com"));

        // HTTP GETリクエストをシミュレートし、検証する
        mockMvc.perform(get("/api/users") // GETリクエストを/api/usersへ
                .contentType(MediaType.APPLICATION_JSON)) // Content-TypeヘッダーをJSONに設定
                .andExpect(status().isOk()) // HTTPステータスが200 OKであることを検証
                .andExpect(jsonPath("$").isArray()) // レスポンスボディがJSON配列であることを検証
                .andExpect(jsonPath("$.length()").value(2)) // 配列の要素数が2であることを検証
                .andExpect(jsonPath("$[0].username").value("Grace")) // 最初の要素のusernameが"Grace"であることを検証
                .andExpect(jsonPath("$[1].username").value("Henry")); // 2番目の要素のusernameが"Henry"であることを検証
    }

    @Test
    void testGetUserById() throws Exception {
        // テストデータを挿入し、IDを取得
        User user = userRepository.save(new User("Ivy", "ivy@example.com"));
        Long userId = user.getId();

        // HTTP GETリクエストをシミュレートし、検証する
        mockMvc.perform(get("/api/users/{id}", userId) // GETリクエストを/api/users/{id}へ
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTPステータスが200 OKであることを検証
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("Ivy"));
    }

    @Test
    void testGetUserByIdNotFound() throws Exception {
        // 存在しないIDでリクエスト
        mockMvc.perform(get("/api/users/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // HTTPステータスが404 Not Foundであることを検証
    }

    @Test
    void testUpdateUser() throws Exception {
        // 更新対象のユーザーをデータベースに挿入
        User existingUser = userRepository.save(new User("Jack", "jack@example.com"));
        Long userId = existingUser.getId();

        // 更新するデータ (UserRequestに相当)
        // User updatedUserRequest = new User("Jack Updated",
        // "jack.updated@example.com");
        // ★修正点：更新するデータをUserRequest DTOのインスタンスとして作成する
        UserRequest updatedUserRequestDto = new UserRequest("Jack Updated", "jack.updated@example.com");

        // HTTP PUTリクエストをシミュレートし、検証する
        mockMvc.perform(put("/api/users/{id}", userId) // PUTリクエストを/api/users/{id}へ
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUserRequestDto)))
                .andExpect(status().isOk()) // HTTPステータスが200 OKであることを検証
                .andExpect(jsonPath("$.username").value("Jack Updated"))
                .andExpect(jsonPath("$.email").value("jack.updated@example.com"));

        // データベースで実際に更新されたかを確認 (オプションの検証)
        User foundUser = userRepository.findById(userId).orElse(null);
        assertNotNull(foundUser);
        assertEquals("Jack Updated", foundUser.getName());
    }

    @Test
    void testDeleteUser() throws Exception {
        // 削除対象のユーザーをデータベースに挿入
        User userToDelete = userRepository.save(new User("Kelly", "kelly@example.com"));
        Long userId = userToDelete.getId();

        // HTTP DELETEリクエストをシミュレートし、検証する
        mockMvc.perform(delete("/api/users/{id}", userId)) // DELETEリクエストを/api/users/{id}へ
                .andExpect(status().isNoContent()); // HTTPステータスが204 No Contentであることを検証

        // データベースからユーザーが削除されたことを確認
        assertFalse(userRepository.findById(userId).isPresent());
    }
}