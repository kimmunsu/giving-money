package com.moonsu.givingMoney.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "giving_money_receive")
@SequenceGenerator(
        name = "GIVING_MONEY_RECEIVE_SEQ_GEN",
        sequenceName = "GIVING_MONEY_RECEIVE_SEQ",
        initialValue = 1,
        allocationSize = 1)
public class GivingMoneyReceive {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GIVING_MONEY_RECEIVE_SEQ_GEN")
    @Column(name = "id", nullable = false)
    private long id;

    @Version
    private int version;    // lock

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private GivingMoneyOrder givingMoneyOrder; // 주문 id

    @Column(name = "money", nullable = false)
    private int money;  // 수령대상 금액

    @Column(name = "receive_user_id")
    private Long receiveUserId = 0L;    // 수령자 id

    @Column(name = "received_dtm")
    private LocalDateTime receivedDtm = null;   // 수령일시

    public static GivingMoneyReceive createForTest(long receiveUserId, LocalDateTime receivedDtm) {
        GivingMoneyReceive receive = new GivingMoneyReceive();
        receive.setReceiveUserId(receiveUserId);
        receive.setReceivedDtm(receivedDtm);
        return receive;
    }

    public static GivingMoneyReceive createForTest(long id, long receiveUserId, LocalDateTime receivedDtm) {
        GivingMoneyReceive receive = createForTest(receiveUserId, receivedDtm);
        receive.setId(id);
        return receive;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public GivingMoneyOrder getGivingMoneyOrder() {
        return givingMoneyOrder;
    }

    public void setGivingMoneyOrder(GivingMoneyOrder givingMoneyOrder) {
        this.givingMoneyOrder = givingMoneyOrder;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public Long getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(Long receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public LocalDateTime getReceivedDtm() {
        return receivedDtm;
    }

    public void setReceivedDtm(LocalDateTime receivedDtm) {
        this.receivedDtm = receivedDtm;
    }
}
