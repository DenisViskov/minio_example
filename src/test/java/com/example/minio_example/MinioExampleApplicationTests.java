package com.example.minio_example;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.UploadObjectArgs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;

@SpringBootTest
class MinioExampleApplicationTests {

    @Autowired
    private MinioClient minioClient;
    @TempDir
    private Path tempDir;
    private static final String BUCKET_NAME = "test-bucket";
    private static final String TEST_OBJECT_NAME = "test_object.txt";

    @BeforeEach
    void setUp() throws Exception {
        var bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build());
        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
        }
    }

    @Test
    void example() throws Exception {
        var testFilePath = tempDir.resolve("testFile.txt");
        boolean created = testFilePath.toFile().createNewFile();

        if (created) {
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(TEST_OBJECT_NAME)
                            .filename(testFilePath.toString())
                            .build()
            );
        }

        var object = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(BUCKET_NAME)
                        .object(TEST_OBJECT_NAME)
                        .build()
        );

        String res = object.object();
        System.out.println(res);
    }

    @AfterEach
    void tearDown() throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(BUCKET_NAME)
                        .object(TEST_OBJECT_NAME)
                        .build()
        );
    }
}
