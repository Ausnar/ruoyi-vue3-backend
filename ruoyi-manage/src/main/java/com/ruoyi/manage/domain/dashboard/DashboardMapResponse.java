package com.ruoyi.manage.domain.dashboard;

import java.util.ArrayList;
import java.util.List;

public class DashboardMapResponse
{
    private Long rootDeptId;
    private List<DashboardMapNode> roots = new ArrayList<DashboardMapNode>();

    public Long getRootDeptId()
    {
        return rootDeptId;
    }

    public void setRootDeptId(Long rootDeptId)
    {
        this.rootDeptId = rootDeptId;
    }

    public List<DashboardMapNode> getRoots()
    {
        return roots;
    }

    public void setRoots(List<DashboardMapNode> roots)
    {
        this.roots = roots;
    }
}
