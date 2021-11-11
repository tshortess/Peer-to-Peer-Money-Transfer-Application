package com.techelevator.tenmo.model;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class Transfer {
    @NotNull(message = "An amount is required.")
    @DecimalMin(value = "0.01", message="Amount must be greater than zero.")
    private BigDecimal amount;
    private int accountFrom;
    private int accountTo;
    private String toUsername;
    private String fromUsername;

    @NotNull(message = "A To User ID is required.")
    private int toUserId;
    @NotNull(message = "A From User ID is required.")
    private int fromUserId;
    private String transferStatus;
    private String transferType;
    private int transferId;

    public Transfer() {

    }

    public Transfer(BigDecimal amount, int fromUserId, int toUserId, int accountFrom, int accountTo, String transferStatus, String transferType, int transferId, String toUsername, String fromUsername) {
        this.amount = amount;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.transferStatus = transferStatus;
        this.transferType = transferType;
        this.transferId = transferId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.toUsername = toUsername;
        this.fromUsername = fromUsername;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(int accountTo) {
        this.accountTo = accountTo;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getToUserId() {
        return toUserId;
    }

    public void setToUserId(int toUserId) {
        this.toUserId = toUserId;
    }

    public int getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(int fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUsername() {
        return toUsername;
    }

    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }
}
