package org.osttra.message.repository;

import org.osttra.message.repository.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findAllByRecipientIdAndReadFalseOrderByTimestampAsc(Pageable pageable, UUID recipient);

    Page<Message> findAllByRecipientIdOrderByTimestampAsc(Pageable pageable, UUID recipient);

    Page<Message> findAllByRecipientIdAndReadFalse(Pageable pageable, UUID recipient);
}
