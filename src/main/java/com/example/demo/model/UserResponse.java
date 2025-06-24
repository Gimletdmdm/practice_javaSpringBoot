package com.example.demo.model;

// Lombokを使うなら @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserResponse {
    private Long id; // データベースで生成されるIDを含む
    private String username;
    private String email;

    // コンストラクタ
    public UserResponse(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    // getter (DTOは通常読み取り専用で十分なのでsetterは不要な場合も多い)
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    // setterも必要なら追加 (通常は不要)
    // public void setId(Long id) { this.id = id; }
    // public void setUsername(String username) { this.username = username; }
    // public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "UserResponse [id=" + id + ", username=" + username + ", email=" + email + "]";
    }
}