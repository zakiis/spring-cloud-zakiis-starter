package com.zakiis.dtp.common.constants;

public enum GlobalStatus {

	/**
     * Un known global status.
     */
    UNKNOWN(0),

    /**
     * The Begin.
     */
    // PHASE 1: can accept new branch registering.
    BEGIN(1),

    /**
     * PHASE 2: Running Status: may be changed any time.
     */
    // Committing.
    COMMITTING(2),

    /**
     * The Commit retrying.
     */
    // Retrying commit after a recoverable failure.
    COMMIT_RETRYING(3),

    /**
     * Rollbacking global status.
     */
    // Rollbacking
    ROLLBACKING(4),

    /**
     * The Rollback retrying.
     */
    // Retrying rollback after a recoverable failure.
    ROLLBACK_RETRYING(5),

    /**
     * The Timeout rollbacking.
     */
    // Rollbacking since timeout
    TIMEOUT_ROLLBACKING(6),

    /**
     * The Timeout rollback retrying.
     */
    // Retrying rollback (since timeout) after a recoverable failure.
    TIMEOUT_ROLLBACK_RETRYING(7),

    /**
     * All branches can be async committed. The committing is NOT done yet, but it can be seen as committed for TM/RM
     * client.
     */
    ASYNC_COMMITTING(8),

    /**
     * PHASE 2: Final Status: will NOT change any more.
     */
    // Finally: global transaction is successfully committed.
    COMMITTED(9),

    /**
     * The Commit failed.
     */
    // Finally: failed to commit
    COMMIT_FAILED(10),

    /**
     * The Rollbacked.
     */
    // Finally: global transaction is successfully rollbacked.
    ROLLBACKED(11),

    /**
     * The Rollback failed.
     */
    // Finally: failed to rollback
    ROLLBACK_FAILED(12),

    /**
     * The Timeout rollbacked.
     */
    // Finally: global transaction is successfully rollbacked since timeout.
    TIMEOUT_ROLLBACKED(13),

    /**
     * The Timeout rollback failed.
     */
    // Finally: failed to rollback since timeout
    TIMEOUT_ROLLBACK_FAILED(14),

    /**
     * The Finished.
     */
    // Not managed in session MAP any more
    FINISHED(15),

    /**
     * The commit retry Timeout .
     */
    // Finally: failed to commit since retry timeout
    COMMIT_RETRY_TIMEOUT(16),

    /**
     * The rollback retry Timeout .
     */
    // Finally: failed to rollback since retry timeout
    ROLLBACK_RETRY_TIMEOUT(17)

    ;

    private final int code;

    GlobalStatus(int code) {
        this.code = code;
    }


    /**
     * Gets code.
     *
     * @return the code
     */
    public int getCode() {
        return code;
    }


    /**
     * Get global status.
     *
     * @param code the code
     * @return the global status
     */
    public static GlobalStatus get(byte code) {
        return get((int) code);
    }

    /**
     * Get global status.
     *
     * @param code the code
     * @return the global status
     */
    public static GlobalStatus get(int code) {
        GlobalStatus value = null;
        try {
            value = GlobalStatus.values()[code];
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown GlobalStatus[" + code + "]");
        }
        return value;
    }
}
