package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository; // Spring Data JPAの基本インターフェース
import com.example.demo.entity.User;

// JpaRepository<エンティティクラス, エンティティの主キーの型> を継承する
public interface UserRepository extends JpaRepository<User, Long> {
    // ここには、特別なメソッド（例: findByName, findByEmailなど）が必要な場合にのみ定義します。
    // findById, findAll, save, deleteById などの基本的なCRUDメソッドは、
    // JpaRepositoryを継承するだけで自動的に提供されます。

}
