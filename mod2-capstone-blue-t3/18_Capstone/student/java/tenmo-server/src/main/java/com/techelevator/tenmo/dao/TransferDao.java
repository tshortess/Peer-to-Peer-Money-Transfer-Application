package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {
    public Transfer createTransfer(Transfer transfer);

    public int getAccountIdByUserId(int userId);

    public String getUsernameByUserId(int userId);

    public List<Transfer> getTransfers(int userId, int transferStatusId);

    public void approvedTransfer(Transfer transfer);

    public Transfer updateTransfer(int transferId, int transferStatusId);

}
