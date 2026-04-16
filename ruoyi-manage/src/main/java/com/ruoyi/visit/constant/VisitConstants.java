package com.ruoyi.visit.constant;

public final class VisitConstants
{
    private VisitConstants()
    {
    }

    public static final String CUSTOMER_TYPE_CONTRACT = "contract";
    public static final String CUSTOMER_TYPE_INDEPENDENT = "independent";
    public static final String TARGET_TYPE_CONTRACT_DEPT = "contract_dept";
    public static final String TARGET_TYPE_INDEPENDENT_CUSTOMER = "independent_customer";

    public static final String VISIT_MODE_ACTIVE = "active";
    public static final String VISIT_MODE_PASSIVE = "passive";

    public static final String SOURCE_TYPE_MANUAL = "manual";
    public static final String SOURCE_TYPE_GATEWAY_DISPLACEMENT = "gateway_displacement";

    public static final String STATUS_PENDING_APPROVE = "pending_approve";
    public static final String STATUS_REJECTED = "rejected";
    public static final String STATUS_WITHDRAWN = "withdrawn";
    public static final String STATUS_APPROVED_PENDING_FEEDBACK = "approved_pending_feedback";
    public static final String STATUS_COMPLETED = "completed";

    public static final String PASSIVE_EVENT_STATUS_PENDING_CONFIRM = "pending_confirm";
    public static final String PASSIVE_EVENT_STATUS_CONVERTED = "converted";
    public static final String PASSIVE_EVENT_STATUS_IGNORED = "ignored";

    public static final String OWNER_ASSIGN_STATUS_ENABLED = "0";
    public static final String OWNER_ASSIGN_STATUS_DISABLED = "1";

    public static final String ACTION_SUBMIT = "submit";
    public static final String ACTION_RESUBMIT = "resubmit";
    public static final String ACTION_WITHDRAW = "withdraw";
    public static final String ACTION_APPROVE = "approve";
    public static final String ACTION_REJECT = "reject";
    public static final String ACTION_FEEDBACK = "feedback";
    public static final String ACTION_PASSIVE_CONVERT = "passive_convert";
}
