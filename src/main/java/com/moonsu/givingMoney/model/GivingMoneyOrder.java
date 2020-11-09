package com.moonsu.givingMoney.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moonsu.givingMoney.exception.GivingMoneyException;
import com.moonsu.givingMoney.exception.GivingMoneyExceptionType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "giving_money_order", uniqueConstraints = @UniqueConstraint(columnNames = {"room_id", "token"}))
@SequenceGenerator(
        name = "GIVING_MONEY_ORDER_SEQ_GEN",
        sequenceName = "GIVING_MONEY_ORDER_SEQ",
        initialValue = 1,
        allocationSize = 1)
public class GivingMoneyOrder {

    public static final int VALID_RECEIVE_TERM_MINUTE = 10;
    public static final int VALID_SEARCH_TERM_DAYS = 7;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GIVING_MONEY_ORDER_SEQ_GEN")
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "room_id", nullable = false)
    private long roomId; // 방 id

    @Column(name = "giving_user_id", nullable = false)
    private long givingUserId;  // 뿌리는 사용자 id

    @Column(name = "token", length = 3, nullable = false)
    private String token;   // 토큰

    @Column(name = "create_dtm", nullable = false)
    private LocalDateTime createDtm;    // 방 생성일시

    @OneToMany(mappedBy = "givingMoneyOrder", cascade = CascadeType.PERSIST)
    private List<GivingMoneyReceive> givingMoneyReceives = new ArrayList<>();    // 분배 리스트

    public static GivingMoneyOrder create(long roomId, String token) {
        GivingMoneyOrder order = new GivingMoneyOrder();
        order.setRoomId(roomId);
        order.setToken(token);
        return order;
    }

    public static GivingMoneyOrder create(long roomId, long givingUserId, int receivableCount, String token, LocalDateTime createDtm) {
        GivingMoneyOrder givingMoneyOrder = create(roomId, token);
        givingMoneyOrder.setGivingUserId(givingUserId);
        givingMoneyOrder.setCreateDtm(createDtm);
        givingMoneyOrder.setGivingMoneyReceives(new ArrayList<>(receivableCount));
        return givingMoneyOrder;
    }

    @JsonIgnore
    public LocalDateTime getReceiveExpireDtm() {
        return getCreateDtm().plusMinutes(VALID_RECEIVE_TERM_MINUTE);
    }

    @JsonIgnore
    public LocalDateTime getSearchExpireDtm() {
        return getCreateDtm().plusDays(VALID_SEARCH_TERM_DAYS);
    }

    /**
     * @return id asc 를 기준하여 아직 미수령중인 money 를 return
     */
    @JsonIgnore
    public GivingMoneyReceive getNextGivingMoneyReceivable() {
        // GivingMoneyReceive.id asc 를 가정한다.
        for (GivingMoneyReceive receive: getGivingMoneyReceives()) {
            if (receive.getReceiveUserId() == 0L | receive.getReceivedDtm() == null) {
                return receive;
            }
        }
        throw new GivingMoneyException(GivingMoneyExceptionType.EXCEED_RECEIVE_GIVING_MONEY);
    }

    @JsonIgnore
    public boolean isAleadyReceived(long userId) {
        for (GivingMoneyReceive receive: getGivingMoneyReceives()) {
            if (receive.getReceiveUserId() == userId) {
                return true;
            }
        }
        return false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public long getGivingUserId() {
        return givingUserId;
    }

    public void setGivingUserId(long givingUserId) {
        this.givingUserId = givingUserId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getCreateDtm() {
        return createDtm;
    }

    public void setCreateDtm(LocalDateTime createDtm) {
        this.createDtm = createDtm;
    }

    public List<GivingMoneyReceive> getGivingMoneyReceives() {
        return givingMoneyReceives;
    }

    public void setGivingMoneyReceives(List<GivingMoneyReceive> givingMoneyReceives) {
        this.givingMoneyReceives = givingMoneyReceives;
    }

}
