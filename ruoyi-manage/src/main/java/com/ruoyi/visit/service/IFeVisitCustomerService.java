package com.ruoyi.visit.service;

import java.util.List;
import com.ruoyi.visit.domain.FeVisitCustomer;

public interface IFeVisitCustomerService
{
    FeVisitCustomer selectFeVisitCustomerByCustomerId(Long customerId);

    List<FeVisitCustomer> selectFeVisitCustomerList(FeVisitCustomer customer);

    int insertFeVisitCustomer(FeVisitCustomer customer);

    int updateFeVisitCustomer(FeVisitCustomer customer);

    int deleteFeVisitCustomerByCustomerIds(Long[] customerIds);
}
