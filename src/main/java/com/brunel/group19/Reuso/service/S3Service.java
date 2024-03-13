package com.brunel.group19.Reuso.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class S3Service {
	private String bucket;
	private AmazonS3 s3;

	@Autowired
	S3Service(@Value("${aws.access_key_id}") String keyId, @Value("${aws.secret_access_key}") String secretKey,
			@Value("${s3.region}") String region, @Value("${s3.bucket}") String bucket) {
		this.bucket = bucket;
		AWSCredentials credentials = new BasicAWSCredentials(keyId, secretKey);
		s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(region).build();
	}

	public void upload(File f, String key_name) {
		TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(s3).build();
		try {
			Upload xfer = xfer_mgr.upload(bucket, key_name, f);
			xfer.waitForCompletion();
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
		xfer_mgr.shutdownNow(false);
	}

	public void upload(MultipartFile image, String key_name) {
		TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(s3).build();
		try {
			//Upload xfer = xfer_mgr.upload(bucket, key_name, toJpeg(image));
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(image.getSize());
			Upload xfer = xfer_mgr.upload(
				bucket, key_name, 
				image.getInputStream(), meta);
			xfer.waitForCompletion();
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		xfer_mgr.shutdownNow(false);
	}

	public File download(String key_name) throws IOException {
		File f = File.createTempFile("post", null);
		f.deleteOnExit();
		TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(s3).build();
		try {
			Download xfer = xfer_mgr.download(bucket, key_name, f);
			xfer.waitForCompletion();
			
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
		xfer_mgr.shutdownNow(false);
		return f;
	}

	public byte[] downloadToBytes(String key_name) throws IOException {
		File f = File.createTempFile("post", null);
		TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(s3).build();
		try {
			Download xfer = xfer_mgr.download(bucket, key_name, f);
			xfer.waitForCompletion();
			
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			return null;
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
		xfer_mgr.shutdownNow(false);
		byte[] data = Files.readAllBytes(f.toPath());
		f.delete(); return data;
	}
}
