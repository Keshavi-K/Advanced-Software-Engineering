package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaTransactionListener {

    private final TransactionService transactionService;

    public KafkaTransactionListener(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group")
    public void listen(String message) {
        System.out.println("🟢 Received raw message: " + message);

        String[] parts = message.split(",");
        if (parts.length == 3) {
            try {
                long senderId = Long.parseLong(parts[0].trim());
                long recipientId = Long.parseLong(parts[1].trim());
                float amount = Float.parseFloat(parts[2].trim());
                Transaction transaction = new Transaction(senderId, recipientId, amount);
                transactionService.processTransaction(transaction);
            } catch (Exception e) {
                System.out.println("❌ Invalid transaction: " + message);
            }
        }
    }


}
