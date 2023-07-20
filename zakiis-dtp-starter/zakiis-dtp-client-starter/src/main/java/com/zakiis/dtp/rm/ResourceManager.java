package com.zakiis.dtp.rm;

import java.util.Map;

import io.seata.core.model.BranchType;
import io.seata.core.model.Resource;

public interface ResourceManager extends ResourceManagerInbound, ResourceManagerOutbound {

	/**
     * Register a Resource to be managed by Resource Manager.
     *
     * @param resource The resource to be managed.
     */
    void registerResource(Resource resource);

    /**
     * Unregister a Resource from the Resource Manager.
     *
     * @param resource The resource to be removed.
     */
    void unregisterResource(Resource resource);

    /**
     * Get all resources managed by this manager.
     *
     * @return resourceId -- Resource Map
     */
    Map<String, Resource> getManagedResources();

    /**
     * Get the BranchType.
     *
     * @return The BranchType of ResourceManager.
     */
    BranchType getBranchType();
}
