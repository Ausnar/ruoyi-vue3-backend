package com.ruoyi.visit.mapper;

import java.util.List;
import com.ruoyi.visit.domain.FeVisitCustomer;

public interface FeVisitCustomerMapper
{
    FeVisitCustomer selectFeVisitCustomerByCustomerId(Long customerId);

    List<FeVisitCustomer> selectFeVisitCustomerList(FeVisitCustomer customer);

    int insertFeVisitCustomer(FeVisitCustomer customer);

    int updateFeVisitCustomer(FeVisitCustomer customer);

    int deleteFeVisitCustomerByCustomerIds(Long[] customerIds);
}
