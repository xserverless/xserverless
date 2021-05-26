package io.xserverless.fdfsex;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.tobato.fastdfs.domain.fdfs.GroupState;
import com.github.tobato.fastdfs.domain.fdfs.StorageNode;
import com.github.tobato.fastdfs.domain.fdfs.StorageNodeInfo;
import com.github.tobato.fastdfs.domain.fdfs.StorageState;
import com.github.tobato.fastdfs.service.TrackerClient;

public class XTrackerClientWrapper implements TrackerClient {
    private TrackerClient trackerClient;
    private Map<String, Map.Entry<String, Integer>> map;

    public XTrackerClientWrapper(XFdfsConfig config, TrackerClient trackerClient) {
        this.trackerClient = trackerClient;

        map = new HashMap<>();
        config.storageMapping().forEach((k, v) -> {
            String[] split = v.split(":");
            map.put(k, new AbstractMap.SimpleEntry<>(split[0], Integer.parseInt(split[1])));
        });
    }

    private StorageNode wrap(StorageNode node) {
        String key = node.getIp() + ":" + node.getPort();
        if (map.containsKey(key)) {
            node.setIp(map.get(key).getKey());
            node.setPort(map.get(key).getValue());
        }
        return node;
    }


    private StorageNodeInfo wrap(StorageNodeInfo node) {
        String key = node.getIp() + ":" + node.getPort();
        if (map.containsKey(key)) {
            node.setIp(map.get(key).getKey());
            node.setPort(map.get(key).getValue());
        }
        return node;
    }

    @Override
    public StorageNode getStoreStorage() {
        return wrap(trackerClient.getStoreStorage());
    }

    @Override
    public StorageNode getStoreStorage(String groupName) {
        return wrap(trackerClient.getStoreStorage(groupName));
    }

    @Override
    public StorageNodeInfo getFetchStorage(String groupName, String filename) {
        return wrap(trackerClient.getFetchStorage(groupName, filename));
    }

    @Override
    public StorageNodeInfo getUpdateStorage(String groupName, String filename) {
        return wrap(trackerClient.getUpdateStorage(groupName, filename));
    }

    @Override
    public List<GroupState> listGroups() {
        return trackerClient.listGroups();
    }

    @Override
    public List<StorageState> listStorages(String groupName) {
        return trackerClient.listStorages(groupName);
    }

    @Override
    public List<StorageState> listStorages(String groupName, String storageIpAddr) {
        return trackerClient.listStorages(groupName, storageIpAddr);
    }

    @Override
    public void deleteStorage(String groupName, String storageIpAddr) {
        trackerClient.deleteStorage(groupName, storageIpAddr);
    }
}
