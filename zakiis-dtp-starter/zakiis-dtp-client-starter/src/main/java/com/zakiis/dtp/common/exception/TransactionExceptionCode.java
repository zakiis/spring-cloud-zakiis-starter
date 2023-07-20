package com.zakiis.dtp.common.exception;

public enum TransactionExceptionCode {

	/**
     * Unknown transaction exception code.
     */
    Unknown,

    /**
     * BeginFailed
     */
    BeginFailed,

    /**
     * Lock key conflict transaction exception code.
     */
    LockKeyConflict,

    /**
     * Io transaction exception code.
     */
    IO,

    /**
     * Branch rollback failed retriable transaction exception code.
     */
    BranchRollbackFailed_Retriable,

    /**
     * Branch rollback failed unretriable transaction exception code.
     */
    BranchRollbackFailed_Unretriable,

    /**
     * Branch register failed transaction exception code.
     */
    BranchRegisterFailed,

    /**
     * Branch report failed transaction exception code.
     */
    BranchReportFailed,

    /**
     * Lockable check failed transaction exception code.
     */
    LockableCheckFailed,

    /**
     * Branch transaction not exist transaction exception code.
     */
    BranchTransactionNotExist,

    /**
     * Global transaction not exist transaction exception code.
     */
    GlobalTransactionNotExist,

    /**
     * Global transaction not active transaction exception code.
     */
    GlobalTransactionNotActive,

    /**
     * Global transaction status invalid transaction exception code.
     */
    GlobalTransactionStatusInvalid,

    /**
     * Failed to send branch commit request transaction exception code.
     */
    FailedToSendBranchCommitRequest,

    /**
     * Failed to send branch rollback request transaction exception code.
     */
    FailedToSendBranchRollbackRequest,

    /**
     * Failed to add branch transaction exception code.
     */
    FailedToAddBranch,

    /**
     * Failed to lock global transaction exception code.
     */
    FailedLockGlobalTranscation,

    /**
     * FailedWriteSession
     */
    FailedWriteSession,

    /**
     * Failed to store exception code
     */
    FailedStore,

    /**
     * Lock key conflict fail fast transaction exception code.
     */
    LockKeyConflictFailFast
    ;


    /**
     * Get transaction exception code.
     *
     * @param ordinal the ordinal
     * @return the transaction exception code
     */
    public static TransactionExceptionCode get(byte ordinal) {
        return get((int)ordinal);
    }

    /**
     * Get transaction exception code.
     *
     * @param ordinal the ordinal
     * @return the transaction exception code
     */
    public static TransactionExceptionCode get(int ordinal) {
        TransactionExceptionCode value = null;
        try {
            value = TransactionExceptionCode.values()[ordinal];
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown TransactionExceptionCode[" + ordinal + "]");
        }
        return value;
    }
}
