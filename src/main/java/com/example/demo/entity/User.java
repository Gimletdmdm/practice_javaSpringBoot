package com.example.demo.entity;

import jakarta.persistence.Entity; // JPAのエンティティであることを示すアノテーション
import jakarta.persistence.GeneratedValue; // IDが自動生成されることを示す
import jakarta.persistence.GenerationType; // IDの自動生成戦略
import jakarta.persistence.Id; // 主キーであることを示すアノテーション
import jakarta.persistence.Table; // データベースのテーブル名を指定 (任意)

@Entity // このクラスがJPAエンティティであることを示す
@Table(name = "users") // データベースのテーブル名を指定 (省略するとクラス名が使われる)
public class User {
    @Id // 主キーを示す
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDがデータベースによって自動生成されることを示す
    private Long id; // Long型が一般的

    private String name;
    private String email;

    // デフォルトコンストラクタ (JPAが利用するため必須)
    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // getterとsetter (Lombokを使わない場合は手動で定義)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", email=" + email + "]";
    }
}
