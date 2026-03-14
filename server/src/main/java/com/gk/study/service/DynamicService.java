package com.gk.study.service;

import com.gk.study.entity.Dynamic;
import java.util.List;

public interface DynamicService {
    List<Dynamic> getDynamicList();
    void createDynamic(Dynamic dynamic);
    void deleteDynamic(String id);
    List<Dynamic> getDynamicList(String keyword, String sort, String userId);
    void updateDynamic(Dynamic dynamic);
    List<Dynamic> getDynamicListById(String userId);
    Dynamic getDynamicDetail(String id);
}