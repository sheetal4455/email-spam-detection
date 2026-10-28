
package com.spamdetector.emailspamdetection.repository;

import com.spamdetector.emailspamdetection.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {

    long countByResultIgnoreCase(String result);

}
