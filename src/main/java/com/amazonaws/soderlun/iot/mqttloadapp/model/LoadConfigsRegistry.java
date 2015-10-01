package com.amazonaws.soderlun.iot.mqttloadapp.model;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.soderlun.iot.mqttloadapp.SystemConfig;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;

/**
 * Integration with storage is not complete
 *
 * @author soderlun
 */
public class MetricsConfigsRegistry {

    private static final Map<String, MetricsConfig> configs = new HashMap<>();
    private static Logger LOG = Logger.getLogger(MetricsConfigsRegistry.class.getName());

    public static List<MetricsConfig> getAllConfigs() {

        String root = SystemConfig.getMetricsConfigRoot();

        configs.clear();

        List<MetricsConfig> result = new ArrayList<>(configs.values());

        if (isLocalFilesystem(root)) {
            File r = new File(root);
            File[] configFiles = r.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".json");
                }
            });

            for (File c : configFiles) {
                try {
                    MetricsConfig cfg = MetricsConfig.newInstance(Json.createReader(new FileInputStream(c)).readObject());
                    int idx = c.getName().indexOf(".json");
                    cfg.setId(c.getName().substring(0, idx));
                    LOG.log(Level.INFO, "Read metrics config {0} from file {1}", new Object[]{cfg.id, c.getAbsolutePath()});
                    result.add(cfg);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(MetricsConfigsRegistry.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (isS3(root)) {
            // TBD
            AmazonS3 api = new AmazonS3Client(SystemConfig.credentials);

            SystemConfig.S3Info info = SystemConfig.getS3Info(root);

            ListObjectsRequest req = new ListObjectsRequest();
            req.setBucketName(info.bucket);
            req.setPrefix(info.prefixPath);

            ObjectListing resp = api.listObjects(req);
            List<S3ObjectSummary> configObjects = resp.getObjectSummaries();

            for (S3ObjectSummary c : configObjects) {
                if (c.getKey().endsWith(".json")) {

                    S3Object conf = api.getObject(info.bucket, c.getKey());
                    S3ObjectInputStream ois = conf.getObjectContent();

                    MetricsConfig cfg = MetricsConfig.newInstance(Json.createReader(ois).readObject());

                    result.add(cfg);
                }
            }
        }

        for (MetricsConfig c : result) {
            configs.put(c.getId(), c);
        }

        return result;
    }

    public static MetricsConfig getConfig(String id) {
        return configs.get(id);
    }

    public static void createConfig(MetricsConfig cfg) {
        saveConfig(cfg);

        configs.put(cfg.getId(), cfg);
    }

    public static void deleteConfig(String id) throws MetricsConfigException {
        if (configs.remove(id) == null) {
            throw new MetricsConfigException("Could not find config " + id);
        }

        String root = SystemConfig.getMetricsConfigRoot();

        if (isLocalFilesystem(root)) {
            File f = new File(root, id + ".json");
            if (!f.exists()) {
                LOG.log(Level.INFO, "Config {0} does not exist in storage {1} so doing nothing which is idempotent", new Object[]{id, root});
            }
            f.delete();
        } else if (isS3(root)) {
            AmazonS3 api = new AmazonS3Client(SystemConfig.credentials);

            SystemConfig.S3Info info = SystemConfig.getS3Info(SystemConfig.getConfigRoot());

            byte[] data;
            String key = info.prefixPath + "/" + id + ".json";
            api.deleteObject(info.bucket, key);
            LOG.log(Level.INFO, "successfully deleted MetricsConfiguration in S3 {0}/{1}", new Object[]{info.bucket, key});

        }
    }

    public static void updateConfig(String id, MetricsConfig cfg) {
        configs.put(id, cfg);
        saveConfig(cfg);
    }

    private static void saveConfig(MetricsConfig cfg) {
        String root = SystemConfig.getMetricsConfigRoot();
        String path = root + "/" + cfg.getId() + ".json";
        if (isLocalFilesystem(root)) {
            Path p = Paths.get(path);
            try {
                Files.write(p, cfg.toJson().getBytes("UTF-8"));
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Could not write metrics config to local storage: " + p.toString(), ex);
                throw new RuntimeException("Could not write to local storage");
            }
        } else if (isS3(root)) {
            AmazonS3 api = new AmazonS3Client(SystemConfig.credentials);

            SystemConfig.S3Info info = SystemConfig.getS3Info(SystemConfig.getConfigRoot());

            byte[] data;
            try {
                data = cfg.toJson().getBytes("UTF-8");
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                ObjectMetadata meta = new ObjectMetadata();
                meta.setContentLength(data.length);
                String key = info.prefixPath + "/" + cfg.getId() + ".json";
                PutObjectResult resp = api.putObject(new PutObjectRequest(info.bucket, key, bais, meta));
                LOG.info("successfully stored MetricsConfiguration in S3 " + info.bucket + "/" + key);
            } catch (UnsupportedEncodingException ex) {
                LOG.log(Level.SEVERE, "Unknown encoding", ex);
            }

        }
    }

    private static boolean isLocalFilesystem(String root) {
        File f = new File(root);
        return f.isAbsolute();
    }

    private static boolean isS3(String root) {
        return root.startsWith("s3://");
    }
}
