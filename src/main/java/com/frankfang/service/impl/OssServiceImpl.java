package com.frankfang.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.frankfang.service.OssService;
import com.frankfang.bean.OssPolicyResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@PropertySource(value = {"classpath:application.properties"}, encoding = "utf-8")
public class OssServiceImpl implements OssService {

	@Value("${aliyun.oss.endpoint}")
	private String ENDPOINT;

	@Value("${aliyun.oss.accessKeyId}")
	private String ACCESS_KEY_ID;

	@Value("${aliyun.oss.accessKeySecret}")
	private String SECRET_ACCESS_KEY;

	@Value("${aliyun.oss.bucketName}")
	private String BUCKET_NAME;

	@Value("${aliyun.oss.policy.expire}")
	private int EXPIRE;

	@Value("${aliyun.oss.maxSize}")
	private int MAX_SIZE;

	@Value("${aliyun.oss.dir.prefix}")
	private String DIR_PREFIX;

	private final OSS ossClient;

	@Autowired
	public OssServiceImpl(OSS ossClient) {
		this.ossClient = ossClient;
	}

	@Override
	public OssPolicyResult policy(Integer id, String type, String details) {
		OssPolicyResult result = new OssPolicyResult();
		// 存储目录
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dir = DIR_PREFIX + "/uid" + id.toString() + "/" + type + "/" + details;
		// 签名有效期
		long expireEndTime = System.currentTimeMillis() + EXPIRE * 1000;
		Date expiration = new Date(expireEndTime);
		// 文件大小
		long maxSize = MAX_SIZE * 1024 * 1024;
		// 提交节点
		String action = "http://" + BUCKET_NAME + "." + ENDPOINT;
		try {
			PolicyConditions policyConds = new PolicyConditions();
			policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, maxSize);
			policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
			String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
			byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
			String policy = BinaryUtil.toBase64String(binaryData);
			String signature = ossClient.calculatePostSignature(postPolicy);
			// 返回结果
			result.setAccessKeyId(ACCESS_KEY_ID);
			result.setPolicy(policy);
			result.setSignature(signature);
			result.setKey(UUID.randomUUID().toString());
			result.setDir(dir);
			result.setHost(action);
		} catch (Exception e) {
			log.error("签名生成失败", e);
		}
		return result;
	}

}
