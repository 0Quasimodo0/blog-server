package com.frankfang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.frankfang.service.OssService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "阿里云对象存储接口")
@RequestMapping("/api")
@RestController
public class OssController {
	
	@Autowired
	private OssService ossService;
	
	@ApiOperation(value = "OSS上传签名生成")
	@GetMapping("/admin/aliyun/oss/policy")
	public Object policy(@RequestParam("id") Integer id, @RequestParam("type") String type, @RequestParam("details") String details) {
		return ossService.policy(id, type, details);
	}
}
