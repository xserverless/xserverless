package io.xserverless.fdfsex;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class XFdfs {

    @Value("${xfdfs.trackerServer}")
    private String trackerServers;
    @Value("${xfdfs.addressMapping}")
    private String addressMapping;
    @PostConstruct
    private void init() {
        try {
            final Properties props = new Properties();
            props.setProperty(ClientGlobal.PROP_KEY_TRACKER_SERVERS, trackerServers);
            props.setProperty(ClientGlobal.PROP_KEY_ADDRESS_MAPPING, addressMapping);
            ClientGlobal.initByProperties(props);
        } catch (IOException | MyException e) {
            e.printStackTrace();
        }
    }

    public String upload(InputStream inputStream, String ext) {
        try {
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getTrackerServer();
            StorageClient storageClient = new StorageClient(trackerServer);
            String[] uploadFile = storageClient.upload_file(IOUtils.toByteArray(inputStream), ext, new NameValuePair[0]);

            return uploadFile[0] + "/" + uploadFile[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] download(String path) {
        try {
            int index = path.indexOf("/");
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getTrackerServer();
            StorageClient storageClient = new StorageClient(trackerServer);
            return storageClient.download_file(path.substring(0, index), path.substring(index + 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
