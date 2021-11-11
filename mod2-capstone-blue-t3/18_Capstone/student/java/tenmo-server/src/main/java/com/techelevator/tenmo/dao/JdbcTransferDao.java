package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {
    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transfer createTransfer(Transfer transfer) {
        int transferId = createTransfer(transfer.getAccountTo(), transfer.getAccountFrom(), transfer.getTransferType(), transfer.getTransferStatus(), transfer.getAmount());

        return getTransfer(transferId);
    }

    private Transfer getTransfer(int transferId) {
        String sql = "SELECT * FROM transfers WHERE transfer_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
        Transfer transfer = new Transfer();
        if (results.next()) {
            transfer = mapRowToTransfer(results);
        }
        return transfer;
    }

    public void approvedTransfer(Transfer transfer) {
        adjustSenderBalance(transfer.getFromUserId(), transfer.getAmount());
        adjustRecipientBalance(transfer.getToUserId(), transfer.getAmount());
    }

    @Override
    public List<Transfer> getTransfers(int userId, int transferStatusId) {
        int accountId = getAccountIdByUserId(userId);
        List<Transfer> transferList = new ArrayList<>();
        String sql = "SELECT * FROM transfers WHERE (account_from = ? OR account_to = ?) " + //specify what you want back instead of using *
                "AND transfer_status_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId, transferStatusId);
        while (results.next()) {
            transferList.add(mapRowToTransfer(results));
        }
        return transferList;
    }

    @Override
    public Transfer updateTransfer(int transferId, int transferStatusId) {
        String sql = "UPDATE transfers SET transfer_status_id = ? WHERE transfer_id = ?;";
        jdbcTemplate.update(sql, transferStatusId, transferId);
        return getTransfer(transferId);
    }

    private void adjustSenderBalance(int fromUserId, BigDecimal amount) {
        String sql = "UPDATE accounts SET balance = balance - ? WHERE user_id = ?;";
        jdbcTemplate.update(sql, amount, fromUserId);
    }

    private void adjustRecipientBalance(int toUserId, BigDecimal amount) {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE user_id = ?;";
        jdbcTemplate.update(sql, amount, toUserId);
    }

    private int createTransfer(int toAccountId, int fromAccountId, String transferType, String transferStatus, BigDecimal amount) {
        String sql = "INSERT INTO transfers(transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES(?, ?, ?, ?, ?) RETURNING transfer_id;";
        int transferId = jdbcTemplate.queryForObject(sql, Integer.class, getTransferTypeId(transferType), getTransferStatusId(transferStatus),
                fromAccountId, toAccountId, amount);
        return transferId;
    }

    public int getAccountIdByUserId(int userId) {
        String sql = "SELECT account_id FROM accounts WHERE user_id = ?;";
        int accountId = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return accountId;
    }

    public int getUserIdByAccountId(int accountId) {
        String sql = "SELECT user_id FROM accounts WHERE account_id = ?;";
        int userId = jdbcTemplate.queryForObject(sql, Integer.class, accountId);
        return userId;
    }

    public String getUsernameByUserId(int userId) {
        String sql = "SELECT username FROM users WHERE user_id = ?;";
        String username = jdbcTemplate.queryForObject(sql, String.class, userId);
        return username;
    }

    public String getTransferStatus(int transferStatusId) {
        String sql = "SELECT transfer_status_desc FROM transfer_statuses WHERE transfer_status_id = ?;";
        String transferStatus = jdbcTemplate.queryForObject(sql, String.class, transferStatusId);
        return transferStatus;
    }

    public String getTransferType(int transferTypeId) {
        String sql = "SELECT transfer_type_desc FROM transfer_types WHERE transfer_type_id = ?;";
        String transferType = jdbcTemplate.queryForObject(sql, String.class, transferTypeId);
        return transferType;
    }

    public int getTransferStatusId(String transferStatus) {
        String sql = "SELECT transfer_status_id FROM transfer_statuses WHERE transfer_status_desc = ?;";
        int transferStatusId = jdbcTemplate.queryForObject(sql, Integer.class, transferStatus);
        return transferStatusId;
    }

    public int getTransferTypeId(String transferType) {
        String sql = "SELECT transfer_type_id FROM transfer_types WHERE transfer_type_desc = ?;";
        int transferTypeId = jdbcTemplate.queryForObject(sql, Integer.class, transferType);
        return transferTypeId;
    }

    private Transfer mapRowToTransfer(SqlRowSet results) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(results.getInt("transfer_id"));
        transfer.setTransferType(getTransferType(results.getInt("transfer_type_id")));
        transfer.setTransferStatus(getTransferStatus(results.getInt("transfer_status_id")));
        transfer.setAccountFrom(results.getInt("account_from"));
        transfer.setAmount(results.getBigDecimal("amount"));
        transfer.setAccountTo(results.getInt("account_to"));
        transfer.setToUserId(getUserIdByAccountId(transfer.getAccountTo()));
        transfer.setFromUserId(getUserIdByAccountId(transfer.getAccountFrom()));
        transfer.setFromUsername(getUsernameByUserId(transfer.getFromUserId()));
        transfer.setToUsername(getUsernameByUserId(transfer.getToUserId()));
        return transfer;
    }
}
