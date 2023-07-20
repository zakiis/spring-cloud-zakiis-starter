package com.zakiis.dtp.common.constants;

import com.zakiis.dtp.common.exception.ShouldNeverHappenException;


public enum BranchStatus {

	/**
     * The Unknown.
     * description:Unknown branch status.
     */
    UNKNOWN(0),

    /**
     * The Registered.
     * description:Registered to TC.
     */
    REGISTERED(1),

    /**
     * The Phase one done.
     * description:Branch logic is successfully done at phase one.
     */
    PHASE_ONE_DONE(2),

    /**
     * The Phase one failed.
     * description:Branch logic is failed at phase one.
     */
    PHASE_ONE_FAILED(3),

    /**
     * The Phase one timeout.
     * description:Branch logic is NOT reported for a timeout.
     */
    PHASE_ONE_TIMEOUT(4),

    /**
     * The Phase two committed.
     * description:Commit logic is successfully done at phase two.
     */
    PHASE_TWO_COMMITTED(5),

    /**
     * The Phase two commit failed retryable.
     * description:Commit logic is failed but retryable.
     */
    PHASE_TWO_COMMIT_FAILED_RETRYABLE(6),

    /**
     * The Phase two commit failed unretryable.
     * description:Commit logic is failed and NOT retryable.
     */
    PHASE_TWO_COMMIT_FAILED_UNRETRYABLE(7),

    /**
     * The Phase two rollbacked.
     * description:Rollback logic is successfully done at phase two.
     */
    PHASE_TWO_ROLLBACKED(8),

    /**
     * The Phase two rollback failed retryable.
     * description:Rollback logic is failed but retryable.
     */
    PHASE_TWO_ROLLBACK_FAILED_RETRYABLE(9),

    /**
     * The Phase two rollback failed unretryable.
     * description:Rollback logic is failed but NOT retryable.
     */
    PHASE_TWO_ROLLBACK_FAILED_UNRETRYABLE(10),

    /**
     * The Phase two commit failed retryable because of XAException.XAER_NOTA.
     * description:Commit logic is failed because of XAException.XAER_NOTA but retryable.
     */
    PHASE_TWO_COMMIT_FAILED_XAER_NOTA_RETRYABLE(11),

    /**
     * The Phase two rollback failed retryable because of XAException.XAER_NOTA.
     * description:rollback logic is failed because of XAException.XAER_NOTA but retryable.
     */
    PHASE_TWO_ROLLBACK_FAILED_XAER_NOTA_RETRYABLE(12);

    private int code;

    BranchStatus(int code) {
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
     * Get branch status.
     *
     * @param code the code
     * @return the branch status
     */
    public static BranchStatus get(byte code) {
        return get((int)code);
    }

    /**
     * Get branch status.
     *
     * @param code the code
     * @return the branch status
     */
    public static BranchStatus get(int code) {
        BranchStatus value = null;
        try {
            value = BranchStatus.values()[code];
        } catch (Exception e) {
            throw new ShouldNeverHappenException("Unknown BranchStatus[" + code + "]");
        }
        return value;
    }
}
