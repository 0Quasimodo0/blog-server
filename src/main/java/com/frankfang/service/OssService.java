package com.frankfang.service;

import com.frankfang.bean.OssPolicyResult;

public interface OssService {

	OssPolicyResult policy(Integer id, String type, String details);
}
