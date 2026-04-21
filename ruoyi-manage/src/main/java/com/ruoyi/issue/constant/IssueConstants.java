package com.ruoyi.issue.constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class IssueConstants
{
    private IssueConstants()
    {
    }

    public static final String DEL_FLAG_NORMAL = "0";
    public static final String DEL_FLAG_DELETED = "2";

    public static final String PROJECT_STATUS_ACTIVE = "active";
    public static final String PROJECT_STATUS_INACTIVE = "inactive";

    public static final String MODULE_STATUS_ACTIVE = "active";
    public static final String MODULE_STATUS_INACTIVE = "inactive";

    public static final String ISSUE_STATUS_PENDING = "pending";
    public static final String ISSUE_STATUS_IN_PROGRESS = "in_progress";
    public static final String ISSUE_STATUS_RESOLVED = "resolved";
    public static final String ISSUE_STATUS_CLOSED = "closed";

    public static final String PRIORITY_P1 = "P1";
    public static final String PRIORITY_P2 = "P2";
    public static final String PRIORITY_P3 = "P3";

    public static final String OWNER_FRONTEND = "frontend";
    public static final String OWNER_BACKEND = "backend";
    public static final String OWNER_FULLSTACK = "fullstack";
    public static final String OWNER_OTHER = "other";

    public static final Set<String> PROJECT_STATUS_SET = Collections.unmodifiableSet(new HashSet<String>(
        Arrays.asList(PROJECT_STATUS_ACTIVE, PROJECT_STATUS_INACTIVE)));

    public static final Set<String> MODULE_STATUS_SET = Collections.unmodifiableSet(new HashSet<String>(
        Arrays.asList(MODULE_STATUS_ACTIVE, MODULE_STATUS_INACTIVE)));

    public static final Set<String> ISSUE_STATUS_SET = Collections.unmodifiableSet(new HashSet<String>(
        Arrays.asList(ISSUE_STATUS_PENDING, ISSUE_STATUS_IN_PROGRESS, ISSUE_STATUS_RESOLVED, ISSUE_STATUS_CLOSED)));

    public static final Set<String> PRIORITY_SET = Collections.unmodifiableSet(new HashSet<String>(
        Arrays.asList(PRIORITY_P1, PRIORITY_P2, PRIORITY_P3)));

    public static final Set<String> OWNER_SIDE_SET = Collections.unmodifiableSet(new HashSet<String>(
        Arrays.asList(OWNER_FRONTEND, OWNER_BACKEND, OWNER_FULLSTACK, OWNER_OTHER)));
}
